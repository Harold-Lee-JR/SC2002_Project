package control;

import entity.Order;

/** Chef: spends one preparation unit per turn on one order of the manager's choice. */
public class Chef {

    /** Advances the order by one unit; when the last unit is done the order becomes READY. */
    public void prepareOrder(Order o, int turn) {
        o.advancePreparation();
        if (o.isFullyPrepared()) {
            o.markReady(turn);
        }
    }
}
