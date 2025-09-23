package card.andrew.dungeoncrawler;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private ProgressBar hpProgressBar;
    private ProgressBar xpProgressBar;
    private TextView hpText;
    private TextView xpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.gameView);
        hpProgressBar = findViewById(R.id.hpProgressBar);
        xpProgressBar = findViewById(R.id.xpProgressBar);
        hpText = findViewById(R.id.hpText);
        xpText = findViewById(R.id.xpText);

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

    public void updateProgressBars() {
        if (gameView != null) {
            int playerHealth = gameView.getPlayerHealth();
            int playerMaxHealth = gameView.getPlayerMaxHealth();
            int playerXp = gameView.getPlayerXp();
            int playerXpNeeded = gameView.getPlayerXpNeeded();
            String hpMessage = "Health: " + playerHealth + "/" + playerMaxHealth;
            String xpMessage = "XP: " + playerXp + "/" + playerXpNeeded;
            hpText.setText(hpMessage);
            xpText.setText(xpMessage);
            hpProgressBar.setMax(playerMaxHealth);
            hpProgressBar.setProgress(playerHealth);
            xpProgressBar.setMax(playerXpNeeded);
            xpProgressBar.setProgress(playerXp);
        }
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
}