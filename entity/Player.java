package com.sonic19260.boxworld2d.entity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;

import com.sonic19260.boxworld2d.R;
import com.sonic19260.boxworld2d.engine.Hitbox;
import com.sonic19260.boxworld2d.level.Spawner;
import com.sonic19260.boxworld2d.ui.Button;

import java.util.ArrayList;

public class Player extends Entity {

    private double jumpMaxPos;
    private final double MAX_JUMP_HEIGHT = 256;
    private boolean reachedMaxHeight = true;
    private final double JUMP_ACCEL = -1.5;
    private final double MAX_SPEED = 9.0;

    private final long INVINCIBILITY_TIME = (long) (3 * 1E+3);
    private long invincibleTime;
    private boolean isInvincible;

    private Spawner projectileSpawner;
    private double nextProjectileShot;
    private final double NEXT_SHOT_TIME = 0.5 * 1E+3;

    // Constructors
    public Player() {
        super();
        isInvincible = false;

        projectileSpawner = new Spawner(posX, posY, EntityType.Projectile, null,
                null, -1, 1, false, -1, null);
        nextProjectileShot = 0;
    }

    public Player(double posX, double posY, Drawable sprite, Drawable spriteFlipped, int HP, int damage, Spawner spawnedBy, Drawable projectileSprite) {
        super(posX, posY, sprite, spriteFlipped, 64, EntityType.Player, HP, damage, spawnedBy);
        isInvincible = false;

        projectileSpawner = new Spawner(posX, posY + pixelSize / 2, EntityType.Projectile, projectileSprite,
                projectileSprite, -1, 1, false, -1, null);
        nextProjectileShot = 0;
    }


    // Member functions
    public void draw(Canvas canvas, Context context) {
        if (currentSprite != null) {
            Rect spriteBounds = new Rect(
                    (int) posX,
                    (int) posY,
                    (int) posX + pixelSize,
                    (int) posY + pixelSize);

            currentSprite.setBounds(spriteBounds);
            currentSprite.draw(canvas);
        }

        ArrayList<Entity> projectilesSpawned = projectileSpawner.getEntitiesSpawned();

        for (Entity projectile : projectilesSpawned) {
            ((Projectile) projectile).draw(canvas, context);
        }

        hitbox.drawWireframe(canvas, context);
    }

    public void update(Button buttonLeft, Button buttonRight, Button buttonUp, Button buttonShoot) {
        isInvincible = System.currentTimeMillis() < invincibleTime;

        move(buttonLeft, buttonRight);
        jump(buttonUp);
        shootProjectile(buttonShoot);

        if (posY > 1500) {
            takeDamage(1);

            posX = 100;
            posY = 800;
        }

        if (isInvincible) {
            blinkSprite();
        } else {
            flipSprite();
        }

        hitbox.update(posX, posY);

        ArrayList<Entity> projectilesSpawned = projectileSpawner.getEntitiesSpawned();

        for (Entity projectile : projectilesSpawned) {
            ((Projectile) projectile).update();
        }
    }

    private void move(Button buttonLeft, Button buttonRight) {
        boolean isMoving = buttonLeft.getIsPressed() || buttonRight.getIsPressed();
        if (buttonLeft.getIsPressed()) {
            facingRight = false;
            if (!isInvincible) {
                flipSprite();
            }
        }

        if (buttonRight.getIsPressed()) {
            facingRight = true;
            if (!isInvincible) {
                flipSprite();
            }
        }

        if (isMoving) {
            if (Math.abs(velX) < MAX_SPEED) {
                velX += (0.3 * (facingRight ? 1 : -1));
            }
        } else {
            if (velX < 0.0) {
                velX += 0.3;
            } else if (velX > 0.0) {
                velX -= 0.3;
            }
            if (Math.abs(velX) < 0.3) {
                velX = 0.0;
            }
        }

        posX += velX;
    }

    private void jump(Button buttonUp) {
        if (isStanding) {
            if (buttonUp.getIsPressed()) {
                if (jumpMaxPos == 0.0) {
                    jumpMaxPos = posY - MAX_JUMP_HEIGHT;
                    isStanding = false;
                    reachedMaxHeight = false;
                }

                velY = JUMP_ACCEL;
            }
        } else {
            if (posY < jumpMaxPos) {
                reachedMaxHeight = true;
            }

            if (!(buttonUp.getIsPressed()) || reachedMaxHeight) {
                if (velY < 10.0) {
                    velY += GRAVITY_ACCEL;
                }
            } else if (buttonUp.getIsPressed() && !reachedMaxHeight) {
                if (velY > -10.0) {
                    velY += JUMP_ACCEL;
                }
            }
        }

        posY += velY;
    }

    private void shootProjectile(Button buttonShoot) {
        if (buttonShoot.getIsPressed()) {
            if (System.currentTimeMillis() > nextProjectileShot) {
                double projectilePosX = (facingRight == true) ? posX + pixelSize : posX;
                double projectilePosY = posY + pixelSize / 2;
                projectileSpawner.setPosX(projectilePosX);
                projectileSpawner.setPosY(projectilePosY);
                projectileSpawner.setSpawnFacingRight(facingRight);
                projectileSpawner.spawnEntity();

                nextProjectileShot = System.currentTimeMillis() + NEXT_SHOT_TIME;
            }
        }
    }

    public void onCollision(Hitbox other) {
        if (other.getPlatform() != null) { // we collided with a platform
            boolean aboveThisPlatform = (posY + pixelSize <= other.getPosY() + 10.0 &&
                                            posY + pixelSize >= other.getPosY() - 1.0) &&
                                            !(posY > other.getPosY());
            if (aboveThisPlatform) { // collide with a platform below us
                isStanding = true;
                velY = 0;
                jumpMaxPos = 0.0;
            } else if (posY >= other.getPosY() + other.getHeight()) { // collide with a platform above us
                jumpMaxPos = posY;
                reachedMaxHeight = true;
                isStanding = false;
            }

            if (posX <= other.getPosX() + other.getWidth() &&
                    posX >= other.getPosX() + other.getWidth() - MAX_SPEED &&
                    !aboveThisPlatform) { // collide with a wall to our left
                velX = 0.0;
                posX = other.getPosX() + other.getWidth();
            } else if (posX + pixelSize >= other.getPosX() &&
                        posX + pixelSize <= other.getPosX() + MAX_SPEED &&
                        !aboveThisPlatform) { // collide with a wall to our right
                velX = 0.0;
                posX = other.getPosX() - pixelSize;
            }
        } else if (other.getEntity() != null) { // we collided with an enemy
            if (!isInvincible) {
                velX = 15.0 * (facingRight ? -1 : 1);
                velY += (-10.0);

                takeDamage(other.getEntity().damage);

                invincibleTime = INVINCIBILITY_TIME + System.currentTimeMillis();
                isInvincible = true;
            }
        }
    }

    private void blinkSprite() {
        if ((int)System.currentTimeMillis() % 10 == 0) {
            if (currentSprite == null) {
                flipSprite();
            } else {
                currentSprite = null;
            }
        }
    }

    // Setters
    public void setIsStanding(boolean value) {
        isStanding = value;
    }

    // Getters
    public ArrayList<Hitbox> getProjectilesSpawnedHitboxes() {
        ArrayList<Entity> projectiles = projectileSpawner.getEntitiesSpawned();
        ArrayList<Hitbox> hitboxes = new ArrayList<Hitbox>();
        for (Entity projectile : projectiles) {
            hitboxes.add(((Projectile) projectile).hitbox);
        }

        return hitboxes;
    }
}
