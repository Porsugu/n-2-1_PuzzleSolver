import solver.tools;

import java.io.IOException;

public class boardGenerator {
    public  static void main(String[] arg) throws IOException {
        int dimension=3,start=1;
        tools tool=new tools();
//        String board;
//        while(dimension<=30){
//            for(int k=start;k<start+5;k++){
//                board="board";
//                if(k<10){
//                    board+=0;
//                }
//                board+=k+".txt";
//                tool.BoardGen(dimension,(int)Math.pow(dimension,4),board);
//            }
//            dimension++;
//            start+=5;
//        }


        tool.BoardGen(dimension,1,"test.txt");
        System.out.println("Boards Generated!!");

    }
}
