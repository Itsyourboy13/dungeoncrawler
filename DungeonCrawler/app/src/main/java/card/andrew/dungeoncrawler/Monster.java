package card.andrew.dungeoncrawler;

import android.graphics.Canvas;
import android.os.Bundle;

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
        characterPaint.setColor(0xFFFF0000); // Comment out for testing
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
        if (moveCounter % 2 == 0) {
            Graph roomGraph = dungeon.getRoomGraph();
            int playerIndex = target.getY() * dungeon.getWidth() + target.getX();
            int monsterIndex = y * dungeon.getWidth() + x;
            BreadthFirstPaths bfs = new BreadthFirstPaths(roomGraph, monsterIndex);
            if (bfs.hasPathTo(playerIndex)) {
                Iterable<Integer> path = bfs.pathTo(playerIndex);
                int distance = 0;
                for (int ignored : path) {
                    distance++;
                }
                if (distance <= 10 || (moveCounter % 8 == 0 && distance >= 15)) {
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

    public void saveState(Bundle outState) {
        outState.putInt("x", x);
        outState.putInt("y", y);
        outState.putInt("health", health);
        outState.putInt("maxHealth", maxHealth);
        outState.putInt("minAttack", minAttack);
        outState.putInt("maxAttack", maxAttack);
        outState.putInt("level", level);
        outState.putInt("moveCounter", moveCounter);
    }

    public void restoreState(Bundle savedState) {
        if (savedState != null) {
            x = savedState.getInt("x");
            y = savedState.getInt("y");
            health = savedState.getInt("health");
            maxHealth = savedState.getInt("maxHealth");
            minAttack = savedState.getInt("minAttack");
            maxAttack = savedState.getInt("maxAttack");
            level = savedState.getInt("level");
            moveCounter = savedState.getInt("moveCounter");
        }
    }
}
