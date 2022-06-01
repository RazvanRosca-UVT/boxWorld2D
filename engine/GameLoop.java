package com.sonic19260.boxworld2d.engine;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.sonic19260.boxworld2d.GameActivity;

public class GameLoop extends Thread {
    private static final double MAX_UPS = 60.0;
    private static final double UPS_PERIOD = 1E+3 / MAX_UPS; // 1000

    private boolean isRunning;
    private double averageUPS;
    private double averageFPS;

    private Game game;
    private GameActivity gameActivity;
    private SurfaceHolder surfaceHolder;

    public GameLoop(Game game, SurfaceHolder surfaceHolder, GameActivity gameActivity) {
        isRunning = false;

        this.game = game;
        this.gameActivity = gameActivity;
        this.surfaceHolder = surfaceHolder;
    }

    public double getAverageUPS() {
        return averageUPS;
    }

    public double getAverageFPS() { return averageFPS; }

    public void startLoop() {
        isRunning = true;
        try {
            start();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        // Declare time related variables
        int updateCount = 0,
                frameCount = 0;

        long startTime,
                elapsedTime,
                sleepTime;


        // Game loop
        Canvas canvas = null;
        startTime = System.currentTimeMillis();
        while (isRunning) {

            // Try to update logic and render to screen
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    game.update();
                    updateCount++;
                    game.draw(canvas);

                    if (game.isGameOver()) {
                        isRunning = false;
                    }
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        frameCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Pause thread to not exceed target UPS
            elapsedTime = System.currentTimeMillis() - startTime;
            sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
            if (sleepTime > 0) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Skip frames to keep FPS up with UPS
            while (sleepTime < 0 && updateCount < MAX_UPS - 1) {
                game.update();
                updateCount++;

                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
            }
            // Calculate average FPS and UPS
            elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= 1E+3) { // 1000
                averageUPS = updateCount / (1E-3 * elapsedTime); // 1/1000
                averageFPS = frameCount / (1E-3 * elapsedTime); // 1/1000
                updateCount = frameCount = 0;
                startTime = System.currentTimeMillis();
            }
        }

        gameActivity.stopGame();
    }
}
