import java.util.*;

public class BellmanFord_NegativeWeight {
    static class Edge {
        String source, target; int weight;
        Edge(String s, String t, int w) { source = s; target = t; weight = w; }
    }

    public static void runBellmanFord(List<String> vertices, List<Edge> edges, String start) {
        Map<String, Integer> distances = new HashMap<>();
        for (String v : vertices) distances.put(v, 999999);
        distances.put(start, 0);

        System.out.println("--- [計算過程] Bellman-Ford 全面邊鬆弛追蹤 ---");
        int V = vertices.size();

        for (int i = 1; i <= V - 1; i++) {
            System.out.println("\n=== 第 " + i + " 輪掃描 ===");
            boolean updated = false; 
            
            for (Edge edge : edges) {
                int oldDist = distances.get(edge.target);
                int currentSrcDist = distances.get(edge.source);
                
                if (currentSrcDist == 999999) continue; // 起點還沒碰到，跳過
                
                int newDist = currentSrcDist + edge.weight;
                String oldDistStr = (oldDist == 999999) ? "∞" : String.valueOf(oldDist);

                System.out.print("  檢查邊 [" + edge.source + " -> " + edge.target + " (權重:" + edge.weight + ")]: 舊距離=" + oldDistStr + ", 新距離=" + newDist);

                if (newDist < oldDist) {
                    distances.put(edge.target, newDist);
                    updated = true;
                    System.out.println("  => 🔄 更新距離！");
                } else {
                    System.out.println("  => 保留原值。");
                }
            }
            if (!updated) {
                System.out.println("✅ 本輪沒有任何距離被更新，系統已提早達到全局最佳解，提早結束！");
                break;
            }
        }

        System.out.println("\n--- [最終結果] ---");
        for (String v : vertices) {
            System.out.println(start + " -> " + v + " : " + distances.get(v));
        }
        System.out.println("\n[時間複雜度] O(V * E) - 暴力掃描容忍負權重。");
    }

    public static void main(String[] args) {
        List<String> vertices = Arrays.asList("A", "B", "C");
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "B", 2));
        edges.add(new Edge("A", "C", 5));
        edges.add(new Edge("C", "B", -10)); // 負權重

        runBellmanFord(vertices, edges, "A");
    }
}