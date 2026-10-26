/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
import java.util.Arrays;

public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();

        int[][] outgoing = Graphs.slidesFrom(s);
        int[][] incoming = Graphs.slidesInto(s);

        boolean[] seen = new boolean[n];
        int[] nextEdge = new int[n];
        int[] stack = new int[n];

        int[] finishOrder = new int[n];
        int finished = 0;

        // Первый проход: исходный граф, порядок завершения DFS.
        for (int start = 0; start < n; start++) {
            if (seen[start]) {
                continue;
            }

            int top = 0;
            stack[top] = start;
            seen[start] = true;

            while (top >= 0) {
                int cell = stack[top];

                if (nextEdge[cell] < outgoing[cell].length) {
                    int slide = outgoing[cell][nextEdge[cell]++];
                    int next = s.to()[slide];

                    if (!seen[next]) {
                        seen[next] = true;
                        stack[++top] = next;
                    }
                } else {
                    finishOrder[finished++] = cell;
                    top--;
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);

        int zone = 0;

        // Второй проход: обратный граф,
        // от последней завершённой клетки к первой.
        for (int i = finished - 1; i >= 0; i--) {
            int start = finishOrder[i];

            if (zoneOf[start] != -1) {
                continue;
            }

            int top = 0;
            stack[top] = start;
            zoneOf[start] = zone;

            while (top >= 0) {
                int cell = stack[top--];

                for (int slide : incoming[cell]) {
                    int next = s.from()[slide];

                    if (zoneOf[next] == -1) {
                        zoneOf[next] = zone;
                        stack[++top] = next;
                    }
                }
            }

            zone++;
        }

        return zoneOf;
    }

    // Mission 5: динамическое программирование по графу зон.
    public static long[] routes(
            Input.Slides s,
            int[] zoneOf,
            int zoneCount
    ) {
        // Группируем межзонные горки по исходной зоне.
        int[] degree = new int[zoneCount];

        for (int i = 0; i < s.count(); i++) {
            int fromZone = zoneOf[s.from()[i]];
            int toZone = zoneOf[s.to()[i]];

            if (fromZone != toZone) {
                degree[fromZone]++;
            }
        }

        int[][] outgoing = new int[zoneCount][];

        for (int zone = 0; zone < zoneCount; zone++) {
            outgoing[zone] = new int[degree[zone]];
        }

        int[] filled = new int[zoneCount];

        for (int i = 0; i < s.count(); i++) {
            int fromZone = zoneOf[s.from()[i]];
            int toZone = zoneOf[s.to()[i]];

            if (fromZone != toZone) {
                outgoing[fromZone][filled[fromZone]++] = i;
            }
        }

        // stages[z]: максимальное число зон в цепочке,
        // заканчивающейся в z.
        int[] stages = new int[zoneCount];
        Arrays.fill(stages, 1);

        // distance[z]: минимальная стоимость от зоны старта.
        long[] distance = new long[zoneCount];
        Arrays.fill(distance, Long.MAX_VALUE);

        int startZone = zoneOf[0];
        int exitZone = zoneOf[s.n() - 1];

        distance[startZone] = 0;

        int flowStages = 1;

        // Номера зон уже образуют топологический порядок.
        for (int zone = 0; zone < zoneCount; zone++) {
            for (int slide : outgoing[zone]) {
                int nextZone = zoneOf[s.to()[slide]];

                stages[nextZone] = Math.max(
                        stages[nextZone],
                        stages[zone] + 1
                );

                if (distance[zone] != Long.MAX_VALUE) {
                    long candidate =
                            distance[zone] + s.time()[slide];

                    distance[nextZone] = Math.min(
                            distance[nextZone],
                            candidate
                    );
                }
            }

            flowStages = Math.max(flowStages, stages[zone]);
        }

        return new long[]{flowStages, distance[exitZone]};
    }


    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */

}
