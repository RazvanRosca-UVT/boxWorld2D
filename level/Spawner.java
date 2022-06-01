package com.sonic19260.boxworld2d.level;

import android.graphics.drawable.Drawable;

import com.sonic19260.boxworld2d.entity.Enemy;
import com.sonic19260.boxworld2d.entity.Entity;
import com.sonic19260.boxworld2d.entity.Player;
import com.sonic19260.boxworld2d.entity.Projectile;

import java.util.ArrayList;

public class Spawner {
    // Spawner variables
    private double posX, posY;
    private boolean spawnOnTimer;
    private long spawnTime;
    private final long MAX_SPAWN_TIME;
    private boolean spawnFacingRight;

    // Entity-related variables
    private Entity.EntityType entityTypeToSpawn;
    private Drawable entitySprite, entitySpriteFlipped;
    private int entityHP, entityDMG;
    private ArrayList<Entity> entitiesSpawned;
    private Drawable projectileSprite;

    // Constructors
    public Spawner() {
        posX = posY = 0.0;
        spawnTime = 0;
        spawnOnTimer = false;
        MAX_SPAWN_TIME = 0;

        entityTypeToSpawn = Entity.EntityType.NULL;
        entitySprite = entitySpriteFlipped = null;
        entityHP = entityDMG = 0;
        entitiesSpawned = null;
    }

    public Spawner(double posX, double posY, Entity.EntityType entityTypeToSpawn,
                   Drawable entitySprite, Drawable entitySpriteFlipped, int entityHP, int entityDMG,
                   boolean spawnOnTimer, long maxSpawnTime, Drawable projectileSprite) {
        this.posX = posX;
        this.posY = posY;
        this.entityTypeToSpawn = entityTypeToSpawn;
        this.entitySprite = entitySprite;
        this.entitySpriteFlipped = entitySpriteFlipped;
        this.entityHP = entityHP;
        this.entityDMG = entityDMG;
        this.spawnOnTimer = spawnOnTimer;
        this.MAX_SPAWN_TIME = maxSpawnTime;
        this.spawnTime = MAX_SPAWN_TIME + System.currentTimeMillis();
        this.projectileSprite = projectileSprite;
        entitiesSpawned = new ArrayList<Entity>();
    }

    public Entity update() {
        if (spawnOnTimer == true) {
            if (System.currentTimeMillis() >= spawnTime) {
                spawnTime += MAX_SPAWN_TIME;
                return spawnEntity();
            }
        }

        return null;
    }

    // Member functions
    public Entity spawnEntity() {
        Entity newEntity;
        if (entityTypeToSpawn == Entity.EntityType.Player) {
            newEntity = new Player(posX, posY, entitySprite, entitySpriteFlipped, entityHP, entityDMG, this, projectileSprite);
        } else if (entityTypeToSpawn == Entity.EntityType.Enemy) {
            newEntity = new Enemy(posX, posY, entitySprite, entitySpriteFlipped, entityHP, entityDMG, this, true, 0);
        } else if (entityTypeToSpawn == Entity.EntityType.Projectile){
            newEntity = new Projectile(posX, posY, entitySprite, entityDMG, this, spawnFacingRight);
        } else {
            newEntity = null;
        }

        entitiesSpawned.add(newEntity);
        return newEntity;
    }

    public void deleteEntity(Entity entity) {
        entitiesSpawned.remove(entity);
    }

    // Setters
    public void setPosX(double value) {
        posX = value;
    }

    public void setPosY(double value) {
        posY = value;
    }

    public void setSpawnFacingRight(boolean value) {
        spawnFacingRight = value;
    }

    // Getters
    public ArrayList<Entity> getEntitiesSpawned() {
        return entitiesSpawned;
    }
}
