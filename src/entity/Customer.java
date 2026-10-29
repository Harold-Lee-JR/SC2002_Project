package entity;

import java.util.List;

/**
 * A restaurant customer (Stage 1: every customer is a Regular).
 * Behaviour that may differ between customer types (satisfaction loss, food choice)
 * is exposed as methods so that later stages can override it in subclasses
 * without touching the controllers.
 */
public class Customer {
    private static final int STARTING_SATISFACTION = 100;
    private static final int REGULAR_SATISFACTION_LOSS = 8;

    private final int id;
    private int satisfaction;
    private CustomerStatus status;
    private int turnServed;
    private Order order; // the "places" association: null until the Waiter takes the order

    public Customer(int id) {
        this.id = id;
        this.satisfaction = STARTING_SATISFACTION;
        this.status = CustomerStatus.WAITING;
        this.turnServed = -1;
    }

    /** Points lost at the end of each turn while waiting for a table or for food. Regular = 8. */
    public int getSatisfactionLoss() {
        return REGULAR_SATISFACTION_LOSS;
    }

    /** Applies one turn of satisfaction loss, never going below 0. */
    public void reduceSatisfaction() {
        satisfaction = Math.max(0, satisfaction - getSatisfactionLoss());
    }

    /**
     * Customers choose their own food. Deterministic rule (documented for reproducible runs):
     * customer #n picks menu item ((n - 1) mod menu size), i.e. Salad, Burger, Pasta, Salad, Burger, Pasta.
     */
    public MenuItem chooseMenuItem(List<MenuItem> menu) {
        return menu.get((id - 1) % menu.size());
    }

    /** Marks the customer as served: eating starts and the serving turn is remembered. */
    public void setServed(int turn) {
        this.status = CustomerStatus.EATING;
        this.turnServed = turn;
    }

    public int getTurnServed() {
        return turnServed;
    }

    /** True while the customer has not been served yet (and therefore keeps losing satisfaction). */
    public boolean isWaitingForService() {
        return status == CustomerStatus.WAITING
                || status == CustomerStatus.SEATED
                || status == CustomerStatus.ORDERED;
    }

    public int getId() {
        return id;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Customer #" + id;
    }
}
