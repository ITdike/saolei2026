import javax.swing.*;

// 这个类相当于中间层，主要负责把界面和逻辑接起来。
// 另外计时功能也是放在这里处理的。
public class MineController {
    private final MineBoard board = new MineBoard();
    private final MineView view;

    private Timer timer;
    private int seconds;

    // 创建界面的时候，把每个操作对应的方法也一起绑定好。
    public MineController() {
        view = new MineView(new MineView.Actions() {
            @Override
            public void onOpen(int row, int col) {
                handleOpen(row, col);
            }

            @Override
            public void onMark(int row, int col) {
                handleMark(row, col);
            }

            @Override
            public void onReset() {
                resetGame();
            }
        });
        resetGame();
    }

    // 左键翻格子，第一次成功翻开后再开始计时。
    private void handleOpen(int row, int col) {
        boolean firstOpen = !board.isPlaced();
        if (!board.openCell(row, col)) return;
        if (firstOpen) startTimer();
        if (board.isOver()) stopTimer();
        refresh();
        if (board.isWon()) view.showWinMessage(seconds);
    }

    // 右键是插旗，再点一次就是取消旗子。
    private void handleMark(int row, int col) {
        if (!board.toggleFlag(row, col)) return;
        refresh();
    }

    // 点上面的笑脸按钮就重新开始一局。
    private void resetGame() {
        stopTimer();
        seconds = 0;
        board.reset();
        refresh();
    }

    // 这个定时器每秒加一，用来记录用了多少秒。
    private void startTimer() {
        if (timer != null) timer.stop();
        timer = new Timer(1000, e -> {
            seconds = Math.min(seconds + 1, 999);
            refresh();
        });
        timer.start();
    }

    // 游戏结束或者重开的时候把计时停掉。
    private void stopTimer() {
        if (timer != null) timer.stop();
    }

    // 只要状态变了，就重新刷新一次界面显示。
    private void refresh() {
        view.render(board, seconds);
    }

    public void show() {
        view.setVisible(true);
    }
}
