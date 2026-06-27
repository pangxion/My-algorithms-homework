import cv2
import time
from ultralytics import YOLO

class RecycleBackbone:
    def __init__(self, model_path="YOLOv8n.pt"):
        """
        初始化 YOLO 模型。
        自動偵測硬體：有 GPU 用 GPU，沒 GPU 用 CPU。
        """
        print(f"Backbone: 正在載入 {model_path}...")
        # 移除 device 限制，讓系統自動選擇最佳硬體
        self.model = YOLO(model_path)

    def process_frame(self, frame):
        """
        執行推論並回傳結果。
        """
        # 1. 執行 YOLO 推論
        results = self.model(frame, verbose=False)
        
        # 2. 取得標註後的影像 (內含 Bounding Box)
        annotated_frame = results[0].plot()
        
        # 3. 偵測數量
        detections_count = len(results[0].boxes)
        
        # 4. 提取精確的延遲數據 (單位: ms)
        # 包含前處理、推論、後處理，這是你論文中最科學的指標
        pre = results[0].speed['preprocess']
        inf = results[0].speed['inference']
        post = results[0].speed['postprocess']
        total_latency = pre + inf + post

        return annotated_frame, total_latency, detections_count