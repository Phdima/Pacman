package com.example.pacman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.List;


public class GameView extends View {
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paint, paintWall;
    private List<Rect> walls;
    private int FieldColor = ContextCompat.getColor(getContext(), R.color.purpleLight);
    private int colorWall = ContextCompat.getColor(getContext(), R.color.purpleDark);
    private GestureDetector gestureDetector;
    private Pacman pacman;
    private List<Ghost> ghosts;

    private GameLoop gameLoop;


    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmap = Bitmap.createBitmap(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        paint = new Paint();
        paintWall = new Paint();
        paintWall.setColor(colorWall);
        WallGenerator wallGenerator = new WallGenerator();
        walls = wallGenerator.generateWalls(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Constants.blockSize);
        drawGrid(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        gestureDetector = new GestureDetector(context, new GestureListener());
        pacman = new Pacman(context, 6, 13);
        createGhosts(context);
        for (Ghost ghost : ghosts) ghost.setFollowToPacman(pacman);
        gameLoop = new GameLoop(this, pacman, ghosts);
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
        for (int x = 0; x < screenWidth; x += Constants.blockSize) {
            for (int y = 0; y < screenHeight; y += Constants.blockSize) {
                paint.setColor(FieldColor);
                bitmapCanvas.drawRect(x, y, x + Constants.blockSize, y + Constants.blockSize, paint);
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

        for (Ghost ghost : ghosts) {
            ghost.draw(canvas);
        }
        pacman.draw(canvas);

    }

    public List<Ghost> createGhosts(Context context) {
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(context, 3, 2, Color.RED));
        ghosts.add(new Ghost(context, 9, 2, Color.BLUE));
        ghosts.add(new Ghost(context, 3, 25, Color.YELLOW));
        ghosts.add(new Ghost(context, 9, 25, Color.GREEN));

        return ghosts;
    }
}

