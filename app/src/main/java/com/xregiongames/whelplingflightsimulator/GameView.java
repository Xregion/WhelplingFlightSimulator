package com.xregiongames.whelplingflightsimulator;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by Kyle on 10/25/2017.
 */

public class GameView extends SurfaceView implements Runnable{

    final int MS_PER_UPDATE = 33;
    final float gravity = 20;

    Thread gameThread = null;

    volatile boolean isPlaying;

    SurfaceHolder holder;

    Canvas canvas;
    Paint paint;
    Rect floor;

    Player player;
    boolean hopped;
    boolean gameOver;

    Obstacle [] obstacles = new Obstacle[2];

    public GameView(Context context) {
        super(context);

        holder = getHolder();
        paint = new Paint();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        floor = new Rect(0, height, width, height);

        player = new Player(BitmapFactory.decodeResource(this.getResources(), R.drawable.flappybird));
        obstacles[0] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe), 1000, 700);
        obstacles[1] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.upsidedownpipe), 2000, 0);

        isPlaying = true;
        hopped = false;
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
                Log.e("Negative sleep", e.getMessage());
            }
        }

    }

    void update() {
        if (hopped && !player.getIsDead()) {
            player.hop();
            hopped = false;
        } else if (!hopped)
            player.setPlayerPositionY(gravity);

        player.setColliderBounds();
        if (!player.getIsDead()) {
            if (player.getCollider().intersect(floor)) {
                player.die();
                gameOver = true;
                return;
            } else if (player.getPlayerPositionY() < 0) {
                Log.i("Player Pos", String.valueOf(player.getPlayerPositionY()));
                player.setPlayerPositionY(-player.getPlayerPositionY());
                player.setColliderBounds();
                Log.i("Player Pos", String.valueOf(player.getPlayerPositionY()));
            }
            for (Obstacle obstacle : obstacles) {
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
            for (Obstacle obstacle : obstacles) {
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
                    for (Obstacle obstacle : obstacles) {
                        obstacle.reset();
                    }
                    gameOver = false;
                } else
                    hopped = true;

                break;
        }
        return true;
    }
}
