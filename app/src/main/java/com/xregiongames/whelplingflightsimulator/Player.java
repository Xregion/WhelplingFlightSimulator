package com.xregiongames.whelplingflightsimulator;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by Kyle on 10/25/2017.
 */

class Player {

    private Bitmap player;
    private Rect collider;
    final int playerPositionX = 400;
    private int playerPositionY;
    private boolean isDead;
    private boolean isJumping;
    private float velocity;
    private float currentJumpHeight = 0;
    private float maxJump;

    Player(Bitmap player) {
        this.player = player;
        isDead = false;
        isJumping = false;
        playerPositionY = 600;
        collider = new Rect();
        setColliderBounds();
    }

    void update(float gravity) {
        if (getIsJumping() && !getIsDead()) {
            hop();
        } else if (!getIsJumping())
            setPlayerPositionY(gravity);

        setColliderBounds();
    }

    void setMaxJump(float maxJump) {
        this.maxJump = maxJump;
    }

    void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    private void hop () {
        if (currentJumpHeight < maxJump) {
            isJumping = true;
            playerPositionY -= velocity;
            currentJumpHeight += velocity;
        } else {
            currentJumpHeight = 0;
            isJumping = false;
        }
    }

    int getPlayerPositionY() {
        return playerPositionY;
    }

    void setPlayerPositionY(float amount) {
        playerPositionY += amount;
    }

    void reset() {
        playerPositionY = 600;
        isDead = false;
        currentJumpHeight = 0;
    }

    Bitmap getPlayer () {
        return player;
    }

    Rect getCollider() {
        return collider;
    }

    void setColliderBounds() {
        int width = player.getWidth();
        int height = player.getHeight();
        int left = playerPositionX + width / 3;
        int right = playerPositionX + width;
        int top = playerPositionY;
        int bottom = playerPositionY + height;
        collider.set(left, top, right, bottom);
    }

    boolean getIsDead() {
        return isDead;
    }

    void die() {
        isDead = true;
        isJumping = false;
    }

    private boolean getIsJumping() {
        return isJumping;
    }

    void setIsJumping(boolean jumping) {
        isJumping = jumping;
    }
}
