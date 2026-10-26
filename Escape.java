/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
import java.util.Arrays;


public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int[][] graph = Graphs.open(w, broken);

        int n = w.cells();
        int cols = w.cols();
        int exit = n - 1;

        int deadEnds = 0;

        for (int cell = 0; cell < n; cell++) {
            if (graph[cell].length == 1) {
                deadEnds++;
            }
        }

        boolean[] seen = new boolean[n];

        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int[] stack = new int[n];
        int[] nextDirection = new int[n];

        int top = 0;
        stack[0] = 0;
        seen[0] = true;

        int visited = 1;

        while (top >= 0) {
            int cell = stack[top];

            if (cell == exit) {
                break;
            }

            if (nextDirection[cell] == 4) {
                top--;
                continue;
            }

            int direction = nextDirection[cell]++;
            int next = -1;

            switch (direction) {
                case 0 -> {
                    if (cell >= cols) {
                        next = cell - cols;
                    }
                }
                case 1 -> {
                    if (cell % cols < cols - 1) {
                        next = cell + 1;
                    }
                }
                case 2 -> {
                    if (cell + cols < n) {
                        next = cell + cols;
                    }
                }
                case 3 -> {
                    if (cell % cols > 0) {
                        next = cell - 1;
                    }
                }
            }

            if (next == -1 || seen[next]) {
                continue;
            }

            boolean open = false;

            for (int neighbour : graph[cell]) {
                if (neighbour == next) {
                    open = true;
                    break;
                }
            }

            if (!open) {
                continue;
            }

            parent[next] = cell;
            seen[next] = true;
            visited++;

            stack[++top] = next;
        }

        if (!seen[exit]) {
            return new Result(new int[0], visited, deadEnds);
        }

        int length = 0;

        for (int cell = exit; cell != -1; cell = parent[cell]) {
            length++;
        }

        int[] path = new int[length];
        int cell = exit;

        for (int i = length - 1; i >= 0; i--) {
            path[i] = cell;
            cell = parent[cell];
        }

        return new Result(path, visited, deadEnds);
    }
}
