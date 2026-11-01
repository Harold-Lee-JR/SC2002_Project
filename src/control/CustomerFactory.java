package control;

import entity.Customer;
import entity.CriticCustomer;
import entity.VIPCustomer;

/**
 * Creates each arriving customer according to the fixed Stage 2 arrival schedule,
 * so every run is reproducible. Still six arrivals (turns 1, 4, 7, 10, 13, 16):
 *
 * arrival 1 Regular, 2 VIP, 3 Regular, 4 Critic, 5 Regular, 6 Regular.
 *
 * This is the only place that knows the concrete customer types; after creation
 * every customer is handled as a plain Customer.
 */
public class CustomerFactory {
    private static final int VIP_ARRIVAL = 2;
    private static final int CRITIC_ARRIVAL = 4;

    /** The customer id is also its arrival number (customer #1 arrives first). */
    public Customer createCustomer(int id) {
        if (id == VIP_ARRIVAL) {
            return new VIPCustomer(id);
        }
        if (id == CRITIC_ARRIVAL) {
            return new CriticCustomer(id);
        }
        return new Customer(id);
    }
}
