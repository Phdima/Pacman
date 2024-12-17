package com.example.pacman;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class WallGenerator {
    public List<Rect> generateWalls() {
        List<Rect> walls = new ArrayList<>();
        int[][] wallCoordinates = {
                //spawnPlace Ghost 1
                {2, 2, 1, 2},
                {3, 3, 1, 1},
                {4, 2, 1, 2},

                //spawnPlace Ghost 2
                {8, 2, 1, 2},
                {9, 3, 1, 1},
                {10, 2, 1, 2},

                //spawnPlace Ghost 3
                {2, 24, 1, 2},
                {3, 24, 1, 1},
                {4, 24, 1, 2},

                //spawnPlace Ghost 4
                {8, 24, 1, 2},
                {9, 24, 1, 1},
                {10, 24, 1, 2},

                //spawnPlace Pacman
                {5, 12, 1, 1},
                {5, 14, 1, 1},
                {7, 12, 1, 1},
                {7, 14, 1, 1},

                //anyWalls
                {3, 11, 1, 2},
                {3, 14, 1, 2},
                {9, 11, 1, 2},
                {9, 14, 1, 2},
                {2, 17, 4, 1},
                {7, 17, 4, 1},
                {2, 19, 1, 3},
                {4, 19, 1, 3},
                {6, 19, 1, 5},
                {8, 19, 1, 3},
                {10, 19, 1, 3},
                {2, 9, 4, 1},
                {7, 9, 4, 1},
                {8, 7, 2, 1},
                {7, 5, 4, 1},
                {3, 7, 2, 1},
                {2, 5, 4, 1},
                {6,2,1,2}

        };
        for (int[] coords : wallCoordinates) {
            int x = coords[0] * Constants.BLOCKSIZE;
            int y = coords[1] * Constants.BLOCKSIZE;
            int width = coords[2] * Constants.BLOCKSIZE;
            int height = coords[3] * Constants.BLOCKSIZE;

            walls.add(new Rect(x, y, x + width, y + height));
        }
        //горизонтальные по краям стены
        for (int x = 0; x < Constants.SCREEN_WIDTH; x += Constants.BLOCKSIZE) {
            walls.add(new Rect(x, 0, x + Constants.BLOCKSIZE, Constants.BLOCKSIZE));
            walls.add(new Rect(x, Constants.SCREEN_HEIGHT - Constants.BLOCKSIZE, x + Constants.BLOCKSIZE, Constants.SCREEN_HEIGHT));
        }
        //вертикальные по краям стены
        walls.add(new Rect(0, 0, Constants.BLOCKSIZE, Constants.SCREEN_HEIGHT));
        walls.add(new Rect(Constants.SCREEN_WIDTH - Constants.BLOCKSIZE, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));

        return walls;
    }


}
