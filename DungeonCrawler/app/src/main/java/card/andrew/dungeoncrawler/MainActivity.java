package card.andrew.dungeoncrawler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import card.andrew.dungeoncrawler.data.LeaderboardRepository;
import card.andrew.dungeoncrawler.data.model.LeaderboardEntry;

public class MainActivity extends AppCompatActivity
        implements GameOverFragment.GameOverListener {
    private GameView gameView;
    private ProgressBar hpProgressBar;
    private ProgressBar xpProgressBar;
    private TextView hpText;
    private TextView xpText;
    private ActivityResultLauncher<Intent> battleResult;
    private final LeaderboardRepository mRepository = LeaderboardRepository.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.gameView);
        hpProgressBar = findViewById(R.id.hpProgressBar);
        xpProgressBar = findViewById(R.id.xpProgressBar);
        hpText = findViewById(R.id.hpText);
        xpText = findViewById(R.id.xpText);

        // Initialize battle result launcher
        battleResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            boolean enemyDefeated = data.getBooleanExtra("enemyDefeated", false);
                            gameView.onBattleResult(enemyDefeated);
                            updateProgressBars();
                            int monstersLeft = gameView.getMonstersLeft();
                            Toast.makeText(this, enemyDefeated ? "Enemy defeated! " + monstersLeft + " monsters left" : "Battle ended", Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        gameView.onBattleResult(false);
                        updateProgressBars();
                        if (gameView.getPlayerHealth() > 0) {
                            Toast.makeText(this, "Battle fled", Toast.LENGTH_SHORT).show();
                        } else {
                            GameOverFragment gameOverFragment = new GameOverFragment();
                            gameOverFragment.show(getSupportFragmentManager(), "game_over");
                        }
                    }
                }
        );

        if (savedInstanceState != null) {
            gameView.restoreState(savedInstanceState.getBundle("gameState"));
        }

        // Initial update of progress bars
        gameView.post(this::updateProgressBars);

        // Set up directional button listeners
        Button buttonUp = findViewById(R.id.buttonUp);
        Button buttonDown = findViewById(R.id.buttonDown);
        Button buttonLeft = findViewById(R.id.buttonLeft);
        Button buttonRight = findViewById(R.id.buttonRight);

        buttonUp.setOnClickListener(v -> gameView.movePlayer(0, -1)); // NORTH
        buttonDown.setOnClickListener(v -> gameView.movePlayer(0, 1)); // SOUTH
        buttonLeft.setOnClickListener(v -> gameView.movePlayer(-1, 0)); // WEST
        buttonRight.setOnClickListener(v -> gameView.movePlayer(1, 0));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle gameState = new Bundle();
        gameView.saveState(gameState);
        outState.putBundle("gameState", gameState);
    }

    public void updateProgressBars() {
        if (gameView != null) {
            int playerHealth = gameView.getPlayerHealth();
            int playerMaxHealth = gameView.getPlayerMaxHealth();
            int playerXp = gameView.getPlayerXp();
            int playerXpNeeded = gameView.getPlayerXpNeeded();
            String hpMessage = "Health: " + playerHealth + "/" + playerMaxHealth;
            String xpMessage = "XP: " + playerXp + "/" + playerXpNeeded;
            Log.d("MainActivity", "HP: " + playerHealth + ", XP: " + playerXp);
            hpText.setText(hpMessage);
            xpText.setText(xpMessage);
            hpProgressBar.setMax(playerMaxHealth);
            hpProgressBar.setProgress(playerHealth);
            xpProgressBar.setMax(playerXpNeeded);
            xpProgressBar.setProgress(playerXp);
        }
    }

    public void startBattle(Intent intent) {
        battleResult.launch(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public void onLeaderboardEntryEntered(String name) {
        LeaderboardEntry entry = new LeaderboardEntry(name, gameView.getCurrentLevel(), gameView.getMonstersKilled(), true);
        mRepository.insertEntry(entry);
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Leaderboard entry added", Toast.LENGTH_SHORT).show();
    }
}