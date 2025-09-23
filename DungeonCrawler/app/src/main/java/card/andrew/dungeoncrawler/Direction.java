package card.andrew.dungeoncrawler;

/**
 * This enum represents the four cardinal directions.
 *
 * @author AnDrew Card
 */
public enum Direction {
    NORTH(0, 1), EAST(1, 0), SOUTH(0, -1), WEST(-1, 0);

    final int dx, dy;

    /**
     * Constructor for the Direction enum.
     * @param dx The change in x-coordinate for this direction.
     * @param dy The change in y-coordinate for this direction.
     */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * This method returns the opposite direction.
     * @return The opposite direction.
     */
    Direction opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            default: throw new IllegalStateException("Unknown direction");
        }
    }

    /**
     * This method returns the direction corresponding to a given change in x and y coordinates.
     * @param dx The change in x-coordinate.
     * @param dy The change in y-coordinate.
     * @return The direction corresponding to the given change in coordinates.
     */
    static Direction fromDxDy(int dx, int dy) {
        for (Direction dir : Direction.values()) {
            if (dir.dx == dx && dir.dy == dy) {
                return dir;
            }
        }
        throw new IllegalArgumentException("Invalid direction dx: " + dx + ", dy: " + dy);
    }
}
