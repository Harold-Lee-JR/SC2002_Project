package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/** Customers waiting for a table, in arrival order. The manager may still pick any of them to seat first. */
public class WaitingQueue {
    private final Queue<Customer> customers = new LinkedList<>();

    public void enqueue(Customer c) {
        customers.add(c);
    }

    /** Removes and returns the customer who has waited longest, or null if the queue is empty. */
    public Customer dequeueNext() {
        return customers.poll();
    }

    /** Removes a specific customer (seated by choice, or left unhappy). Returns false if not in the queue. */
    public boolean remove(Customer c) {
        return customers.remove(c);
    }

    public boolean isEmpty() {
        return customers.isEmpty();
    }

    public int getSize() {
        return customers.size();
    }

    /** Read-only snapshot in arrival order. */
    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(new ArrayList<>(customers));
    }
}
