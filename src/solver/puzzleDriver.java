package solver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//A modified version from SFU CMPT 225 assignment 1 solution
//source: https://www.cs.sfu.ca/~ishinkar/teaching/spring23/cmpt225/assignments.html
public class puzzleDriver {
    public static int SIZE = 4;
    public int[] zero={0,0};

    int board[][];

    private void checkBoard() throws BadBoardException {
        int[] vals = new int[SIZE * SIZE];

        // check that the board contains all number 0...15
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j]<0 || board[i][j]>=SIZE*SIZE)
                    throw new BadBoardException("found tile " + board[i][j]);
                vals[board[i][j]] += 1;
            }
        }

        for (int i = 0; i < vals.length; i++)
            if (vals[i] != 1)
                throw new BadBoardException("tile " + i +
                        " appears " + vals[i] + "");

    }

    /**
     * @param fileName
     * @throws FileNotFoundException if file not found
     * @throws BadBoardException     if the board is incorrectly formatted Reads a
     *                               board from file and creates the board
     */
    public puzzleDriver(String fileName) throws IOException, BadBoardException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        this.SIZE= Integer.parseInt(br.readLine());
        board = new int[SIZE][SIZE];
        int c1, c2, s;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                c1 = br.read();
                c2 = br.read();
                s = br.read(); // skip the space
                if (s != ' ' && s != '\n') {
                    br.close();
                    System.out.println(i+","+j);
                    System.out.println(s==' ');
                    System.out.println(s=='\n');
                    System.out.println((char)s);
                    throw new BadBoardException("error in line " + i);
                }
                if (c1 == ' ')
                    c1 = '0';
                if (c2 == ' ')
                    c2 = '0';
                board[i][j] = 10 * (c1 - '0') + (c2 - '0');
                if(board[i][j]==0){
                    zero[0]=i;
                    zero[1]=j;
                }
            }
        }
        checkBoard();
        br.close();
    }

    private class Pair {
        int i, j;

        Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private Pair findCoord(int tile) {
        int i = 0, j = 0;
        for (i = 0; i < SIZE; i++)
            for (j = 0; j < SIZE; j++)
                if (board[i][j] == tile)
                    return new Pair(i, j);
        return null;
    }

    public int getSIZE(){
        return this.SIZE;
    }

    public int[][] toArray(){
        int[][] temp=new int[this.SIZE][this.SIZE];
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                temp[i][j]=this.board[i][j];
            }
        }
        return temp;
    }

    public int[] getZero(){
        int[] temp=new int[2];
        System.arraycopy(zero, 0, temp, 0, 2);
        return temp;
    }

    public byte[][] to2DbyteArr(){
        byte[][] temp=new byte[this.SIZE][this.SIZE];
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                temp[i][j]=(byte)this.board[i][j];
            }
        }
        return temp;
    }
}
