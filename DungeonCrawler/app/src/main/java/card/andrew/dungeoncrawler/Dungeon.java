package card.andrew.dungeoncrawler;

import android.graphics.Canvas;
import android.graphics.Paint;

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.StdRandom;

/**
 * This class represents a Dungeon which is a 2D grid of rooms.
 *
 * @author AnDrew Card
 */
public class Dungeon {
    private final int width, height;
    private final Room[][] rooms;
    private Graph roomGraph;

    /**
     * Constructor for the Dungeon class.
     * @param width The width of the dungeon.
     * @param height The height of the dungeon.
     */
    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        rooms = new Room[width][height];
        generateMaze();
    }

    /**
     * Getter for the width of the dungeon.
     * @return The width of the dungeon.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter for the height of the dungeon.
     * @return The height of the dungeon.
     */
    public int getHeight() {
        return height;
    }

    /**
     * This method generates a maze in the dungeon.
     */
    private void generateMaze() {
        // Initialize rooms
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                rooms[x][y] = new Room(x, y);
            }
        }

        // Start DFS from the top-left room
        dfs(rooms[0][0]);
        createRoomGraph();
    }

    /**
     * This method creates a graph of rooms in the dungeon.
     */
    private void createRoomGraph() {
        // Initialize the graph with one vertex for each room
        roomGraph = new Graph(width * height);

        // Iterate over each room
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Room room = rooms[x][y];

                // For each direction, check if there is no wall
                for (Direction dir : Direction.values()) {
                    Room nextRoom = getRoomInDirection(room, dir);
                    if (nextRoom != null && !room.walls[dir.ordinal()]) {
                        // If there is no wall, add an edge in the graph
                        int roomIndex = y * width + x;
                        int nextRoomIndex = nextRoom.y * width + nextRoom.x;
                        roomGraph.addEdge(roomIndex, nextRoomIndex);
                    }
                }
            }
        }
    }

    /**
     * Getter for the graph of rooms in the dungeon.
     * @return The graph of rooms in the dungeon.
     */
    public Graph getRoomGraph() {
        return roomGraph;
    }

    /**
     * This method performs a depth-first search (DFS) from a given room. This helps make sure all the rooms are
     * arranged randomly while making sure every room can be explored.
     * @param room The room from which the DFS starts.
     */
    private void dfs(Room room) {
        room.visited = true;

        // Directions in which the DFS can step
        Direction[] directions = Direction.values();
        StdRandom.shuffle(directions); // Shuffle to create a more varied maze

        for (Direction dir : directions) {
            Room nextRoom = getRoomInDirection(room, dir);

            if (nextRoom != null && !nextRoom.visited) {
                // Remove walls between rooms
                room.removeWall(dir);
                nextRoom.removeWall(dir.opposite());

                // Recursive DFS call
                dfs(nextRoom);
            }
        }
    }

    /**
     * This method returns the room in a given direction from a given room.
     * @param room The room from which the direction is considered.
     * @param dir The direction in which to look for the room.
     * @return The room in the given direction, or null if there is no room in that direction.
     */
    public Room getRoomInDirection(Room room, Direction dir) {
        int newX = room.x + dir.dx;
        int newY = room.y + dir.dy;

        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
            return rooms[newX][newY];
        }
        return null;
    }

    /**
     * This method checks if there is a wall in a given direction from a given room.
     * @param x The x-coordinate of the room.
     * @param y The y-coordinate of the room.
     * @param dx The change in x-coordinate (direction).
     * @param dy The change in y-coordinate (direction).
     * @return True if there is a wall in the given direction, false otherwise.
     */
    public boolean hasWall(int x, int y, int dx, int dy) {
        Room room = rooms[x][y];
        Direction dir = Direction.fromDxDy(dx, dy);
        return room.walls[dir.ordinal()];
    }

    /**
     * This method draws the dungeon by drawing each room.
     */
    public void draw(Canvas canvas, Paint lightGrayPaint, Paint darkGrayPaint, Paint blackPaint) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rooms[i][j].draw(canvas, lightGrayPaint, darkGrayPaint, blackPaint);
            }
        }
    }

    /**
     * This method updates the seen rooms based on the player's current position.
     * @param player The player whose position is used to update the seen rooms.
     */
    public void updateSeenRooms(Character player) {
        int playerX = player.getX();
        int playerY = player.getY();

        rooms[playerX][playerY].seen = true;
        rooms[playerX][playerY].discovered = true;

        // Update the seen status for rooms in each direction up to 4 spaces
        for (Direction dir : Direction.values()) {
            int distance = 0;
            int currentX = playerX;
            int currentY = playerY;

            while (distance < 4) {
                Room currentRoom = rooms[currentX][currentY];
                // If there's a wall in the direction, stop updating seen rooms
                if (currentRoom.walls[dir.ordinal()]) {
                    break;
                }

                // Move to the next room in the direction
                currentX += dir.dx;
                currentY += dir.dy;

                // If the next room is out of bounds, stop updating seen rooms
                if (currentX < 0 || currentX >= width || currentY < 0 || currentY >= height) {
                    break;
                }

                // Mark the next room as seen
                rooms[currentX][currentY].seen = true;
                rooms[currentX][currentY].discovered = true;
                distance++;
            }
        }

        // Reset the seen status for all rooms that are not currently seen
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!rooms[i][j].seen && rooms[i][j].discovered) {
                    rooms[i][j].seen = false;
                }
            }
        }
    }

    /**
     * This method returns the room at a given position.
     * @param x The x-coordinate of the room.
     * @param y The y-coordinate of the room.
     * @return The room at the given position.
     */
    public Room getRoom(int x, int y) {
        return rooms[x][y];
    }

}
