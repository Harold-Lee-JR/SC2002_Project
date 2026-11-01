package entity;

/**
 * Fixed rules of the base setting (see assignment Section 2.1).
 * Immutable: all values are set once and only exposed through getters.
 */
public class GameConfig {
    private final int maxTurns = 18;
    private final int tableCount = 2;
    private final double victoryRevenue = 45;
    private final int victoryPaidCustomers = 4;
    private final double victorySatisfaction = 60;

    // Arrival schedule: one customer at the start of turns 1, 4, 7, 10, 13, 16 (six in total).
    private static final int FIRST_ARRIVAL_TURN = 1;
    private static final int ARRIVAL_INTERVAL = 3;
    private static final int TOTAL_ARRIVALS = 6;

    public int getMaxTurns() {
        return maxTurns;
    }

    public int getTableCount() {
        return tableCount;
    }

    public double getVictoryRevenue() {
        return victoryRevenue;
    }

    public int getVictoryPaidCustomers() {
        return victoryPaidCustomers;
    }

    public double getVictorySatisfaction() {
        return victorySatisfaction;
    }

    /** True if a customer is scheduled to arrive at the start of the given turn. */
    public boolean isArrivalTurn(int turn) {
        if (turn < FIRST_ARRIVAL_TURN) {
            return false;
        }
        int offset = turn - FIRST_ARRIVAL_TURN;
        return offset % ARRIVAL_INTERVAL == 0 && offset / ARRIVAL_INTERVAL < TOTAL_ARRIVALS;
    }
}
