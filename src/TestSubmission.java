import answerChecker.checkDriver;
import answerChecker.statCollector;
import solver.*;

import java.io.IOException;

public class TestSubmission {
    public static void main(String[] args) throws BadBoardException, IOException {
        if(true){
            statCollector s=new statCollector();
            long start,end;
            String fileName;
            for(int k=1;k<=40;k++){
                fileName="board";
                if(k<10){
                    fileName+=0;
                }
                fileName+=(k+".txt");
                start=System.currentTimeMillis();
                puzzleSolverSubmission solver = new puzzleSolverSubmission(fileName,"sol"+fileName.substring(5));
                end=System.currentTimeMillis();
                puzzleDriver puzzle=new puzzleDriver(fileName);
                checkDriver check=new checkDriver(puzzle.toArray(),solver.getSolution(),false);
                // dimension, runtime,filename, isValid, stepUsed
                s.add(solver.getDimension(), end-start,fileName,check.ansChecker(),solver.getStepUsed());
            }
            s.genTable();
        }
        else{
            long start=System.currentTimeMillis();
            String board="board74.txt";
            puzzleSolver6v2 solver = new puzzleSolver6v2(board,"sol"+board.substring(5),true);
            long end=System.currentTimeMillis();
            String sol=solver.getSolution();
            HiDimPuzzleDriver puzzle=new HiDimPuzzleDriver(board);
            checkDriver check=new checkDriver(puzzle.toArray(),sol,true);
            System.out.println("Solution is valid: "+check.ansChecker());
            System.out.println("Num of steps: "+ solver.getStepUsed());
            System.out.println("runtime: "+(end-start)/1000F +" sec");
        }
    }

}
