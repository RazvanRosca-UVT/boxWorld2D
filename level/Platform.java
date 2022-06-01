package com.sonic19260.boxworld2d.level;

import com.sonic19260.boxworld2d.R;
import com.sonic19260.boxworld2d.engine.Hitbox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.core.content.ContextCompat;

public class Platform {
    private double posX,
                    posY,
                    width,
                    height;
    private Hitbox hitbox;


    // Constructors
    public Platform() {
        posX = posY = width = height = 0;
        hitbox = null;
    }
    public Platform(double posX, double posY, double width, double height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        hitbox = new Hitbox(posX, posY, width, height, this);
    }

    // Member functions
    public void draw(Canvas canvas, Context context) {
        Rect platformRect = new Rect(
                (int)posX,
                (int)posY,
                (int)(posX + width),
                (int)(posY + height));
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.magenta));
        canvas.drawRect(platformRect, paint);

        hitbox.drawWireframe(canvas, context);
    }

    // Getters
    public Hitbox getHitbox() {
        return hitbox;
    }
}
