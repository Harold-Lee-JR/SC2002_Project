package entity;

import java.util.List;

/**
 * Food critic: impatient (loses 12 satisfaction per waiting turn) and pays full price.
 * Special behaviour (our documented order preference): a critic always orders the most
 * expensive item on the menu, so with the Stage 1 menu that is always Pasta, which needs
 * 2 preparation units. The manager has to decide whether to rush the critic or serve
 * quicker dishes first.
 */
public class CriticCustomer extends Customer {
    private static final int CRITIC_SATISFACTION_LOSS = 12;

    public CriticCustomer(int id) {
        super(id);
    }

    @Override
    public int getSatisfactionLoss() {
        return CRITIC_SATISFACTION_LOSS;
    }

    /** Picks the most expensive item; on a tie the one listed first on the menu. */
    @Override
    public MenuItem chooseMenuItem(List<MenuItem> menu) {
        MenuItem choice = menu.get(0);
        for (MenuItem m : menu) {
            if (m.getPrice() > choice.getPrice()) {
                choice = m;
            }
        }
        return choice;
    }

    @Override
    protected String getTypeLabel() {
        return "Critic";
    }
}
