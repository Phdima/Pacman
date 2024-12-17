package com.example.pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Random;

public class Cherry {
    private int x, y;
    private boolean isVisible;
    private Drawable cherryDrawable;
    private Pacman pacman;
    private List<Ghost> ghosts;
    private FoodCircles foodCircles;
    private int timerForSpawn = 0;
    private static final int flagForSpawn = 5;


    public Cherry(Context context) {
        cherryDrawable = ContextCompat.getDrawable(context, R.drawable.cherry);
        isVisible = false;
    }

    public void setGameObjects(Pacman pacman,List<Ghost> ghosts, FoodCircles foodCircles) {
        this.pacman = pacman;
        this.ghosts = ghosts;
        this.foodCircles = foodCircles;
    }

    public void generatePositionFromFood() {
        List<int[]> foodPositions = foodCircles.getFoodPositions();
        if (foodPositions.isEmpty()) {
            isVisible = false;
            return;
        }
        Random random = new Random();
        int index = random.nextInt(foodPositions.size());
        int[] foodPosition = foodPositions.remove(index);
        x = foodPosition[0];
        y = foodPosition[1];
        isVisible = true;
    }

    public void draw(Canvas canvas) {
        if (isVisible && cherryDrawable != null) {
            cherryDrawable.setBounds(
                    x - Constants.BLOCKSIZE / 2,
                    y - Constants.BLOCKSIZE / 2,
                    x + Constants.BLOCKSIZE / 2,
                    y + Constants.BLOCKSIZE / 2
            );
            cherryDrawable.draw(canvas);
        }
    }

    public void CherryCollisionWithPacman() {
        if (isVisible) {
            Rect cherryRect = new Rect(
                    x - Constants.BLOCKSIZE / 2,
                    y - Constants.BLOCKSIZE / 2,
                    x + Constants.BLOCKSIZE / 2,
                    y + Constants.BLOCKSIZE / 2
            );
            if (Rect.intersects(pacman.getRect(), cherryRect)) {
                isVisible = false;
                for (Ghost ghost : ghosts) {
                    ghost.scare();
                }

            }
        }
    }

    public void spawnCherrySometimes() {
        if (!isVisible) {
            timerForSpawn++;
            if (timerForSpawn >= flagForSpawn) {
                timerForSpawn = 0;
                generatePositionFromFood();
            }
        }
    }
}
