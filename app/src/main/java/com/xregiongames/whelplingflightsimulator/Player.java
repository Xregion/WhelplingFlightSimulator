package com.xregiongames.whelplingflightsimulator;

import android.graphics.Bitmap;
import android.graphics.Rect;

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
    private boolean isJumping;
    private float velocity;
    private float currentJumpHeight = 0;
    private float maxJump;

    public Player(Bitmap player) {
        this.player = player;
        isDead = false;
        isJumping = false;
        playerPositionY = 600;
        collider = new Rect();
        setColliderBounds();
    }

    public void update(float gravity) {
        if (getIsJumping() && !getIsDead()) {
            hop();
        } else if (!getIsJumping())
            setPlayerPositionY(gravity);

        setColliderBounds();
    }

    public void setMaxJump(float maxJump) {
        this.maxJump = maxJump;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    void hop () {
        if (currentJumpHeight < maxJump) {
            isJumping = true;
            playerPositionY -= velocity;
            currentJumpHeight += velocity;
        } else {
            currentJumpHeight = 0;
            isJumping = false;
        }
    }

    public int getPlayerPositionY() {
        return playerPositionY;
    }

    public void setPlayerPositionY(float amount) {
        playerPositionY += amount;
    }

    public void reset() {
        playerPositionY = 600;
        isDead = false;
        currentJumpHeight = 0;
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
        left = playerPositionX + width / 3;
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
        isJumping = false;
    }

    public boolean getIsJumping() {
        return isJumping;
    }

    public void setIsJumping(boolean jumping) {
        isJumping = jumping;
    }
}
