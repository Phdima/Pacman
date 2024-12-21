package com.example.pacman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.List;


public class GameView extends View {
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paint, paintWall, paintFood;
    private List<Rect> walls;
    private int FieldColor = ContextCompat.getColor(getContext(), R.color.purpleLight);
    private int colorWall = ContextCompat.getColor(getContext(), R.color.purpleDark);
    private GestureDetector gestureDetector;
    private Pacman pacman;
    private List<Ghost> ghosts;
    private FoodCircles foodCircles;
    private Cherry cherry;
    private GameLoop gameLoop;


    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmap = Bitmap.createBitmap(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        paint = new Paint();
        paintWall = new Paint();
        paintFood = new Paint();
        paintWall.setColor(colorWall);
        WallGenerator wallGenerator = new WallGenerator();
        walls = wallGenerator.generateWalls();
        drawGrid(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        gestureDetector = new GestureDetector(context, new GestureListener());
        pacman = new Pacman(context, 6, 13);
        createGhosts(context);
        foodCircles = new FoodCircles();
        cherry = new Cherry(context);
        setSettingsForGameObjects();
        foodCircles.generateFood();
        cherry.generatePositionFromFood();
        gameLoop = new GameLoop(this, pacman, ghosts, foodCircles, cherry);
        gameLoop.startLoop();
    }


    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (diffX > 0) {
                    pacman.lookRight();
                } else {
                    pacman.lookLeft();
                }
            } else {
                if (diffY > 0) {
                    pacman.lookDown();
                } else {
                    pacman.lookUp();
                }
            }
            return true;
        }
    }

    private void drawGrid(int screenWidth, int screenHeight) {
        for (int x = 0; x < screenWidth; x += Constants.BLOCKSIZE) {
            for (int y = 0; y < screenHeight; y += Constants.BLOCKSIZE) {
                paint.setColor(FieldColor);
                bitmapCanvas.drawRect(x, y, x + Constants.BLOCKSIZE, y + Constants.BLOCKSIZE, paint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        for (Rect wall : walls) {
            canvas.drawRect(wall, paintWall);
        }
        foodCircles.drawFood(canvas, paintFood);
        cherry.draw(canvas);
        for (Ghost ghost : ghosts) {
            ghost.draw(canvas);
        }
        pacman.draw(canvas);

    }

    private void setSettingsForGameObjects() {
        for (Ghost ghost : ghosts) ghost.setSettingsForGhosts(pacman, walls, ghosts);
        pacman.setWalls(walls);
        foodCircles.setGameObjects(pacman, ghosts, walls);
        cherry.setGameObjects(pacman,ghosts,foodCircles);
    }

    private void createGhosts(Context context) {
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(context, 3, 2, Color.RED));
        ghosts.add(new Ghost(context, 9, 2, Color.BLUE));
        ghosts.add(new Ghost(context, 3, 25, ContextCompat.getColor(getContext(), R.color.orange)));
        ghosts.add(new Ghost(context, 9, 25, Color.GREEN));

    }
}

