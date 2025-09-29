package card.andrew.dungeoncrawler;

import android.os.Bundle;

import java.util.Random;

public class Player extends Character {
    private int xpNeeded, potions;
    private static final Random random = new Random(); // Consistent with Character

    public Player(int dungeonWidth, int dungeonHeight) {
        super(dungeonWidth, dungeonHeight, 1);
        statCalculations();
        this.xp = 0;
        this.potions = 4;
        // Override paint to ensure blue color (optional, as Character already sets blue)
        characterPaint.setColor(0xFF0000FF); // Comment out for testing
    }

    @Override
    protected void statCalculations() {
        this.health = level * 25;
        this.maxHealth = level * 25;
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

    public void saveState(Bundle outState) {
        outState.putInt("x", x);
        outState.putInt("y", y);
        outState.putInt("health", health);
        outState.putInt("maxHealth", maxHealth);
        outState.putInt("minAttack", minAttack);
        outState.putInt("maxAttack", maxAttack);
        outState.putInt("level", level);
        outState.putInt("xp", xp);
        outState.putInt("xpNeeded", xpNeeded);
        outState.putInt("potions", potions);
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
            xp = savedState.getInt("xp");
            xpNeeded = savedState.getInt("xpNeeded");
            potions = savedState.getInt("potions");
        }
    }
}
