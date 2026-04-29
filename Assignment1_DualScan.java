import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Assignment1_DualScan {
    public static void main(String[] args) {
        try {
            // 讀取原始邊緣圖
            File inputFile = new File("image1.png");
            BufferedImage img = ImageIO.read(inputFile);
            int width = img.getWidth();
            int height = img.getHeight();

            // 建立兩組距離矩陣
            int[][] verticalMap = new int[height][width];
            int[][] horizontalMap = new int[height][width];
            int INF = 9999;

            // 初始化：找出特徵點
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int r = (img.getRGB(x, y) >> 16) & 0xFF;
                    if (r < 128) {
                        verticalMap[y][x] = 0;
                        horizontalMap[y][x] = 0;
                    } else {
                        verticalMap[y][x] = INF;
                        horizontalMap[y][x] = INF;
                    }
                }
            }

            // ==========================================
            // 1. 生成 image2.png：僅進行「上下 (Vertical)」掃描
            // ==========================================
            // Forward: 由上往下
            for (int y = 1; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    verticalMap[y][x] = Math.min(verticalMap[y][x], verticalMap[y - 1][x] + 1);
                }
            }
            // Backward: 由下往上
            for (int y = height - 2; y >= 0; y--) {
                for (int x = 0; x < width; x++) {
                    verticalMap[y][x] = Math.min(verticalMap[y][x], verticalMap[y + 1][x] + 1);
                }
            }
            saveImage(verticalMap, width, height, "image2.png");

            // ==========================================
            // 2. 生成 image3.png：僅進行「左右 (Horizontal)」掃描
            // ==========================================
            // Forward: 由左往右
            for (int y = 0; y < height; y++) {
                for (int x = 1; x < width; x++) {
                    horizontalMap[y][x] = Math.min(horizontalMap[y][x], horizontalMap[y][x - 1] + 1);
                }
            }
            // Backward: 由右往左
            for (int y = 0; y < height; y++) {
                for (int x = width - 2; x >= 0; x--) {
                    horizontalMap[y][x] = Math.min(horizontalMap[y][x], horizontalMap[y][x + 1] + 1);
                }
            }
            saveImage(horizontalMap, width, height, "image3.png");

            System.out.println("成功！已生成 image2.png (上下掃描) 與 image3.png (左右掃描)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 輔助方法：將陣列轉為圖片存檔
    private static void saveImage(int[][] map, int w, int h, String fileName) throws Exception {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int dist = map[y][x] * 5; // 調整倍數讓漸層更清楚
                if (dist > 255) dist = 255;
                int gray = (dist << 16) | (dist << 8) | dist;
                out.setRGB(x, y, gray);
            }
        }
        ImageIO.write(out, "png", new File(fileName));
    }
}
