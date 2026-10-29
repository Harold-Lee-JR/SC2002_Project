package entity;

/** Life-cycle of a customer: WAITING -> SEATED -> ORDERED -> EATING -> READY_TO_PAY -> PAID (or ABANDONED). */
public enum CustomerStatus {
    WAITING,
    SEATED,
    ORDERED,
    EATING,
    READY_TO_PAY,
    PAID,
    ABANDONED
}
