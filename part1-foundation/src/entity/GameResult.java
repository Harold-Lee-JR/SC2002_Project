package entity;

/**
 * Running score of the game. Revenue only ever grows through recordPayment(),
 * so unpaid or abandoned orders can never count towards it.
 */
public class GameResult {
    private double revenue;
    private int paidCount;
    private int satisfactionTotal;
    private GameOutcome outcome = GameOutcome.IN_PROGRESS;
    private int servedCount;
    private int abandonedCount;

    /** Called by the Cashier when a customer pays. Records the satisfaction at the moment of payment. */
    public void recordPayment(double price, int satisfaction) {
        revenue += price;
        paidCount++;
        satisfactionTotal += satisfaction;
    }

    public void recordServed() {
        servedCount++;
    }

    public void recordAbandonment() {
        abandonedCount++;
    }

    /** Average satisfaction of paid customers; 0 if nobody has paid (the UI shows "N/A" in that case). */
    public double calculateAverageSatisfaction() {
        if (paidCount == 0) {
            return 0;
        }
        return (double) satisfactionTotal / paidCount;
    }

    /** Decides VICTORY or DEFEAT. Only meant to be called after the final turn. */
    public GameOutcome evaluate(GameConfig cfg) {
        boolean enoughCustomers = paidCount >= cfg.getVictoryPaidCustomers();
        boolean enoughRevenue = revenue >= cfg.getVictoryRevenue();
        boolean happyEnough = paidCount > 0 && calculateAverageSatisfaction() >= cfg.getVictorySatisfaction();
        outcome = (enoughCustomers && enoughRevenue && happyEnough) ? GameOutcome.VICTORY : GameOutcome.DEFEAT;
        return outcome;
    }

    public boolean isVictory() {
        return outcome == GameOutcome.VICTORY;
    }

    public double getRevenue() {
        return revenue;
    }

    public int getPaidCount() {
        return paidCount;
    }

    public int getSatisfactionTotal() {
        return satisfactionTotal;
    }

    public GameOutcome getOutcome() {
        return outcome;
    }

    public int getServedCount() {
        return servedCount;
    }

    public int getAbandonedCount() {
        return abandonedCount;
    }
}
