package card.andrew.dungeoncrawler;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.Random;

public class Character {

    protected int health, maxHealth;
    protected int x, y;
    protected int minAttack, maxAttack, level, xp;
    private static final Random random = new Random();
    protected Paint characterPaint; // Paint for drawing

    public Character(int dungeonWidth, int dungeonHeight, int level) {
        this.level = level;
        statCalculations();
        // Generate random starting coordinates
        this.x = random.nextInt(dungeonWidth);
        this.y = random.nextInt(dungeonHeight);
        // Initialize paint (blue for Character, subclasses can override)
        characterPaint = new Paint();
        characterPaint.setColor(0xFF0000FF); // Blue (ARGB)
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x + 0.5f, y + 0.5f, 0.25f, characterPaint);
    }

    public void move(int directionX, int directionY, Dungeon dungeon) {
        int newX = x + directionX;
        int newY = y + directionY;
        if (newX >= 0 && newX < dungeon.getWidth() && newY >= 0 && newY < dungeon.getHeight() && !dungeon.hasWall(x, y, directionX, directionY)) {
            x = newX;
            y = newY;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getXp() {
        return xp;
    }

    public int attack() {
        return random.nextInt(maxAttack - minAttack + 1) + minAttack;
    }

    protected void statCalculations() {
        this.health = this.level * 10;
        this.maxHealth = this.level * 10;
        this.minAttack = this.level * 2;
        this.maxAttack = this.level * 3;
        this.xp = (int) Math.pow(2, this.level - 1) * 5;
    }

    public int getLevel() {
        return level;
    }
}
