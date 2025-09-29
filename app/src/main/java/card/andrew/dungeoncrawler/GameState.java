package card.andrew.dungeoncrawler;

public class GameState {
    private static final GameState instance = new GameState();
    private Player player;
    private Monster currentMonster;

    private GameState() {}

    public static GameState getInstance() {
        return instance;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCurrentMonster(Monster monster) {
        this.currentMonster = monster;
    }

    public Monster getCurrentMonster() {
        return currentMonster;
    }
}
