package com.xregiongames.whelplingflightsimulator;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Kyle on 10/25/2017.
 */

public class Player {

    private Bitmap player;
    private Rect collider;
    final int playerPositionX = 400;
    private int playerPositionY;
    private int left;
    private int right;
    private int top;
    private int bottom;
    private int width;
    private int height;
    private boolean isDead;
    private float hopAmount = 300;

    public Player(Bitmap player) {
        this.player = player;
        isDead = false;
        playerPositionY = 0;
        collider = new Rect();
        setColliderBounds();
    }

    void hop () {
        playerPositionY -= hopAmount;
    }

    public int getPlayerPositionY() {
        return playerPositionY;
    }

    public void setPlayerPositionY(float amount) {
        playerPositionY += amount;
    }

    public void reset() {
        playerPositionY = 500;
        isDead = false;
    }

    public Bitmap getPlayer () {
        return player;
    }

    public Rect getCollider() {
        return collider;
    }

    public void setColliderBounds() {
        width = player.getWidth();
        height = player.getHeight();
        left = playerPositionX;
        right = playerPositionX + width;
        top = playerPositionY;
        bottom = playerPositionY + height;
        collider.set(left, top, right, bottom);
    }

    public boolean getIsDead() {
        return isDead;
    }

    public void die() {
        isDead = true;
    }
}
