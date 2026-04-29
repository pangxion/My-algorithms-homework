import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Assignment2_SingleImageChiSquare {

    // 定義座標點
    static class Point {
        int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
    }

    public static void main(String[] args) {
        try {
            // 1. 單純只讀取一張圖片 image1.png
            File file = new File("image1.png");
            BufferedImage img = ImageIO.read(file);
            int width = img.getWidth();
            int height = img.getHeight();
            
            // 找出所有特徵點 (黑色邊緣)
            List<Point> edgePoints = new ArrayList<>();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int r = (img.getRGB(x, y) >> 16) & 0xFF;
                    if (r < 128) {
                        edgePoints.add(new Point(x, y));
                    }
                }
            }

            if (edgePoints.isEmpty()) {
                System.out.println("找不到邊緣點！");
                return;
            }

            // 2. 選定一個「基準點 (Target)」
            // 我們取清單的第一個點當作基準點 (你也可以改取中間的點 edgePoints.size()/2)
            Point targetPoint = edgePoints.get(0);
            double[] targetHist = buildShapeContext(targetPoint, edgePoints);

            // 準備一張全白的空白圖片來畫結果
            BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) outImg.setRGB(x, y, 0xFFFFFF);

            System.out.println("基準點座標: (" + targetPoint.x + ", " + targetPoint.y + ")");
            System.out.println("開始計算全圖每一個邊緣點與基準點的卡方距離...");

            // 3. 全圖掃描：計算其他點與基準點的卡方距離
            double maxDistance = 0;
            double[] distances = new double[edgePoints.size()];
            
            for (int i = 0; i < edgePoints.size(); i++) {
                Point p = edgePoints.get(i);
                double[] pHist = buildShapeContext(p, edgePoints);
                
                // 套用老師講義上的卡方公式 (Chi-square)
                double dist = calculateChiSquare(targetHist, pHist);
                distances[i] = dist;
                
                // 找出最大距離，方便等等做顏色正規化
                if (dist > maxDistance) maxDistance = dist;
            }

            // 4. 將卡方距離轉化為影像像素並輸出
            for (int i = 0; i < edgePoints.size(); i++) {
                Point p = edgePoints.get(i);
                
                // 將距離對應到 0~255 的灰階色彩 (距離越近越黑，距離越遠越白)
                int colorValue = (int) ((distances[i] / maxDistance) * 255);
                if (colorValue > 255) colorValue = 255;
                if (colorValue < 0) colorValue = 0;
                
                int rgb = (colorValue << 16) | (colorValue << 8) | colorValue;
                outImg.setRGB(p.x, p.y, rgb);
            }

            // 把基準點標示為紅色，方便老師在圖上看出我們是以誰為基準
            outImg.setRGB(targetPoint.x, targetPoint.y, 0xFF0000); // 紅色

            // 5. 輸出最終的一張圖片
            File outputFile = new File("chi_square_map.png");
            ImageIO.write(outImg, "png", outputFile);
            System.out.println("作業 2 完成！請查看 chi_square_map.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- 建立 Shape Context 特徵陣列 ---
    public static double[] buildShapeContext(Point target, List<Point> allPoints) {
        int distanceBins = 3;
        int angleBins = 8;
        double[] histogram = new double[distanceBins * angleBins];

        for (Point p : allPoints) {
            if (p.x == target.x && p.y == target.y) continue;

            double dx = p.x - target.x;
            double dy = p.y - target.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            double angle = Math.atan2(dy, dx);
            if (angle < 0) angle += 2 * Math.PI;

            int dBin = (dist < 20) ? 0 : (dist < 50) ? 1 : (dist < 100) ? 2 : -1;
            int aBin = (int) Math.floor((angle / (2 * Math.PI)) * angleBins);
            if (aBin == angleBins) aBin = angleBins - 1;

            if (dBin != -1) histogram[dBin * angleBins + aBin] += 1.0;
        }
        return histogram;
    }

    // --- 卡方公式 (多了一個分母的精髓) ---
    public static double calculateChiSquare(double[] h_i, double[] h_j) {
        double sum = 0.0;
        for (int k = 0; k < h_i.length; k++) {
            double diff = h_i[k] - h_j[k];
            double denom = h_i[k] + h_j[k]; // 多出來的分母
            if (denom > 0) {
                sum += (diff * diff) / denom;
            }
        }
        return 0.5 * sum;
    }
}
