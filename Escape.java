import java.util.Arrays;

public class Escape {

    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int cols = w.cols();
        int[][] graph = Graphs.open(w, broken);

        int deadEnds = 0;

        for (int[] neighbours : graph) {
            if (neighbours.length == 1) {
                deadEnds++;
            }
        }

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        int[] stack = new int[n];
        int[] nextIndex = new int[n];

        Arrays.fill(parent, -1);

        int top = 0;
        int visitedCount = 1;
        stack[0] = 0;
        visited[0] = true;

        while (top >= 0) {
            int current = stack[top];

            if (current == n - 1) {
                break;
            }

            int[] candidates = {
                    current - cols,
                    current + 1,
                    current + cols,
                    current - 1
            };

            boolean moved = false;

            while (nextIndex[top] < 4) {
                int next = candidates[nextIndex[top]++];

                if (next < 0 || next >= n) {
                    continue;
                }

                if ((next == current + 1 || next == current - 1)
                        && next / cols != current / cols) {
                    continue;
                }

                if (visited[next]) {
                    continue;
                }

                boolean open = false;

                for (int neighbour : graph[current]) {
                    if (neighbour == next) {
                        open = true;
                        break;
                    }
                }

                if (!open) {
                    continue;
                }

                visited[next] = true;
                visitedCount++;
                parent[next] = current;
                stack[++top] = next;
                nextIndex[top] = 0;
                moved = true;
                break;
            }

            if (!moved && nextIndex[top] >= 4) {
                top--;
            }
        }

        if (!visited[n - 1]) {
            return new Result(new int[0], visitedCount, deadEnds);
        }

        int length = 0;

        for (int v = n - 1; v != -1; v = parent[v]) {
            length++;
        }

        int[] path = new int[length];
        int v = n - 1;

        for (int i = length - 1; i >= 0; i--) {
            path[i] = v;
            v = parent[v];
        }

        return new Result(path, visitedCount, deadEnds);
    }
}