import java.util.Random;

public class YinYangCell implements Comparable<YinYangCell> {
    Random randGenerator;
    private int[][] board; // original board with given constraints
    private int[][] cellBoard; // board with black (1) and white (0) cells
    private int heuristic; // heuristic value of the solution

    // Directions for 4-neighbor connectivity
    private int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    // Constructor with existing board and cellBoard
    public YinYangCell(int[][] board, int[][] cellBoard, Random randomizer) {
        this.board = board;
        this.cellBoard = cellBoard;
        this.randGenerator = randomizer;
        this.heuristic = calculateHeuristic();
    }

    // Constructor to generate a random initial solution
    public YinYangCell(int n, int[][] board, Random randomizer) {
        this.board = board;
        this.randGenerator = randomizer;
        this.cellBoard = new int[n][n];

        // Randomly initialize the cell board
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cellBoard[i][j] = randGenerator.nextInt(2);
            }
        }
        this.heuristic = calculateHeuristic();
    }

    // Check if coordinates are valid within the board
    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < board.length && y >= 0 && y < board.length;
    }

    // Calculate heuristic based on Yin Yang puzzle constraints
    public int calculateHeuristic() {
        int penalty = 0;
        int n = board.length;

        // 1. Check equal number of black and white cells
        int blackCount = 0, whiteCount = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                blackCount += cellBoard[i][j];
                whiteCount += (1 - cellBoard[i][j]);
            }
        }
        penalty += Math.abs(blackCount - whiteCount);

        // 2. No 2x2 same color blocks
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                if (cellBoard[i][j] == cellBoard[i + 1][j] &&
                    cellBoard[i][j] == cellBoard[i][j + 1] &&
                    cellBoard[i][j] == cellBoard[i + 1][j + 1]) {
                    penalty += 10; // High penalty for 2x2 same color block
                }
            }
        }

        // 3. Check connectivity for black and white cells
        if (!isConnected(1)) penalty += 50; // High penalty for disconnected black cells
        if (!isConnected(0)) penalty += 50; // High penalty for disconnected white cells

        return penalty;
    }

    // Check if all cells of a specific color are connected
    private boolean isConnected(int color) {
        int n = cellBoard.length;
        boolean[][] visited = new boolean[n][n];
        boolean foundStart = false;

        // Find a starting point of the given color
        int startX = -1, startY = -1;
        for (int i = 0; i < n && !foundStart; i++) {
            for (int j = 0; j < n && !foundStart; j++) {
                if (cellBoard[i][j] == color) {
                    startX = i;
                    startY = j;
                    foundStart = true;
                }
            }
        }
        if (!foundStart) return false; // No cells of this color

        // Perform BFS/DFS to check connectivity
        dfs(startX, startY, color, visited);

        // Ensure all cells of this color are visited
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (cellBoard[i][j] == color && !visited[i][j]) {
                    return false; // Disconnected cells found
                }
            }
        }
        return true;
    }

    // Depth-First Search (DFS) for connectivity check
    private void dfs(int x, int y, int color, boolean[][] visited) {
        visited[x][y] = true;
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isValidCell(newX, newY) && !visited[newX][newY] && cellBoard[newX][newY] == color) {
                dfs(newX, newY, color, visited);
            }
        }
    }

    // Mutation: Flip random cells in each row
    public void doMutation() {
        for (int k = 0; k < board.length; k++) {
            int j = randGenerator.nextInt(board.length);
            cellBoard[k][j] = cellBoard[k][j] ^ 1;
        }
        this.heuristic = calculateHeuristic(); // Recalculate heuristic after mutation
    }

    // For sorting based on heuristic
    @Override
    public int compareTo(YinYangCell other) {
        return this.heuristic - other.heuristic;
    }

    // Get current heuristic
    public int getHeuristic() {
        return this.heuristic;
    }

    // Get cell board
    public int[][] getCellBoard() {
        return this.cellBoard;
    }

    // Get original board
    public int[][] getBoard() {
        return this.board;
    }

    // String representation of cell board
    @Override
    public String toString() {
        StringBuilder a = new StringBuilder();
        for (int[] row : cellBoard) {
            for (int cell : row) {
                a.append(cell).append(" ");
            }
            a.append('\n');
        }
        return a.toString();
    }
}
