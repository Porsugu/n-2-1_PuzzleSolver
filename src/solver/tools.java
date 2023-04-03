package solver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class tools {
    public String sqArrtoString(int[][] a) {
        String temp = "";
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if(a[i][j]<10){
                    temp+=" ";
                }
                temp += ""+a[i][j];
            }
        }
        return temp;
    }

    public String sqArrtoString(int[][]a, int maxDigit){
        String temp="";
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                for(int m = 0; m< maxDigit- (a[i][j] + "").length(); m++){
                    temp+=" ";
                }
                temp += a[i][j];
            }
        }
        return temp;
    }


    public void prtsqArr(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void prtsqArrInSq(int[][] a){
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if(a[i][j]<10){
                    System.out.print(" ");
                }
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void prtsqArrInSq(byte[][] a){
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if(a[i][j]<10){
                    System.out.print(" ");
                }
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int[][] copy2DArr(int[][] arr){
        int length=arr.length;
        int[][] temp=new int[length][length];
        for(int i=0;i<length;i++){
            for(int j=0;j<length;j++){
                temp[i][j]=arr[i][j];
            }
        }
        return temp;
    }

    public void BoardGen(int dimension,int shuffleTime,String output) throws IOException {
        //Gen endBoard for shuffle
        int[][] endBoard=new int[dimension][dimension];
        for(int i=0;i<dimension;i++){
            for(int j=0;j<dimension;j++){
                endBoard[i][j]=i*dimension+j+1;
            }
        }
        endBoard[dimension-1][dimension-1]=0;

        //Shuffle the board
        int count=0;
        int move,lastMove=4;
        int[]zero={dimension-1,dimension-1};
        while(count<shuffleTime){
            move=new Random().nextInt(4);
            if(move==lastMove){
                continue;
            }
            if(move==0 && zero[0]<dimension-1){    //move a tile up
                endBoard[zero[0]][zero[1]]=endBoard[zero[0]+1][zero[1]];
                endBoard[zero[0]+1][zero[1]]=0;
                zero[0]++;
            }
            else if(move==1 && zero[1]>0){  //move a tile right
                endBoard[zero[0]][zero[1]]=endBoard[zero[0]][zero[1]-1];
                endBoard[zero[0]][zero[1]-1]=0;
                zero[1]--;
            }
            else if(move==2 && zero[0]>0){  //move a tile down
                endBoard[zero[0]][zero[1]]=endBoard[zero[0]-1][zero[1]];
                endBoard[zero[0]-1][zero[1]]=0;
                zero[0]--;
            }
            else if(move==3 && zero[1]<dimension-1){  //move a tile left
                endBoard[zero[0]][zero[1]]=endBoard[zero[0]][zero[1]+1];
                endBoard[zero[0]][zero[1]+1]=0;
                zero[1]++;
            }
            else{
                continue;
            }
            count++;
            lastMove=move;
        }

        //Gen a String board with correct format
        String strBoard="",tile;
        int maxDigit=((dimension*dimension-1)+"").length();
        strBoard+=dimension+"\n";
        for(int i=0;i<dimension;i++){
            for (int j=0;j<dimension;j++){
                tile="";
                for(int m=0;m<maxDigit-(endBoard[i][j]+"").length();m++){
                    tile+=" ";
                }
                if(endBoard[i][j]==0){
                    tile+=" ";
                }
                else{
                    tile+=endBoard[i][j];
                }
                strBoard+=tile;
                if(j<dimension-1){
                    strBoard+=" ";
                }
                else{
                    strBoard+="\n";
                }
            }
        }

        //Output
        FileWriter file = new FileWriter(output,false);	//true means not overwriting
        BufferedWriter buffer = new BufferedWriter(file);
        buffer.write(strBoard);
        //buffer.newLine();
        buffer.close();
    }


}
