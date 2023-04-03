package answerChecker;

import java.util.Scanner;

public class checkDriver {
    private int[][] board;
    private String solution;
    Boolean showStep;
    public checkDriver(int[][] board,String solution,Boolean showStep){
        this.board=new int[board.length][board.length];
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board.length;j++){
                this.board[i][j]=board[i][j];
            }
        }
        this.solution=solution;
        this.showStep=showStep;

    }
    public Boolean ansChecker(){
        int dim=this.board.length;
        Scanner s=new Scanner(this.solution),s1;
        String line=s.nextLine(),move;
        Boolean breakGate;
        int[][]winningState=new int[dim][dim];
        for(int i=0;i<dim;i++){
            for(int j=0;j<dim;j++){
                winningState[i][j]=i*dim+j+1;
            }
        }
        winningState[dim-1][dim-1]=0;
        int num,row=0,col=0;
        if(this.showStep){
            prt2D(board);
        }
        while(s.hasNextLine()){
            s1=new Scanner(line);
            num=Integer.parseInt(s1.next());
            move=s1.next();
            //System.out.println(num+" "+move);
            breakGate=false;
            for(int i=0;i<dim;i++){
                for(int j=0;j<dim;j++){
                    if(board[i][j]==num){
                        row=i;
                        col=j;
                        breakGate=true;
                        break;
                    }
                }
                if(breakGate){
                    break;
                }
            }
            move(this.board,row,col,num, move);
            if(this.showStep){
                prt2D(board);
            }
            line=s.nextLine();
        }
        s1=new Scanner(line);
        num=Integer.parseInt(s1.next());
        move=s1.next();
        breakGate=false;
        for(int i=0;i<dim;i++){
            for(int j=0;j<dim;j++){
                if(board[i][j]==num){
                    row=i;
                    col=j;
                    breakGate=true;
                    break;
                }
            }
            if(breakGate){
                break;
            }
        }
        move(this.board,row,col,num, move);
        if(this.showStep){
            prt2D(board);
        }
        for(int i=0;i<dim;i++){
            for(int j=0;j<dim;j++){
                if(board[i][j]!=winningState[i][j]){
                    return false;
                }
            }
        }
        return true;
    }
    private void move(int[][] board,int row,int col,int num,String move){
        if(move.equals("U")){
            board[row-1][col]=num;
            board[row][col]=0;
        }
        else if(move.equals("D")){
            board[row+1][col]=num;
            board[row][col]=0;
        }
        else if(move.equals("R")){
            board[row][col+1]=num;
            board[row][col]=0;
        }
        else{
            board[row][col-1]=num;
            board[row][col]=0;
        }
    }
    private void prt2D(int[][] arr){
        for(int i=0;i<arr.length;i++){
            for(int j=0;j< arr.length;j++){
                if(arr[i][j]<10){
                    System.out.print(" ");
                }
                System.out.print(arr[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
