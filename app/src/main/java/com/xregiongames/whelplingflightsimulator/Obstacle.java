package com.xregiongames.whelplingflightsimulator;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Kyle on 10/26/2017.
 */

class Obstacle {

    private Bitmap obstacle;
    private Rect collider;
    private int obstaclePositionX;
    private int obstaclePositionY;
    private int originalPosX;
    private static int moveSpeed;
    private int height;

    Obstacle(Bitmap obstacle, int posX) {
        this.obstacle = obstacle;
        obstaclePositionX = posX;
        originalPosX = obstaclePositionX;
        collider = new Rect();
        setColliderBounds();
    }

    void update() {
        obstaclePositionX -= moveSpeed;
        setColliderBounds();
    }

    void setMoveSpeed(int speed) {
        moveSpeed = speed;
    }

    Bitmap getObstacle () {
        return obstacle;
    }

    int getWidth() { return obstacle.getWidth(); }

    int getObstaclePositionX() {
        return obstaclePositionX;
    }

    void setObstaclePositionX(int positionX) {
        obstaclePositionX = positionX;
    }

    int getObstaclePositionY() {
        return obstaclePositionY;
    }

    void generateObstaclePositionY(Obstacle other, int bottomOfScreen) {
        double randomNum = ThreadLocalRandom.current().nextDouble(0, 1);
        double screenSpace = randomNum * bottomOfScreen;
        double blankSpace = bottomOfScreen * 0.4;
        int otherObstacleSpace = (int) (screenSpace + blankSpace)/*(bottomOfScreen * (0.4 + randomNum))*/;
        obstaclePositionY = (int) screenSpace - height;
        other.setObstaclePositionY(otherObstacleSpace);
    }

    private void setObstaclePositionY(int positionY) {
        obstaclePositionY = positionY;
    }

    void reset() {
        obstaclePositionX = originalPosX;
    }

    Rect getCollider() {
        return collider;
    }

    private void setColliderBounds() {
        int width = obstacle.getWidth();
        height = obstacle.getHeight();
        int left = obstaclePositionX;
        int right = obstaclePositionX + width;
        int top = obstaclePositionY;
        int bottom = obstaclePositionY + height;
        collider.set(left, top, right, bottom);
    }
}
