package com.xregiongames.whelplingflightsimulator;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by Kyle on 10/26/2017.
 */

public class Obstacle {

    private Bitmap obstacle;
    private Rect collider;
    private int obstaclePositionX;
    private int obstaclePositionY;
    private int originalPosX;
    private int originalPosY;
    private int left;
    private int right;
    private int top;
    private int bottom;
    private int width;
    private int height;

    public Obstacle(Bitmap obstacle, int posX, int posY) {
        this.obstacle = obstacle;
        obstaclePositionX = posX;
        obstaclePositionY = posY;
        originalPosX = obstaclePositionX;
        originalPosY = obstaclePositionY;
        collider = new Rect();
        setColliderBounds();
    }

    public void update() {
        obstaclePositionX -= 10;
        setColliderBounds();
    }

    public Bitmap getObstacle () {
        return obstacle;
    }

    public int getObstaclePositionX() {
        return obstaclePositionX;
    }

    public int getObstaclePositionY() {
        return obstaclePositionY;
    }

    public void reset() {
        obstaclePositionX = originalPosX;
        obstaclePositionY = originalPosY;
    }

    public Rect getCollider() {
        return collider;
    }

    public void setColliderBounds() {
        width = obstacle.getWidth();
        height = obstacle.getHeight();
        left = obstaclePositionX;
        right = obstaclePositionX + width;
        top = obstaclePositionY;
        bottom = obstaclePositionY + height;
        collider.set(left, top, right, bottom);
    }
}
