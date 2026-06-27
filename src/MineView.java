import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// 这个类主要负责界面显示，窗口长什么样基本都在这里写。
public class MineView extends JFrame {
    public interface Actions {
        void onOpen(int row, int col);
        void onMark(int row, int col);
        void onReset();
    }

    private static final int SIZE = 28;

    // 数字颜色我基本按经典扫雷的样子来设置。
    private static final Color[] COLORS = {
            null,
            Color.BLUE,
            new Color(0, 128, 0),
            Color.RED,
            new Color(0, 0, 128),
            new Color(128, 0, 0),
            new Color(0, 128, 128),
            Color.BLACK,
            Color.GRAY
    };

    // 这里放棋盘按钮，还有上面的雷数、表情和计时。
    private final JButton[][] cells = new JButton[MineBoard.ROWS][MineBoard.COLS];
    private final JLabel mineLabel = led(String.valueOf(MineBoard.MINES));
    private final JLabel timeLabel = led("0");
    private final JButton face = new JButton("🙂");

    // 创建主窗口，把顶部状态栏和下面棋盘面板加进去。
    public MineView(Actions actions) {
        setTitle("扫雷2026");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        add(buildTop(actions), BorderLayout.NORTH);
        add(buildGrid(actions), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    // 顶部状态栏，左边是剩余雷数，中间是表情，右边是时间。
    private JPanel buildTop(Actions actions) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        face.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        face.setFocusPainted(false);
        face.addActionListener(e -> actions.onReset());
        panel.add(wrap(mineLabel), BorderLayout.WEST);
        panel.add(face, BorderLayout.CENTER);
        panel.add(wrap(timeLabel), BorderLayout.EAST);
        return panel;
    }

    // 外面包一层面板，不然直接放进去会被拉伸。
    private JPanel wrap(JComponent component) {
        JPanel panel = new JPanel();
        panel.add(component);
        return panel;
    }

    // 这里生成整个棋盘，每个按钮对应一个格子。
    private JPanel buildGrid(Actions actions) {
        JPanel panel = new JPanel(new GridLayout(MineBoard.ROWS, MineBoard.COLS));
        panel.setPreferredSize(new Dimension(MineBoard.COLS * SIZE, MineBoard.ROWS * SIZE));
        for (int row = 0; row < MineBoard.ROWS; row++) {
            for (int col = 0; col < MineBoard.COLS; col++) {
                cells[row][col] = buildCell(row, col, actions);
                panel.add(cells[row][col]);
            }
        }
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        return panel;
    }

    // 创建单个格子按钮，同时把左右键操作加上去。
    private JButton buildCell(int row, int col, Actions actions) {
        JButton button = new JButton();
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    actions.onMark(row, col);
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    actions.onOpen(row, col);
                }
            }
        });
        return button;
    }

    // 这里把数字标签做成扫雷常见的黑底红字风格。
    private JLabel led(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Consolas", Font.BOLD, 28));
        label.setForeground(Color.RED);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(72, 36));
        return label;
    }

    // 刷新界面时，顶部信息和每个格子都要一起更新。
    public void render(MineBoard board, int seconds) {
        mineLabel.setText(String.valueOf(board.getRemainingMines()));
        timeLabel.setText(String.valueOf(seconds));
        face.setText(board.isWon() ? "😎" : board.isOver() ? "☹️" : "🙂");

        for (int row = 0; row < MineBoard.ROWS; row++) {
            for (int col = 0; col < MineBoard.COLS; col++) {
                renderCell(cells[row][col], board, row, col);
            }
        }
    }

    // 按当前状态更新一个格子的显示效果。
    private void renderCell(JButton button, MineBoard board, int row, int col) {
        button.setForeground(Color.BLACK);
        if (board.isOpen(row, col)) {
            drawOpenCell(button, board, row, col);
            return;
        }

        button.setBorderPainted(true);
        button.setBackground(null);
        if (board.isFlagged(row, col)) {
            boolean wrongFlag = board.isOver() && board.getCellValue(row, col) != -1;
            button.setText(wrongFlag ? "X" : "F");
        } else {
            button.setText("");
        }
    }

    // 格子翻开以后，要显示数字、雷或者空白。
    private void drawOpenCell(JButton button, MineBoard board, int row, int col) {
        int value = board.getCellValue(row, col);
        button.setBorderPainted(false);
        button.setBackground(Color.LIGHT_GRAY);

        if (value == -1) {
            button.setText("*");
            if (board.isBoom(row, col)) {
                button.setBackground(Color.RED);
            }
            return;
        }

        button.setText(value == 0 ? "" : String.valueOf(value));
        if (value > 0) {
            button.setForeground(COLORS[value]);
        }
    }

    // 赢了以后弹一个提示框出来。
    public void showWinMessage(int seconds) {
        JOptionPane.showMessageDialog(
                this,
                "恭喜通关，用时 " + seconds + " 秒。",
                "胜利",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
