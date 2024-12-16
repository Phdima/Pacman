package com.example.pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class Pacman {
    private int x, y;
    private Drawable pacmanDrawable;
    private Rect pacmanRect;
    private float rotationAngle = 0f;
    private Direction direction = Direction.RIGHT;

    private enum Direction {
        UP, DOWN, RIGHT, LEFT
    }

    public Pacman(Context context, int x, int y) {
        this.x = (x * Constants.blockSize) + Constants.blockSize / 2;
        this.y = (y * Constants.blockSize) + Constants.blockSize / 2;
        pacmanDrawable = ContextCompat.getDrawable(context, R.drawable.pacman);
        updateRect();
    }

    private void updateRect() {
        pacmanRect = new Rect(x - pacmanDrawable.getIntrinsicHeight(), y - pacmanDrawable.getIntrinsicWidth(), x + pacmanDrawable.getIntrinsicHeight(), y + pacmanDrawable.getIntrinsicWidth());
    }

    public void draw(Canvas canvas) {

        canvas.save();
        float centerX = pacmanRect.centerX();
        float centerY = pacmanRect.centerY();

        Matrix matrix = new Matrix();
        if (direction == Direction.LEFT) {
            matrix.preScale(1, -1, centerX, centerY);
        }
        if (direction == Direction.DOWN) {
            matrix.preScale(1, -1, centerX, centerY);
        }
        matrix.postRotate(rotationAngle, centerX, centerY);
        canvas.concat(matrix);

        pacmanDrawable.setBounds(pacmanRect.left, pacmanRect.top, pacmanRect.right, pacmanRect.bottom);
        pacmanDrawable.draw(canvas);

        canvas.restore();
    }


    public void lookLeft() {
        direction = Direction.LEFT;
        rotationAngle = 180f;
        updateRect();
    }

    public void lookRight() {
        direction = Direction.RIGHT;
        rotationAngle = 0f;
        updateRect();
    }

    public void lookUp() {
        direction = Direction.UP;
        rotationAngle = 270f;
        updateRect();
    }

    public void lookDown() {
        direction = Direction.DOWN;
        rotationAngle = 90f;
        updateRect();
    }

    public void move() {
        switch (direction) {
            case UP:
                y = y - Constants.blockSize;
                updateRect();
                break;
            case DOWN:
                y = y + Constants.blockSize;
                updateRect();
                break;
            case LEFT:
                x = x - Constants.blockSize;
                updateRect();
                break;
            case RIGHT:
                x = x + Constants.blockSize;
                updateRect();
                break;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
        updateRect();
    }

    public void setY(int y) {
        this.y = y;
        updateRect();
    }

}
