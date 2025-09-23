package card.andrew.dungeoncrawler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import java.util.LinkedList;
import java.util.Queue;

public class Battle {
    private Player player;
    private Monster monster;
    private Dungeon dungeon;
    private Queue<Character> turnQueue;
    private int blinkCounter = 0; // For blink animation
    private boolean blinkActive = false;
    private Character blinkingCharacter;
    private int blinkDamage;
    private float blinkPosX, blinkPosY;
    private String message = ""; // For potion messages
    private int messageCounter = 0; // For message display timing

    public Battle(Player player, Monster monster, Dungeon dungeon) {
        this.player = player;
        this.monster = monster;
        this.dungeon = dungeon;
        turnQueue = new LinkedList<>();
        turnQueue.add(player);
        turnQueue.add(monster);
    }

    public void drawBattleScreen(Canvas canvas, Paint bluePaint, Paint redPaint, Paint greenPaint, Paint blackPaint, Paint textPaint) {
        // Scale canvas to match original 30x30 battle screen
        canvas.save();
        canvas.scale(canvas.getWidth() / 30f, canvas.getHeight() / 30f);

        // Draw player (blue circle)
        canvas.drawCircle(10, 10, 2, bluePaint);

        // Draw monster (red circle)
        if (!blinkActive || (blinkCounter / 5) % 2 == 0) { // Blink every 5 frames
            canvas.drawCircle(20, 20, 2, redPaint);
        }

        // Draw player's health bar
        float playerHealthRatio = (float) player.getHealth() / player.getMaxHealth();
        canvas.drawRect(8, 7.5f, 8 + playerHealthRatio * 10, 8.5f, greenPaint);
        canvas.drawText(player.getHealth() + "/" + player.getMaxHealth(), 13, 8, textPaint);
        canvas.drawRect(8, 7.5f, 18, 8.5f, blackPaint);

        // Draw monster's health bar
        float monsterHealthRatio = (float) monster.getHealth() / monster.getMaxHealth();
        canvas.drawRect(12, 21.5f, 12 + monsterHealthRatio * 10, 22.5f, greenPaint);
        canvas.drawText(monster.getHealth() + "/" + monster.getMaxHealth(), 17, 22, textPaint);
        canvas.drawRect(12, 21.5f, 22, 22.5f, blackPaint);

        // Draw buttons
        canvas.drawRect(13, 4, 17, 6, blackPaint);
        canvas.drawText("FLEE", 15, 5, textPaint);
        canvas.drawRect(8, 3.5f, 12, 5.5f, blackPaint);
        canvas.drawText("USE POTION", 10, 4, textPaint);
        canvas.drawText("Potions: " + player.getPotions(), 10, 5, textPaint);
        canvas.drawRect(13, 2, 17, 4, blackPaint);
        canvas.drawText("ATTACK", 15, 3, textPaint);

        // Draw damage or message
        if (blinkActive) {
            displayDamage(canvas, blinkDamage, blinkPosX, blinkPosY, textPaint);
        }
        if (!message.isEmpty()) {
            textPaint.setColor(0xFFFF0000); // Red
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(message, 15, 15, textPaint);
            textPaint.setColor(0xFF000000); // Reset to black
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        }

        canvas.restore();
    }

    public boolean isBlinking() {
        return blinkActive;
    }

    public void update() {
        if (blinkActive) {
            blinkCounter++;
            if (blinkCounter >= 25) { // 5 blinks * 5 frames
                blinkActive = false;
                blinkCounter = 0;
            }
        }
        if (messageCounter > 0) {
            messageCounter--;
            if (messageCounter == 0) {
                message = "";
            }
        }
    }

    public void playerAttack() {
        for (int i = 0; i < turnQueue.size(); i++) {
            Character currentCharacter = turnQueue.poll();
            if (currentCharacter instanceof Player) {
                int damage = currentCharacter.attack();
                monster.setHealth(monster.getHealth() - damage);
                startBlink(monster, damage, 20, 20);
                if (monster.getHealth() <= 0) {
                    player.addXP(monster.getXp());
                    dungeon.updateSeenRooms(player);
                    GameView.setBattleInProgress(false);
                    return;
                }
            } else if (currentCharacter instanceof Monster) {
                int monsterDamage = currentCharacter.attack();
                player.setHealth(player.getHealth() - monsterDamage);
                startBlink(player, monsterDamage, 10, 10);
                if (player.getHealth() <= 0) {
                    GameView.setBattleInProgress(false);
                    return;
                }
            }
            turnQueue.add(currentCharacter);
        }
    }

    public void playerFlee() {
        int newHealth = (player.getHealth() >= 10) ? (int) (player.getHealth() - player.getHealth() / 10) : player.getHealth() - 1;
        player.setHealth(newHealth);
        GameView.setBattleInProgress(false);
    }

    public void playerUsePotion() {
        if (player.getPotions() > 0) {
            int healAmount = player.usePotion();
            message = "You healed +" + healAmount;
            messageCounter = 37; // ~0.75s at 50 FPS (20ms per frame)
            int monsterDamage = monster.attack();
            startBlink(player, monsterDamage, 10, 10);
            if (player.getHealth() <= 0) {
                GameView.setBattleInProgress(false);
            }
        } else {
            message = "You are out of potions!";
            messageCounter = 37;
        }
    }

    private void startBlink(Character character, int damage, float posX, float posY) {
        blinkActive = true;
        blinkCounter = 0;
        blinkingCharacter = character;
        blinkDamage = damage;
        blinkPosX = posX;
        blinkPosY = posY;
    }

    private void displayDamage(Canvas canvas, int damage, float posX, float posY, Paint textPaint) {
        textPaint.setTextSize(60); // Larger for damage
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("-" + damage, posX, posY + 1, textPaint);
        textPaint.setTextSize(30); // Reset
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
    }
}
