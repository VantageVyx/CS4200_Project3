public class AlphaBeta {
    char me;
    char opp;
    long endTime;
    boolean outOfTime;
    private int deepestPlyReached;
    private int completedDepth;

    public AlphaBeta(char me) {
        this.me = me;
        if (me == 'X') {
            opp = 'O';
        } else {
            opp = 'X';
        }
    }

    public int[] chooseMove(char[][] board, int seconds) {
        endTime = System.nanoTime() + seconds * 1000000000L - 25000000L;
        outOfTime = false;
        this.deepestPlyReached = 0;
        this.completedDepth = 0;

        int[] bestMove = new int[]{-1, -1};
        // pick first open spot just in case time runs out super fast
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == '-' && bestMove[0] == -1) {
                    bestMove[0] = r;
                    bestMove[1] = c;
                }
            }
        }

        // iterative deepening
        for (int maxDepth = 1; maxDepth <= 64; maxDepth++) {
            int roundBestScore = -999999999;
            int roundBestR = -1;
            int roundBestC = -1;
            int alpha = -999999999;
            int beta = 999999999;
            boolean finishedDepth = true;

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (board[r][c] == '-') {
                        board[r][c] = me;
                        int val = search(board, maxDepth - 1, maxDepth, alpha, beta, false);
                        board[r][c] = '-';

                        if (System.nanoTime() >= endTime) {
                            outOfTime = true;
                        }
                        if (outOfTime) {
                            finishedDepth = false;
                            break;
                        }

                        if (val > roundBestScore) {
                            roundBestScore = val;
                            roundBestR = r;
                            roundBestC = c;
                        }
                        if (val > alpha) {
                            alpha = val;
                        }
                    }
                }
                if (!finishedDepth) break;
            }

            if (finishedDepth && roundBestR != -1) {
                bestMove = new int[]{roundBestR, roundBestC};
                completedDepth = maxDepth; //recording that this plie finished 
                if (roundBestScore >= 100000) {
                    break;
                }
            } else {
                break;
            }
        }
        System.out.println("Completed plies:" + completedDepth + " Deepest plies reached:" + deepestPlyReached);

        return bestMove;
    }

    private int search(char[][] board, int depth, int rootTargetDepth, int alpha, int beta, boolean isMax) {
        int plyReached = rootTargetDepth - depth; // handling recording deepest plies 
        if (plyReached > deepestPlyReached) {      
            deepestPlyReached = plyReached;        
        }
        
        if (System.nanoTime() >= endTime) {
            outOfTime = true;
            return 0;
        }

        int winStatus = checkWin(board);
        if (winStatus == 1) {
            return 100000 + depth;
        }
        if (winStatus == -1) {
            return -100000 - depth;
        }
        if (depth == 0) {
            return evalBoard(board);
        }

        if (isMax) {
            int best = -999999999;
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (board[r][c] == '-') {
                        board[r][c] = me;
                        int score = search(board, depth - 1, rootTargetDepth, alpha, beta, false);
                        board[r][c] = '-';

                        if (outOfTime) return 0;
                        if (score > best) best = score;
                        if (best > alpha) alpha = best;
                        if (alpha >= beta) return best;
                    }
                }
            }
            return best;
        } else {
            int best = 999999999;
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (board[r][c] == '-') {
                        board[r][c] = opp;
                        int score = search(board, depth - 1, rootTargetDepth, alpha, beta, true);
                        board[r][c] = '-';

                        if (outOfTime) return 0;
                        if (score < best) best = score;
                        if (best < beta) beta = best;
                        if (alpha >= beta) return best;
                    }
                }
            }
            return best;
        }
    }

    // 1 = me wins, -1 = opp wins, 0 = nobody won yet
    private int checkWin(char[][] board) {
        // rows
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c <= 4; c++) {
                char a = board[r][c];
                if (a != '-' && a == board[r][c + 1] && a == board[r][c + 2] && a == board[r][c + 3]) {
                    return (a == me) ? 1 : -1;
                }
            }
        }
        // cols
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r <= 4; r++) {
                char a = board[r][c];
                if (a != '-' && a == board[r + 1][c] && a == board[r + 2][c] && a == board[r + 3][c]) {
                    return (a == me) ? 1 : -1;
                }
            }
        }
        return 0;
    }

    private int evalBoard(char[][] board) {
        int total = 0;

        // check horizontal 4-cell chunks
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c <= 4; c++) {
                int myCount = 0;
                int oppCount = 0;
                for (int k = 0; k < 4; k++) {
                    if (board[r][c + k] == me) myCount++;
                    else if (board[r][c + k] == opp) oppCount++;
                }
                total += scoreCounts(myCount, oppCount);
            }
        }

        // check vertical 4-cell chunks
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r <= 4; r++) {
                int myCount = 0;
                int oppCount = 0;
                for (int k = 0; k < 4; k++) {
                    if (board[r + k][c] == me) myCount++;
                    else if (board[r + k][c] == opp) oppCount++;
                }
                total += scoreCounts(myCount, oppCount);
            }
        }

        return total;
    }

    private int scoreCounts(int myCount, int oppCount) {
        if (myCount > 0 && oppCount > 0) return 0;
        if (myCount == 3) return 50;
        if (myCount == 2) return 10;
        if (myCount == 1) return 1;
        if (oppCount == 3) return -60;
        if (oppCount == 2) return -10;
        if (oppCount == 1) return -1;
        return 0;
    }
}