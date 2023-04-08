package solver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//solver2+clearing system
//hi : )
public class puzzleSolver6v2 {
    public int[][] ansArr;
    private String initStandardBoard,solution,endState,zeroTile;
    public HashMap<Integer,int[]> indexToCorrectRC=new HashMap<>();
    private HashMap<String,int[]>tileToCorrectRC=new HashMap<>();
    public HashMap<Integer,String> indexToTile=new HashMap<>();
    public HashMap<String,Integer> tileToIndex=new HashMap<>();
    public HashMap<String,String> HashClostMap=new HashMap<>();
    public HashMap<Integer,int[]> indexToRC=new HashMap<>();
    public HashMap<Integer,HashSet<String>> rowToTile=new HashMap<>(),colToTile=new HashMap<>();
    private int dimension,boundRC,size,workingPriority,jump,numBoardLength,maxFixedRow,maxFixedCol,boundDBtask,workingIndex,tileLeft,stepUsed,maxManhattan,maxDigit;
    private Boolean goSolveRow,goSolveCol,show,solvedNewR,solvedNewC,firstRC;
    private Boolean putLargeDownRight,putLessToCorner,LargeToLess,FinalManhattan,addGate,solved;
    List<String>[] rowTask,colTask;
    Queue<String> rowToDoList=new LinkedList<>(),colToDoList=new LinkedList<>();
    HashSet<String> doneTile=new HashSet<>(),nearestRCTile=new HashSet<>();
    String[] concernTile= new String[2];
    int[] lockTile;
    private PriorityQueue<Integer> scoreQ;

    // Constructor
    public puzzleSolver6v2(String input, String output, Boolean showRunningInfo) throws IOException {
        JustaBitOfInitialSetUp(input,showRunningInfo);
        //run the search
        String fin = A_star();
        GenSolution(fin);
        GenSolTxt(output);
    }

    //Method set of searching
    //Maybe it is greedy,but idk
    private String A_star(){
        this.scoreQ=new PriorityQueue<>();
        String poped_str = null;
        HashMap<Integer,LinkedList<String>> taskMap=new HashMap<>();
        taskMap.put(workingPriority,new LinkedList<>());
        taskMap.get(workingPriority).add(this.initStandardBoard);
        this.HashClostMap.put(this.initStandardBoard,"S");
        this.scoreQ.add(workingPriority);
        while(!taskMap.isEmpty()){
            poped_str=taskMap.get(this.scoreQ.peek()).remove();
            if(this.solved){
                break;
            }
            if(taskMap.get(this.scoreQ.peek()).isEmpty()){
                taskMap.remove(this.scoreQ.peek());
                this.scoreQ.remove();
            }
            this.addGate=true;
            addNeighbour(poped_str,taskMap);
//            if(!taskMap.containsKey(workingPriority)){
//                workingPriority++;
//                while(true){
//                    if(taskMap.containsKey(workingPriority)){
//                        break;
//                    }
//                    workingPriority++;
//                }
//            }
        }
        return poped_str;
//        if(solved){
//            return poped_str;
//        }
//        else{
//            return null;
//        }
    }

    //Could add at most 4 extra board state
    private void addNeighbour(String current, HashMap<Integer,LinkedList<String>> a) {  //must be UDRL
//        if(this.tileLeft>9){
//            if(goSolveRow){
//                AddNeighbourByDir(current,a,'D');
//                AddNeighbourByDir(current,a,'R');
//                AddNeighbourByDir(current,a,'L');
//                AddNeighbourByDir(current,a,'U');
//            }
//            else{   //goSolveCol
//
//                AddNeighbourByDir(current,a,'R');
//                AddNeighbourByDir(current,a,'U');
//                AddNeighbourByDir(current,a,'D');
//                AddNeighbourByDir(current,a,'L');
//            }
//        }
//        else{
//            AddNeighbourByDir(current,a,'D');
//            AddNeighbourByDir(current,a,'R');
//            AddNeighbourByDir(current,a,'L');
//            AddNeighbourByDir(current,a,'U');
//        }
        AddNeighbourByDir(current,a,'D');
        AddNeighbourByDir(current,a,'R');
        AddNeighbourByDir(current,a,'L');
        AddNeighbourByDir(current,a,'U');


    }

    //Could add board state into the to do list and this,HashCloseMap
    public void AddNeighbourByDir(String current, HashMap<Integer,LinkedList<String>> a,char MoveDir){
        if(!this.addGate){
            return;
        }
//        if(workingIndex<this.boundDBtask && !doneTileAreDone(current)){
//            return;
//        }
        Boolean conditionToAdd=false;
        int tileIndex=0;
        int currentZero = Integer.parseInt(current.substring(this.numBoardLength)),
                newZero;
        int[] currentCoordinate=this.indexToCorrectRC.get(currentZero);
        String tileTomove="",newBoard;
        if((currentCoordinate[0] < this.boundRC) && MoveDir=='U'){
            tileIndex=currentZero+this.jump;
            tileTomove=current.substring(tileIndex,tileIndex+this.maxDigit);
            //conditionToAdd=!doneTile.contains(tileTomove);
            conditionToAdd=this.lockTile[tileToNum(tileTomove)]==0;
        }
        else if(currentCoordinate[0] > 0 && MoveDir=='D'){
            tileIndex=currentZero-this.jump;
            tileTomove=current.substring(tileIndex,tileIndex+this.maxDigit);
            //conditionToAdd=!doneTile.contains(tileTomove);
            conditionToAdd=this.lockTile[tileToNum(tileTomove)]==0;
        }
        else if(currentCoordinate[1] > 0 && MoveDir=='R'){
            tileIndex=currentZero-this.maxDigit;
            tileTomove=current.substring(tileIndex,currentZero);
            //conditionToAdd=!doneTile.contains(tileTomove);
            conditionToAdd=this.lockTile[tileToNum(tileTomove)]==0;
        }
        else if(currentCoordinate[1] < this.boundRC) {   //MoveDir=='L'
            tileIndex=currentZero+this.maxDigit;
            tileTomove=current.substring(tileIndex,tileIndex+this.maxDigit);
            //conditionToAdd=!doneTile.contains(tileTomove);
            conditionToAdd=this.lockTile[tileToNum(tileTomove)]==0;
        }
        if(conditionToAdd){
            newZero = tileIndex;
            if(newZero<currentZero){
                newBoard=current.substring(0,newZero)+this.zeroTile+current.substring(newZero+this.maxDigit,currentZero)+tileTomove+current.substring(currentZero+this.maxDigit,this.numBoardLength);
            }
            else{   //currentZero<newZero
                newBoard=current.substring(0,currentZero)+tileTomove+current.substring(currentZero+this.maxDigit,newZero)+this.zeroTile+current.substring(newZero+this.maxDigit,this.numBoardLength);
            }

            newBoard=StandardStringBuilder(newBoard,newZero);
            if(!this.HashClostMap.containsKey(newBoard)){
                int temp_Priority=heuristicFunction(newBoard,false);
                if(temp_Priority==0){
                    if(this.tileLeft>9){
                        this.addGate=false;
                        workingPriority=Integer.MAX_VALUE;
                        if(workingIndex<this.boundDBtask){
                            a.clear();
                            this.scoreQ.clear();
                            rebuildHashCloseMap(current);
                            this.tileLeft--;
                            if(this.show){
                                System.out.println("board:");
                                strTo2D(newBoard);
                                //System.out.println(newBoard);
                                System.out.println("b4: ");
                                System.out.println("working index: "+this.workingIndex);
                                System.out.println("goSolveRow: "+this.goSolveRow);
                                System.out.println("goSolveCol: "+this.goSolveCol);
                                System.out.println("tile done:|"+this.concernTile[0]+"|");
                                System.out.println("index of done tile in the string: "+indexOfTileInBoard(newBoard,this.concernTile[0]));
                                System.out.println("RC of done tile in the board: "+this.indexToRC.get(indexOfTileInBoard(newBoard,this.concernTile[0]))[0]+", "+this.indexToRC.get(indexOfTileInBoard(current,this.concernTile[0]))[1]);
                                System.out.println("MD of done concern tile: "+ManhattanDis(indexOfTileInBoard(newBoard,this.concernTile[0]),this.tileToIndex.get(this.concernTile[0])));
                                System.out.println("tile left: "+this.tileLeft);
                                System.out.println("to do list is empty: "+a.isEmpty());
                            }
                            workingIndex++;
                            //this.doneTile.add(concernTile[0]);
                            this.lockTile[tileToNum(concernTile[0])]=1;

                            if(this.show){
                                System.out.println("A7: ");
                                System.out.println("new working index: "+this.workingIndex);
                            }

                            if(goSolveRow){
                                this.concernTile[0]=rowToDoList.remove();
//                                while(this.doneTile.contains(this.concernTile[0])){
//                                    this.concernTile[0]=rowToDoList.remove();
//                                }
                                while(this.lockTile[tileToNum(concernTile[0])]==1){
                                    this.concernTile[0]=rowToDoList.remove();
                                }
                                if(workingIndex==this.boundDBtask){
                                    this.concernTile[1]=rowToDoList.remove();
//                                    while(this.doneTile.contains(this.concernTile[1])){
//                                        this.concernTile[1]=rowToDoList.remove();
//                                    }
                                    while(this.lockTile[tileToNum(concernTile[1])]==1){
                                        this.concernTile[1]=rowToDoList.remove();
                                    }
                                }
                            }
                            else if(goSolveCol){
                                this.concernTile[0]=colToDoList.remove();
//                                while(this.doneTile.contains(this.concernTile[0])){
//                                    this.concernTile[0]=colToDoList.remove();
//                                }
                                while(this.lockTile[tileToNum(concernTile[0])]==1){
                                    this.concernTile[0]=colToDoList.remove();
                                }
                                if(workingIndex==this.boundDBtask){
                                    this.concernTile[1]=colToDoList.remove();
//                                    while(this.doneTile.contains(this.concernTile[1])){
//                                        this.concernTile[1]=colToDoList.remove();
//                                    }
                                    while(this.lockTile[tileToNum(concernTile[1])]==1){
                                        this.concernTile[1]=colToDoList.remove();
                                    }
                                }
                            }

                            while (workingIndex==this.boundDBtask){
                                int MD1=ManhattanDis(indexOfTileInBoard(newBoard,this.concernTile[0]),this.tileToIndex.get(this.concernTile[0]));
                                int MD2=ManhattanDis(indexOfTileInBoard(newBoard,this.concernTile[1]),this.tileToIndex.get(this.concernTile[1]));
                                int targetIndex;
                                if(MD1==0 && MD2==0){
                                    this.putLargeDownRight=false;
                                    this.putLessToCorner=false;
                                    this.LargeToLess=false;
                                    this.FinalManhattan=true;
                                    break;
                                }
                                tileIndex=indexOfTileInBoard(newBoard,this.concernTile[1]);
                                targetIndex=this.tileToIndex.get(this.zeroTile);
                                MD1=ManhattanDis(tileIndex,targetIndex);
                                if(MD1!=0){
                                    this.putLargeDownRight=true;
                                    this.putLessToCorner=false;
                                    this.LargeToLess=false;
                                    this.FinalManhattan=false;
                                    break;
                                }
                                tileIndex=indexOfTileInBoard(newBoard,this.concernTile[0]);
                                targetIndex=this.tileToIndex.get(this.concernTile[1]);
                                MD1=ManhattanDis(tileIndex,targetIndex);
                                if(MD1==0){
                                    this.putLargeDownRight=false;
                                    this.putLessToCorner=true;
                                    this.LargeToLess=false;
                                    this.FinalManhattan=false;
                                    break;
                                }
                                this.putLargeDownRight=true;
                                this.putLessToCorner=false;
                                this.LargeToLess=false;
                                this.FinalManhattan=false;
                                break;
                            }
                            if(this.show){
                                System.out.println("goSolveRow: "+this.goSolveRow);
                                System.out.println("goSolveCol: "+this.goSolveCol);
                                System.out.println("to do list is empty: "+a.isEmpty());
                                if(workingIndex<this.boundDBtask){
                                    System.out.println("new concern tile :|"+this.concernTile[0]+"|");
                                    System.out.println("MD of new concern tile: "+ManhattanDis(indexOfTileInBoard(newBoard,this.concernTile[0]),this.tileToIndex.get(this.concernTile[0])));
                                }
                                else{
                                    System.out.println("new concern tiles :|"+this.concernTile[0]+"|"+" and "+"|"+this.concernTile[1]+"|");
                                    System.out.println("MDs of new concern tiles: "+ManhattanDis(indexOfTileInBoard(newBoard,this.concernTile[0]),this.tileToIndex.get(this.concernTile[0]))+", "+ManhattanDis(indexOfTileInBoard(newBoard,this.concernTile[1]),this.tileToIndex.get(this.concernTile[1])));
                                    System.out.println("Special case: 2 tiles left for the row or col, start using manhattan dis to move the concerns tile to special place with 4 steps");
                                    System.out.println("Step 1: move |"+concernTile[1]+"| to bottom right corner");
                                }
                                System.out.println("tile done:");
                                for(String k:this.doneTile){
                                    System.out.print("|"+k+"| ");
                                }
                                System.out.println("\n"+"---------------------------"+"\n");
                            }
                        }
                        else if(workingIndex==this.boundDBtask){
                            if(this.putLargeDownRight){
                                a.clear();
                                this.scoreQ.clear();
                                rebuildHashCloseMap(current);
                                //this.doneTile.add(this.concernTile[1]);
                                this.lockTile[tileToNum(concernTile[1])]=1;
                                if(this.show){

                                    System.out.println("Step1 is done: |"+concernTile[1]+"| has been placed into the bottom right corner");
                                    System.out.println("board:");
                                    strTo2D(newBoard);
                                    System.out.println("done tiles:");
                                    for(String k:this.doneTile){
                                        System.out.print("|"+k+"| ");
                                    }
                                    System.out.println();
                                    System.out.println("Step 2: putting |"+concernTile[0]+"| "+"into correct position of "+"|"+concernTile[1]+"|");
                                }


                                this.putLargeDownRight=false;
                                this.putLessToCorner=true;
                                this.LargeToLess=false;
                                this.FinalManhattan=false;
                            }
                            else if(this.putLessToCorner){
                                a.clear();
                                this.scoreQ.clear();
                                rebuildHashCloseMap(current);
//                                this.doneTile.add(this.concernTile[0]);
//                                this.doneTile.remove(this.concernTile[1]);
                                this.lockTile[tileToNum(concernTile[0])]=1;
                                this.lockTile[tileToNum(concernTile[1])]=0;
                                if(this.show){
                                    System.out.println("---------------------------"+"\n");
                                    System.out.println("Step2 is done:"+"|"+concernTile[0]+"| "+"has been placed into correct position of "+"|"+concernTile[1]+"|");
                                    System.out.println("board:");
                                    strTo2D(newBoard);
                                    if(goSolveRow){
                                        System.out.println("Step3: move "+"|"+concernTile[1]+"| "+"under "+"|"+concernTile[0]+"|");
                                    }
                                    else if(goSolveCol){
                                        System.out.println("Step3: "+" move |"+concernTile[1]+"| "+"to the right of "+"|"+concernTile[0]+"|");
                                    }
                                    System.out.println("done tiles:");
                                    for(String k:this.doneTile){
                                        System.out.print("|"+k+"| ");
                                    }
                                    System.out.println();
                                }

                                this.putLargeDownRight=false;
                                this.putLessToCorner=false;
                                this.LargeToLess=true;
                                this.FinalManhattan=false;
                            }
                            else if(this.LargeToLess){
                                a.clear();
                                this.scoreQ.clear();
                                rebuildHashCloseMap(current);
                                //this.doneTile.remove(this.concernTile[0]);
                                this.lockTile[tileToNum(concernTile[0])]=0;
                                if(this.show){
                                    System.out.println("---------------------------"+"\n");
                                    System.out.println("Step3 is done: |"+concernTile[1]+"| "+" has been placed to the right of "+"|"+concernTile[0]+"|");
                                    System.out.println("board:");
                                    strTo2D(newBoard);
                                    System.out.println("Step 4:"+"move |"+this.concernTile[0]+"| and |"+this.concernTile[1]+"| to their correct position");
                                    System.out.println("done tiles:");
                                    for(String k:this.doneTile){
                                        System.out.print("|"+k+"| ");
                                    }
                                    System.out.println();
                                }

                                this.putLargeDownRight=false;
                                this.putLessToCorner=false;
                                this.LargeToLess=false;
                                this.FinalManhattan=true;

                            }
                            else if(this.FinalManhattan){
                                a.clear();
                                this.scoreQ.clear();
                                rebuildHashCloseMap(current);
                                this.tileLeft-=2;
//                                this.doneTile.add(concernTile[0]);
//                                this.doneTile.add(concernTile[1]);
                                this.lockTile[tileToNum(concernTile[0])]=1;
                                this.lockTile[tileToNum(concernTile[1])]=1;
                                this.putLargeDownRight=false;
                                this.putLessToCorner=false;
                                this.LargeToLess=false;
                                this.FinalManhattan=false;
                                String tileBuffer=concernTile[0];
                                if(this.tileLeft>9){
                                    if(goSolveCol){
                                        goSolveCol=false;
                                        goSolveRow=true;
                                        //System.out.println("pre maxFixedCol: "+this.maxFixedCol);
                                        this.maxFixedCol++;
                                        this.solvedNewC=true;
                                        workingIndex=this.maxFixedCol+1;
                                        while(this.lockTile[tileToNum(concernTile[0])]==1){
                                            concernTile[0]=this.rowToDoList.remove();
                                        }
                                    }
                                    else if(goSolveRow){
                                        goSolveRow=false;
                                        goSolveCol=true;
                                        this.maxFixedRow++;
                                        this.solvedNewR=true;
                                        workingIndex=this.maxFixedRow+1;
                                        while(this.lockTile[tileToNum(concernTile[0])]==1){
                                            concernTile[0]=this.colToDoList.remove();
                                        }
                                    }
                                }

                                if(this.show){
                                    System.out.println("---------------------------"+"\n");
                                    System.out.println("Step4 is done:"+"|"+tileBuffer+"| and |"+this.concernTile[1]+"| have been placed in correct position");
                                    System.out.println("board:");
                                    strTo2D(newBoard);
                                    System.out.println("tiles done:|"+tileBuffer+"|"+" and "+"|"+this.concernTile[1]+"|");
                                    System.out.println("tile left: "+this.tileLeft);
                                    System.out.println("MD of first concern tile: "+ManhattanDis(indexOfTileInBoard(current,this.concernTile[0]),this.tileToIndex.get(this.concernTile[0])));
                                    System.out.println("MD of second concern tile: "+ManhattanDis(indexOfTileInBoard(current,this.concernTile[1]),this.tileToIndex.get(this.concernTile[1])));
//                                    System.out.println("done tiles:");
//                                    for(String k:this.doneTile){
//                                        System.out.print("|"+k+"| ");
//                                    }
//                                    System.out.println();
                                    if(this.tileLeft>9){
                                        if(this.goSolveCol){
                                            System.out.println("Next step: solve the row");
                                        }
                                        else if(this.goSolveRow){
                                            System.out.println("Next step: solve the col");
                                        }
                                        System.out.println("goSolveRow: "+this.goSolveRow);
                                        System.out.println("goSolveCol: "+this.goSolveCol);
                                        System.out.println("New concern tile: "+ this.concernTile[0]);
                                        System.out.println("done tiles:");
                                        for(String k:this.doneTile){
                                            System.out.print("|"+k+"| ");
                                        }
                                        System.out.println();
                                    }
                                    else{
                                        System.out.println("9 tiles left: "+(this.tileLeft==9));
                                        System.out.println("3*3 board on the bottom right corner, start using h(n)=misplaced(n) ");
                                    }
                                    System.out.println("tile done:");
                                    for(String k:this.doneTile){
                                        System.out.print("|"+k+"| ");
                                    }
                                    System.out.println("\n"+"---------------------------"+"\n");

                                }
                            }
                        }
                    }
                    else{
                        this.solved=true;
                    }
                }

                if(temp_Priority==0 && this.tileLeft>9){
                    temp_Priority=heuristicFunction(newBoard,false);
                }
                if(!a.containsKey(temp_Priority)){
                    a.put(temp_Priority,new LinkedList<>());
                    this.scoreQ.add(temp_Priority);
                }
                a.get(temp_Priority).add(newBoard);
                this.HashClostMap.put(newBoard,current);
            }
        }

    }

    //the heuristic function for the searching, this version could only support heuristic 6
    public int heuristicFunction(String board,Boolean showInfo) {
        int score = 0,tempIndex,MDof1,MDof2,tileIndex,targetIndex,currentZero;
        //Heuristic 1: count misplaced tile

        //Heuristic 2: total Manhattan distances

        //Heuristic 3: count Manhattan only if the corresponding row/col is not fixed

        //Heuristic 4: count misplace according to concern row/ col or both

        //Heuristic 5: ManhattanDis of concern tile(s), note: cant solve last two tile

        //Heuristic 6: idk what is this but works :)
        if(this.tileLeft>9){
            //A better single tile pushing heuristic function
            //step1: if the manDis of the tile to its correct postion =0, return 0, else go step2.
            //step2: if the manDis of blank space to the tile<=1, return the manDis of the tile to its correct postion, else return the sum of their MD+2*makxmanhattan
            if(this.workingIndex<this.boundDBtask){
                tileIndex=indexOfTileInBoard(board,this.concernTile[0]);
                targetIndex=this.tileToIndex.get(this.concernTile[0]);
                MDof1=ManhattanDis(tileIndex,targetIndex);
                if(MDof1!=0){ //dont del/ over-modify it, it works :)
                    currentZero=Integer.parseInt(board.substring(this.numBoardLength));
                    MDof2=ManhattanDis(tileIndex,currentZero);
                    if(MDof2>1){
                        score=MDof1+MDof2+this.maxManhattan+this.maxManhattan;
                    }
                    else{
                        score=MDof1+this.maxManhattan;
                        //score=MDof1;
                    }
                }
                else{
                    score=0;
                }

                //A slower pushing heuristic but also works :)
                //score=ManhattanDis(indexOfTileInBoard(board,this.concernTile[0]),this.concernTile[0]);
                if(showInfo){
                    System.out.println("working index: "+ this.workingIndex);
                    System.out.println("Score: "+score);
                }
            }
            else{

                if(this.putLargeDownRight){
                    tileIndex=indexOfTileInBoard(board,this.concernTile[1]);
                    targetIndex=this.tileToIndex.get(this.zeroTile);
                    MDof1=ManhattanDis(tileIndex,targetIndex);
                    if(MDof1!=0){
                        currentZero=Integer.parseInt(board.substring(this.numBoardLength));
                        MDof2=ManhattanDis(tileIndex,currentZero);
                        if(MDof2>1){
                            score=MDof1+MDof2+this.maxManhattan+this.maxManhattan;
                        }
                        else{
                            score=MDof1+this.maxManhattan;
                            //score=MDof1;
                        }
                    }
                    else{
                        score=0;
                    }
                    //simply cal the man Dis from the larger tile to the btm right corner
                    //score= ManhattanDis(indexOfTileInBoard(board,this.concernTile[1]),this.tileToIndex.get(this.zeroTile));
                }
                else if(this.putLessToCorner){
                    tileIndex=indexOfTileInBoard(board,this.concernTile[0]);
                    targetIndex=this.tileToIndex.get(this.concernTile[1]);
                    MDof1=ManhattanDis(tileIndex,targetIndex);
                    if(MDof1!=0){
                        currentZero=Integer.parseInt(board.substring(this.numBoardLength));
                        MDof2=ManhattanDis(tileIndex,currentZero);
                        if(MDof2>1){
                            score=MDof1+MDof2+this.maxManhattan+this.maxManhattan;
                        }
                        else{
                            score=MDof1+this.maxManhattan;
                            //score=MDof1;
                        }
                    }
                    else{
                        score=0;
                    }
                    //simply cal the man Dis from the smaller tile to the correct pos of the larger tile
                    //score= ManhattanDis(indexOfTileInBoard(board,this.concernTile[0]),this.tileToIndex.get(this.concernTile[1]));
                }
                else if(this.LargeToLess){
                    //cal the man Dis from the larger tile to the pos under or right of the correct pos of the larger tile(should be the current pos of the smaller tile
                    if(goSolveRow){//this.tileToIndex.get(this.concernTile[1])+this.jump
                        tileIndex=indexOfTileInBoard(board,this.concernTile[1]);
                        targetIndex=this.tileToIndex.get(this.concernTile[1])+this.jump;
                        MDof1=ManhattanDis(tileIndex,targetIndex);
                        if(MDof1!=0){
                            currentZero=Integer.parseInt(board.substring(this.numBoardLength));
                            MDof2=ManhattanDis(tileIndex,currentZero);
                            if(MDof2>1){
                                score=MDof1+MDof2+this.maxManhattan+this.maxManhattan;
                            }
                            else{
                                score=MDof1+this.maxManhattan;
                                //score=MDof1;
                            }
                        }
                        else{
                            score=0;
                        }
                        //score= ManhattanDis(indexOfTileInBoard(board,this.concernTile[1]),this.tileToIndex.get(this.concernTile[1])+this.jump);
                    }
                    else if(goSolveCol){
//                        cal the man Dis of both the concern tile to their correct pos
                        tileIndex=indexOfTileInBoard(board,this.concernTile[1]);
                        targetIndex=this.tileToIndex.get(this.concernTile[1])+this.maxDigit;
                        MDof1=ManhattanDis(tileIndex,targetIndex);
                        if(MDof1!=0){
                            currentZero=Integer.parseInt(board.substring(this.numBoardLength));
                            MDof2=ManhattanDis(tileIndex,currentZero);
                            if(MDof2>1){
                                score=MDof1+MDof2+this.maxManhattan+this.maxManhattan;
                            }
                            else{
                                score=MDof1+this.maxManhattan;
                                //score=MDof1;
                            }
                        }
                        else{
                            score=0;
                        }
                        //score= ManhattanDis(indexOfTileInBoard(board,this.concernTile[1]),this.tileToIndex.get(this.concernTile[1])+this.maxDigit);
                    }
                }
                else if(this.FinalManhattan){
                    score=ManhattanDis(indexOfTileInBoard(board,this.concernTile[0]),this.tileToIndex.get(this.concernTile[0]))+ManhattanDis(indexOfTileInBoard(board,this.concernTile[1]),this.tileToIndex.get(this.concernTile[1]));
                }
            }
        }
        else{
            String toCheck,correct;
            tempIndex=0;
            while(tempIndex<this.numBoardLength){
                toCheck=board.substring(tempIndex,tempIndex+this.maxDigit);
                correct=indexToTile.get(tempIndex);
                if(!toCheck.equals(correct)){
                    score++;
                }
                tempIndex+=this.maxDigit;
            }
        }


        //Heuristic Optional: consider the number of completed row&col
//        int mix=fixedRow+fixedCol;
//        score=score - mix;
//        if(mix>=0){
//            score-=200;

        return score;
    }

    //Cal the manhattan dis between two index in the string board
    private int ManhattanDis(int index1inBoard,int index2inBoard ){
        int[] wrong=indexToCorrectRC.get(index1inBoard);
        int[] correct=indexToCorrectRC.get(index2inBoard);
        return Math.abs(wrong[0]-correct[0])+Math.abs(wrong[1]-correct[1]);
    }


    //could clear the this.HashClostMap
    //but the root to the target state left
    //save mem and runtime
    private void rebuildHashCloseMap(String board){
        String child=board,
                parent=this.HashClostMap.get(board);
        HashMap<String,String> newHashCloseMap=new HashMap<>();
        while(!parent.equals("S")){
            newHashCloseMap.put(child,parent);
            child=parent;
            parent=this.HashClostMap.get(child);
        }
        newHashCloseMap.put(child,parent);
        this.HashClostMap.clear();
        this.HashClostMap=newHashCloseMap;
    }
    //end of searching set

    //Method set for generating solution
    public void GenSolTxt(String output) throws IOException {
        FileWriter file = new FileWriter(output,false);	//true means not overwriting
        BufferedWriter buffer = new BufferedWriter(file);
        buffer.write(this.solution);
        //buffer.newLine();
        buffer.close();
    }

    //Gen sol by iterating the child and parent throught the hashCloseMap
    private void GenSolution(String end) {
        int line=0;
        this.solution = "";
        String child=end,parent=this.HashClostMap.get(child),movedTile,temp;
//        System.out.println("Solution from end to start: ");
        int childZeroIndex,parentZeroIndex,numTile;
        //System.out.println("num of steps by ending node: "+end.steps);
        while (!parent.equals("S")) {
            temp="";
            line++;
            childZeroIndex=Integer.parseInt(child.substring(this.numBoardLength));
            parentZeroIndex=Integer.parseInt(parent.substring(this.numBoardLength));
            movedTile=parent.substring(childZeroIndex,childZeroIndex+this.maxDigit);
            numTile=tileToNum(movedTile);
            temp+=numTile;
            if(childZeroIndex==parentZeroIndex+this.maxDigit){
                temp+=" L";
            }
            else if(childZeroIndex==parentZeroIndex-this.maxDigit){
                temp+=" R";
            }
            else if(childZeroIndex==parentZeroIndex+this.jump){
                temp+=" U";
            }
            else{
                temp+=" D";
            }
            solution=temp+"\n"+solution;
            child=parent;
            parent=this.HashClostMap.get(child);
        }
        this.stepUsed=line;
    }

    //temp use
    private void GenSolution2(String end) {
        int line=0;
        this.solution = "";
        String temp=end;
        System.out.println("Solution from end to start: ");
        //System.out.println("num of steps by ending node: "+end.steps);
        while (!temp.equals("S")) {
            line++;
            System.out.println("--------------------------");
            strTo2D(temp);
            temp=HashClostMap.get(temp);
        }
        System.out.println("num of steps: "+line);
    }

    //could trans tiles into num," 0"-->0, "15"-->15
    public int tileToNum(String tile){
        int index=0;
        while(tile.charAt(index)==' '){
            index++;
        }
        return Integer.parseInt(tile.substring(index));
    }

    //Return the solution:
    //EXP:
    //string this.solution;
    //4 U
    //8 R
    //16 D
    //...
    public String getSolution() {
        return this.solution;
    }
    //end pf GenSol set


    //Tool methods
    //return the index Of Tile In the Board
    private int indexOfTileInBoard(String board,String tile){
        int index=0,temp;
        while(index<this.numBoardLength){
            temp=index+this.maxDigit;
            if(board.substring(index,temp).equals(tile)){
                break;
            }
            index+=this.maxDigit;
        }
        return index;
    }

    //You don't wanna read and I don't wanna explain
    private void JustaBitOfInitialSetUp(String filename,Boolean showRunningInfo) throws IOException{
        tools tools=new tools();
        this.firstRC=true;
        this.solved=false;
        this.solvedNewR=false;
        this.solvedNewC = false;
        HiDimPuzzleDriver p1=new HiDimPuzzleDriver(filename);
        int[][]tempArr=p1.toArray();
        this.dimension = p1.getDimension();
        this.size=this.dimension*this.dimension;
        this.lockTile=new int[this.size];
        this.maxDigit=((this.size-1)+"").length();
        this.boundRC=this.dimension-1;
        this.jump=this.maxDigit*this.dimension;
        this.maxFixedCol=-1;
        this.maxFixedRow=-1;
        this.boundDBtask=this.dimension-2;
        this.zeroTile=genZeroTile(this.maxDigit);
        this.tileLeft=this.size;
        this.maxManhattan=this.boundRC*2;
        GenCorrectAns();//put it after this.dimension
        this.endState=tools.sqArrtoString(ansArr,this.maxDigit);
        String initNumBoard=tools.sqArrtoString(tempArr,this.maxDigit);
        this.numBoardLength=initNumBoard.length();
        int init_Zero=p1.getZero()[0]*(this.maxDigit*this.dimension)+p1.getZero()[1]*this.maxDigit;
        this.initStandardBoard=StandardStringBuilder(initNumBoard,init_Zero);
        this.goSolveCol=true;
        this.goSolveRow=false;
        this.workingIndex=0;
        this.putLargeDownRight=false;
        this.putLessToCorner=false;
        this.LargeToLess=false;
        this.FinalManhattan=false;
        GenCorrectMap();
        setUpInitRCtoDoList();
        concernTile[0]=colToDoList.remove();
        workingPriority= heuristicFunction(this.initStandardBoard,false);
        this.show=showRunningInfo;
        //---Print info---
        if(this.show){
            System.out.println("-----"+filename+"-----");
            System.out.println("orig: ");
            tools.prtsqArrInSq(tempArr);
            System.out.println();
            System.out.println("ans:");
            tools.prtsqArrInSq(ansArr);
            System.out.println();
            System.out.println("---------------------------");
            System.out.println("Start searching: ");
            System.out.println("---------------------------");
        }
    }

    //print strBoard into readable 2D board
    private void strTo2D(String board){
        int k=0;
        while(k<this.numBoardLength){;
            System.out.print(board.substring(k,k+this.maxDigit)+" ");
            k+=this.maxDigit;
            if((k%this.jump)==0){
                System.out.println();
            }
        }
    }

    private String genZeroTile(int maxDigit){
        String zeroTile="";
        for(int k=1;k<maxDigit;k++){
            zeroTile+=" ";
        }
        zeroTile+=0;
        return zeroTile;
    }

    //Multi purpose:
    //Create some HashMap:
    //this.tileToCorrectRC-->key: any tile; Value={correctRow,correctCol)
    //this.indexToTile.put(k,tile)--> key: any index in the String board; Value: the correct tile of ans in that index
    //this.tileToIndex.put(tile,k)--> key: any tile in the string board; Value: the correct index of that tile in ans
    //this.indexToRC.put(k,indexSet)--> key: any index in the string board; Value: the={Row,Col)
    //initialize the rowTask and colTask according to its order-->plug the tile in it
    //exp:
    //RowTask(String[][]):
    // 1, 2, 3, 4
    // 5, 6, 7, 8
    // 9,10,11,12
    //13,14,15, 0
    //ColTask(String[][]):
    // 1, 5, 9,13
    // 2, 6,10,14
    // 3, 7,11,15
    // 4, 8,12, 0
    private void GenCorrectMap(){
        this.rowTask=new LinkedList[this.dimension];
        this.colTask=new LinkedList[this.dimension];
        int boardSize=this.numBoardLength;
        int k=0,rowLength=this.jump,row=0,col=0;
        String tile;
        int[] indexSet;
        while(k<boardSize){
            tile="";
            row=k/rowLength;
            col=k%rowLength/this.maxDigit;
            indexSet= new int[]{row, col};
            this.indexToCorrectRC.put(k, indexSet);
            // 1 2 3 4 5 6 7 8 9101112131415 0
            if(k==boardSize-this.maxDigit){
                tile=this.zeroTile;
            }
            else{
                int num=(k/this.maxDigit) + 1;
                for(int m=0;m<maxDigit-(num+"").length();m++){
                    tile+=" ";
                }
                tile+=num;
            }
            this.tileToCorrectRC.put(tile,indexSet);
            this.indexToTile.put(k,tile);
            this.tileToIndex.put(tile,k);
            this.indexToRC.put(k,indexSet);
            if(!this.rowToTile.containsKey(row)){
                this.rowToTile.put(row,new HashSet<>());
            }
            this.rowToTile.get(row).add(tile);
            if(!this.colToTile.containsKey(col)){
                this.colToTile.put(col,new HashSet<>());
            }
            this.colToTile.get(col).add(tile);
            if(this.rowTask[row]==null){
                this.rowTask[row]=new LinkedList<>();
            }
            if(this.colTask[col]==null){
                this.colTask[col]=new LinkedList<>();
            }
            this.rowTask[row].add(tile);
            this.colTask[col].add(tile);
            k+=this.maxDigit;
        }
    }

    //plug the rowTask in to the queue of this.rowToDoList
    //plug the colTask into the queue of this.colToDoList
    private void setUpInitRCtoDoList(){
        for(int i=0;i<this.dimension;i++){
            for(String tile: this.rowTask[i]){
//                if(!this.doneTile.contains(tile)){
//                    rowToDoList.add(tile);
//                }
                this.rowToDoList.add(tile);
            }
        }
        for(int j=0;j<this.dimension;j++){
            for(String tile: this.colTask[j]){
//                if(!this.doneTile.contains(tile)){
//                    colToDoList.add(tile);
//                }
                this.colToDoList.add(tile);
            }
        }
    }

    //Builder for the standard string for operation
    // 1  2  3  4
    // 5  6  7  8
    // 9 10 11 12
    //13 14  0 15
    //standard string= 1 2 3 4 5 6 7 8 91011121314 015 2 128
    //-->numBoard in String+commutative fixedRow+commutative fixedCol+index of blank space in the string
    private String StandardStringBuilder(String numBoard,int zero){
        String standardBoard=numBoard;
        standardBoard+=zero;
        return standardBoard;
    }

    //Return the dimension of the puzzle
    public int getDimension(){
        return this.dimension;
    }

    //Return the step used to solve the puzzle
    public int getStepUsed(){
        return this.stepUsed;
    }

    //Generate the correct ans in this.ansArr
    //Exp (4*4):
    //this.ansArr[][]
    // 1  2  3  4
    // 5  6  7  8
    // 9 10 11 12
    //13 14 15  0
    private void GenCorrectAns(){
        this.ansArr = new int[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                this.ansArr[i][j] = i * this.dimension + j + 1;
            }
        }
        this.ansArr[this.dimension - 1][this.dimension - 1] = 0;
    }

}
