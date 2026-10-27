import java.util.Arrays;
import java.util.PriorityQueue;

public class Maze {

    public static int[] breakWalls(Input.Walls w) {
        DisjointSet ds = new DisjointSet(w.cells());
        int[] result = new int[w.cells() - 1];
        int count = 0;

        for (int i = 0; i < w.count() && count < result.length; i++) {
            if (ds.union(w.a()[i], w.b()[i])) {
                result[count++] = i;
            }
        }

        return Arrays.copyOf(result, count);
    }

    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];

        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }

        Arrays.sort(order, (i, j) -> Integer.compare(w.cost()[i], w.cost()[j]));

        DisjointSet ds = new DisjointSet(w.cells());
        int[] result = new int[w.cells() - 1];
        int count = 0;

        for (int i : order) {
            if (ds.union(w.a()[i], w.b()[i])) {
                result[count++] = i;

                if (count == result.length) {
                    break;
                }
            }
        }

        return Arrays.copyOf(result, count);
    }

    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int[][] adjacency = new int[n][];
        int[] degree = new int[n];

        for (int i = 0; i < w.count(); i++) {
            degree[w.a()[i]]++;
            degree[w.b()[i]]++;
        }

        for (int i = 0; i < n; i++) {
            adjacency[i] = new int[degree[i]];
        }

        Arrays.fill(degree, 0);

        for (int i = 0; i < w.count(); i++) {
            int a = w.a()[i];
            int b = w.b()[i];

            adjacency[a][degree[a]++] = i;
            adjacency[b][degree[b]++] = i;
        }

        boolean[] visited = new boolean[n];

        PriorityQueue<int[]> pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(a[0], b[0])
        );

        visited[0] = true;

        for (int edge : adjacency[0]) {
            int next = w.a()[edge] == 0 ? w.b()[edge] : w.a()[edge];
            pq.offer(new int[]{w.cost()[edge], next, edge});
        }

        long total = 0;
        int count = 1;

        while (!pq.isEmpty() && count < n) {
            int[] current = pq.poll();
            int cost = current[0];
            int cell = current[1];

            if (visited[cell]) {
                continue;
            }

            visited[cell] = true;
            total += cost;
            count++;

            for (int edge : adjacency[cell]) {
                int next = w.a()[edge] == cell
                        ? w.b()[edge]
                        : w.a()[edge];

                if (!visited[next]) {
                    pq.offer(new int[]{w.cost()[edge], next, edge});
                }
            }
        }

        return total;
    }
}