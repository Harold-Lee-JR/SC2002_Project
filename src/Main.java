import boundary.UserInterface;
import control.GameController;
import entity.GameConfig;

/** Entry point of Restaurant Rush (Stage 1). */
public class Main {
    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        GameController game = new GameController(new GameConfig(), ui);
        game.startGame();
        game.runGame();
    }
}
