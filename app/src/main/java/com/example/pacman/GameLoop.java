package com.example.pacman;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


public class GameLoop {
    private Handler handler;
    private Pacman pacman;

    private GameView gameView;

    public GameLoop(GameView gameView,Pacman pacman) {
        this.gameView = gameView;
        this.pacman = pacman;
        handler = new Handler(Looper.getMainLooper());
    }


    public void start() {
        Runnable gameLoop = new Runnable() {
            @Override
            public void run() {
                updateGameObjects();
                gameView.postInvalidate();
                handler.postDelayed(this, 1000);
                Log.d("GameView", "updateAndInvalidate() called - updating game objects and invalidating screen");
            }
        };
        handler.post(gameLoop);
    }


    private void updateGameObjects() {
        pacman.move();

    }


}
