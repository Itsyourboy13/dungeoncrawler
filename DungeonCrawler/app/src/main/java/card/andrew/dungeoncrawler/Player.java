package card.andrew.dungeoncrawler;

import java.util.Random;

public class Player extends Character {
    private int xpNeeded, potions;
    private static int level = 1;
    private static final Random random = new Random(); // Consistent with Character

    public Player(int dungeonWidth, int dungeonHeight) {
        super(dungeonWidth, dungeonHeight, level);
        this.xp = 0;
        this.potions = 3;
        // Override paint to ensure blue color (optional, as Character already sets blue)
        characterPaint.setColor(0xFF0000FF); // Blue
    }

    @Override
    protected void statCalculations() {
        this.health = level * 20;
        this.maxHealth = level * 20;
        this.minAttack = level * 2;
        this.maxAttack = level * 4;
        this.xpNeeded = (int) Math.pow(2, level - 1) * 25;
    }

    public int getXpNeeded() {
        return xpNeeded;
    }

    public void addXP(int xp) {
        this.xp += xp;
        if (this.xp >= xpNeeded) {
            this.xp -= xpNeeded;
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        potions++;
        statCalculations();
    }

    public int getPotions() {
        return potions;
    }

    public int usePotion() {
        double healRatio = 0.5 + random.nextDouble() * 0.25;
        int healAmount = (int) (maxHealth * healRatio);
        if (potions > 0) {
            health = Math.min(maxHealth, health + healAmount);
            potions--;
        } else {
            healAmount = 0;
        }
        return healAmount;
    }
}
