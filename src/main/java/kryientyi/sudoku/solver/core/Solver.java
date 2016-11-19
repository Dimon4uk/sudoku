package kryientyi.sudoku.solver.core;



import java.util.ArrayList;

/**
 * Created by ִלטענטי on 27.08.2015.
 */
public class Solver {
    private static int sudokuSize = 9;
    private static int blockSize = 3;
    private int[][] board;
    private int[][] status;
    private ArrayList<String> results;

    public Solver(ArrayList<String> cellValues) {
        setBoard(cellValues);
        setStatus();
        solve(board, status,0,0);
    }

    public ArrayList<String> getResults() {
        results = new ArrayList<String>();
        for (int i = 0; i < sudokuSize; i++) {
            for (int j = 0; j < sudokuSize; j++) {
                results.add(""+board[i][j]);
            }
        }
        return results;
    }

    public void setBoard(ArrayList<String> cellValues) {
        board = new int[sudokuSize][sudokuSize];
        for (int i = 0; i < sudokuSize; i++) {
            for (int j = 0; j < sudokuSize; j++) {
                board[i][j] = Integer.parseInt(cellValues.get(i*sudokuSize+j));
            }
        }
    }

    //all pre-set numbers are numbers between 1-9 and the value haven`t set is zero
    public void setStatus() {
        //in slide we need a support array with same size as board to keep of status
        int[][] status = new int[board.length][board[0].length];
        //now we set the status, for each non-zero position we set status as 2 means fixed!
        for (int i = 0; i < sudokuSize; i++) {
            for (int j = 0; j < sudokuSize; j++) {
                status[i][j] = board[i][j] > 0 ? 2 : 0;
            }
        }
        this.status = status;
    }

    /**
     * @param board
     * @param status
     * @param x
     * @param y
     * define the key recursive  searching method
     * board is the values for the game, status is support array to know the status of each position
     * x,y is the coordinates of position we are interest in now
     */
    public static boolean solve(int[][] board, int[][] status, int x, int y) {
        //if we come to the end, we start from (0,0) until (8,8)
        //we come to the end
        if(x == sudokuSize) {
            //we need check of all values are set
            int count = 0; // we need sudokuSize*sudokuSize set values
            for (int i = 0; i < sudokuSize; i++) {
                for (int j = 0; j < sudokuSize; j++) {
                    count += status[i][j] > 0 ? 1 : 0;
                }
            }
            //all set
            if (count == sudokuSize * sudokuSize)
                return true; // great we find one
             else
                return false; //sorry this trial failed
        }
        // other wise we can proceed further
        //if current position has already been set!
        if(status[x][y] > 0) {
            //we step further to next value
            int nextX = x;
            int nextY = y + 1; // we proceed to the right if it is the end, we come to front of next row
            if(nextY == sudokuSize) {
                nextX = x+1;
                nextY = 0;
            }
            // recursive call of next position
            return solve(board, status,nextX, nextY);
        } else {
            //this is the key of the method, we check row/column/block to decide all possible values
            boolean[] used = new boolean[sudokuSize];
            //check row
            for (int i = 0; i < sudokuSize; i++) {
                if(status[x][i] > 0)
                    used[board[x][i]-1] = true;

            }
            //check column
            for (int i = 0; i < sudokuSize; i++) {
                if(status[i][y] > 0)
                    used[board[i][y]-1] = true;

            }
            // check block 3*3 to check nearby values
            // the rows start from 0,3,6, columns also start from 0,3,6
            //this makes sure we start from the current rows
            for (int a = x - (x%blockSize), i = a; i <  a + blockSize; i++) {
                // y settings are similar
                for (int b = y - (y%blockSize), j = b; j < b + blockSize; j++) {
                    if(status[i][j] > 0)
                        used[board[i][j]-1] = true;
                }

            }
            // after the check of all row/column/block, we try to assign each possible value to current position and try next!
            // also remember the use of status array is for further recovery if that try failed, so we set status to 1
            // to be different from 2 (pre-fixed)so later we can reverse the settings
            for (int i = 0; i < used.length; i++) {
                //notice only those unused values can be set here
                if(!used[i]) {
                    // we set and proceed and lastly reverse the settings for next iteration!
                    status[x][y] = 1; // 1 as we-set status, different from 0-nonset and 2-pre-fixed
                    board[x][y] = i+1;// index + 1 is the value
//                    System.out.println("["+ x + "," + y + "] = " + counter);
                    // we repeat the index increasing operation

                    int nextX = x;
                    int nextY = y + 1; // we proceed to the right if it is the end, we come to front of next row
                    if(nextY == sudokuSize) {
                        nextX = x+1;
                        nextY = 0;
                    }
                    // recursive call of next position
                    if (solve(board, status,nextX, nextY)) {
                        return true;
                    }

                    // now it means the setting failed we should reverse the setting to try next value
                    for (int j = 0; j < sudokuSize; j++) {
                        for (int k = 0; k < sudokuSize; k++) {
                            // only reverse-set those values behind current (x,y) position
                            if(j > x || (j == x && k >= y)){
                                //only reverse those we-set nodes
                                if(status[j][k] == 1){
                                    status[j][k] = 0;
                                    board[j][k] = 0;
                                }
                            }
                        }
                    }

                }
            }

        }

        return false;
    }
}
