package com.sonic19260.boxworld2d.entity;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.sonic19260.boxworld2d.engine.Hitbox;
import com.sonic19260.boxworld2d.level.Spawner;

public abstract class Entity {
    // Position variables
    protected double posX,
                    posY,
                    velX,
                    velY;

    protected boolean isStanding;
    protected final double GRAVITY_ACCEL = 0.3;

    // Sprite variables
    protected Drawable currentSprite,
                        sprite,
                        spriteFlipped;
    protected final int pixelSize;
    protected boolean facingRight;

    // Entity type variables
    public enum EntityType {
        Player,
        Enemy,
        Projectile,
        Collectible,
        NULL
    }
    private EntityType type;

    // Combat variables
    protected int HP, damage;
    protected boolean isDead;

    // Other variables
    protected Spawner spawnedBy;
    protected Hitbox hitbox;

    // Constructors
    public Entity() {
        posX = posY = velX = velY = HP = damage = 0;
        isStanding = false;
        currentSprite = sprite = spriteFlipped = null;
        pixelSize = 128;
        facingRight = true;
        type = EntityType.NULL;
        hitbox = null;
        spawnedBy = null;
        isDead = false;
    }

    public Entity(double posX, double posY, Drawable sprite, Drawable spriteFlipped, int pixelSize, EntityType type, int HP, int damage, Spawner spawnedBy) {
        this.posX = posX;
        this.posY = posY;
        velX = velY = 0;
        isStanding = false;
        this.currentSprite = sprite;
        this.sprite = sprite;
        this.spriteFlipped = spriteFlipped;
        this.pixelSize = pixelSize;
        facingRight = true;
        this.type = type;
        this.HP = HP;
        this.damage = damage;
        hitbox = new Hitbox(posX, posY, pixelSize, pixelSize, this);
        this.spawnedBy = spawnedBy;
        isDead = false;
    }

    protected void flipSprite() {
        currentSprite = facingRight ? sprite : spriteFlipped;
    }

    protected void takeDamage(int damage) {
        HP -= damage;
        if (HP <= 0) {
            isDead = true;
        }
    }

    // Getters
    public Hitbox getHitbox() {
        return hitbox;
    }

    public EntityType getType() {
        return type;
    }

    public boolean getIsDead() {
        return isDead;
    }

    // Setters
}
