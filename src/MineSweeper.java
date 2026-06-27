import javax.swing.*;

// 主程序入口，从这里启动整个扫雷。
public class MineSweeper {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MineController().show());
    }
}
