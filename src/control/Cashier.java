package control;

import entity.Customer;
import entity.CustomerStatus;
import entity.GameResult;
import entity.Order;

/** Cashier: collects at most one payment per turn (the TurnController picks which customer). */
public class Cashier {

    /** Charges the order: order and customer become PAID and the sale is recorded in the GameResult. */
    public void processPayment(Order o, GameResult r) {
        Customer c = o.getCustomer();
        o.markPaid();
        c.setStatus(CustomerStatus.PAID);
        r.recordPayment(o.getPrice(), c.getSatisfaction());
    }
}
