import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ContrastiveLearning {

    /**
     * Step 1: 特徵萃取函數 D(x)
     * 將圖片讀取並縮放為 16x16 灰階，回傳長度為 256 的正規化特徵陣列
     */
    public static double[] extractFeatures(String imagePath) {
        try {
            // 使用 Java 原生套件讀取圖片
            BufferedImage img = ImageIO.read(new File(imagePath));
            if (img == null) throw new IllegalArgumentException("找不到圖片: " + imagePath);

            // 建立 16x16 的灰階畫布
            BufferedImage resized = new BufferedImage(16, 16, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = resized.createGraphics();
            g.drawImage(img, 0, 0, 16, 16, null); // 縮放並轉灰階
            g.dispose();

            // 宣告特徵陣列
            double[] features = new double[256];
            int index = 0;
            
            // 雙層迴圈走訪像素
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    // 取得 RGB 值並提取灰階亮度 (0~255)
                    int rgb = resized.getRGB(x, y);
                    int gray = (rgb & 0xFF); 
                    // 正規化 (Normalization) 至 0.0 ~ 1.0
                    features[index++] = gray / 255.0; 
                }
            }
            return features;
        } catch (Exception e) {
            System.err.println("讀取圖片發生錯誤: " + e.getMessage());
            return null;
        }
    } // ！！！注意：原本這裡漏掉了這個用來結束 extractFeatures 的大括號！！！

    /**
     * Step 2: 計算 L2 距離
     * 數學公式: sqrt( sum( (v1[i] - v2[i])^2 ) )
     */
    public static double calculateL2Distance(double[] v1, double[] v2) {
        if (v1 == null || v2 == null || v1.length != v2.length) {
            throw new IllegalArgumentException("特徵陣列無效或維度不一致");
        }

        double sumSquare = 0.0;
        for (int i = 0; i < v1.length; i++) {
            double diff = v1[i] - v2[i];
            sumSquare += (diff * diff);
        }
        return Math.sqrt(sumSquare);
    }

    /**
     * Step 3: 計算負樣本損失 (Distance Loss for Negative Samples)
     * 數學公式: max(0, C - distance)
     */
    public static double calculateNegativeLoss(double distance, double marginC) {
        // 如果距離已經大於安全邊界 C，Loss 為 0；否則產生 Loss
        return (marginC - distance > 0.0) ? (marginC - distance) : 0.0;
    }

    /**
     * Step 4: 系統進入點 (Main)
     */
    public static void main(String[] args) {
        System.out.println("--- 影像對比學習特徵距離計算 ---");

        // 1. 定義圖片路徑 (請確保這三張圖片與你的 Java 程式放在同一個執行目錄下)
        String anchorPath = "dog1.png";   // 基準圖 (狗A)
        String positivePath = "dog2.png"; // 正樣本 (狗B)
        String negativePath = "cat.png";  // 負樣本 (貓)

        // 2. 特徵萃取 (Feature Extraction)
        System.out.println("正在萃取影像特徵...");
        double[] anchorFeature = extractFeatures(anchorPath);
        double[] positiveFeature = extractFeatures(positivePath);
        double[] negativeFeature = extractFeatures(negativePath);

        // 防呆檢查
        if (anchorFeature == null || positiveFeature == null || negativeFeature == null) {
            System.err.println("特徵萃取失敗，請確認圖片路徑是否正確。");
            return;
        }

        // 3. 計算特徵距離 (L2 Distance)
        double distancePos = calculateL2Distance(anchorFeature, positiveFeature);
        double distanceNeg = calculateL2Distance(anchorFeature, negativeFeature);

        System.out.printf("同類距離 (狗A vs 狗B): %.4f\n", distancePos);
        System.out.printf("異類距離 (狗A vs 貓)  : %.4f\n", distanceNeg);

        // 4. 設定邊界值 C (Margin) 並計算 Loss
        double marginC = 4.7; 
        double loss = calculateNegativeLoss(distanceNeg, marginC);

        System.out.println("\n--- 損失函數評估 (Loss Evaluation) ---");
        System.out.printf("設定安全邊界 (Margin C) = %.2f\n", marginC);
        System.out.printf("負樣本損失 (Negative Loss) = %.4f\n", loss);

        if (loss == 0) {
            System.out.println("結果: 系統已成功將貓狗分開！(距離 >= C)");
        } else {
            System.out.println("結果: 貓狗特徵靠得太近，系統產生 Loss 進行懲罰！(距離 < C)");
        }
    }
} // 這是 ContrastiveLearning 類別的結束括號（原本最底下多了一個）
