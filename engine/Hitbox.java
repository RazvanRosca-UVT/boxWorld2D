package com.sonic19260.boxworld2d.engine;

import static com.sonic19260.boxworld2d.entity.Entity.EntityType.Enemy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.core.content.ContextCompat;

import com.sonic19260.boxworld2d.R;
import com.sonic19260.boxworld2d.entity.Enemy;
import com.sonic19260.boxworld2d.entity.Entity;
import com.sonic19260.boxworld2d.entity.Player;
import com.sonic19260.boxworld2d.entity.Projectile;
import com.sonic19260.boxworld2d.level.Platform;

import java.util.ArrayList;

public class Hitbox {
    private double posX,
                    posY,
                    width,
                    height;

    private Entity entity;
    private Platform platform;

    private boolean collides;
    private ArrayList<Hitbox> collidesWith;

    // Constructors
    public Hitbox() {
        posX = posY = width = height = 0;
        collides = false;
        entity = null;
        platform = null;
        collidesWith = new ArrayList<Hitbox>();
    }

    public Hitbox(double posX, double posY, double width, double height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        collides = false;
        entity = null;
        platform = null;
        collidesWith = new ArrayList<Hitbox>();
    }

    public Hitbox(double posX, double posY, double width, double height, Entity entity) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        collides = false;
        this.entity = entity;
        platform = null;
        collidesWith = new ArrayList<Hitbox>();
    }

    public Hitbox(double posX, double posY, double width, double height, Platform platform) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        collides = false;
        entity = null;
        this.platform = platform;
        collidesWith = new ArrayList<Hitbox>();
    }

    // Member functions
    public void drawWireframe(Canvas canvas, Context context) {
        Rect hitboxRect = new Rect(
                (int)posX,
                (int)posY,
                (int)(posX + width),
                (int)(posY + height));
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.red));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0f);
        canvas.drawRect(hitboxRect, paint);
    }

    public void update(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public ArrayList<Hitbox> checkCollision(ArrayList<Hitbox> hitboxes, int checkFrom) {
        for (int i = checkFrom; i < hitboxes.size(); i++) {
            if (collides(
                    hitboxes.get(i).getPosX(),
                    hitboxes.get(i).getPosY(),
                    hitboxes.get(i).getWidth(),
                    hitboxes.get(i).getHeight())) {
                hitboxes.get(i).setCollides(true);
                hitboxes.get(i).addCollidesWith(this);
                this.addCollidesWith(hitboxes.get(i));
            }
        }

        return collidesWith;
    }

    private boolean collides(double posX, double posY, double width, double height) {
        return (this.posX < posX + width &&
                    this.posX + this.width > posX &&
                    this.posY < posY + height &&
                    this.height + this.posY > posY);
    }

    public void onCollision(Hitbox other) {
        if (entity != null) {
            switch (entity.getType()) {
                case Player: {
                    ((Player) entity).onCollision(other);
                    break;
                }
                case Enemy: {
                    ((Enemy) entity).onCollision(other);
                    break;
                }
                case Projectile: {
                    ((Projectile) entity).onCollision(other);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    // Getters
    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Entity getEntity() {
        return entity;
    }

    public Platform getPlatform() {
        return platform;
    }

    public boolean getCollides() {
        return collides;
    }

    public ArrayList<Hitbox> getCollidesWith() {
        return collidesWith;
    }

    // Setters
    public void setCollides(boolean value) {
        collides = value;
    }

    public void addCollidesWith(Hitbox other) {
        collidesWith.add(other);
        collides = true;
    }

    public void deleteCollidesWith(Hitbox other) {
        collidesWith.remove(other);

        if (collidesWith.size() == 0) {
            collides = false;
        }
    }

    public void deleteAllCollidesWith() {
        collidesWith.clear();
        collides = false;
    }
}
