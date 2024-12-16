package com.example.pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

public class Ghost {
    int ghostPositionX, ghostPositionY;
    private Rect ghostRect;
    private Drawable ghostDrawable;
    private int ghostColor;
    private Pacman pacman;
    private int pacmanPositionX, pacmanPositionY;

    public Ghost(Context context, int x, int ghostPositionY, int ghostColor) {
        this.ghostPositionX = (x * Constants.blockSize) + Constants.blockSize / 2;
        this.ghostPositionY = (ghostPositionY * Constants.blockSize) + Constants.blockSize / 2;
        this.ghostColor = ghostColor;
        ghostDrawable = ContextCompat.getDrawable(context, R.drawable.ghost);
        updateRect();
    }

    private void updateRect() {
        ghostRect = new Rect(ghostPositionX - ghostDrawable.getIntrinsicHeight(), ghostPositionY - ghostDrawable.getIntrinsicWidth(), ghostPositionX + ghostDrawable.getIntrinsicHeight(), ghostPositionY + ghostDrawable.getIntrinsicWidth());
    }

    public void draw(Canvas canvas) {
        ColorFilter colorFilter = new PorterDuffColorFilter(ghostColor, PorterDuff.Mode.SRC_IN);
        ghostDrawable.setColorFilter(colorFilter);
        ghostDrawable.setBounds(ghostRect.left, ghostRect.top, ghostRect.right, ghostRect.bottom);
        ghostDrawable.draw(canvas);
    }

    public void setFollowToPacman(Pacman pacman) {
        this.pacman = pacman;
        updatePacmanPosition();
    }

    private void updatePacmanPosition() {
        this.pacmanPositionX = pacman.getX();
        this.pacmanPositionY = pacman.getY();
    }

    public void updateGhostPosition() {
        if (ghostPositionX < pacmanPositionX){
            ghostPositionX = ghostPositionX + Constants.blockSize;
            updateRect();
        }  else if (ghostPositionX > pacmanPositionX) {
            ghostPositionX = ghostPositionX - Constants.blockSize;
            updateRect();
        }
        if (ghostPositionY < pacmanPositionY) {
            ghostPositionY = ghostPositionY + Constants.blockSize;
            updateRect();
        } else if (ghostPositionY > pacmanPositionY) {
            ghostPositionY = ghostPositionY - Constants.blockSize;
            updateRect();
        }
    }
}
