package com.sonic19260.boxworld2d.ui;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Button {
    // Position variables
    private double centerX,
                    centerY,
                    radius;

    // Status variables
    private boolean isPressed;
    private int pressedByPointer;

    // Sprite variables
    private Drawable buttonPressed, buttonUnpressed;

    // Constructors
    public Button() {
        centerX = centerY = radius = 0;
        isPressed = false;
        buttonPressed = buttonUnpressed = null;
        pressedByPointer = -1;
    }

    public Button(double centerX, double centerY, double radius, Drawable pressed, Drawable unpressed) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        buttonPressed = pressed;
        buttonUnpressed = unpressed;
        isPressed = false;

        buttonUnpressed.setAlpha(127);
        pressedByPointer = -1;
    }

    // Member functions
    public void draw(Canvas canvas) {
        Rect buttonBounds = new Rect(
                (int)(centerX - radius / 2),
                (int)(centerY - radius / 2),
                (int)(centerX + radius / 2),
                (int)(centerY + radius / 2));
        if (!isPressed) {
            buttonUnpressed.setBounds(buttonBounds);
            buttonUnpressed.draw(canvas);
        } else {
            buttonPressed.setBounds(buttonBounds);
            buttonPressed.draw(canvas);
        }
    }

    public boolean isPressed(double touchPosX, double touchPosY) {
        double centerToTouchDist = Math.sqrt(
                Math.pow(centerX - touchPosX, 2) +
                Math.pow(centerY - touchPosY, 2)
        );

        return centerToTouchDist < radius;
    }

    // Getters
    public boolean getIsPressed() {
        return isPressed;
    }

    // Setters
    public void setIsPressed(boolean value, int pointer) {
        if (pressedByPointer == -1 || pressedByPointer == pointer) {
            isPressed = value;
        } else {
            return;
        }

        if (isPressed) {
            buttonPressed.setAlpha(255);
            pressedByPointer = pointer;
        } else {
            buttonUnpressed.setAlpha(127);
            pressedByPointer = -1;
        }
    }
}
