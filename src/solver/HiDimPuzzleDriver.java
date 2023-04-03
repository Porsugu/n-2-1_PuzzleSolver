package solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HiDimPuzzleDriver {
    private int dimension;
    private int[][] board;
    private int[]zero;
    public HiDimPuzzleDriver(String fileName) throws FileNotFoundException {
        Scanner s=new Scanner(new File(fileName));
        this.dimension=Integer.parseInt(s.nextLine());
        int maxDigit=((dimension*dimension-1)+"").length();
        this.board=new int[dimension][dimension];
        this.zero=new int[2];
        String line,tile;
        int index=0,blank,col;
        for(int i=0;i<dimension;i++){
            index=0;
            line=s.nextLine();
            while(index<line.length()){
                col=index/(maxDigit+1);
                tile=line.substring(index,index+maxDigit);
                blank=0;
                while(blank<tile.length()){
                    if(tile.charAt(blank)==' '){
                        blank++;
                    }
                    else{
                        break;
                    }
                }
                if(blank==tile.length()){
                    board[i][col]=0;
                    this.zero[0]=i;
                    this.zero[1]=col;
                }
                else{
                    board[i][col]=Integer.parseInt(tile.substring(blank));
                }
                index+=(maxDigit+1);
            }
        }
    }

    public int[] getZero() {
        int[] temp={this.zero[0],this.zero[1]};
        return temp;
    }

    public int getDimension(){
        return this.dimension;
    }
    public int[][] toArray(){
        int[][] temp=new int[this.dimension][this.dimension];
        for(int i=0;i<this.dimension;i++){
            for(int j=0;j<this.dimension;j++){
                temp[i][j]=this.board[i][j];
            }
        }
        return temp;
    }
}
