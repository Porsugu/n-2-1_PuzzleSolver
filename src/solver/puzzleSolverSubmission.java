package solver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//solver2+clearing system
//hi : )
public class puzzleSolverSubmission {
    public int[][] ansArr;
    private String initStandardBoard;
    private String solution;
    public HashMap<Integer,int[]> indexToCorrectRC=new HashMap<>();
    private HashMap<String,int[]>tileToCorrectRC=new HashMap<>();
    public HashMap<Integer,String> indexToTile=new HashMap<>();
    public HashMap<String,Integer> tileToIndex=new HashMap<>();
    public HashMap<String,String> HashClostMap=new HashMap<>();
    public HashMap<Integer,int[]> indexToRC=new HashMap<>();
    public HashMap<Integer,HashSet<String>> rowToTile=new HashMap<>(),colToTile=new HashMap<>();
    private int dimension,boundRC,size,workingPriority,jump,numBoardLength,maxFixedRow,maxFixedCol,boundDBtask,workingIndex,tileLeft,stepUsed,maxManhattan;
    private Boolean goSolveRow,goSolveCol,show,solvedNewR,solvedNewC,firstRC;
    private Boolean putLargeDownRight,putLessToCorner,LargeToLess,FinalManhattan,addGate,solved;
    List<String>[] rowTask,colTask;
    Queue<String> rowToDoList=new LinkedList<>(),colToDoList=new LinkedList<>();
    private String[] concernTile= new String[2];
    private int[] lockTile;
    private PriorityQueue<Integer> scoreQ;
    private ArrayList<Integer> scoreA;
    private LinkedList<Integer> scoreL;
    // Constructor
    public puzzleSolverSubmission(String input,String ouput) throws IOException, BadBoardException {
        JustaBitOfInitialSetUp(input);
        //run the search
        String fin = greedy();
        GenSolution(fin);
        GenSolTxt(ouput);
    }

    //Method set of searching
    //Maybe it is greedy,but idk
    private String greedy(){
//        this.minScore=workingPriority;
//        this.scoreQ=new PriorityQueue<>();
//        this.scoreA=new ArrayList<>();
        this.scoreL=new LinkedList<>();
        String poped_str = null;
        HashMap<Integer,LinkedList<String>> taskMap=new HashMap<>();
        taskMap.put(workingPriority,new LinkedList<>());
        taskMap.get(workingPriority).add(this.initStandardBoard);
        this.HashClostMap.put(this.initStandardBoard,"S");
        //this.scoreQ.add(workingPriority);
        //this.scoreA.add(workingPriority);
        this.scoreL.add(workingPriority);
        while(!taskMap.isEmpty()){
            //poped_str=taskMap.get(this.scoreQ.peek()).remove();
            //poped_str=taskMap.get(this.scoreA.get(0)).remove();
            poped_str=taskMap.get(this.scoreL.peek()).remove();
            if(this.solved){
                break;
            }
            //this.scoreQ.peek()
            //this.scoreA.get(0)
            if(taskMap.get(this.scoreL.peek()).isEmpty()){
                taskMap.remove(this.scoreL.peek());
                //this.scoreQ.remove();
                //this.scoreA.remove(0);
                this.scoreL.remove();
                Collections.sort(this.scoreL);
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
        Boolean conditionToAdd=false;
        int tileIndex=0;
        int currentZero = Integer.parseInt(current.substring(this.numBoardLength)),
                newZero;
        int[] currentCoordinate=this.indexToCorrectRC.get(currentZero);
        String tileTomove="",newBoard;
        if((currentCoordinate[0] < this.boundRC) && MoveDir=='U'){
            tileIndex=currentZero+this.jump;
            tileTomove=current.substring(tileIndex,tileIndex+2);
            conditionToAdd=this.lockTile[tileToNum(tileTomove)]==0;
        }
        else if(currentCoordinate[0] > 0 && MoveDir=='D'){
            tileIndex=currentZero-this.jump;
            tileTomove=current.substring(tileIndex,tileIndex+2);
            conditionToAdd=this.lockTile[tileToNum(tileTomove)]==0;
        }
        else if(currentCoordinate[1] > 0 && MoveDir=='R'){
            tileIndex=currentZero-2;
            tileTomove=current.substring(tileIndex,currentZero);
            conditionToAdd=this.lockTile[tileToNum(tileTomove)]==0;
        }
        else if(currentCoordinate[1] < this.boundRC) {   //MoveDir=='L'
            tileIndex=currentZero+2;
            tileTomove=current.substring(tileIndex,tileIndex+2);
            conditionToAdd=this.lockTile[tileToNum(tileTomove)]==0;
        }
        if(conditionToAdd){
            newZero = tileIndex;
            if(newZero<currentZero){
                newBoard=current.substring(0,newZero)+" 0"+current.substring(newZero+2,currentZero)+tileTomove+current.substring(currentZero+2,this.numBoardLength);
            }
            else{   //currentZero<newZero
                newBoard=current.substring(0,currentZero)+tileTomove+current.substring(currentZero+2,newZero)+" 0"+current.substring(newZero+2,this.numBoardLength);
            }

            newBoard=StandardStringBuilder(newBoard,newZero);
            if(!this.HashClostMap.containsKey(newBoard)){
                int temp_Priority=heuristicFunction(newBoard);
                if(temp_Priority==0){
                    if(this.tileLeft>9){
                        this.addGate=false;
                        workingPriority=Integer.MAX_VALUE;
                        if(workingIndex<this.boundDBtask){
                            rebuildHashCloseMap(current);
                            this.tileLeft--;
                            a.clear();
                            //this.scoreQ.clear();
                            //this.scoreA.clear();
                            this.scoreL.clear();
                            workingIndex++;
                            this.lockTile[tileToNum(concernTile[0])]=1;
                            if(goSolveRow){
                                this.concernTile[0]=rowToDoList.remove();
                                while(this.lockTile[tileToNum(concernTile[0])]==1){
                                    this.concernTile[0]=rowToDoList.remove();
                                }
                                if(workingIndex==this.boundDBtask){
                                    this.concernTile[1]=rowToDoList.remove();
                                    while(this.lockTile[tileToNum(concernTile[1])]==1){
                                        this.concernTile[1]=rowToDoList.remove();
                                    }
                                }
                            }
                            else if(goSolveCol){
                                this.concernTile[0]=colToDoList.remove();
                                while(this.lockTile[tileToNum(concernTile[0])]==1){
                                    this.concernTile[0]=colToDoList.remove();
                                }
                                if(workingIndex==this.boundDBtask){
                                    this.concernTile[1]=colToDoList.remove();
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
                                targetIndex=this.tileToIndex.get(" 0");
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
                        }
                        else if(workingIndex==this.boundDBtask){
                            if(this.putLargeDownRight){
                                a.clear();
                                //this.scoreQ.clear();
                                //this.scoreA.clear();
                                this.scoreL.clear();
                                rebuildHashCloseMap(current);
                                this.lockTile[tileToNum(concernTile[1])]=1;
                                this.putLargeDownRight=false;
                                this.putLessToCorner=true;
                                this.LargeToLess=false;
                                this.FinalManhattan=false;
                            }
                            else if(this.putLessToCorner){
                                a.clear();
                                //this.scoreQ.clear();
                                //this.scoreA.clear();
                                this.scoreL.clear();
                                rebuildHashCloseMap(current);
                                this.lockTile[tileToNum(concernTile[0])]=1;
                                this.lockTile[tileToNum(concernTile[1])]=0;
                                this.putLargeDownRight=false;
                                this.putLessToCorner=false;
                                this.LargeToLess=true;
                                this.FinalManhattan=false;
                            }
                            else if(this.LargeToLess){
                                a.clear();
                                //this.scoreQ.clear();
                                //this.scoreA.clear();
                                this.scoreL.clear();
                                rebuildHashCloseMap(current);
                                this.lockTile[tileToNum(concernTile[0])]=0;
                                this.putLargeDownRight=false;
                                this.putLessToCorner=false;
                                this.LargeToLess=false;
                                this.FinalManhattan=true;

                            }
                            else if(this.FinalManhattan){
                                a.clear();
                                //this.scoreQ.clear();
                                //this.scoreA.clear();
                                this.scoreL.clear();
                                rebuildHashCloseMap(current);
                                this.tileLeft-=2;
                                this.lockTile[tileToNum(concernTile[0])]=1;
                                this.lockTile[tileToNum(concernTile[1])]=1;
                                this.putLargeDownRight=false;
                                this.putLessToCorner=false;
                                this.LargeToLess=false;
                                this.FinalManhattan=false;
                                if(this.tileLeft>9){
                                    if(goSolveCol){
                                        goSolveCol=false;
                                        goSolveRow=true;
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
                            }
                        }
                    }
                    else{
                        this.solved=true;
                    }
                }

                if(temp_Priority==0 && this.tileLeft>9){
                    temp_Priority=heuristicFunction(newBoard);
                }
                if(!a.containsKey(temp_Priority)){
                    a.put(temp_Priority,new LinkedList<>());
                    //this.scoreQ.add(temp_Priority);
                    //this.scoreA.add(temp_Priority);
                    if(!this.scoreL.isEmpty() && temp_Priority<this.scoreL.peek()){
                        this.scoreL.addFirst(temp_Priority);
                        //Collections.sort(this.scoreA);
                    }
                    else{
                        this.scoreL.add(temp_Priority);
                    }
                }
                a.get(temp_Priority).add(newBoard);
                this.HashClostMap.put(newBoard,current);
            }
        }

    }

    //the heuristic function for the searching, this version could only support heuristic 6
    public int heuristicFunction(String board) {
        int score = 0,tempIndex,MDof1,MDof2,tileIndex,targetIndex,currentZero;
        //Heuristic 6: idk what is this but works :)
        if(this.tileLeft>9){
            //A better single tile pushing heuristic function
            //step1: if the manDis of the tile to its correct postion =0, return 0, else go step2.
            //step2: if the manDis of blank space to the tile<=1, return the manDis of the tile to its correct postion, else return the sum of their MD+2*makxmanhattan
            if(this.workingIndex<this.boundDBtask){
                tileIndex=indexOfTileInBoard(board,this.concernTile[0]);
                targetIndex=this.tileToIndex.get(this.concernTile[0]);
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
                //A slower pushing heuristic but also works :)
                //score=ManhattanDis(indexOfTileInBoard(board,this.concernTile[0]),this.concernTile[0]);
            }
            else{
                if(this.putLargeDownRight){
                    tileIndex=indexOfTileInBoard(board,this.concernTile[1]);
                    targetIndex=this.tileToIndex.get(" 0");
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
                        targetIndex=this.tileToIndex.get(this.concernTile[1])+2;
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
                toCheck=board.substring(tempIndex,tempIndex+2);
                correct=indexToTile.get(tempIndex);
                if(!toCheck.equals(correct)){
                    score++;
                }
                tempIndex+=2;
            }
        }
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
        int childZeroIndex,parentZeroIndex,numTile;
        while (!parent.equals("S")) {
            temp="";
            line++;
            childZeroIndex=Integer.parseInt(child.substring(this.numBoardLength));
            parentZeroIndex=Integer.parseInt(parent.substring(this.numBoardLength));
            movedTile=parent.substring(childZeroIndex,childZeroIndex+2);
            numTile=tileToNum(movedTile);
            temp+=numTile;
            if(childZeroIndex==parentZeroIndex+2){
                temp+=" L";
            }
            else if(childZeroIndex==parentZeroIndex-2){
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

    //could trans tiles into num," 0"-->0, "15"-->15
    public int tileToNum(String tile){
        if(tile.charAt(0)==' '){
            return Integer.parseInt(tile.substring(1));
        }
        else{
            return Integer.parseInt(tile);
        }
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
            temp=index+2;
            if(board.substring(index,temp).equals(tile)){
                break;
            }
            index+=2;
        }
        return index;
    }

    //You don't wanna read and I don't wanna explain
    private void JustaBitOfInitialSetUp(String filename) throws IOException, BadBoardException {
        tools tools=new tools();
        this.firstRC=true;
        this.solved=false;
        this.solvedNewR=false;
        this.solvedNewC = false;
        puzzleDriver p1 = new puzzleDriver(filename); // bad board--> exception
        int[][]tempArr=p1.toArray();
        this.dimension = p1.getSIZE();
        this.boundRC=this.dimension-1;
        this.jump=2*this.dimension;
        this.boundDBtask=this.dimension-2;
        this.size=this.dimension*this.dimension;
        this.tileLeft=this.size;
        this.maxManhattan=this.boundRC*2;
        this.lockTile=new int[size];
        GenCorrectAns();//put it after this.dimension
        String endState = tools.sqArrtoString(ansArr);
        endState +=(this.size-1)*2;
        String initNumBoard=tools.sqArrtoString(tempArr);
        this.numBoardLength=initNumBoard.length();
        int init_Zero=p1.getZero()[0]*(2*this.dimension)+p1.getZero()[1]*2;
        this.maxFixedRow=-1;
        this.maxFixedCol=-1;
        this.initStandardBoard=StandardStringBuilder(initNumBoard,init_Zero);
        if (this.initStandardBoard.equals(endState)) {
            this.solved=true;
        }
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
        workingPriority= heuristicFunction(this.initStandardBoard);
    }

    //print strBoard into readable 2D board
    private void strTo2D(String board){
        int k=0;
        while(k<this.numBoardLength){
            System.out.print(board.substring(k,k+2)+" ");
            k+=2;
            if((k%this.jump)==0){
                System.out.println();
            }
        }
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
        int k=0,rowLength=this.jump,row,col;
        String tile;
        int[] indexSet;
        while(k<boardSize){
            row=k/rowLength;
            col=k%rowLength/2;
            indexSet= new int[]{row, col};
            this.indexToCorrectRC.put(k, indexSet);
            // 1 2 3 4 5 6 7 8 9101112131415 0
            if(k==boardSize-2){
                tile=" 0";
            }
            else if ((k/2+1) <10){
                tile=" "+(k/2 + 1);
            }
            else{
                tile=""+(k/2 + 1);
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
            k+=2;
        }

    }

    //plug the rowTask in to the queue of this.rowToDoList
    //plug the colTask into the queue of this.colToDoList
    private void setUpInitRCtoDoList(){
        for(int i=0;i<this.dimension;i++){
            for(String tile: this.rowTask[i]){
                this.rowToDoList.add(tile);
            }
        }
        for(int j=0;j<this.dimension;j++){
            for(String tile: this.colTask[j]){
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
