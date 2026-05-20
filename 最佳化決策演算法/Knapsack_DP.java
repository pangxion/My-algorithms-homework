public class Knapsack_DP {

    public static void solveKnapsack(int[] weights, int[] values, int maxCapacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][maxCapacity + 1];

        System.out.println("--- [計算過程] 0/1 背包問題 DP 狀態轉移 ---");

        for (int i = 1; i <= n; i++) {
            System.out.println("正在評估第 " + i + " 個物品 (重量: " + weights[i-1] + ", 價值: " + values[i-1] + ")");
            for (int w = 1; w <= maxCapacity; w++) {
                if (weights[i - 1] > w) {
                    dp[i][w] = dp[i - 1][w]; // 放不下，繼承上一列
                } else {
                    int skip = dp[i - 1][w];
                    int take = values[i - 1] + dp[i - 1][w - weights[i - 1]];
                    dp[i][w] = Math.max(skip, take);
                }
            }
        }

        // 視覺化印出 DP 二維矩陣
        System.out.println("\n=== 動態規劃查表矩陣 (DP Table) ===");
        System.out.print("容量 W |\t");
        for (int w = 0; w <= maxCapacity; w+=10) System.out.print(w + "\t"); // 為了排版只印出每10單位的標題
        System.out.println("\n---------------------------------------------------------");

        for (int i = 0; i <= n; i++) {
            System.out.print("物品 " + i + " |\t");
            for (int w = 0; w <= maxCapacity; w+=10) {
                System.out.print(dp[i][w] + "\t");
            }
            System.out.println();
        }

        System.out.println("\n--- [最終結果] ---");
        System.out.println("背包可容納的最大價值為: " + dp[n][maxCapacity]);
        System.out.println("\n[時間複雜度] O(N * W) - 建立並填滿 N x W 的查表矩陣。");
    }

    public static void main(String[] args) {
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        int maxCapacity = 50;

        solveKnapsack(weights, values, maxCapacity);
    }
}