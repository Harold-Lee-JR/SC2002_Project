package boundary;

import entity.Customer;
import entity.Decision;
import entity.GameConfig;
import entity.GameResult;
import entity.Order;
import entity.OrderStatus;
import entity.Restaurant;
import entity.Table;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Command-line boundary. It only displays state and collects the manager's decisions;
 * it contains no game rules.
 */
public class UserInterface {
    private final Scanner in;
    private boolean inputEnded = false;

    public UserInterface() {
        this(new Scanner(System.in));
    }

    public UserInterface(Scanner in) {
        this.in = in;
    }

    /** Start-of-turn display: turn number, score, queue, tables, satisfaction and order status. */
    public void displayState(int turn, int maxTurns, Restaurant restaurant, GameResult result) {
        System.out.println();
        System.out.println("==================== TURN " + turn + " / " + maxTurns + " ====================");
        System.out.println("Revenue: " + money(result.getRevenue())
                + " | Avg satisfaction (paid): " + averageText(result)
                + " | Paid: " + result.getPaidCount()
                + " | Served: " + result.getServedCount()
                + " | Left unhappy: " + result.getAbandonedCount());

        System.out.println("Waiting queue:");
        List<Customer> queue = restaurant.getWaitingQueue().getCustomers();
        if (queue.isEmpty()) {
            System.out.println("  (empty)");
        }
        for (Customer c : queue) {
            System.out.println("  " + c + " - satisfaction " + c.getSatisfaction());
        }

        System.out.println("Tables:");
        for (Table t : restaurant.getTables()) {
            Customer c = t.getOccupant();
            if (c == null) {
                System.out.println("  Table " + t.getTableNumber() + ": free");
                continue;
            }
            String line = "  Table " + t.getTableNumber() + ": " + c + " [" + c.getStatus()
                    + ", satisfaction " + c.getSatisfaction() + "]";
            Order o = c.getOrder();
            if (o != null) {
                line += " | Order: " + o.getItem().getName() + " " + money(o.getPrice()) + " - " + o.getStatus();
                if (o.getStatus() == OrderStatus.PREPARING) {
                    line += " (" + o.getPreparationRemaining() + " unit(s) left)";
                }
            }
            System.out.println(line);
        }
    }

    /**
     * Shows the available staff tasks and returns the one the player picks.
     * Invalid input is explained and the player is asked again; nothing is consumed.
     * If the input stream ends (e.g. a script ran out of lines), the Wait option is chosen.
     */
    public Decision promptDecision(List<Decision> options) {
        System.out.println();
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + options.get(i).getDescription());
        }
        while (true) {
            System.out.print("Your choice [1-" + options.size() + "]: ");
            if (!in.hasNextLine()) {
                if (!inputEnded) {
                    inputEnded = true;
                    System.out.println();
                    System.out.println("[Input ended - the manager will choose Wait for all remaining tasks.]");
                }
                return waitOption(options);
            }
            String line = in.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= options.size()) {
                    System.out.println("> " + options.get(choice - 1).getDescription());
                    return options.get(choice - 1);
                }
                displayError("Option " + choice + " does not exist. Pick a number from 1 to " + options.size() + ".");
            } catch (NumberFormatException e) {
                displayError("\"" + line + "\" is not a number. Type the number of an option, e.g. 1.");
            }
        }
    }

    public void displayError(String message) {
        System.out.println("  ! " + message);
    }

    public void displayMessage(String message) {
        System.out.println("  - " + message);
    }

    /** Final screen after the last turn. */
    public void displayResult(GameResult result, GameConfig cfg, int turnsUsed) {
        System.out.println();
        System.out.println("==================== GAME OVER ====================");
        System.out.println("Turns used:                 " + turnsUsed);
        System.out.println("Customers served:           " + result.getServedCount());
        System.out.println("Customers paid:             " + result.getPaidCount()
                + "   (target >= " + cfg.getVictoryPaidCustomers() + ")");
        System.out.println("Unhappy departures:         " + result.getAbandonedCount());
        System.out.println("Revenue:                    " + money(result.getRevenue())
                + "   (target >= " + money(cfg.getVictoryRevenue()) + ")");
        System.out.println("Avg satisfaction (paid):    " + averageText(result)
                + "   (target >= " + (int) cfg.getVictorySatisfaction() + ")");
        System.out.println();
        System.out.println("Outcome: " + result.getOutcome());
    }

    private Decision waitOption(List<Decision> options) {
        for (Decision d : options) {
            if (d.getType() == Decision.Type.WAIT) {
                return d;
            }
        }
        throw new IllegalStateException("Every decision list must contain a Wait option");
    }

    private String averageText(GameResult result) {
        if (result.getPaidCount() == 0) {
            return "N/A";
        }
        return String.format(Locale.US, "%.1f", result.calculateAverageSatisfaction());
    }

    private String money(double amount) {
        return String.format(Locale.US, "$%.2f", amount);
    }
}
