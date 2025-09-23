package card.andrew.dungeoncrawler;

import android.graphics.Canvas;

import java.util.Iterator;
import java.util.Random;

import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Graph;

public class Monster extends Character {
    private Player target;
    private Dungeon dungeon;
    private int moveCounter = 0;
    private static final Random random = new Random();

    public Monster(int dungeonWidth, int dungeonHeight, Player target, Dungeon dungeon, int level) {
        super(dungeonWidth, dungeonHeight, level);
        this.target = target;
        this.dungeon = dungeon;
        // Set red color for monster
        characterPaint.setColor(0xFFFF0000); // Red
        // Generate random starting coordinates
        do {
            this.x = random.nextInt(dungeonWidth);
            this.y = random.nextInt(dungeonHeight);
        } while (isInViewRange(target, x, y, dungeon));
    }

    private boolean isInViewRange(Character player, int x, int y, Dungeon dungeon) {
        int playerIndex = player.getY() * dungeon.getWidth() + player.getX();
        int monsterIndex = y * dungeon.getWidth() + x;
        BreadthFirstPaths bfs = new BreadthFirstPaths(dungeon.getRoomGraph(), playerIndex);
        if (bfs.hasPathTo(monsterIndex)) {
            Iterable<Integer> path = bfs.pathTo(monsterIndex);
            int distance = 0;
            for (int node : path) {
                distance++;
            }
            int viewRange = 4;
            return distance <= viewRange;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        Room room = dungeon.getRoom(x, y);
        if (room.seen) {
            canvas.drawCircle(x + 0.5f, y + 0.5f, 0.25f, characterPaint);
        }
    }

    /**
     * This method moves the monster towards the target character if possible.
     * The movement is based on a breadth-first search (BFS) path to the player's position in the room graph of the dungeon.
     *
     */
    public void move() {
        if (moveCounter % 5 == 0) {
            Graph roomGraph = dungeon.getRoomGraph();
            int playerIndex = target.getY() * dungeon.getWidth() + target.getX();
            int monsterIndex = y * dungeon.getWidth() + x;
            BreadthFirstPaths bfs = new BreadthFirstPaths(roomGraph, monsterIndex);
            if (bfs.hasPathTo(playerIndex)) {
                Iterable<Integer> path = bfs.pathTo(playerIndex);
                int distance = 0;
                for (int node : path) {
                    distance++;
                }
                if (distance <= 10) {
                    Iterator<Integer> iterator = path.iterator();
                    iterator.next(); // Skip current room
                    if (iterator.hasNext()) {
                        int nextRoomIndex = iterator.next();
                        x = nextRoomIndex % dungeon.getWidth();
                        y = nextRoomIndex / dungeon.getWidth();
                    }
                }
            }
        }
        moveCounter++;
    }

    public void startBattle() {
        // Simply set battle flag; GameView handles rendering and input
        GameView.setBattleInProgress(true);
    }
}
