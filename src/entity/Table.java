package entity;

/** A table holds at most one customer. It stays occupied until that customer pays or leaves. */
public class Table {
    private final int tableNumber;
    private Customer occupant;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public boolean isAvailable() {
        return occupant == null;
    }

    public void seatCustomer(Customer c) {
        if (!isAvailable()) {
            throw new IllegalStateException("Table " + tableNumber + " is already occupied");
        }
        this.occupant = c;
    }

    public void release() {
        this.occupant = null;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Customer getOccupant() {
        return occupant;
    }
}
