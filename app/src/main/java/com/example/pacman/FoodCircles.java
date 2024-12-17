package com.example.pacman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FoodCircles {

    private Pacman pacman;
    private List<Ghost> ghosts;
    private List<Rect> walls;
    private List<int[]> foodPositions = new ArrayList<>();
    private Iterator<int[]> iterator;

    public void setGameObjects(Pacman pacman, List<Ghost> ghosts, List<Rect> walls) {
        this.pacman = pacman;
        this.ghosts = ghosts;
        this.walls = walls;
    }

    public void generateFood() {
        for (int row = 0; row < Constants.SCREEN_HEIGHT / Constants.BLOCKSIZE; row++) {
            for (int col = 0; col < Constants.SCREEN_WIDTH / Constants.BLOCKSIZE; col++) {
                if (canPlaceFood(col, row)) {
                    foodPositions.add(new int[]{
                            col * Constants.BLOCKSIZE + Constants.BLOCKSIZE / 2,
                            row * Constants.BLOCKSIZE + Constants.BLOCKSIZE / 2
                    });
                }
            }
        }


    }

    private boolean canPlaceFood(int col, int row) {
        Rect foodRect = new Rect(
                col * Constants.BLOCKSIZE,
                row * Constants.BLOCKSIZE,
                (col + 1) * Constants.BLOCKSIZE,
                (row + 1) * Constants.BLOCKSIZE
        );
        for (Rect wall : walls) {
            if (Rect.intersects(foodRect, wall)) {
                return false;
            }
        }

        if (Rect.intersects(foodRect, pacman.getRect())) {
            return false;
        }

        for (Ghost ghost : ghosts) {
            if (Rect.intersects(foodRect, ghost.getRect())) {
                return false;
            }
        }

        return true;
    }

    public void drawFood(Canvas canvas, Paint paint) {
        for (int[] position : foodPositions) {
            paint.setColor(Color.BLACK);
            canvas.drawCircle(position[0], position[1], (float) Constants.BLOCKSIZE / 8 + 2, paint);
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(position[0], position[1], (float) Constants.BLOCKSIZE / 8, paint);
        }
    }

    public void foodCollisionWithPacman() {
        Rect pacmanRect = pacman.getRect();
        iterator = foodPositions.iterator();
        while (iterator.hasNext()) {
            int[] food = iterator.next();
            Rect foodRect = new Rect(
                    food[0] - Constants.BLOCKSIZE / 4,
                    food[1] - Constants.BLOCKSIZE / 4,
                    food[0] + Constants.BLOCKSIZE / 4,
                    food[1] + Constants.BLOCKSIZE / 4
            );

            if (Rect.intersects(pacmanRect, foodRect)) {
                removeFood();
            }
        }
    }

    public void removeFood() {
        iterator.remove();
    }

    public List<int[]> getFoodPositions() {
        return foodPositions;
    }

}
