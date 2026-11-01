package entity;

/** Life-cycle of an order: PREPARING -> READY -> SERVED -> PAID (or CANCELLED if the customer leaves). */
public enum OrderStatus {
    PREPARING,
    READY,
    SERVED,
    PAID,
    CANCELLED
}
