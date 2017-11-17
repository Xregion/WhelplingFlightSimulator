package com.xregiongames.whelplingflightsimulator;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.concurrent.ThreadLocalRandom;

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
    private static int moveSpeed;
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
        obstaclePositionX -= moveSpeed;
        setColliderBounds();
    }

    public void setMoveSpeed(int speed) {
        moveSpeed = speed;
    }

    public Bitmap getObstacle () {
        return obstacle;
    }

    public int getWidth() { return obstacle.getWidth(); }

    public int getObstaclePositionX() {
        return obstaclePositionX;
    }

    public void setObstaclePositionX(int positionX) {
        obstaclePositionX = positionX;
    }

    public int getObstaclePositionY() {
        return obstaclePositionY;
    }

    public void generateObstaclePositionY(Obstacle other, int bottomOfScreen) {
        double randomNum = ThreadLocalRandom.current().nextDouble(0, 1);
        double screenSpace = randomNum * bottomOfScreen;
        double blankSpace = bottomOfScreen * 0.4;
        int otherObstacleSpace = (int) (screenSpace + blankSpace)/*(bottomOfScreen * (0.4 + randomNum))*/;
        obstaclePositionY = (int) screenSpace - height;
        other.setObstaclePositionY(otherObstacleSpace);
    }

    void setObstaclePositionY(int positionY) {
        obstaclePositionY = positionY;
    }

    public void reset() {
        obstaclePositionX = originalPosX;
        obstaclePositionY = originalPosY;
    }

    public Rect getCollider() {
        return collider;
    }

    void setColliderBounds() {
        width = obstacle.getWidth();
        height = obstacle.getHeight();
        left = obstaclePositionX;
        right = obstaclePositionX + width;
        top = obstaclePositionY;
        bottom = obstaclePositionY + height;
        collider.set(left, top, right, bottom);
    }
}
