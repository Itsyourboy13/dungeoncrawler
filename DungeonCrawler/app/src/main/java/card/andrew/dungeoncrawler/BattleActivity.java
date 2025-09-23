package card.andrew.dungeoncrawler;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BattleActivity extends AppCompatActivity {
    private Player player;
    private Monster monster;
    private TextView playerHealthText, monsterHealthText, healedAmountText,
            playerDamageText, enemyDamageText;
    private ProgressBar playerHealthBar, monsterHealthBar;
    private Button attackButton, potionButton, fleeButton;
    private View playerCircle, enemyCircle;
    private final Handler handler = new Handler();
    private static final int BLINK_COUNT = 3;
    private static final int BLINK_DURATION = 200;
    private static final int HEAL_FLASH_DURATION = 1000;
    private static final int ATTACK_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_battle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        player = GameState.getInstance().getPlayer();
        monster = GameState.getInstance().getCurrentMonster();
        if (player == null || monster == null) {
            finish();
            return;
        }

        // UI setup
        playerHealthText = findViewById(R.id.player_hp_text);
        monsterHealthText = findViewById(R.id.enemy_hp_text);
        healedAmountText = findViewById(R.id.healed_amount);
        playerDamageText = findViewById(R.id.player_damage_text);
        enemyDamageText = findViewById(R.id.enemy_damage_text);
        playerHealthBar = findViewById(R.id.player_health_bar);
        monsterHealthBar = findViewById(R.id.enemy_health_bar);
        playerCircle = findViewById(R.id.player_circle);
        enemyCircle = findViewById(R.id.enemy_circle);

        // Set initial colors
        playerCircle.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
        enemyCircle.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        updateHealthDisplay();

        // Button listeners
        attackButton = findViewById(R.id.attack_button);
        potionButton = findViewById(R.id.potions_button);
        fleeButton = findViewById(R.id.flee_button);

        String potionCountString = "Use potions: (" + player.getPotions() + ")";
        potionButton.setText(potionCountString);

        attackButton.setOnClickListener(this::onAttack);
        potionButton.setOnClickListener(this::onPotion);
        fleeButton.setOnClickListener(this::onFlee);
    }

    private void updateHealthDisplay() {
        int playerHealth = player.getHealth();
        int monsterHealth = monster.getHealth();
        int playerMaxHealth = player.getMaxHealth();
        int monsterMaxHealth = monster.getMaxHealth();
        String playerHealthString = "HP: " + player.getHealth() + "/" + player.getMaxHealth();
        String monsterHealthString = "HP: " + monster.getHealth() + "/" + monster.getMaxHealth();
        playerHealthText.setText(playerHealthString);
        monsterHealthText.setText(monsterHealthString);
        playerHealthBar.setMax(playerMaxHealth);
        playerHealthBar.setProgress(playerHealth);
        monsterHealthBar.setMax(monsterMaxHealth);
        monsterHealthBar.setProgress(monsterHealth);
    }

    private void onAttack(View v) {
        int buttonDelay = ATTACK_DELAY;
        attackButton.setEnabled(false);
        potionButton.setEnabled(false);
        fleeButton.setEnabled(false);
        int playerDamage = player.attack();
        blinkAndShowDamage(enemyCircle, monster, playerDamage, Color.RED);
        if (monster.getHealth() > 0) {
            handler.postDelayed(() -> {
                int monsterDamage = monster.attack();
                blinkAndShowDamage(playerCircle, player, monsterDamage, Color.BLUE);
                updateHealthDisplay();
            }, ATTACK_DELAY);
            buttonDelay += 1400;
        }
        updateHealthDisplay();

        handler.postDelayed(() -> {
            attackButton.setEnabled(true);
            potionButton.setEnabled(true);
            fleeButton.setEnabled(true);
            checkBattleEnd();
        }, buttonDelay);
    }

    private void onPotion(View v) {
        attackButton.setEnabled(false);
        potionButton.setEnabled(false);
        fleeButton.setEnabled(false);
        int healAmount = player.usePotion();
        if (healAmount > 0) {
            String healAmountText = "HP: +" + healAmount;
            healedAmountText.setText(healAmountText);
            healedAmountText.setVisibility(View.VISIBLE);
            String potionCountString = "Use potions: (" + player.getPotions() + ")";
            potionButton.setText(potionCountString);
            flashGreen(playerCircle);
            updateHealthDisplay();
            handler.postDelayed(() -> healedAmountText.setVisibility(View.GONE), 2000); // Hide after 2s
            handler.postDelayed(() -> {
                int monsterDamage = monster.attack();
                blinkAndShowDamage(playerCircle, player, monsterDamage, Color.BLUE);
                updateHealthDisplay();
            }, ATTACK_DELAY);
        } else {
            Toast.makeText(this, "No potions left!", Toast.LENGTH_SHORT).show();
        }
        handler.postDelayed(() -> {
            attackButton.setEnabled(true);
            potionButton.setEnabled(true);
            fleeButton.setEnabled(true);
            checkBattleEnd();
        }, ATTACK_DELAY + 1400);
    }

    private void onFlee(View v) {
        attackButton.setEnabled(false);
        potionButton.setEnabled(false);
        fleeButton.setEnabled(false);
        int monsterDamage = monster.attack();
        blinkAndShowDamage(playerCircle, player, monsterDamage, Color.BLUE);
        handler.postDelayed(() -> {
            setResult(RESULT_CANCELED);
            finish();
        }, ATTACK_DELAY);
    }

    private void checkBattleEnd() {
        if (monster.getHealth() <= 0) {
            int gainedXp = monster.getXp();
            player.addXP(gainedXp);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("enemyDefeated", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else if (player.getHealth() <= 0) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void blinkAndShowDamage(View circle, Character target, int damage, int originalColor) {
        int health = target.getHealth();
        String damageMessage = "-" + damage;
        target.setHealth(health - damage);
        TextView damageText = (circle == playerCircle) ? playerDamageText : enemyDamageText; // Reuse or add new TextView
        damageText.setText(damageMessage);
        damageText.setVisibility(View.VISIBLE);
        final int[] blinkCount = {0};
        final int[] color = {Color.BLACK}; // Start with black
        handler.post(new Runnable() {
            @Override
            public void run() {
                circle.setBackgroundTintList(ColorStateList.valueOf(color[0]));
                color[0] = (color[0] == Color.BLACK) ? originalColor : Color.BLACK;
                if (++blinkCount[0] < BLINK_COUNT * 2) { // *2 for on/off cycle
                    handler.postDelayed(this, BLINK_DURATION);
                } else {
                    circle.setBackgroundTintList(ColorStateList.valueOf(originalColor));
                    damageText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void flashGreen(View circle) {
        circle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
        handler.postDelayed(() -> circle.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE)), HEAL_FLASH_DURATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Clean up handler
    }
}