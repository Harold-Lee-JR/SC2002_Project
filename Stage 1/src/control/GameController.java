package control;

import boundary.UserInterface;
import entity.GameConfig;
import entity.GameResult;
import entity.Restaurant;

/**
 * Manages the 18-turn game loop; the TurnController manages what happens inside each turn.
 * The game always runs every turn (no early ending), then the result is evaluated.
 */
public class GameController {
    private final GameConfig config;
    private final UserInterface ui;

    private GameResult result;
    private TurnController turnController;
    private int currentTurn;

    public GameController(GameConfig config, UserInterface ui) {
        this.config = config;
        this.ui = ui;
    }

    /** Builds a fresh restaurant, score sheet and turn controller. */
    public void startGame() {
        result = new GameResult();
        Restaurant restaurant = new Restaurant(config.getTableCount());
        turnController = new TurnController(config, restaurant, result, ui);
        currentTurn = 0;
        ui.displayMessage("Welcome to Restaurant Rush! You are the manager. Play " + config.getMaxTurns()
                + " turns, then see whether you win.");
    }

    /** Plays every turn, then evaluates and shows the result. */
    public void runGame() {
        if (turnController == null) {
            throw new IllegalStateException("Call startGame() before runGame()");
        }
        for (currentTurn = 1; currentTurn <= config.getMaxTurns(); currentTurn++) {
            turnController.executeTurn(currentTurn);
        }
        result.evaluate(config);
        ui.displayResult(result, config, config.getMaxTurns());
    }

    public GameResult getResult() {
        return result;
    }
}
