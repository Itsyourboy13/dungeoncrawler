package card.andrew.dungeoncrawler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class PlayerTest {
    private Player player;
    private Dungeon mockDungeon;

    @Before
    public void setUp() {
        mockDungeon = mock(Dungeon.class);
        when(mockDungeon.getWidth()).thenReturn(15);
        when(mockDungeon.getHeight()).thenReturn(15);
    }

    @Test
    public void testMovementValidation() {
        // TC01: Valid move
        player = new Player(mockDungeon.getWidth(), mockDungeon.getHeight());
        int currentX = player.getX();
        player.move(1, 0, mockDungeon); // Assume no wall
        assertEquals(1 + currentX, player.getX()); // Position updated

        // TC01: Invalid move (wall or out of bounds)
        currentX = player.getX();
        when(mockDungeon.hasWall(player.getX(), player.getY(), 1, 0)).thenReturn(true);
        player.move(1, 0, mockDungeon);
        assertEquals(currentX, player.getX()); // No change
    }

    @Test
    public void testAttackDamageRange() {
        // TC02: Attack between min and max
        player = new Player(mockDungeon.getWidth(), mockDungeon.getHeight());
        int damage1 = player.attack();
        int damage2 = player.attack();
        assertTrue(damage1 >= player.minAttack && damage1 <= player.maxAttack);
        assertTrue(damage2 >= player.minAttack && damage2 <= player.maxAttack);
    }

    @Test
    public void testPotionUseAndHeal() {
        // TC03: Heal 50-75% with potions
        player = new Player(mockDungeon.getWidth(), mockDungeon.getHeight());
        player.statCalculations();
        player.setHealth(10);
        int healAmount = player.usePotion();
        assertTrue(healAmount >= 12 && healAmount <= 18);
        assertEquals(3, player.getPotions()); // Decremented
    }

    @Test
    public void testXPGainAndLevelUp() {
        // TC04: Add XP and level up // xpNeeded = 25
        player = new Player(mockDungeon.getWidth(), mockDungeon.getHeight());
        player.statCalculations();
        player.addXP(30);
        assertEquals(2, player.getLevel()); // Leveled up
        assertEquals(50, player.getMaxHealth()); // Updated stats
        assertEquals(5, player.getXp()); // Overflow
    }

    @Test
    public void testStatCalculationsOnLevelUp() {
        // TC05: Verify stats after level up
        player = new Player(mockDungeon.getWidth(), mockDungeon.getHeight());
        player.statCalculations();
        player.addXP(25);
        player.statCalculations();
        assertEquals(50, player.getMaxHealth());
        int damage = player.attack();
        assertTrue(damage >= 4 && damage <= 8);
        assertEquals(50, player.getXpNeeded());
    }
}