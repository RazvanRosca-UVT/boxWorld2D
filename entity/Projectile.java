package com.sonic19260.boxworld2d.entity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.sonic19260.boxworld2d.engine.Hitbox;
import com.sonic19260.boxworld2d.level.Spawner;

public class Projectile extends Entity {
    public Projectile() {
        super();
    }

    public Projectile(double posX, double posY, Drawable sprite, int damage, Spawner spawnedBy, boolean facingRight) {
        super(posX, posY, sprite, sprite, 16, EntityType.Projectile, Integer.MAX_VALUE, damage, spawnedBy);

        shootProjectile(facingRight);
    }

    public void draw(Canvas canvas, Context context) {
        Rect spriteBounds = new Rect(
                (int) posX,
                (int) posY,
                (int) posX + pixelSize,
                (int) posY + pixelSize);

        currentSprite.setBounds(spriteBounds);
        currentSprite.draw(canvas);

        hitbox.drawWireframe(canvas, context);
    }

    public void update() {
        posX += velX;
        hitbox.update(posX, posY);

        if (posX < 0 || posX > 3000) {
            spawnedBy.deleteEntity(this);
        }
    }

    private void shootProjectile(boolean facingRight) {
        velX = 15 * (facingRight == true ? 1 : -1);
    }

    public void onCollision(Hitbox other) {
        if (other.getEntity() != null) { // we collided with an entity
            if (other.getEntity().getType() == Entity.EntityType.Enemy) {
                other.getEntity().takeDamage(damage);
                spawnedBy.deleteEntity(this);
            }
        }
    }
}
