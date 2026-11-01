package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The restaurant floor: its tables, the waiting queue and the menu. */
public class Restaurant {
    private final List<Table> tables = new ArrayList<>();
    private final WaitingQueue waitingQueue = new WaitingQueue();
    private final List<MenuItem> menu = new ArrayList<>();

    public Restaurant(int tableCount) {
        for (int i = 1; i <= tableCount; i++) {
            tables.add(new Table(i));
        }
        menu.add(new MenuItem("Salad", 9.00, 1));
        menu.add(new MenuItem("Burger", 15.00, 1));
        menu.add(new MenuItem("Pasta", 18.00, 2));
    }

    /** First free table, or null if every table is occupied. */
    public Table getAvailableTable() {
        for (Table t : tables) {
            if (t.isAvailable()) {
                return t;
            }
        }
        return null;
    }

    public WaitingQueue getWaitingQueue() {
        return waitingQueue;
    }

    public List<Table> getOccupiedTables() {
        List<Table> occupied = new ArrayList<>();
        for (Table t : tables) {
            if (!t.isAvailable()) {
                occupied.add(t);
            }
        }
        return occupied;
    }

    /** The table this customer is sitting at, or null. */
    public Table findTableOf(Customer c) {
        for (Table t : tables) {
            if (t.getOccupant() == c) {
                return t;
            }
        }
        return null;
    }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public List<MenuItem> getMenu() {
        return Collections.unmodifiableList(menu);
    }
}
