import java.util.Arrays;
import java.util.Scanner;

public class Main {
    //this is gonna be weird bc index 0 will be referred to as '1' in the visual label and so on. 
    //originally was gonna use the em dash — but they fill the whole character space so its harder to tell where cells end ill use - instead.
    public char[][] board = new char[8][8];
    static int thinkTime; 
    private final Scanner scanner = new Scanner(System.in); //global scanner all methods can use

    public static void  main(String[] args) {
        Main game = new Main();
        game.setupBoard();
        game.runGame();


    }

    public void runGame(){
        setupBoard();
        System.out.println("Finished board setup");
        printBoard();
        //ask think time
        thinkTime = askThinkTime();

        //: ask if bot v bot
        boolean botVsBot = askBotVsBot();

        // ask human if they want to go first
        boolean humanFirst = !botVsBot && askHumanFirst();

        

        AlphaBeta botX = new AlphaBeta('X');
        AlphaBeta botO = new AlphaBeta('O');

        
        boolean xTurn = !humanFirst; //x= computer, O = human

        while(true){
            if(xTurn){
                int[] move = botX.chooseMove(board, thinkTime);
                board[move[0]][move[1]] = 'X';
                System.out.println("Computer (X) moves to: "+ toCoord(move[0], move[1]));
            }else if(botVsBot){
                int[] move = botO.chooseMove(board, thinkTime);
                board[move[0]][move[1]] = 'O';
                System.out.println("Computer (O) moves to: "+ toCoord(move[0], move[1]));
            }else{
                int[] move = askHumanMove();
                board[move[0]][move[1]] = 'O';
                System.out.println("Human (O) moves: " + toCoord(move[0], move[1]));
            
            }

            printBoard();

            //check state
            char winner = checkWinner();
            if(winner!='-'){
                System.out.println("Winner is: "+winner);
                break;
            }
            if(isBoardFull()){
                System.out.println("Board is full, this game is a draw.");
                break;
            }
            xTurn = !xTurn; //swap turns for the next loop iteration
        }

    }
private int askThinkTime() {
        System.out.print("How many seconds per move? (1-30, press enter for 5): ");
        String ans = scanner.nextLine().trim();

        if (ans.equals("")) {
            return 5;
        }

        int sec = 5;
        try {
            sec = Integer.parseInt(ans);
        } catch (Exception e) {
            System.out.println("hmm not a valid number, using 5 seconds.");
            return 5;
        }

        if (sec < 1) {
            System.out.println("too small, setting to 1 second.");
            sec = 1;
        }
        if (sec > 30) {
            System.out.println("max is 30 seconds, setting to 30.");
            sec = 30;
        }

        return sec;
    }

    private boolean askBotVsBot() {
        while (true) {
            System.out.print("Bot vs Bot mode for testing? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("y") || choice.equals("yes") || choice.equals("1")) {
                return true;
            }
            if (choice.equals("n") || choice.equals("no") || choice.equals("0")) {
                return false;
            }
            System.out.println("enter 'y' or 'n'");
        }
    }

    public void setupBoard(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = '-';
            }
        }
        
    }

 
    public void printBoard(){
        char[] letters = {'A','B','C','D','E','F','G','H'};
        System.out.println("  1  2  3  4  5  6  7  8");
        for(int i=0; i < 8; i++){
            System.out.println(letters[i]+ Arrays.toString(board[i]).replace(",", " ").replace("[", " ").replace("]", ""));
        }
        return;
    }

    private char checkWinner() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                char p = board[r][c];
                if (p == '-') continue;
                // Horizontal (4 in a row)
                if (c <= 4 && p == board[r][c + 1] && p == board[r][c + 2] && p == board[r][c + 3]) {
                    return p;
                }
                // Vertical (4 in a column)
                if (r <= 4 && p == board[r + 1][c] && p == board[r + 2][c] && p == board[r + 3][c]) {
                    return p;
                }
            }
        }
        return '-';
    }

    private boolean isBoardFull() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == '-') return false;
            }
        }
        return true;
    }

    private String toCoord(int r, int c) {
        return "" + (char) ('a' + r) + (c + 1);
    }

    private int[] askHumanMove() {
        while (true) {
            System.out.print("Enter your move (e.g., e5): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.length() == 2) {
                int r = input.charAt(0) - 'a';
                int c = input.charAt(1) - '1';
                if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                    if (board[r][c] == '-') {
                        return new int[]{r, c};
                    }
                    System.out.println("Illegal move: that square is already taken.");
                    continue;
                }
            }
            System.out.println("Illegal move: use row A-H and column 1-8 (e.g., e5).");
        }
    }
    private boolean askHumanFirst() {
        while (true) {
            System.out.print("Do you (O) want to go first? (y/n): ");
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) return true;
            if (line.equals("n") || line.equals("no")) return false;
            System.out.println("Please enter y or n.");
        }
    }
}