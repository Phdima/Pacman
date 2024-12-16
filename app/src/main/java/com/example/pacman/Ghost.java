package com.example.pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ghost {
    private int ghostPositionX, ghostPositionY;
    private Rect ghostRect;
    private Drawable ghostDrawable;
    private int ghostColor;
    private Pacman pacman;
    private int pacmanPositionX, pacmanPositionY;
    private List<Rect> walls;
    private List<Ghost> ghosts;

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

    public void setSettingsForGhosts(Pacman pacman, List<Rect> walls, List<Ghost> ghosts) {
        setFollowToPacman(pacman);
        setRectsForGhosts(walls, ghosts);
    }

    private void setRectsForGhosts(List<Rect> walls, List<Ghost> ghosts) {
        this.walls = walls;
        this.ghosts = ghosts;
    }

    private void setFollowToPacman(Pacman pacman) {
        this.pacman = pacman;
        updatePacmanPosition();
    }


    private void updatePacmanPosition() {
        this.pacmanPositionX = pacman.getX();
        this.pacmanPositionY = pacman.getY();
    }

    public void updateGhostPosition() {
        updatePacmanPosition();

        // Находим путь от текущей позиции призрака до позиции Пакмана
        List<int[]> path = findPath(
                ghostPositionX / Constants.blockSize,
                ghostPositionY / Constants.blockSize,
                pacmanPositionX / Constants.blockSize,
                pacmanPositionY / Constants.blockSize
        );

        // Если путь найден и есть следующий шаг
        if (path.size() > 1) {
            int nextX = path.get(1)[0] * Constants.blockSize + Constants.blockSize / 2;
            int nextY = path.get(1)[1] * Constants.blockSize + Constants.blockSize / 2;

            ghostPositionX = nextX;
            ghostPositionY = nextY;
            updateRect();
        }
    }


    private boolean canMoveTo(int gridX, int gridY) {
        int pixelX = gridX * Constants.blockSize + Constants.blockSize / 2;
        int pixelY = gridY * Constants.blockSize + Constants.blockSize / 2;

        Rect newRect = new Rect(
                pixelX - ghostDrawable.getIntrinsicHeight() / 2,
                pixelY - ghostDrawable.getIntrinsicWidth() / 2,
                pixelX + ghostDrawable.getIntrinsicHeight() / 2,
                pixelY + ghostDrawable.getIntrinsicWidth() / 2
        );

        for (Rect wall : walls) {
            if (Rect.intersects(newRect, wall)) {
                return false;
            }
        }
        for (Ghost ghost : ghosts) {
            if (ghost != this && Rect.intersects(newRect, ghost.ghostRect)) {
                return false;
            }
        }
        return true;
    }


    private List<int[]> findPath(int startX, int startY, int targetX, int targetY) {
        List<Node> openList = new ArrayList<>(); // Узлы, которые нужно обработать
        List<Node> closedList = new ArrayList<>(); // Узлы, которые уже обработаны

        Node startNode = new Node(startX, startY);
        Node targetNode = new Node(targetX, targetY);
        openList.add(startNode);

        while (!openList.isEmpty()) {
            // Сортируем по F стоимости (сумма G и H) и берём узел с минимальным F
            Collections.sort(openList, Comparator.comparingInt(Node::getFCost));
            Node currentNode = openList.remove(0);
            closedList.add(currentNode);

            // Если достигли цели — строим путь
            if (currentNode.equals(targetNode)) {
                return reconstructPath(currentNode);
            }

            // Проверяем соседние узлы (все 4 направления)
            for (int[] dir : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int neighborX = currentNode.x + dir[0];
                int neighborY = currentNode.y + dir[1];

                // Пропускаем стены и границы
                if (!canMoveTo(neighborX, neighborY)) continue;

                Node neighbor = new Node(neighborX, neighborY);

                // Пропускаем уже обработанные узлы
                if (closedList.contains(neighbor)) continue;

                int newGCost = currentNode.gCost + 1;

                // Если узел не в открытом списке или новый путь лучше
                if (newGCost < neighbor.gCost || !openList.contains(neighbor)) {
                    neighbor.gCost = newGCost;
                    neighbor.hCost = Math.abs(neighborX - targetX) + Math.abs(neighborY - targetY);
                    neighbor.parent = currentNode;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
                System.out.println("Current Node: " + currentNode.x + ", " + currentNode.y);

            }
        }
        System.out.println("Start: " + startX + ", " + startY + " -> Target: " + targetX + ", " + targetY);

        return Collections.emptyList(); // Если путь не найден
    }

    private List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(new int[]{node.x, node.y});
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }


}

class Node {
    int x, y; // Координаты узла на сетке
    int gCost, hCost; // gCost - стоимость от старта, hCost - эвристическая стоимость до цели
    Node parent; // Родительский узел, нужен для восстановления пути

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getFCost() {
        return gCost + hCost; // F = G + H
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }
}

