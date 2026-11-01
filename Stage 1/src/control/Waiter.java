package control;

import entity.Customer;
import entity.CustomerStatus;
import entity.MenuItem;
import entity.Order;
import entity.Restaurant;
import entity.Table;

/** Waiter: completes at most one task per turn (the TurnController enforces that limit). */
public class Waiter {
    private final Restaurant restaurant;

    public Waiter(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    /** Seats a customer at a free table. The customer must already have been removed from the queue. */
    public void seatCustomer(Customer c, Table t) {
        t.seatCustomer(c);
        c.setStatus(CustomerStatus.SEATED);
    }

    /** Takes the order: the customer chooses the dish, an Order is created in state PREPARING. */
    public Order takeOrder(Customer c) {
        MenuItem item = c.chooseMenuItem(restaurant.getMenu());
        Order order = new Order(c, item);
        c.setOrder(order);
        c.setStatus(CustomerStatus.ORDERED);
        return order;
    }

    /** Serves a READY order: the order becomes SERVED and the customer starts eating. */
    public void serve(Order o, int turn) {
        o.markServed(turn);
        o.getCustomer().setServed(turn);
    }
}
