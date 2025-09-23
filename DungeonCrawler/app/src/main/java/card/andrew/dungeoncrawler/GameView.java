package card.andrew.dungeoncrawler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private Thread gameThread;
    private SurfaceHolder holder;
    private volatile boolean running = false;
    private Dungeon dungeon;
    private Player player;
    private List<Monster> monsters = new ArrayList<>();
    private static final int MONSTER_AMOUNT = 4;
    private static boolean battleInProgress = false;
    private Monster engagingMonster = null;
    private boolean levelFinished = false;
    private int timesPlayed = 1;
    private Battle battle = null;
    private String gameMessage = "";
    private int messageCounter = 0;
    private boolean playerMoved = false; // Track player movement
    private GestureDetector gestureDetector;

    // Camera for centering and scrolling
    private float cameraX = 0, cameraY = 0;
    private float tileSize = 1.0f;

    // Paints
    private Paint lightGrayPaint = new Paint();
    private Paint darkGrayPaint = new Paint();
    private Paint blackPaint = new Paint();
    private Paint greenPaint = new Paint();
    private Paint yellowPaint = new Paint();
    private Paint bluePaint = new Paint();
    private Paint redPaint = new Paint();
    private Paint textPaint = new Paint();

    private float scaleX, scaleY;
    private float screenWidth, screenHeight;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    // Required for XML inflation
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // Optional for style attributes (not needed here but included for completeness)
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        holder = getHolder();
        holder.addCallback(this);
        lightGrayPaint.setColor(Color.LTGRAY);
        darkGrayPaint.setColor(Color.DKGRAY);
        blackPaint.setColor(Color.BLACK);
        greenPaint.setColor(Color.GREEN);
        yellowPaint.setColor(Color.YELLOW);
        bluePaint.setColor(Color.BLUE);
        redPaint.setColor(Color.RED);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);

        dungeon = new Dungeon(15, 15);
        player = new Player(dungeon.getWidth(), dungeon.getHeight());
        initializeMonsters();
    }

    private void initializeMonsters() {
        monsters.clear();
        if (timesPlayed == 1) {
            if (MONSTER_AMOUNT % 2 == 0) {
                for (int i = 0; i < MONSTER_AMOUNT / 2; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, 1));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 2; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, 2));
                }
            } else {
                for (int i = 0; i < MONSTER_AMOUNT / 2; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, 1));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 2; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, 2));
                }
                monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, 2));
            }
        } else {
            if (MONSTER_AMOUNT % 3 == 0) {
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed - 1));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed + 1));
                }
            } else if (MONSTER_AMOUNT % 3 == 1) {
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed - 1));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed + 1));
                }
                monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed + 1));
            } else {
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed - 1));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed + 1));
                }
                monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed));
                monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, timesPlayed + 1));
            }
        }
        levelFinished = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        tileSize = Math.min(screenWidth / 7, screenHeight / 7); // Show ~7x7 tiles centered (adjust for zoom level)
        updateCamera(); // Center player initially
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            if (!holder.getSurface().isValid()) continue;

            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    synchronized (holder) {
                        update();
                        draw(canvas);
                    }
                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(20); // ~50 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (battleInProgress && battle != null) {
            battle.update(); // Handle blink/message timers
            if (!battle.isBlinking() && !GameView.isBattleInProgress()) {
                battle = null;
                // Check for game over
                if (player.getHealth() <= 0) {
                    gameMessage = "GAME OVER! YOU LOST!";
                    messageCounter = 100; // ~2s
                }
            }
        } else if (!levelFinished) {
            dungeon.updateSeenRooms(player);
            monsters.removeIf(m -> m.getHealth() <= 0);
            if (monsters.isEmpty()) {
                levelFinished = true;
                gameMessage = "YOU WON!";
                messageCounter = 100; // ~2s
            }
            if (playerMoved) {
                for (Monster monster : monsters) {
                    monster.move();
                    if (monster.getX() == player.getX() && monster.getY() == player.getY() && !battleInProgress) {
                        battleInProgress = true;
                        engagingMonster = monster;
                        battle = new Battle(player, monster, dungeon);
                        monster.startBattle();
                    }
                }
                playerMoved = false; // Reset after movement
            }
        } else if (messageCounter > 0) {
            messageCounter--;
            if (messageCounter == 0 && !gameMessage.equals("GAME OVER! YOU LOST!")) {
                timesPlayed++;
                dungeon = new Dungeon(15, 15);
                initializeMonsters();
                levelFinished = false;
                gameMessage = "NEXT LEVEL STARTING...";
                messageCounter = 50; // ~1s
            }
        }
    }

    public static void setBattleInProgress(boolean inProgress) {
        battleInProgress = inProgress;
    }

    public static boolean isBattleInProgress() {
        return battleInProgress;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);

        if (battleInProgress && battle != null) {
            battle.drawBattleScreen(canvas, bluePaint, redPaint, greenPaint, blackPaint, textPaint);
        } else {
            canvas.save();
            // Apply camera translation and scale
            canvas.translate(screenWidth / 2 - (player.getX() + 0.5f) * tileSize - cameraX,
                    screenHeight / 2 - (player.getY() + 0.5f) * tileSize - cameraY);
            canvas.scale(tileSize, tileSize); // Apply tile size (for zoom later)

            dungeon.draw(canvas, lightGrayPaint, darkGrayPaint, blackPaint);
            player.draw(canvas);
            for (Monster monster : monsters) {
                monster.draw(canvas);
            }

            canvas.restore(); // Reset for UI elements

            if (!gameMessage.isEmpty()) {
                textPaint.setTextSize(60);
                canvas.drawText(gameMessage, screenWidth / 2, screenHeight / 2, textPaint);
                textPaint.setTextSize(30);
            }
        }

        if (!gameMessage.isEmpty()) {
            textPaint.setTextSize(60);
            canvas.drawText(gameMessage, screenWidth / 2f / scaleX, screenHeight / 2f / scaleY, textPaint);
            textPaint.setTextSize(30);
        }
    }

    private void updateCamera() {
        // Center player (adjust for zoom later)
        cameraX = 0; // Optional fine-tuning for offsets
        cameraY = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (battleInProgress && battle != null) {
            // Handle battle taps (unchanged)
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float battleTouchX = event.getX() * 30f / screenWidth;
                float battleTouchY = event.getY() * 30f / screenHeight;
                if (battleTouchX >= 13 && battleTouchX <= 17 && battleTouchY >= 4.5 && battleTouchY <= 5.5) {
                    battle.playerFlee();
                } else if (battleTouchX >= 13 && battleTouchX <= 17 && battleTouchY >= 2.5 && battleTouchY <= 3.5) {
                    battle.playerAttack();
                } else if (battleTouchX >= 8 && battleTouchX <= 12 && battleTouchY >= 3.5 && battleTouchY <= 5.5) {
                    battle.playerUsePotion();
                }
                return true;
            }
        }
        return super.onTouchEvent(event); // No swipes/taps for movement; buttons handle it
    }

    // Called from MainActivity button listeners
    public void movePlayer(int dx, int dy) {
        if (battleInProgress || levelFinished) return;
        player.move(dx, dy, dungeon);
        playerMoved = true;
        updateCamera(); // Recenter after move
    }

    public void pause() {
        running = false;
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Getters for progress bars
    public int getPlayerHealth() {
        return player.getHealth();
    }

    public int getPlayerMaxHealth() {
        return player.getMaxHealth();
    }

    public int getPlayerXp() {
        return player.getXp();
    }

    public int getPlayerXpNeeded() {
        return player.getXpNeeded();
    }
}