package com.xregiongames.whelplingflightsimulator;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Kyle on 10/25/2017.
 */

public class GameView extends SurfaceView implements Runnable{

    final int MS_PER_UPDATE = 33;

    Thread gameThread = null;

    volatile boolean isPlaying;

    SurfaceHolder holder;

    Canvas canvas;
    Paint paint;
    Rect floor;

    Player player;
    boolean gameOver;

    Obstacle [] bottomPipes = new Obstacle[2];
    private int bottomDisplacement;
    Obstacle [] topPipes = new Obstacle[2];
    private int topDisplacement;

    float gravity;
    int bottomOfScreen;
    int screenWidth;

    public GameView(Context context) {
        super(context);

        holder = getHolder();
        paint = new Paint();

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.i("Height", String.valueOf(screenHeight));
        Log.i("Width", String.valueOf(screenWidth));
        bottomOfScreen = (int) (screenHeight / 1.6f);
        floor = new Rect(0, screenHeight, screenWidth, screenHeight);
        gravity = screenHeight / 90;

        bottomDisplacement = topDisplacement = (int) (screenWidth / 1.5);
        int startPos = 1250;
        int nextPos = 1250 + (int) (screenWidth / 1.5);
        player = new Player(BitmapFactory.decodeResource(this.getResources(), R.drawable.babydragon));
        player.setMaxJump(screenHeight / 8);
        player.setVelocity(screenHeight / 60);
        bottomPipes[0] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.castle), startPos, bottomOfScreen);
        topPipes[0] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.upsidedowncastle), startPos, -750);
        bottomPipes[1] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.castle), nextPos, bottomOfScreen);
        topPipes[1] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.upsidedowncastle), nextPos, -750);
        bottomPipes[0].setMoveSpeed(screenWidth / 108);

        for (int i = 0; i < topPipes.length; i++) {
            topPipes[i].generateObstaclePositionY(bottomPipes[i], bottomOfScreen);
        }

        isPlaying = true;
        gameOver = false;
    }

    @Override
    public void run() {
        while (isPlaying) {
            long start = System.currentTimeMillis();

            update();
            draw();

            try {
                Thread.sleep(start + MS_PER_UPDATE - System.currentTimeMillis());
            } catch (InterruptedException e) {
                Log.e("Interrupted", e.getMessage());
            } catch (IllegalArgumentException e) {
                Log.e("Negative sleep time", e.getMessage());
            }
        }

    }

    void update() {

        player.update(gravity);

        if (!player.getIsDead()) {
            if (player.getCollider().intersect(floor)) {
                player.die();
                gameOver = true;
                return;
            } else if (player.getPlayerPositionY() < 0) {
                player.setPlayerPositionY(-player.getPlayerPositionY());
                player.setColliderBounds();
            }
            for (Obstacle obstacle : bottomPipes) {
                obstacle.update();
                if (player.getCollider().intersect(obstacle.getCollider())) {
                    player.die();
                    gameOver = true;
                    break;
                }
            }

            for (Obstacle obstacle : topPipes) {
                obstacle.update();
                if (player.getCollider().intersect(obstacle.getCollider())) {
                    player.die();
                    gameOver = true;
                    break;
                }
            }
        }
    }

    void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            int center = canvas.getWidth() / 4;
            //canvas.drawBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.medieval_background), new Matrix(), paint);

            canvas.drawColor(Color.argb(255,  26, 128, 182));
            paint.setColor(Color.argb(255,  255, 0, 0));
            //canvas.drawRect(player.getCollider(), paint);
            int i = 0;
            for (Obstacle obstacle : topPipes) {
                if (obstacle.getObstaclePositionX() < 0 - obstacle.getWidth()) {
                    obstacle.setObstaclePositionX(topPipes[topPipes.length - 1].getObstaclePositionX() + topDisplacement);
                    topDisplacement += (int) (screenWidth / 1.5);
                    obstacle.generateObstaclePositionY(bottomPipes[i], bottomOfScreen);
                    if (obstacle == topPipes[topPipes.length - 1])
                        topDisplacement = (int) (screenWidth / 1.5);
                }
                canvas.drawBitmap(obstacle.getObstacle(), obstacle.getObstaclePositionX(), obstacle.getObstaclePositionY(), paint);
                i++;
            }

            for (Obstacle obstacle : bottomPipes) {
                if (obstacle.getObstaclePositionX() < 0 - obstacle.getWidth()) {
                    obstacle.setObstaclePositionX(bottomPipes[bottomPipes.length - 1].getObstaclePositionX() + bottomDisplacement);
                    bottomDisplacement += (int) (screenWidth / 1.5);
                    if (obstacle == bottomPipes[bottomPipes.length - 1])
                        bottomDisplacement = (int) (screenWidth / 1.5);
                }
                canvas.drawBitmap(obstacle.getObstacle(), obstacle.getObstaclePositionX(), obstacle.getObstaclePositionY(), paint);
            }

            canvas.drawBitmap(player.getPlayer(), player.playerPositionX, player.getPlayerPositionY(), paint);
            if (gameOver) {
                paint.setTextSize(100);
                canvas.drawText("Game Over", center, 500, paint);
                canvas.drawText("Tap to try again", center, 1000, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                if (player.getIsDead()) {
                    player.reset();
                    for (Obstacle obstacle : bottomPipes) {
                        obstacle.reset();
                    }
                    for (Obstacle obstacle : topPipes) {
                        obstacle.reset();
                    }
                    for (int i = 0; i < topPipes.length; i++) {
                        topPipes[i].generateObstaclePositionY(bottomPipes[i], bottomOfScreen);
                    }
                    bottomDisplacement = topDisplacement = (int) (screenWidth / 1.5);
                    gameOver = false;
                } else
                    player.setIsJumping(true);
                break;
        }
        return true;
    }
}
