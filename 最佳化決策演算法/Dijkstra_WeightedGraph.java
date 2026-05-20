import java.util.*;

public class Dijkstra_WeightedGraph {
    static class Edge {
        String target; int weight;
        Edge(String t, int w) { target = t; weight = w; }
    }

    static class Node implements Comparable<Node> {
        String name; int distance;
        Node(String n, int d) { name = n; distance = d; }
        public int compareTo(Node o) { return Integer.compare(this.distance, o.distance); }
    }

    public static void runDijkstra(Map<String, List<Edge>> graph, String start) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Map<String, Integer> distances = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String v : graph.keySet()) distances.put(v, Integer.MAX_VALUE);
        distances.put(start, 0);
        pq.add(new Node(start, 0));

        System.out.println("--- [計算過程] Dijkstra 貪婪推進與鬆弛追蹤 ---");

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current.name)) continue;
            
            visited.add(current.name);
            System.out.println("\n🔒 [上鎖] 確定 [" + current.name + "] 的最短距離為: " + current.distance + " (基於貪婪法則，不再更改)");

            for (Edge edge : graph.getOrDefault(current.name, new ArrayList<>())) {
                if (visited.contains(edge.target)) continue;

                int oldDist = distances.get(edge.target);
                int newDist = distances.get(current.name) + edge.weight;
                String oldDistStr = (oldDist == Integer.MAX_VALUE) ? "∞" : String.valueOf(oldDist);

                System.out.print("  -> 評估鄰居 [" + edge.target + "]: 舊距離=" + oldDistStr + ", 經由 " + current.name + " 的新距離=" + current.distance + "+" + edge.weight + "=" + newDist);

                if (newDist < oldDist) {
                    distances.put(edge.target, newDist);
                    pq.add(new Node(edge.target, newDist));
                    System.out.println("  => ✨ 更新成功！");
                } else {
                    System.out.println("  => ❌ 不更新 (新距離更遠)。");
                }
            }
        }

        System.out.println("\n--- [最終結果] ---");
        for (Map.Entry<String, Integer> entry : distances.entrySet()) {
            System.out.println(start + " -> " + entry.getKey() + " : " + entry.getValue());
        }
        System.out.println("\n[時間複雜度] O((V + E) log V) - 依賴 Priority Queue 加速最小值檢索。");
    }

    public static void main(String[] args) {
        Map<String, List<Edge>> graph = new HashMap<>();
        graph.put("A", Arrays.asList(new Edge("B", 4), new Edge("C", 2)));
        graph.put("B", Arrays.asList(new Edge("D", 3)));
        graph.put("C", Arrays.asList(new Edge("B", 1), new Edge("D", 5)));
        graph.put("D", new ArrayList<>());

        runDijkstra(graph, "A");
    }
}