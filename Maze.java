/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());

        int[] broken = new int[w.cells() - 1];
        int count = 0;

        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count++] = k;
            }
        }

        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];

        for (int k = 0; k < w.count(); k++) {
            order[k] = k;
        }

        Arrays.sort(
                order,
                Comparator.comparingInt(k -> w.cost()[k])
        );

        DisjointSet dsu = new DisjointSet(w.cells());

        int[] broken = new int[w.cells() - 1];
        int count = 0;

        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count++] = k;

                if (count == w.cells() - 1) {
                    break;
                }
            }
        }

        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();

        // Для каждой клетки храним индексы прилегающих стен.
        int[] degree = new int[n];

        for (int k = 0; k < w.count(); k++) {
            degree[w.a()[k]]++;
            degree[w.b()[k]]++;
        }

        int[][] incident = new int[n][];

        for (int cell = 0; cell < n; cell++) {
            incident[cell] = new int[degree[cell]];
        }

        int[] filled = new int[n];

        for (int k = 0; k < w.count(); k++) {
            int a = w.a()[k];
            int b = w.b()[k];

            incident[a][filled[a]++] = k;
            incident[b][filled[b]++] = k;
        }

        PriorityQueue<Integer> queue = new PriorityQueue<>(
                Comparator.comparingInt(k -> w.cost()[k])
        );

        boolean[] inTree = new boolean[n];
        inTree[0] = true;

        int added = 1;
        long total = 0;

        for (int k : incident[0]) {
            queue.offer(k);
        }

        while (!queue.isEmpty() && added < n) {
            int k = queue.poll();

            int a = w.a()[k];
            int b = w.b()[k];

            // Оба конца уже в дереве — эта стена не нужна.
            if (inTree[a] && inTree[b]) {
                continue;
            }

            int next = inTree[a] ? b : a;

            inTree[next] = true;
            added++;
            total += w.cost()[k];

            for (int edge : incident[next]) {
                int other = w.a()[edge] == next
                        ? w.b()[edge]
                        : w.a()[edge];

                if (!inTree[other]) {
                    queue.offer(edge);
                }
            }
        }

        if (added != n) {
            throw new IllegalStateException("The wall graph is disconnected");
        }

        return total;
    }
}
