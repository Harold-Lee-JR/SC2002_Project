package entity;

/**
 * One option the manager can pick for a staff member in a turn.
 * The controller builds a list of valid Decisions, the UI lets the player choose one,
 * and the controller executes it. Because only valid options are ever offered,
 * the UI never needs to know any game rules.
 */
public class Decision {

    public enum Type {
        SEAT,
        TAKE_ORDER,
        SERVE,
        PREPARE,
        WAIT
    }

    private final Type type;
    private final Customer customer;
    private final Table table;
    private final Order order;
    private final String description;

    private Decision(Type type, Customer customer, Table table, Order order, String description) {
        this.type = type;
        this.customer = customer;
        this.table = table;
        this.order = order;
        this.description = description;
    }

    public static Decision seat(Customer c, Table t) {
        return new Decision(Type.SEAT, c, t, null,
                "Waiter: seat " + c + " (satisfaction " + c.getSatisfaction() + ") at Table " + t.getTableNumber());
    }

    public static Decision takeOrder(Customer c, Table t) {
        return new Decision(Type.TAKE_ORDER, c, t, null,
                "Waiter: take order from " + c + " at Table " + t.getTableNumber()
                        + " (satisfaction " + c.getSatisfaction() + ")");
    }

    public static Decision serve(Order o, Table t) {
        return new Decision(Type.SERVE, o.getCustomer(), t, o,
                "Waiter: serve " + o.getItem().getName() + " (READY) to " + o.getCustomer()
                        + " at Table " + t.getTableNumber());
    }

    public static Decision prepare(Order o, Table t) {
        return new Decision(Type.PREPARE, o.getCustomer(), t, o,
                "Chef: cook " + o.getItem().getName() + " for " + o.getCustomer()
                        + " at Table " + t.getTableNumber()
                        + " (" + o.getPreparationRemaining() + " unit(s) left)");
    }

    public static Decision doNothing(String role) {
        return new Decision(Type.WAIT, null, null, null, role + ": wait (do nothing this turn)");
    }

    public Type getType() {
        return type;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Table getTable() {
        return table;
    }

    public Order getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
