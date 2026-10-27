import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Zones {

    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] graph = Graphs.slidesFrom(s);
        int[][] reverse = Graphs.slidesInto(s);

        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int orderSize = 0;

        int[] stack = new int[n];
        int[] nextIndex = new int[n];

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            int top = 0;
            stack[0] = start;
            nextIndex[0] = 0;
            visited[start] = true;

            while (top >= 0) {
                int u = stack[top];

                if (nextIndex[top] < graph[u].length) {
                    int edge = graph[u][nextIndex[top]++];
                    int v = s.to()[edge];

                    if (!visited[v]) {
                        visited[v] = true;
                        stack[++top] = v;
                        nextIndex[top] = 0;
                    }
                } else {
                    order[orderSize++] = u;
                    top--;
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);

        int zone = 0;
        int[] work = new int[n];

        for (int i = orderSize - 1; i >= 0; i--) {
            int start = order[i];

            if (zoneOf[start] != -1) {
                continue;
            }

            int top = 0;
            work[top++] = start;
            zoneOf[start] = zone;

            while (top > 0) {
                int u = work[--top];

                for (int edge : reverse[u]) {
                    int v = s.from()[edge];

                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zone;
                        work[top++] = v;
                    }
                }
            }

            zone++;
        }

        return zoneOf;
    }

    public static long[] routes(
            Input.Slides s,
            int[] zoneOf,
            int zoneCount
    ) {
        int n = s.n();
        int[] longest = new int[zoneCount];
        long[] distance = new long[zoneCount];

        Arrays.fill(distance, Long.MAX_VALUE);

        int start = zoneOf[0];
        int exit = zoneOf[n - 1];

        longest[start] = 1;
        distance[start] = 0;

        long[] best = new long[zoneCount * zoneCount];
        Arrays.fill(best, Long.MAX_VALUE);

        for (int i = 0; i < s.count(); i++) {
            int from = zoneOf[s.from()[i]];
            int to = zoneOf[s.to()[i]];

            if (from != to) {
                int index = from * zoneCount + to;
                best[index] = Math.min(best[index], s.time()[i]);
            }
        }

        for (int u = 0; u < zoneCount; u++) {
            for (int v = 0; v < zoneCount; v++) {
                long cost = best[u * zoneCount + v];

                if (cost == Long.MAX_VALUE) {
                    continue;
                }

                if (longest[u] > 0) {
                    longest[v] = Math.max(longest[v], longest[u] + 1);
                }

                if (distance[u] != Long.MAX_VALUE) {
                    distance[v] = Math.min(distance[v], distance[u] + cost);
                }
            }
        }

        return new long[]{
                Arrays.stream(longest).max().orElse(0),
                distance[exit] == Long.MAX_VALUE ? 0 : distance[exit]
        };
    }
}