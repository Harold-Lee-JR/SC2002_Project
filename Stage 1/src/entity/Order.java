package entity;

/**
 * One customer's order. The price is locked when the order is created.
 * Status only moves forward: PREPARING -> READY -> SERVED -> PAID, or to CANCELLED.
 */
public class Order {
    private final Customer customer;
    private final MenuItem item;
    private final double price;
    private OrderStatus status;
    private int preparationRemaining;
    private int turnReady;
    private int turnServed;

    public Order(Customer customer, MenuItem item) {
        this.customer = customer;
        this.item = item;
        this.price = item.getPrice();
        this.preparationRemaining = item.getPrepUnit();
        this.status = OrderStatus.PREPARING; // default state when an order is created
        this.turnReady = -1;
        this.turnServed = -1;
    }

    /** Spends one preparation unit on this order (no effect once it is fully prepared). */
    public void advancePreparation() {
        if (status == OrderStatus.PREPARING && preparationRemaining > 0) {
            preparationRemaining--;
        }
    }

    public boolean isFullyPrepared() {
        return preparationRemaining == 0;
    }

    public void markReady(int turn) {
        this.status = OrderStatus.READY;
        this.turnReady = turn;
    }

    public void markServed(int turn) {
        this.status = OrderStatus.SERVED;
        this.turnServed = turn;
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    /** Used when the customer leaves unhappy: the unfinished order is cancelled. */
    public void markCancelled() {
        this.status = OrderStatus.CANCELLED;
    }

    public Customer getCustomer() {
        return customer;
    }

    public MenuItem getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getPreparationRemaining() {
        return preparationRemaining;
    }

    public int getTurnReady() {
        return turnReady;
    }

    public int getTurnServed() {
        return turnServed;
    }
}
