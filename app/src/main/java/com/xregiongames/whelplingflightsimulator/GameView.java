package com.xregiongames.whelplingflightsimulator;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
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
    boolean gameOver;

    Obstacle [] bottomPipes = new Obstacle[5];
    private int bottomDisplacement = 500;
    Obstacle [] topPipes = new Obstacle[5];
    private int topDisplacement = 500;

    public GameView(Context context) {
        super(context);

        holder = getHolder();
        paint = new Paint();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int phoneWidth = size.x;
        int phoneHeight = size.y;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth=dm.widthPixels;
        int screenheight=dm.heightPixels;
        double wi=(double)screenWidth/(double)dm.xdpi;
        double hi=(double)screenheight/(double)dm.ydpi;
        floor = new Rect(0, phoneHeight, phoneWidth, phoneHeight);

        player = new Player(BitmapFactory.decodeResource(this.getResources(), R.drawable.flappybird));
        //TODO: FIND THE BOTTOM OF SCREEN
        bottomPipes[0] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe), 1000, 1100);
        topPipes[0] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.upsidedownpipe), 1000, (int)hi);
        bottomPipes[1] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe), 1500, 1100);
        topPipes[1] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.upsidedownpipe), 1500, (int)hi);
        bottomPipes[2] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe), 2000, 1100);
        topPipes[2] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.upsidedownpipe), 2000, (int)hi);
        bottomPipes[3] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe), 2500, 1100);
        topPipes[3] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.upsidedownpipe), 2500, (int)hi);
        bottomPipes[4] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe), 3000, 1100);
        topPipes[4] = new Obstacle(BitmapFactory.decodeResource(this.getResources(), R.drawable.upsidedownpipe), 3000, (int)hi);

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
                Log.e("Negative sleep", e.getMessage());
            }
        }

    }

    void update() {
        if (player.getIsJumping() && !player.getIsDead()) {
            player.hop();
        } else if (!player.getIsJumping())
            player.setPlayerPositionY(gravity);

        player.setColliderBounds();
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
            for (Obstacle obstacle : bottomPipes) {
                if (obstacle.getObstaclePositionX() < 0 - obstacle.getWidth()) {
                    //TODO: Fix object pooling
                    obstacle.setObstaclePositionX(bottomPipes[bottomPipes.length - 1].getObstaclePositionX() + bottomDisplacement);
                    bottomDisplacement += 500;
                    if (obstacle == bottomPipes[bottomPipes.length - 1])
                        bottomDisplacement = 500;
                }
                canvas.drawBitmap(obstacle.getObstacle(), obstacle.getObstaclePositionX(), obstacle.getObstaclePositionY(), paint);
            }

            for (Obstacle obstacle : topPipes) {
                if (obstacle.getObstaclePositionX() < 0 - obstacle.getWidth()) {
                    //TODO: Fix object pooling
                    obstacle.setObstaclePositionX(topPipes[topPipes.length - 1].getObstaclePositionX() + topDisplacement);
                    topDisplacement += 500;
                    if (obstacle == topPipes[topPipes.length - 1])
                        topDisplacement = 500;
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
                    bottomDisplacement = topDisplacement = 500;
                    gameOver = false;
                } else
                    player.setIsJumping(true);
                break;
        }
        return true;
    }
}
