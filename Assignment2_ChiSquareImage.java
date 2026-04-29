import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Assignment2_ChiSquareImage {
    public static void main(String[] args) {
        try {
            // 1. 讀取兩張要比較的圖片 (必須放在同一個資料夾，且長寬大小要一樣)
            // 提示：你可以自己畫兩張只有一點點不一樣的圖來測試
            File file1 = new File("image2.png");
            File file2 = new File("image3.png");
            
            BufferedImage img1 = ImageIO.read(file1);
            BufferedImage img2 = ImageIO.read(file2);
            
            int width = img1.getWidth();
            int height = img1.getHeight();
            
            // 準備一張空白圖片，用來存放「卡方距離結果圖」
            BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            System.out.println("開始掃描並計算卡方距離...");

            // 2. 逐一像素掃描 (跟第一題的掃描概念一樣)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    
                    // 取得兩張圖片該座標的灰階值 (0~255)
                    int val1 = (img1.getRGB(x, y) >> 16) & 0xFF;
                    int val2 = (img2.getRGB(x, y) >> 16) & 0xFF;
                    
                    // 3. 【核心公式】套用卡方距離 (Chi-square)
                    double diff = val1 - val2;         // 分子：相減
                    double denom = val1 + val2;        // 分母：相加 (這就是老師說多出來的分母)
                    
                    double chiSquareValue = 0.0;
                    if (denom > 0) { // 防呆：避免分母為 0 導致程式崩潰
                        chiSquareValue = 0.5 * (diff * diff) / denom;
                    }
                    
                    // 4. 將算出來的誤差數值轉回顏色 (0~255)
                    // 乘以一個放大係數 (例如 2)，是為了讓微小的差異在圖片上更明顯
                    int outColor = (int) (chiSquareValue * 2); 
                    if (outColor > 255) outColor = 255; // 限制最高只能到純白(255)
                    
                    // 組合 RGB 並畫到新圖片上
                    int rgb = (outColor << 16) | (outColor << 8) | outColor;
                    outImg.setRGB(x, y, rgb);
                }
            }
            
            // 5. 輸出結果圖檔
            File outputFile = new File("chi_square_result.png");
            ImageIO.write(outImg, "png", outputFile);
            System.out.println("作業 2 成功！請查看資料夾中的 chi_square_result.png");

        } catch (Exception e) {
            System.out.println("發生錯誤！請確認 image2.png 與 image3.png 是否存在，且長寬大小相同。");
            e.printStackTrace();
        }
    }
}
