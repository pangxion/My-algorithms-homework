import cv2
import time
import tkinter as tk
from PIL import Image, ImageTk
from backbone import RecycleBackbone

class RecycleApp:
    def __init__(self, window):
        self.window = window
        self.window.title("智慧回收系統 V1.0 - 澎科大資工系專題")
        self.window.geometry("1100x700")
        self.window.configure(bg="#1e1e1e") # 深色工業風格

        # 1. 啟動後端大腦
        self.backbone = RecycleBackbone("YOLOv8n.pt") 
        self.cap = cv2.VideoCapture(0)
        
        # 初始化計時器 (計算 FPS)
        self.prev_time = time.time()

        # --- UI 配置 ---
        # 標題
        tk.Label(window, text="智慧資源回收系統 - 生態守護者介面", font=("Microsoft JhengHei", 24, "bold"), 
                 fg="#00ff7f", bg="#1e1e1e").place(x=30, y=20)

        # 左側：影像區
        self.video_frame = tk.Frame(window, bg="#1e1e1e", bd=5, relief=tk.SUNKEN)
        self.video_frame.place(x=30, y=80, width=640, height=480)
        self.canvas = tk.Canvas(self.video_frame, width=640, height=480, bg="black", highlightthickness=0)
        self.canvas.pack()

        # 右側：ATM 數據看板
        self.data_panel = tk.Frame(window, bg="#2d2d2d", bd=2, relief=tk.RAISED)
        self.data_panel.place(x=700, y=80, width=350, height=480)

        # 建立數據看板標籤
        self.fps_label = self.create_stat_label("推論幀率 (FPS)", 50, "#00ccff")
        self.latency_label = self.create_stat_label("總體延遲 (Latency/ms)", 150, "#ffcc00")
        self.count_label = self.create_stat_label("目前偵測數量", 250, "#00ff7f")
        
        # 底部狀態提示
        self.status_label = tk.Label(window, text="系統狀態：等待投遞中...", font=("Microsoft JhengHei", 18), 
                                     fg="white", bg="#1e1e1e")
        self.status_label.place(x=30, y=580)

        # 安全關閉按鈕
        tk.Button(window, text="安全關閉系統", command=self.on_closing, bg="#ff4d4d", fg="white", 
                  font=("Arial", 12, "bold"), width=15, height=2).place(x=880, y=620)

        self.update_loop()

    def create_stat_label(self, title, y_pos, color):
        tk.Label(self.data_panel, text=title, font=("Microsoft JhengHei", 12), fg="white", bg="#2d2d2d").place(x=30, y=y_pos)
        val_label = tk.Label(self.data_panel, text="--", font=("Arial", 32, "bold"), fg=color, bg="#2d2d2d")
        val_label.place(x=30, y=y_pos + 35)
        return val_label

    def update_loop(self):
        ret, frame = self.cap.read()
        if ret:
            # 1. 呼叫大腦運算
            p_frame, latency, count = self.backbone.process_frame(frame)
            
            # 2. 計算系統實際 FPS
            curr_time = time.time()
            fps = 1.0 / (curr_time - self.prev_time)
            self.prev_time = curr_time
            
            # 3. 更新數據看板
            self.fps_label.config(text=f"{fps:.1f}")
            self.latency_label.config(text=f"{latency:.1f}")
            self.count_label.config(text=f"{count} pcs")
            
            # 4. 更新狀態列
            if count > 0:
                self.status_label.config(text="系統狀態：偵測到回收物件，辨識中...", fg="#00ff7f")
            else:
                self.status_label.config(text="系統狀態：等待投遞中...", fg="white")

            # 5. 影像轉換渲染
            p_frame = cv2.cvtColor(p_frame, cv2.COLOR_BGR2RGB)
            img = Image.fromarray(p_frame)
            imgtk = ImageTk.PhotoImage(image=img)
            self.canvas.create_image(0, 0, anchor=tk.NW, image=imgtk)
            self.canvas.imgtk = imgtk

        # 每 1 毫秒嘗試刷新 (讓速度發揮到硬體極限)
        self.window.after(1, self.update_loop)

    def on_closing(self):
        self.cap.release()
        self.window.destroy()

if __name__ == "__main__":
    root = tk.Tk()
    app = RecycleApp(root)
    root.mainloop()