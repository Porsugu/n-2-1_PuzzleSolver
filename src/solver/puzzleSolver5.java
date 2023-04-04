package solver;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

//solver2+clearing system
//hi : )
public class puzzleSolver5 {
    private int[][] ansArr;
    private String initStandardBoard,solution,endState;
    private HashSet<String> HashCloseSet=new HashSet<>();
    public HashMap<Integer,int[]> indexToCorrectRC=new HashMap<>();
    private HashMap<String,int[]>tileToCorrectRC=new HashMap<>();
    private HashMap<Integer,String> indexToTile=new HashMap<>();
    private HashMap<String,String> HashClostMap=new HashMap<>();
    public HashMap<Integer,HashSet<String>> rowToTile=new HashMap<>(),colToTile=new HashMap<>();
    private int dimension,boundRC,size,workingPriority,maxSteps,jump,numBoardLength,maxFixedRow,maxFixedCol;
    private int[] fixedRowStartEnd=new int[2],fixedColStartEnd=new int[2];
    private Boolean usingGreedy,goSolveRow,goSolveCol;

    // Constructor
    public puzzleSolver5(String filename) throws IOException {
        tools tools=new tools();
        HiDimPuzzleDriver p1 = new HiDimPuzzleDriver(filename); // bad board--> exception
        int[][]tempArr=p1.toArray();
        this.dimension = p1.getDimension();
        this.boundRC=this.dimension-1;
        this.jump=2*this.dimension;
        this.size=this.dimension*this.dimension;

        GenCorrectAns();//put it after this.dimension
        this.endState=tools.sqArrtoString(ansArr);
        String initNumBoard=tools.sqArrtoString(tempArr);
        this.numBoardLength=initNumBoard.length();
        this.fixedRowStartEnd[0]=this.numBoardLength;
        this.fixedRowStartEnd[1]=this.numBoardLength+2;
        this.fixedColStartEnd[0]=fixedRowStartEnd[1];
        this.fixedColStartEnd[1]=fixedRowStartEnd[1]+2;
        int init_Zero=p1.getZero()[0]*(2*this.dimension)+p1.getZero()[1]*2;
        //this.initial_node = new PuzzleNode2(null, p1.getZero()[0]*(2*this.dimension)+p1.getZero()[1]*2, "S", tools.sqArrtoString(tempArr));
        GenCorrectIndexMap();
        this.maxFixedRow=DetermineFixedRow(-1, initNumBoard);
        this.maxFixedCol=DetermineFixedCol(-1,initNumBoard);
        this.initStandardBoard=StandardStringBuilder(initNumBoard, this.maxFixedRow,this.maxFixedCol,init_Zero);

        DetermineToSolve();
//        System.out.println("goSolveCol: "+goSolveCol);
//        System.out.println("goSolveRow: "+goSolveRow);
//        System.out.println("init standard board: "+this.initStandardBoard);
//        System.out.println("     init num board: "+this.initStandardBoard.substring(0,this.numBoardLength));
//        System.out.println("init fixed Row: "+this.initStandardBoard.substring(this.fixedRowStartEnd[0],this.fixedRowStartEnd[1]));
//        System.out.println("init fixed Col: "+this.initStandardBoard.substring(this.fixedColStartEnd[0],this.fixedColStartEnd[1]));
//        System.out.println("init zero: "+this.initStandardBoard.substring(this.fixedColStartEnd[1]));

//        System.out.println("init FRow: "+this.maxFixedRow);
//        System.out.println("init FCol: "+this.maxFixedCol);
        Boolean limitMaxSteps=false;
        this.usingGreedy=true;
        workingPriority= heuristicFunction(this.initStandardBoard,this.maxFixedRow,this.maxFixedCol);
        maxSteps=(int)Math.ceil((8.0)*Math.pow(this.dimension,3));

        //---Print info---
        System.out.println("-----"+filename+"-----");
        System.out.println("using IDA star: "+(limitMaxSteps && !this.usingGreedy));
        System.out.println("limiting max steps: "+limitMaxSteps);
        System.out.println("using greedy algo: "+this.usingGreedy);
        System.out.println("IDA star Max steps: "+maxSteps);
        System.out.println("orig: ");
        tools.prtsqArrInSq(tempArr);
        System.out.println();
        System.out.println("ans:");
        tools.prtsqArrInSq(ansArr);
        System.out.println();

        //run the search
        String fin = A_star3(limitMaxSteps);
        GenSolution(fin);
    }

    private String StandardStringBuilder(String numBoard,int fixedRow,int fixedCol,int zero){
        String standardBoard=numBoard;
        if(fixedRow>=0){
            standardBoard+=" ";
        }
        standardBoard+=fixedRow;
        if(fixedCol>=0){
            standardBoard+=" ";
        }
        standardBoard+=fixedCol;
        standardBoard+=zero;
        return standardBoard;
    }

    public int fixedStrRCToNum(String fixedRorC){
        if(fixedRorC.charAt(0)=='-'){
            return -1;
        }
        else{
            return Integer.parseInt(fixedRorC.substring(1));
        }
    }

    private String A_star3(Boolean limitMaxSteps){
        Boolean solved=false;
        String poped_str = null;
        HashMap<Integer,LinkedList<String>> taskMap=new HashMap<>();
        taskMap.put(workingPriority,new LinkedList<>());
        taskMap.get(workingPriority).add(this.initStandardBoard);
        this.HashClostMap.put(this.initStandardBoard,"S");
        //int counter=0;
        while(!taskMap.isEmpty()){
            //System.out.println("Enter while loop");
            poped_str=taskMap.get(workingPriority).remove();
            //System.out.println("counter: "+counter);
            //poped_node.board.equals(this.endState)
            if (fixedStrRCToNum(poped_str.substring(this.fixedRowStartEnd[0],this.fixedRowStartEnd[1]))==this.boundRC) {
                solved=true;
                break;
            }
            if(taskMap.get(workingPriority).isEmpty()){
                taskMap.remove(workingPriority);
            }
            addNeighbour3(poped_str,taskMap);
            //System.out.println("here");
            if(!taskMap.containsKey(workingPriority)){
                workingPriority++;
                while(true){
                    if(taskMap.containsKey(workingPriority)){
                        break;
                    }
                    workingPriority++;
                }
            }
            //counter++;
        }
        if(solved){
            return poped_str;
        }
        else{
            return null;
        }
    }

    private void GenSolution(String end) {
        int line=0;
        this.solution = "";
        String temp=end;
        strTo2D(temp);
        //System.out.println("num of steps by ending node: "+end.steps);
        while (!temp.equals("S")) {
            line++;
            System.out.println("--------------------------");
            //strTo2D(temp);
            System.out.println(temp);
            System.out.println("--------------------------");
            temp=HashClostMap.get(temp);
        }
        System.out.println("num of steps: "+line);
    }

    public String getSolution() {
        return this.solution;
    }

//    MoveDir=='R'
//    example:
//     1 2 3
//     4 0 6
//     7 5 8
//     to
//     1 2 3
//     0 4 6
//     7 8 8

    private void addNeighbour3(String current, HashMap<Integer,LinkedList<String>> a) {
        AddNeighbourByDir3(current,a,'U');
        AddNeighbourByDir3(current,a,'D');
        AddNeighbourByDir3(current,a,'R');
        AddNeighbourByDir3(current,a,'L');
    }

    public void AddNeighbourByDir3(String current, HashMap<Integer,LinkedList<String>> a,char MoveDir){
        Boolean conditionToAdd=false;
        int tileIndex=0,bound=this.dimension - 1;
        int currentZero = Integer.parseInt(current.substring(this.fixedColStartEnd[1])),
                currentFixedRow=fixedStrRCToNum(current.substring(this.fixedRowStartEnd[0],this.fixedRowStartEnd[1])),
                currentFixedCol=fixedStrRCToNum(current.substring(this.fixedColStartEnd[0],this.fixedColStartEnd[1])),
                newFixedRow,
                newFixedCol,
                newZero;
//                currentCol;
        int[] currentCoordinate=this.indexToCorrectRC.get(currentZero);
//        currentRow=this.indexToCorrectRC.get(currentZero)[0];
//        currentCol=this.indexToCorrectRC.get(currentZero)[1];
        String tileTomove="",board = current,newBoard;

        if((currentCoordinate[0] < bound) && MoveDir=='U'){
            tileIndex=currentZero+this.jump;
            tileTomove=board.substring(tileIndex,tileIndex+2);
            //conditionToAdd=!current.move.equals(tileTomove + " " + "D");
            conditionToAdd=true;
        }
        else if(currentCoordinate[0] > 0 && MoveDir=='D'){
            tileIndex=currentZero-this.jump;
            tileTomove=board.substring(tileIndex,tileIndex+2);
            //conditionToAdd=(currentCoordinate[0]-current.fixedRow>1) && !current.move.equals(tileTomove + " " + "U");
            conditionToAdd=(currentCoordinate[0]-currentFixedRow>1);
        }
        else if(currentCoordinate[1] > 0 && MoveDir=='R'){
            tileIndex=currentZero-2;
            tileTomove=board.substring(tileIndex,currentZero);
            //conditionToAdd=(currentCoordinate[1]- current.fixedCol>1) &&  !current.move.equals(tileTomove + " " + "L");
            conditionToAdd=(currentCoordinate[1]- currentFixedCol>1);
        }
        else if(currentCoordinate[1] < bound) {   //MoveDir=='L'
            tileIndex=currentZero+2;
            tileTomove=board.substring(tileIndex,tileIndex+2);
            //conditionToAdd= !current.move.equals(tileTomove + " " + "R");
            conditionToAdd=true;
        }
        if(conditionToAdd){
            newZero = tileIndex;
            if(newZero<currentZero){
                newBoard=board.substring(0,newZero)+" 0"+board.substring(newZero+2,currentZero)+tileTomove+board.substring(currentZero+2,this.numBoardLength);
            }
            else{   //currentZero<newZero
                newBoard=board.substring(0,currentZero)+tileTomove+board.substring(currentZero+2,newZero)+" 0"+board.substring(newZero+2,this.numBoardLength);
            }
            newFixedRow=DetermineFixedRow(currentFixedRow, newBoard);
            newFixedCol=DetermineFixedCol(currentFixedCol,newBoard);
            newBoard=StandardStringBuilder(newBoard,newFixedRow,newFixedCol,newZero);
            if(!this.HashClostMap.containsKey(newBoard)){
                int temp_Priority= heuristicFunction(newBoard,newFixedRow,newFixedCol);
                if(this.goSolveRow && newFixedRow>this.maxFixedRow){
                   System.out.println("---row clear---");
//                    System.out.println("Current maxFixedRow: "+this.maxFixedRow);
//                    System.out.println("Current maxFixedCol: "+this.maxFixedCol);
//                    System.out.println("New maxFixedRow: "+temp_node.fixedRow);
                    this.maxFixedRow=newFixedRow;
                    this.goSolveRow=false;
                    //System.out.println("goSolveRow: "+this.goSolveRow);
                    a.clear();
                }
                else if(this.goSolveCol && newFixedCol>this.maxFixedCol){
                    System.out.println("---Col clear---");
//                    System.out.println("Current maxFixedRow: "+this.maxFixedRow);
//                    System.out.println("Current maxFixedCol: "+this.maxFixedCol);
//                    System.out.println("New maxFixedCol: "+temp_node.fixedCol);
                    this.maxFixedCol=newFixedCol;
                    this.goSolveCol=false;
                    //System.out.println("goSolveCol: "+this.goSolveCol);
                    a.clear();
                }
                if(!goSolveRow&&!goSolveCol){
                    System.out.println("---Both goSolve==false---");
                    strTo2D(newBoard);
//                    System.out.println("updated to both true");
                    this.goSolveRow=true;
                    this.goSolveCol=true;
                }

//                rowEqualColA7=(this.maxFixedCol!=-1) && (this.maxFixedCol==this.maxFixedRow);
//                if(!rowEqualColB4 && rowEqualColA7){
//                    System.out.println("clear");
//                    a.clear();
//                }
                if(!a.containsKey(temp_Priority)){
                    a.put(temp_Priority,new LinkedList<>());
                }
                if(temp_Priority<workingPriority){
                    workingPriority=temp_Priority;
                }
                a.get(temp_Priority).add(newBoard);
                this.HashClostMap.put(newBoard,current);
            }
        }

    }

    private void DetermineToSolve(){
        if(this.maxFixedRow==this.maxFixedCol){
            this.goSolveRow=true;
            this.goSolveCol=true;
        }
        else{
            this.goSolveRow=this.maxFixedRow<this.maxFixedCol;
            this.goSolveCol=!this.goSolveRow;
        }
    }

    public int heuristicFunction(String board,int fixedRow,int fixedCol) {
        int score = 0,workingIndex;
        //Heuristic 1: count misplaced tile
        workingIndex=(fixedRow+1)*this.jump+(fixedCol+1)*2;
        while(workingIndex<this.numBoardLength){
            //this.endState.substring(workingIndex,workingIndex+2)
            if(!board.substring(workingIndex,workingIndex+2).equals(this.indexToTile.get(workingIndex))){
                score++;
            }
            workingIndex+=2;
        }

        //Heuristic 2: total Manhattan distances
//        String toCheck,correctTile;
//        workingIndex=(fixedRow+1)*this.jump;
//        while(workingIndex<this.boardLength){
//            toCheck=board.substring(workingIndex,workingIndex+2);
//            correctTile=this.indexToTile.get(workingIndex);
//            if(!toCheck.equals(correctTile)){
//                score+=ManhattanDis(toCheck,correctTile);
//            }
//            workingIndex+=2;
//        }

        //Heuristic 3: count Manhattan only if the corresponding row/col is not fixed
//        int targetRow=fixedRow+1,targetCol=fixedCol+1,workingCol=targetCol;
//        workingIndex=(targetRow)*this.jump+targetCol*2;
//        String toCheck,correctTile;
//        while(workingIndex<this.numBoardLength){
//            if(workingCol>fixedCol){
//                toCheck=board.substring(workingIndex,workingIndex+2);
//                correctTile=this.indexToTile.get(workingIndex);
//                if(!toCheck.equals(correctTile)){
//                    score+=ManhattanDis(workingIndex,toCheck);
//                }
//            }
//            workingCol++;
//            if(workingCol==this.dimension){
//                workingCol=0;
//            }
//            workingIndex+=2;
//        }

        //Heuristic 4: count misplace according to concern row/ col or both
//        String tile;
//        workingIndex=(fixedRow+1)*this.jump+(fixedCol+1)*2;
//        if(this.goSolveRow&&this.goSolveCol){
//            HashSet<String> rowSet=this.rowToTile.get(fixedRow+1),
//                    colSet=this.colToTile.get(fixedCol+1);
//            while(workingIndex<this.boardLength){
//                //this.endState.substring(workingIndex,workingIndex+2)
//                tile=board.substring(workingIndex,workingIndex+2);
//                if(rowSet.contains(tile) || colSet.contains(tile)){
//                    if(!tile.equals(this.indexToTile.get(workingIndex))){
//                        score++;
//                    }
//                }
//                workingIndex+=2;
//            }
//        }
//        else if(this.goSolveRow){
//            HashSet<String> rowSet=this.rowToTile.get(fixedRow+1);
//            while(workingIndex<this.boardLength){
//                //this.endState.substring(workingIndex,workingIndex+2)
//                tile=board.substring(workingIndex,workingIndex+2);
//                if(rowSet.contains(tile)){
//                    if(!tile.equals(this.indexToTile.get(workingIndex))){
//                        score++;
//                    }
//                }
//                workingIndex+=2;
//            }
//        }
//        else if(this.goSolveCol){
//            HashSet<String> colSet=this.colToTile.get(fixedCol+1);
//            while(workingIndex<this.boardLength){
//                //this.endState.substring(workingIndex,workingIndex+2)
//                tile=board.substring(workingIndex,workingIndex+2);
//                if(colSet.contains(tile)){
//                    if(!tile.equals(this.indexToTile.get(workingIndex))){
//                        score++;
//                    }
//                }
//                workingIndex+=2;
//            }
//        }



        //Heuristic Optional: consider the number of completed row&col
//        int mix=fixedRow+fixedCol;
//        score=score - mix;
//        if(mix>=0){
//            score-=200;
//        }

        return score;
    }

    private int ManhattanDis(int wrongIndex,String concernTile){
        int[] wrong=indexToCorrectRC.get(wrongIndex);
        int[] correct=tileToCorrectRC.get(concernTile);
        return Math.abs(wrong[0]-correct[0])+Math.abs(wrong[1]-correct[1]);
    }

    public int DetermineFixedRow(int currentFixedRow,String board){
        int workingBoardIndex,extra=0,colCount=0;
        workingBoardIndex=(currentFixedRow+1)*(this.jump);
        while(workingBoardIndex<this.numBoardLength){
            if(board.substring(workingBoardIndex,workingBoardIndex+2).equals(endState.substring(workingBoardIndex,workingBoardIndex+2))){
                colCount++;
                workingBoardIndex+=2;
            }
            else{
                break;
            }
            if(colCount==this.dimension){
                colCount=0;
                extra++;
            }
        }
        return currentFixedRow+extra;
    }

    public int DetermineFixedCol(int currentFixedCol,String board){    //need to fix
        int workingBoardIndex=(currentFixedCol+1)*2,extra=0,rowCount=0,init_Index=workingBoardIndex;
        while(workingBoardIndex<this.numBoardLength){
            if(board.substring(workingBoardIndex,workingBoardIndex+2).equals(endState.substring(workingBoardIndex,workingBoardIndex+2))){
                workingBoardIndex+=this.jump;
                rowCount++;
            }
            else{
                break;
            }
            if(rowCount==this.dimension){
                rowCount=0;
                workingBoardIndex=init_Index+2;
                init_Index=workingBoardIndex;
                extra++;
            }
        }
        return currentFixedCol+extra;
    }
    private void GenCorrectAns(){
        this.ansArr = new int[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                this.ansArr[i][j] = i * this.dimension + j + 1;
            }
        }
        this.ansArr[this.dimension - 1][this.dimension - 1] = 0;
    }

    private void strTo2D(String board){
        int k=0;
        while(k<this.numBoardLength){;
            System.out.print(board.substring(k,k+2)+" ");
            k+=2;
            if((k%this.jump)==0){
                System.out.println();
            }
        }
    }

    private void GenCorrectIndexMap(){
        int boardSize=this.numBoardLength;
        int k=0,rowLength=this.jump,row,col;
        String tile;
        int[] indexSet;
        while(k<boardSize){
            row=k/rowLength;
            col=k%rowLength/2;
            indexSet= new int[]{row, col};
            this.indexToCorrectRC.put(k, indexSet);
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
            if(!this.rowToTile.containsKey(row)){
                this.rowToTile.put(row,new HashSet<>());
            }
            this.rowToTile.get(row).add(tile);

            if(!this.colToTile.containsKey(col)){
                this.colToTile.put(col,new HashSet<>());
            }
            this.colToTile.get(col).add(tile);
            k+=2;
        }
    }


}
