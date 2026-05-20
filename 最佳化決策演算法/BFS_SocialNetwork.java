import java.util.*;

public class BFS_SocialNetwork {

    public static void findShortestPathBFS(Map<String, List<String>> graph, String start, String target) {
        if (!graph.containsKey(start) || !graph.containsKey(target)) return;

        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();

        queue.add(start);
        distances.put(start, 0);
        parentMap.put(start, null);
        boolean found = false;

        System.out.println("--- [計算過程] BFS 擴展追蹤 ---");
        int stepCount = 1;

        while (!queue.isEmpty()) {
            String current = queue.poll();
            System.out.println("步驟 " + stepCount++ + ": 從 Queue 取出 [" + current + "] (目前距離: " + distances.get(current) + ")");

            if (current.equals(target)) {
                System.out.println("  => 🎯 找到目標 [" + target + "]，提早結束 BFS！");
                found = true;
                break;
            }

            for (String neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                if (!distances.containsKey(neighbor)) {
                    distances.put(neighbor, distances.get(current) + 1);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                    System.out.println("  -> 發現未走訪鄰居 [" + neighbor + "]，記錄距離為 " + distances.get(neighbor) + "，並加入 Queue 等待探索。");
                }
            }
        }

        if (found) {
            System.out.println("\n--- [最終結果] ---");
            System.out.println("最短距離: " + distances.get(target) + " 步。");
            List<String> path = new ArrayList<>();
            String step = target;
            while (step != null) {
                path.add(step);
                step = parentMap.get(step);
            }
            Collections.reverse(path);
            System.out.println("最短路徑: " + String.join(" -> ", path));
        }
    }

    public static void main(String[] args) {
        Map<String, List<String>> graph = new HashMap<>();
        addEdge(graph, "Sunny", "Amy");
        addEdge(graph, "Sunny", "James");
        addEdge(graph, "Amy", "Cara");
        addEdge(graph, "Amy", "Marshall");
        addEdge(graph, "James", "John");
        addEdge(graph, "Cara", "Bella");
        addEdge(graph, "Marshall", "Eric");
        addEdge(graph, "John", "Eric");
        addEdge(graph, "Bella", "Eric");

        findShortestPathBFS(graph, "Sunny", "Eric");

        System.out.println("\n[時間複雜度] O(V + E) - 每個節點與每條邊皆被走訪一次。");
    }

    private static void addEdge(Map<String, List<String>> graph, String u, String v) {
        graph.putIfAbsent(u, new ArrayList<>());
        graph.putIfAbsent(v, new ArrayList<>());
        graph.get(u).add(v);
        graph.get(v).add(u);
    }
}