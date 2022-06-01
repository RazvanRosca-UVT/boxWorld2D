package com.sonic19260.boxworld2d.engine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sonic19260.boxworld2d.GameActivity;
import com.sonic19260.boxworld2d.MainActivity;
import com.sonic19260.boxworld2d.R;
import com.sonic19260.boxworld2d.database.DatabaseHelper;
import com.sonic19260.boxworld2d.entity.Enemy;
import com.sonic19260.boxworld2d.entity.Entity;
import com.sonic19260.boxworld2d.entity.Player;
import com.sonic19260.boxworld2d.level.Platform;
import com.sonic19260.boxworld2d.level.Spawner;
import com.sonic19260.boxworld2d.ui.Button;

import java.util.ArrayList;

public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private GameLoop gameLoop;
    private int lives = 3;
    private boolean isGameOver = false;
    private int score = 0;

    private DatabaseHelper dbHelper;

    // Screen variables
    public final int SCREEN_HEIGHT,
                      SCREEN_WIDTH;

    // Spawners
    private Spawner playerSpawner;
    private ArrayList<Spawner> enemySpawners;

    // Entities
    private Player player;
    private ArrayList<Enemy> enemies;

    // Buttons
    private Button buttonLeft,
                    buttonRight,
                    buttonUp,
                    buttonShoot;

    // Platforms
    private ArrayList<Platform> platforms;

    // Hitboxes
    private ArrayList<Hitbox> hitboxes;

    public Game(Context context, GameActivity gameActivity) {
        super(context);

        // Get surface holder details and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder, gameActivity);
        dbHelper = new DatabaseHelper(context);

        // Initialize screen variables
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;

        // Initialize spawners
        playerSpawner = new Spawner(SCREEN_WIDTH / 4, SCREEN_HEIGHT - 128, Entity.EntityType.Player,
                context.getResources().getDrawable(R.drawable.player),
                context.getResources().getDrawable(R.drawable.player_flipped),
                3,1,false,0,
                context.getResources().getDrawable(R.drawable.projectile));

        enemySpawners = new ArrayList<Spawner>();
        enemySpawners.add(new Spawner(SCREEN_WIDTH / 4, 0, Entity.EntityType.Enemy,
                context.getResources().getDrawable(R.drawable.enemy),
                context.getResources().getDrawable(R.drawable.enemy_flipped),
                1, 1, true, (long) (6 * 1E+3), null));
        enemySpawners.add(new Spawner(SCREEN_WIDTH / 4 + SCREEN_WIDTH / 2, 0, Entity.EntityType.Enemy,
                context.getResources().getDrawable(R.drawable.enemy),
                context.getResources().getDrawable(R.drawable.enemy_flipped),
                1, 1, true, (long) (3 * 1E+3), null));

        // Initialize player
        player = (Player) playerSpawner.spawnEntity();

        // Initialize enemies
        enemies = new ArrayList<Enemy>();

        // Initialize buttons
        buttonLeft = new Button(128, SCREEN_HEIGHT - 128, 256,
                context.getResources().getDrawable(R.drawable.con_arrow_left),
                        context.getResources().getDrawable(R.drawable.con_arrow_left));
        buttonRight = new Button(128 + 256 + 100, SCREEN_HEIGHT - 128, 256,
                context.getResources().getDrawable(R.drawable.con_arrow_right),
                context.getResources().getDrawable(R.drawable.con_arrow_right));
        buttonUp = new Button(SCREEN_WIDTH - 256, SCREEN_HEIGHT - 128, 256,
                context.getResources().getDrawable(R.drawable.con_arrow_up),
                context.getResources().getDrawable(R.drawable.con_arrow_up));
        buttonShoot = new Button(SCREEN_WIDTH - 256 - 256 - 100, SCREEN_HEIGHT - 128, 256,
                context.getResources().getDrawable(R.drawable.con_shoot),
                context.getResources().getDrawable(R.drawable.con_shoot));

        // Initialize platforms
        platforms = new ArrayList<Platform>();
        // Wall platforms
        platforms.add(new Platform(0, -256, 50, SCREEN_HEIGHT + 256));
        platforms.add(new Platform(SCREEN_WIDTH - 50, -256, 50, SCREEN_HEIGHT + 256));
        // Bottom row platforms
        platforms.add(new Platform(50, SCREEN_HEIGHT - 50, SCREEN_WIDTH / 2 - 192 - 50, 50));
        platforms.add(new Platform(SCREEN_WIDTH / 2 + 192, SCREEN_HEIGHT - 50, SCREEN_WIDTH / 2 - 192, 50));
        // 1st middle row platform
        platforms.add(new Platform(SCREEN_WIDTH / 2 - 300, SCREEN_HEIGHT / 2 + SCREEN_HEIGHT / 4, 600, 50));
        // 2nd middle-gap row platforms
        platforms.add(new Platform(50, SCREEN_HEIGHT / 2 + 50, SCREEN_WIDTH / 2 - 192 - 50, 50));
        platforms.add(new Platform(SCREEN_WIDTH / 2 + 192, SCREEN_HEIGHT / 2 + 50, SCREEN_WIDTH / 2 - 192 - 50, 50));
        // 2nd middle row platform
        platforms.add(new Platform(SCREEN_WIDTH / 2 - 600, SCREEN_HEIGHT / 2 - SCREEN_HEIGHT / 4 + 100, 1200, 50));
        // topmost row platforms
        platforms.add(new Platform(50, 150, SCREEN_WIDTH / 2 - 500 - 50, 50));
        platforms.add(new Platform(SCREEN_WIDTH / 2 + 500, 150, SCREEN_WIDTH / 2 - 500 - 50, 50));

        hitboxes = new ArrayList<Hitbox>(); // IMPORTANT: ALL ENTITIES MUST COME FIRST IN THIS LIST.
        hitboxes.add(player.getHitbox());
        for(Platform platform : platforms) {
            hitboxes.add(platform.getHitbox());
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    public void update() {
        if (lives > 0) {
            if (player.getIsDead()) {
                lives--;
                hitboxes.remove(player.getHitbox());
                player = (Player) playerSpawner.spawnEntity();
                hitboxes.add(0, player.getHitbox());

                if (lives == 0) {
                    endGame();
                }
            }

            player.setIsStanding(false);
            for (int i = 0; i < enemies.size(); i++) {
                if (enemies.get(i).getIsDead()) {
                    int oldScore = score;
                    score += (10 * (enemies.get(i).getAngerLevel() + 1));
                    if (oldScore / 1000 != score / 1000) { // every 1000 points, gain one extra life
                        lives++;
                    }

                    hitboxes.remove(enemies.get(i).getHitbox());
                    enemies.remove(i);
                    i--;
                    continue;
                }

                enemies.get(i).setIsStanding(false);
            }

            ArrayList<Hitbox> hitboxesToCheck = player.getProjectilesSpawnedHitboxes();
            for (Hitbox hitbox : hitboxes) {
                hitboxesToCheck.add(hitbox);
            }

            for (int i = 0; i < hitboxesToCheck.size() - 1; i++) {
                if (hitboxesToCheck.get(i).getEntity() == null) {
                    break;
                }
                ArrayList<Hitbox> hits = checkCollision(hitboxesToCheck.get(i), i + 1);

                for (int j = 0; j < hits.size(); j++) {
                    hitboxesToCheck.get(i).onCollision(hits.get(j));
                }
                hitboxesToCheck.get(i).deleteAllCollidesWith();
            }

            for (Enemy enemy : enemies) {
                enemy.update();
            }
            player.update(buttonLeft, buttonRight, buttonUp, buttonShoot);

            for (Spawner enemySpawner : enemySpawners) {
                Entity entity = enemySpawner.update();
                if (entity != null) {
                    enemies.add((Enemy) entity);
                    hitboxes.add(1, entity.getHitbox());
                }
            }
        }
    }

    public ArrayList<Hitbox> checkCollision(Hitbox hitbox, int checkFrom) {
        return hitbox.checkCollision(hitboxes, checkFrom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();

        int pointerId = event.getPointerId(pointerIndex);

        int maskedAction = event.getActionMasked();

        switch(maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                float eventPosX = event.getX(pointerId),
                        eventPosY = event.getY(pointerId);

                if (buttonLeft.isPressed((double) eventPosX, (double) eventPosY)) {
                    buttonLeft.setIsPressed(true, pointerId);
                    return true;
                }
                if (buttonRight.isPressed((double) eventPosX, (double) eventPosY)) {
                    buttonRight.setIsPressed(true, pointerId);
                    return true;
                }
                if (buttonUp.isPressed((double) eventPosX, (double) eventPosY)) {
                    buttonUp.setIsPressed(true, pointerId);
                    return true;
                }
                if (buttonShoot.isPressed((double) eventPosX, (double) eventPosY)) {
                    buttonShoot.setIsPressed(true, pointerId);
                    return true;
                }
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                buttonLeft.setIsPressed(false, pointerId);
                buttonRight.setIsPressed(false, pointerId);
                buttonUp.setIsPressed(false, pointerId);
                buttonShoot.setIsPressed(false, pointerId);

                return true;
            }
        }

        return super.onTouchEvent(event);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Text
        drawUPS(canvas);
        drawFPS(canvas);
        drawScore(canvas);
        drawLives(canvas);

        // Entities
        player.draw(canvas, getContext());
        for (Enemy enemy : enemies) {
            enemy.draw(canvas, getContext());
        }

        // Platforms
        for(Platform platform : platforms) {
            platform.draw(canvas, getContext());
        }

        // Buttons
        buttonLeft.draw(canvas);
        buttonRight.draw(canvas);
        buttonUp.draw(canvas);
        buttonShoot.draw(canvas);
    }

    public void drawUPS(Canvas canvas) {
        String averageUPS = Double.toString(gameLoop.getAverageUPS());

        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText("UPS: " + averageUPS, 100, 50, paint);
    }

    public void drawFPS(Canvas canvas) {
        String averageFPS = Double.toString(gameLoop.getAverageFPS());

        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText("FPS: " + averageFPS, 100, 150, paint);
    }

    public void drawScore(Canvas canvas) {
        String scoreText = Integer.toString(score);

        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText("Score: " + scoreText, SCREEN_WIDTH - 500, 50, paint);
    }

    public void drawLives(Canvas canvas) {
        String livesText = Integer.toString(lives);

        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText("Lives: " + livesText, SCREEN_WIDTH - 500, 150, paint);
    }

    private void endGame() {
        dbHelper.insertData(MainActivity.username, score);

        isGameOver = true;
    }

    // Getters
    public boolean isGameOver() {
        return isGameOver;
    }
}
