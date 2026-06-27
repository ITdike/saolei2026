import java.util.Arrays;
import java.util.Random;

// 这个类主要负责扫雷的核心逻辑。
// 像布雷、翻开格子、插旗还有输赢判断都写在这里。
public class MineBoard {
    // 固定排版大小和雷数。
    public static final int ROWS = 16;
    public static final int COLS = 30;
    public static final int MINES = 99;

    // 先把周围八个方向写好，后面很多地方都会用到。
    private static final int[][] DIRS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };

    // board 里面存的是雷和数字，另外几个数组存格子的状态。
    private final int[][] board = new int[ROWS][COLS];
    private final boolean[][] open = new boolean[ROWS][COLS];
    private final boolean[][] flag = new boolean[ROWS][COLS];
    private final boolean[][] boom = new boolean[ROWS][COLS];
    private final Random random = new Random();

    // flags 记录已经插了多少旗，placed 表示雷是不是已经放好了。
    private int flags;
    private boolean placed;
    private boolean over;
    private boolean won;

    // 重新开始时，把这一局的数据都清空。
    public void reset() {
        for (int r = 0; r < ROWS; r++) {
            Arrays.fill(board[r], 0);
            Arrays.fill(open[r], false);
            Arrays.fill(flag[r], false);
            Arrays.fill(boom[r], false);
        }
        flags = 0;
        placed = false;
        over = false;
        won = false;
    }

    // 第一次点开格子后再布雷，这样第一次点的时候不会直接踩雷。
    private void placeMines(int safeRow, int safeCol) {
        if (placed) return;
        placed = true;

        for (int count = 0; count < MINES; ) {
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLS);
            if (board[row][col] == -1
                    || (Math.abs(row - safeRow) <= 1 && Math.abs(col - safeCol) <= 1)) {
                continue;
            }
            board[row][col] = -1;
            count++;
        }

        // 雷放好以后，再去算每个非雷格周围有几个雷。
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] != -1) {
                    board[row][col] = countAround(row, col);
                }
            }
        }
    }

    // 统计一个格子周围雷的数量。
    private int countAround(int row, int col) {
        int count = 0;
        for (int[] dir : DIRS) {
            int nr = row + dir[0];
            int nc = col + dir[1];
            if (inBounds(nr, nc) && board[nr][nc] == -1) count++;
        }
        return count;
    }

    // 翻开格子以后，如果是雷就输了，不是雷就继续判断。
    public boolean openCell(int row, int col) {
        // 这几种情况说明这个格子不用再处理了。
        if (over || open[row][col] || flag[row][col]) return false;

        placeMines(row, col);
        open[row][col] = true;

        if (board[row][col] == -1) {
            boom[row][col] = true;
            finish(false);
            return true;
        }

        if (board[row][col] == 0) spread(row, col);
        if (checkWin()) finish(true);
        return true;
    }

    // 如果点到空白格，就把附近能展开的格子一起翻开。
    private void spread(int row, int col) {
        for (int[] dir : DIRS) {
            int nr = row + dir[0];
            int nc = col + dir[1];
            if (!inBounds(nr, nc) || open[nr][nc] || flag[nr][nc]) continue;
            open[nr][nc] = true;
            if (board[nr][nc] == 0) spread(nr, nc);
        }
    }

    // 右键插旗，再点一次就把旗子取消掉。
    public boolean toggleFlag(int row, int col) {
        if (over || open[row][col]) return false;
        flag[row][col] = !flag[row][col];
        flags += flag[row][col] ? 1 : -1;
        return true;
    }

    // 只要还有不是雷的格子没打开，就说明还没赢。
    private boolean checkWin() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] != -1 && !open[row][col]) return false;
            }
        }
        return true;
    }

    // 这里统一处理游戏结束的情况。
    // 如果赢了就把雷都标出来，输了就把雷直接显示出来。
    private void finish(boolean success) {
        over = true;
        won = success;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == -1) {
                    if (success) {
                        flag[row][col] = true;
                    } else {
                        open[row][col] = true;
                    }
                }
            }
        }
    }

    // 判断坐标有没有越界。
    private boolean inBounds(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    // 下面这些方法主要是给界面层读取当前状态用的。
    public int getCellValue(int row, int col) {
        return board[row][col];
    }

    public boolean isOpen(int row, int col) {
        return open[row][col];
    }

    public boolean isFlagged(int row, int col) {
        return flag[row][col];
    }

    public boolean isBoom(int row, int col) {
        return boom[row][col];
    }

    public int getRemainingMines() {
        return MINES - flags;
    }

    public boolean isPlaced() {
        return placed;
    }

    public boolean isOver() {
        return over;
    }

    public boolean isWon() {
        return won;
    }
}
