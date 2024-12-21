package com.example.pacman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ghost {
    private int ghostPositionX, ghostPositionY;
    private int ghostStartPositionX, ghostStartPositionY;
    private Rect ghostRect;
    private Drawable ghostDrawable;
    private int ghostColor;
    @SuppressLint("Range")
    private int scaredColor = Color.rgb(Color.RED, Color.GREEN, Color.BLUE);
    private Pacman pacman;
    private int pacmanPositionX, pacmanPositionY;
    private List<Rect> walls;
    private List<Ghost> ghosts;
    private boolean isScared;
    private boolean isVisible = true;
    private long scaredEndTime = 0;
    private static final int SCARED_DURATION = 10000;

    public Ghost(Context context, int ghostPositionX, int ghostPositionY, int ghostColor) {
        this.ghostPositionX = (ghostPositionX * Constants.BLOCKSIZE) + Constants.BLOCKSIZE / 2;
        this.ghostPositionY = (ghostPositionY * Constants.BLOCKSIZE) + Constants.BLOCKSIZE / 2;
        ghostStartPositionX = this.ghostPositionX;
        ghostStartPositionY = this.ghostPositionY;
        this.ghostColor = ghostColor;
        ghostDrawable = ContextCompat.getDrawable(context, R.drawable.ghost);
        updateRect();
    }

    private void updateRect() {
        ghostRect = new Rect(ghostPositionX - ghostDrawable.getIntrinsicHeight(), ghostPositionY - ghostDrawable.getIntrinsicWidth(), ghostPositionX + ghostDrawable.getIntrinsicHeight(), ghostPositionY + ghostDrawable.getIntrinsicWidth());
    }

    public Rect getRect() {
        return ghostRect;
    }

    public void draw(Canvas canvas) {
        if (!isVisible) return; // Не рисуем, если призрак невидим

        // Сохраняем оригинальные размеры призрака
        int left = ghostRect.left;
        int top = ghostRect.top;
        int right = ghostRect.right;
        int bottom = ghostRect.bottom;

        // Рисуем черную обводку
        ColorFilter outlineColorFilter = new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        ghostDrawable.setColorFilter(outlineColorFilter);

        // Увеличиваем размеры для обводки
        int outlineSize = 3; // Толщина обводки
        ghostDrawable.setBounds(left - outlineSize, top - outlineSize, right + outlineSize, bottom + outlineSize);
        ghostDrawable.draw(canvas);
        int currentColor = isScared ? scaredColor : ghostColor;
        // Рисуем основной цвет призрака поверх
        ColorFilter bodyColorFilter = new PorterDuffColorFilter(currentColor, PorterDuff.Mode.SRC_IN);
        ghostDrawable.setColorFilter(bodyColorFilter);
        ghostDrawable.setBounds(left, top, right, bottom);
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

    public void scare() {
        isScared = true;
        scaredEndTime = System.currentTimeMillis() + SCARED_DURATION;
    }

    public void pacmanEatGhost() {
        if (Rect.intersects(pacman.getRect(), getRect()) && canBeEaten()) {
            resetAfterEaten();
        }
    }

    public boolean canBeEaten() {
        return isScared;
    }

    public void resetAfterEaten() {
        isScared = false;
        isVisible = false; // Призрак становится невидимым

        // Устанавливаем задержку перед возрождением
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            // Возвращаем призрака на начальную позицию
            ghostPositionX = ghostStartPositionX;
            ghostPositionY = ghostStartPositionY;
            updateRect();
            isVisible = true; // Призрак снова становится видимым
        }, 3000); // Задержка в миллисекундах (3000 = 3 секунды)
    }


    public void updateGhostPosition() {
        updatePacmanPosition();
        if (isScared) {
            moveAwayFromPacman();
            if (System.currentTimeMillis() > scaredEndTime) {
                isScared = false;
            }
            return;
        }

        // Находим путь от текущей позиции призрака до позиции Пакмана
        List<int[]> path = findPath(
                ghostPositionX / Constants.BLOCKSIZE,
                ghostPositionY / Constants.BLOCKSIZE,
                pacmanPositionX / Constants.BLOCKSIZE,
                pacmanPositionY / Constants.BLOCKSIZE
        );

        // Если путь найден и есть следующий шаг
        if (path.size() > 1) {
            int nextX = path.get(1)[0] * Constants.BLOCKSIZE + Constants.BLOCKSIZE / 2;
            int nextY = path.get(1)[1] * Constants.BLOCKSIZE + Constants.BLOCKSIZE / 2;

            ghostPositionX = nextX;
            ghostPositionY = nextY;
            updateRect();
        }
    }

    private void moveAwayFromPacman() {
        int gridWidth = Constants.SCREEN_WIDTH / Constants.BLOCKSIZE;
        int gridHeight = Constants.SCREEN_HEIGHT / Constants.BLOCKSIZE;

        // Инициализация очереди для BFS
        List<int[]> queue = new ArrayList<>();
        queue.add(new int[]{ghostPositionX / Constants.BLOCKSIZE, ghostPositionY / Constants.BLOCKSIZE});

        // Массив для отслеживания посещенных клеток
        boolean[][] visited = new boolean[gridWidth][gridHeight];
        visited[ghostPositionX / Constants.BLOCKSIZE][ghostPositionY / Constants.BLOCKSIZE] = true;

        // Хранение последней допустимой клетки
        int[] farthestCell = null;

        // Направления движения (вверх, вниз, влево, вправо)
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        // BFS
        while (!queue.isEmpty()) {
            int[] current = queue.remove(0);
            farthestCell = current; // Обновляем последнюю допустимую клетку

            for (int[] dir : directions) {
                int neighborX = current[0] + dir[0];
                int neighborY = current[1] + dir[1];

                // Проверяем границы карты
                if (neighborX < 0 || neighborY < 0 || neighborX >= gridWidth || neighborY >= gridHeight) {
                    continue;
                }

                // Пропускаем посещенные клетки
                if (visited[neighborX][neighborY]) continue;

                // Пропускаем стены и других призраков
                if (!canMoveTo(neighborX, neighborY)) continue;

                // Добавляем клетку в очередь
                queue.add(new int[]{neighborX, neighborY});
                visited[neighborX][neighborY] = true;
            }
        }

        // Если путь найден
        if (farthestCell != null) {
            // Найти путь к самой дальней клетке
            List<int[]> path = findPath(
                    ghostPositionX / Constants.BLOCKSIZE,
                    ghostPositionY / Constants.BLOCKSIZE,
                    farthestCell[0],
                    farthestCell[1]
            );

            // Двигаться к следующей клетке на пути
            if (path.size() > 1) {
                int[] nextCell = path.get(1);
                int nextX = nextCell[0] * Constants.BLOCKSIZE + Constants.BLOCKSIZE / 2;
                int nextY = nextCell[1] * Constants.BLOCKSIZE + Constants.BLOCKSIZE / 2;

                // Перемещаемся только в одном направлении (по горизонтали или вертикали)
                if (ghostPositionX != nextX) {
                    ghostPositionX += (nextX > ghostPositionX) ? Constants.BLOCKSIZE / 2 : -Constants.BLOCKSIZE / 2;
                } else if (ghostPositionY != nextY) {
                    ghostPositionY += (nextY > ghostPositionY) ? Constants.BLOCKSIZE / 2 : -Constants.BLOCKSIZE / 2;
                }

                updateRect();
            }
        }
    }


    private boolean canMoveTo(int gridX, int gridY) {
        int pixelX = gridX * Constants.BLOCKSIZE + Constants.BLOCKSIZE / 2;
        int pixelY = gridY * Constants.BLOCKSIZE + Constants.BLOCKSIZE / 2;

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

