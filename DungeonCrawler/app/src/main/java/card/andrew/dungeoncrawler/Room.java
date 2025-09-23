package card.andrew.dungeoncrawler;

import android.graphics.Canvas;
import android.graphics.Paint;


/**
 * This class represents a Room in the dungeon crawler game.
 *
 * @author AnDrew Card
 */
public class Room {
    protected int x, y;
    protected boolean seen = false;
    protected boolean visited = false;
    protected boolean[] walls = {true, true, true, true}; // Top, Right, Bottom, Left
    protected boolean discovered = false;

    /**
     * Constructor for the Room class.
     * @param x The x-coordinate of the room.
     * @param y The y-coordinate of the room.
     */
    Room(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This method removes a wall in a given direction.
     * @param dir The direction in which to remove the wall.
     */
    void removeWall(Direction dir) {
        walls[dir.ordinal()] = false;
    }

    /**
     * This method draws the room.
     */
    public void draw(Canvas canvas, Paint lightGrayPaint, Paint darkGrayPaint, Paint blackPaint) {
        if (seen) {
            canvas.drawRect(x, y, x + 1, y + 1, lightGrayPaint); // Filled square
            blackPaint.setStrokeWidth(0.02f);
            if (walls[0]) { // Top
                canvas.drawLine(x, y + 1, x + 1, y + 1, blackPaint);
            }
            if (walls[1]) { // Right
                canvas.drawLine(x + 1, y, x + 1, y + 1, blackPaint);
            }
            if (walls[2]) { // Bottom
                canvas.drawLine(x, y, x + 1, y, blackPaint);
            }
            if (walls[3]) { // Left
                canvas.drawLine(x, y, x, y + 1, blackPaint);
            }
        } else if (discovered) {
            canvas.drawRect(x, y, x + 1, y + 1, darkGrayPaint);
        }
    }
}

