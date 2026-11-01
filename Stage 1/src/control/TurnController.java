package control;

import boundary.UserInterface;
import entity.Customer;
import entity.CustomerStatus;
import entity.Decision;
import entity.GameConfig;
import entity.GameResult;
import entity.Order;
import entity.OrderStatus;
import entity.Restaurant;
import entity.Table;
import entity.WaitingQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Runs one turn of the game. The GameController decides how many turns are played;
 * this class decides what happens inside a single turn:
 *
 * 1 arrivals -> display -> 2 manager choices (Waiter, then Chef) -> 3 kitchen and serving
 * -> 4 eating and payment -> 5 waiting and abandonment.
 *
 * (Step 6, the result check, belongs to the GameController after the last turn.)
 */
public class TurnController {
    private final GameConfig config;
    private final Restaurant restaurant;
    private final GameResult result;
    private final UserInterface ui;

    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;

    private int nextCustomerId = 1;

    public TurnController(GameConfig config, Restaurant restaurant, GameResult result, UserInterface ui) {
        this.config = config;
        this.restaurant = restaurant;
        this.result = result;
        this.ui = ui;
        this.waiter = new Waiter(restaurant);
        this.chef = new Chef();
        this.cashier = new Cashier();
    }

    public void executeTurn(int turn) {
        processArrivals(turn);
        ui.displayState(turn, config.getMaxTurns(), restaurant, result);

        // The Waiter always acts before the Chef, so a dish finished by the Chef this turn
        // can only be served from the next turn. The Chef may still cook an order the Waiter just took.
        Decision waiterDecision = ui.promptDecision(buildWaiterOptions());
        processWaiterAction(waiterDecision, turn);

        Decision chefDecision = ui.promptDecision(buildChefOptions());
        processChefAction(chefDecision, turn);

        // Payment runs before eating on purpose: a customer who becomes READY_TO_PAY in this turn
        // must wait until the next turn to pay, and this order guarantees that without extra state.
        processPayment();
        processEating(turn);
        processAbandonment();
    }

    // ---------------------------------------------------------------- step 1

    private void processArrivals(int turn) {
        if (config.isArrivalTurn(turn)) {
            Customer c = new Customer(nextCustomerId++);
            restaurant.getWaitingQueue().enqueue(c);
            ui.displayMessage(c + " arrives and joins the waiting queue.");
        }
    }

    // ---------------------------------------------------------------- step 2 (options)

    private List<Decision> buildWaiterOptions() {
        List<Decision> options = new ArrayList<>();

        Table freeTable = restaurant.getAvailableTable();
        if (freeTable != null) {
            for (Customer c : restaurant.getWaitingQueue().getCustomers()) {
                options.add(Decision.seat(c, freeTable));
            }
        }
        for (Table t : restaurant.getOccupiedTables()) {
            Customer c = t.getOccupant();
            if (c.getStatus() == CustomerStatus.SEATED) {
                options.add(Decision.takeOrder(c, t));
            }
        }
        for (Table t : restaurant.getOccupiedTables()) {
            Order o = t.getOccupant().getOrder();
            if (o != null && o.getStatus() == OrderStatus.READY) {
                options.add(Decision.serve(o, t));
            }
        }
        options.add(Decision.doNothing("Waiter"));
        return options;
    }

    private List<Decision> buildChefOptions() {
        List<Decision> options = new ArrayList<>();
        for (Table t : restaurant.getOccupiedTables()) {
            Order o = t.getOccupant().getOrder();
            if (o != null && o.getStatus() == OrderStatus.PREPARING) {
                options.add(Decision.prepare(o, t));
            }
        }
        options.add(Decision.doNothing("Chef"));
        return options;
    }

    // ---------------------------------------------------------------- step 3 (execution)

    private void processWaiterAction(Decision d, int turn) {
        switch (d.getType()) {
            case SEAT:
                if (!d.getTable().isAvailable() || !restaurant.getWaitingQueue().remove(d.getCustomer())) {
                    ui.displayError("Cannot seat " + d.getCustomer() + ": no free table or customer not in queue.");
                    return;
                }
                waiter.seatCustomer(d.getCustomer(), d.getTable());
                ui.displayMessage(d.getCustomer() + " is seated at Table " + d.getTable().getTableNumber() + ".");
                break;
            case TAKE_ORDER:
                Order order = waiter.takeOrder(d.getCustomer());
                ui.displayMessage(d.getCustomer() + " orders " + order.getItem().getName()
                        + " (" + money(order.getPrice()) + ", " + order.getItem().getPrepUnit() + " prep unit(s)).");
                break;
            case SERVE:
                waiter.serve(d.getOrder(), turn);
                result.recordServed();
                ui.displayMessage(d.getOrder().getItem().getName() + " served to " + d.getCustomer()
                        + ". They start eating.");
                break;
            case WAIT:
            default:
                break;
        }
    }

    private void processChefAction(Decision d, int turn) {
        if (d.getType() != Decision.Type.PREPARE) {
            return;
        }
        Order o = d.getOrder();
        chef.prepareOrder(o, turn);
        if (o.getStatus() == OrderStatus.READY) {
            ui.displayMessage(o.getItem().getName() + " for " + o.getCustomer()
                    + " is READY (it can be served from next turn).");
        } else {
            ui.displayMessage("Chef cooked one unit of " + o.getItem().getName() + " for " + o.getCustomer()
                    + " (" + o.getPreparationRemaining() + " unit(s) left).");
        }
    }

    // ---------------------------------------------------------------- step 4

    /** Cashier handles at most one READY_TO_PAY customer per turn, the one served earliest first. */
    private void processPayment() {
        Customer next = null;
        for (Table t : restaurant.getOccupiedTables()) {
            Customer c = t.getOccupant();
            if (c.getStatus() == CustomerStatus.READY_TO_PAY
                    && (next == null || c.getTurnServed() < next.getTurnServed())) {
                next = c;
            }
        }
        if (next == null) {
            return;
        }
        Table table = restaurant.findTableOf(next);
        Order order = next.getOrder();
        cashier.processPayment(order, result);
        table.release();
        ui.displayMessage(next + " pays " + money(order.getPrice()) + " (satisfaction " + next.getSatisfaction()
                + " recorded). Table " + table.getTableNumber() + " is free.");
    }

    /** A customer served on an earlier turn finishes one full turn of eating and becomes READY_TO_PAY. */
    private void processEating(int turn) {
        for (Table t : restaurant.getOccupiedTables()) {
            Customer c = t.getOccupant();
            if (c.getStatus() == CustomerStatus.EATING && c.getTurnServed() < turn) {
                c.setStatus(CustomerStatus.READY_TO_PAY);
                ui.displayMessage(c + " finished eating and is ready to pay.");
            }
        }
    }

    // ---------------------------------------------------------------- step 5

    /** Unserved customers lose satisfaction; anyone reaching 0 leaves without paying. */
    private void processAbandonment() {
        WaitingQueue queue = restaurant.getWaitingQueue();
        List<Customer> present = new ArrayList<>(queue.getCustomers());
        for (Table t : restaurant.getOccupiedTables()) {
            present.add(t.getOccupant());
        }
        for (Customer c : present) {
            if (!c.isWaitingForService()) {
                continue;
            }
            c.reduceSatisfaction();
            if (c.getSatisfaction() == 0) {
                abandon(c);
            }
        }
    }

    private void abandon(Customer c) {
        c.setStatus(CustomerStatus.ABANDONED);
        if (c.getOrder() != null) {
            c.getOrder().markCancelled();
        }
        Table table = restaurant.findTableOf(c);
        if (table != null) {
            table.release();
        }
        restaurant.getWaitingQueue().remove(c);
        result.recordAbandonment();
        ui.displayMessage(c + " ran out of patience and left without paying.");
    }

    private String money(double amount) {
        return String.format(Locale.US, "$%.2f", amount);
    }
}
