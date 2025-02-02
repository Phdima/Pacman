package com.example.pacman;

import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.util.List;


public class GameLoop {
    private Handler handler;
    private Pacman pacman;
    private List<Ghost> ghosts;
    private FoodCircles foodCircles;
    private Cherry cherry;
    private GameView gameView;



    public GameLoop(GameView gameView, Pacman pacman, List<Ghost> ghosts, FoodCircles foodCircles, Cherry cherry) {
        this.gameView = gameView;
        this.pacman = pacman;
        this.ghosts = ghosts;
        this.foodCircles = foodCircles;
        this.cherry = cherry;
        handler = new Handler(Looper.getMainLooper());
    }


    public void startLoop() {
        Runnable gameLoop = new Runnable() {
            @Override
            public void run() {
                updateGameObjects();
                gameView.postInvalidate();
                handler.postDelayed(this, 1000);

            }
        };
        handler.post(gameLoop);
    }



    private void updateGameObjects() {
        for (Ghost ghost : ghosts) {
            ghost.updateGhostPosition();
        }
        cherry.spawnCherrySometimes();
        pacman.move();
        foodCircles.foodCollisionWithPacman();
        cherry.CherryCollisionWithPacman();
        for (Ghost ghost : ghosts) {
            ghost.pacmanEatGhost();
        }
    }


}
