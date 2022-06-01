package com.sonic19260.boxworld2d.entity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.sonic19260.boxworld2d.engine.Hitbox;
import com.sonic19260.boxworld2d.level.Spawner;
import com.sonic19260.boxworld2d.ui.Button;

public class Enemy extends Entity {
    private final int MAX_ANGER_LEVEL = 5;
    private int angerLevel;

    public Enemy() {
        super();
        angerLevel = 0;
    }

    public Enemy(double posX, double posY, Drawable sprite, Drawable spriteFlipped, int HP, int damage, Spawner spawnedBy, boolean facingRight, int angerLevel) {
        super(posX, posY, sprite, spriteFlipped, 64, EntityType.Enemy, HP, damage, spawnedBy);
        this.facingRight = facingRight;
        this.angerLevel = angerLevel;
        velX = 3.0 * (angerLevel + 1);
        flipSprite();
    }

    // Member functions
    public void draw(Canvas canvas, Context context) {
        Rect spriteBounds = new Rect(
                (int)posX,
                (int)posY,
                (int)posX + pixelSize,
                (int)posY + pixelSize);

        currentSprite.setBounds(spriteBounds);
        currentSprite.draw(canvas);

        hitbox.drawWireframe(canvas, context);
    }

    public void update() {
        move();

        // Debug
        if (posY > 1500) {
            posY = 0;

            if (angerLevel < MAX_ANGER_LEVEL) {
                angerLevel++;
                velX += 3.0;
            }
        }

        hitbox.update(posX, posY);
    }

    public void move() {
        posX += (velX * ((facingRight == true) ? 1 : -1));

        if (!isStanding) {
            if (velY < 10.0) {
                velY += GRAVITY_ACCEL;
            }
        }

        posY += velY;
    }

    public void onCollision(Hitbox other) {
        if (other.getPlatform() != null) { // we collided with a platform
            boolean aboveThisPlatform = (posY + pixelSize <= other.getPosY() + 10.0 &&
                    posY + pixelSize >= other.getPosY() - 1.0) &&
                    !(posY > other.getPosY());

            if (aboveThisPlatform) { // collide with a platform below us
                isStanding = true;
                velY = 0;
            }

            if (posX <= other.getPosX() + other.getWidth() &&
                    posX >= other.getPosX() + other.getWidth() - velX &&
                    !aboveThisPlatform) { // collide with a wall to our left
                facingRight = !facingRight;
                flipSprite();
                //posX = other.getPosX() + other.getWidth();
            } else if (posX + pixelSize >= other.getPosX() &&
                    posX + pixelSize <= other.getPosX() + velX &&
                    !aboveThisPlatform) { // collide with a wall to our right
                facingRight = !facingRight;
                flipSprite();
                //posX = other.getPosX() - pixelSize;
            }
        }
    }

    // Setters
    public void setIsStanding(boolean value) {
        isStanding = value;
    }

    // Getters
    public int getAngerLevel() {
        return angerLevel;
    }
}
