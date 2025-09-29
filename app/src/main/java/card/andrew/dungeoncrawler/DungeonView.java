package card.andrew.dungeoncrawler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DungeonView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread thread;
    private SurfaceHolder holder;
    private volatile boolean running = false;
    private Dungeon dungeon;
    private Player player;
    private final List<Monster> monsters = new ArrayList<>();
    private static final int MONSTER_AMOUNT = 4;
    private float baseTileSize = 1.0f;
    private float cameraX = 0, cameraY = 0;
    private float screenWidth, screenHeight;

    // Paints
    private final Paint lightGrayPaint = new Paint();
    private final Paint darkGrayPaint = new Paint();
    private final Paint blackPaint = new Paint();
    private final Paint greenPaint = new Paint();
    private final Paint redPaint = new Paint();

    public DungeonView(Context context) {
        super(context);
        init();
    }

    public DungeonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DungeonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        lightGrayPaint.setColor(Color.LTGRAY);
        darkGrayPaint.setColor(Color.DKGRAY);
        blackPaint.setColor(Color.BLACK);
        greenPaint.setColor(Color.GREEN);
        redPaint.setColor(Color.RED);

        dungeon = new Dungeon(15, 15);
        dungeon.revealAllRooms(); // Reveal all rooms for background
        player = new Player(dungeon.getWidth() / 2, dungeon.getHeight() / 2); // Center player
        initializeMonsters();
    }

    private void initializeMonsters() {
        monsters.clear();
        for (int i = 0; i < MONSTER_AMOUNT; i++) {
            monsters.add(new Monster(dungeon.getWidth(), dungeon.getHeight(), player, dungeon, 1));
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        baseTileSize = Math.min(screenWidth / (dungeon.getWidth() * 1.5f), screenHeight / (dungeon.getHeight() * 1.5f));
        updateCamera(); // Center on dungeon center
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        running = false;
        try {
            thread.join();
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

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);

        canvas.save();
        float scaleFactor = 1.0f;
        float currentTileSize = baseTileSize * scaleFactor;
        // Center camera on dungeon center
        float centerX = (dungeon.getWidth() - 1) / 2.0f * currentTileSize;
        float centerY = (dungeon.getHeight() - 1) / 2.0f * currentTileSize;
        canvas.translate(screenWidth / 2 - centerX - cameraX, screenHeight / 2 - centerY - cameraY);
        canvas.scale(currentTileSize, currentTileSize);

        dungeon.draw(canvas, lightGrayPaint, darkGrayPaint, blackPaint);
        player.draw(canvas);
        for (Monster monster : monsters) {
            monster.draw(canvas);
        }

        canvas.restore();
    }

    private void updateCamera() {
        cameraX = 0;
        cameraY = 0;
    }
}
