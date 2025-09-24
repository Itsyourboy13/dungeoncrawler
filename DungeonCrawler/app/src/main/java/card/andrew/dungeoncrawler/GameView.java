package card.andrew.dungeoncrawler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
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
    private int currentLevel = 1;
    private boolean playerMoved = false; // Track player movement
    private GestureDetector gestureDetector;

    // Camera for centering and scrolling
    private float cameraX = 0, cameraY = 0;
    private float tileSize = 1.0f;

    // Paints
    private final Paint lightGrayPaint = new Paint();
    private final Paint darkGrayPaint = new Paint();
    private final Paint blackPaint = new Paint();
    private final Paint greenPaint = new Paint();
    private final Paint yellowPaint = new Paint();
    private final Paint bluePaint = new Paint();
    private final Paint redPaint = new Paint();
    private final Paint textPaint = new Paint();

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
        if (currentLevel == 1) {
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
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel - 1));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel + 1));
                }
            } else if (MONSTER_AMOUNT % 3 == 1) {
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel - 1));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel + 1));
                }
                monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel + 1));
            } else {
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel - 1));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel));
                }
                for (int i = 0; i < MONSTER_AMOUNT / 3; i++) {
                    monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel + 1));
                }
                monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel));
                monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, currentLevel + 1));
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
        if (!levelFinished) {
            dungeon.updateSeenRooms(player);
            monsters.removeIf(m -> m.getHealth() <= 0);
            if (monsters.isEmpty()) {
                levelFinished = true;
                showLevelFinishedDialog();
            }
            if (playerMoved) {
                for (Monster monster : monsters) {
                    monster.move();
                    if (monster.getX() == player.getX() && monster.getY() == player.getY() && !battleInProgress) {
                        battleInProgress = true;
                        engagingMonster = monster;
                        GameState.getInstance().setCurrentMonster(monster);
                        GameState.getInstance().setPlayer(player);
                        startBattleActivity();
                    }
                }
                playerMoved = false; // Reset after movement
            }
        } else {
            currentLevel++;
            dungeon = new Dungeon(15, 15);
            initializeMonsters();
            levelFinished = false;
        }
    }

    private void showLevelFinishedDialog() {
        LevelFinishedFragment fragment = LevelFinishedFragment.
                newInstance(currentLevel, player.getLevel());
        fragment.show(((MainActivity) getContext()).getSupportFragmentManager(), "level_finished");
    }

    private void startBattleActivity() {
        Intent intent = new Intent(getContext(), BattleActivity.class);
        ((MainActivity) getContext()).startBattle(intent);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);

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
    }

    private void updateCamera() {
        // Center player (adjust for zoom later)
        cameraX = 0; // Optional fine-tuning for offsets
        cameraY = 0;
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

    public void onBattleResult(boolean enemyDefeated) {
        if (enemyDefeated) {
            monsters.remove(engagingMonster);
        }
        battleInProgress = false;
        engagingMonster = null;
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