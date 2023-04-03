package solver;
import java.io.IOException;
import java.util.*;

public class puzzleSolver0 {
    private int dimension,  //the dimension of the board
                boundRC,    //the max accessible row and col
                boundDBtask,    //the boundary row/col from solving one tile at once to two tile at once
                size,   //the num of tile +1 blank space in the board
                tileLeft,   //the num of unprocessed tile, our algo would behave differently when tileLeft=9
                workingIndex,   //the working index in the row or col, would compare to this.boundDBtask to switch strategy
                rowDone,      //num of commutative solved row, from 0 to n-1
                colDone,      //num of commutative solved row, from 0 to n-1
                part;         //determine the number of part we are working on
    private int[][] initState,  //the initState of the game, should not be modified during the solving
                    endState,   //the endState of the game, every tile should be placed in correct place,should not be modified during the solving
                    workingBoard;   //the board for working
    private int[] concernTile;
    private boolean goSolveCol, //if true, the system would solve the col first,mutually exclusive to goSolveRow
                    goSolveRow; //if true, the system would solve the row first,mutually exclusive to goSolveCol
    HashSet<Integer> doneTile;  //could store the tile we have done

    public HashMap<Integer,int[]> tileToCurrentIndex, //could track the index of tile in the current board,
                                                        // key: tile, value:{currentRow,currentCol}
                                                        // Should update everytime after any single move
                                    tileToCorrectIndex;    //could store a table for a tile to their correct row and col,
                                                            // key:tile, value:{correctRow,correctCol}
                                                            // Should not modify
    private Queue<Integer> rowToDoQueue,  //could store a queue of tile in row to solve in order
                            colToDoQueue;    //could store a queue of tile in col to solve in order
    public String solution; //the sol in the format:
                            // 4 U+\n+13 D+\n+...

    tools tool=new tools();
    public puzzleSolver0(String filename, Boolean showRunningInfo) throws IOException {
        //Just a bit of set up
        HiDimPuzzleDriver p1 = new HiDimPuzzleDriver(filename);
        this.initState=p1.toArray();
        this.workingBoard=p1.toArray();
        this.dimension = p1.getDimension();
        this.boundRC=this.dimension-1;
        this.boundDBtask=this.dimension-2;
        this.size=this.dimension*this.dimension;
        this.tileLeft=this.size;
        this.endState=GenCorrectAns(this.dimension);
        this.goSolveRow=true;
        this.goSolveCol=false;
        this.rowDone=-1;
        this.colDone=-1;
        this.part=1;
        this.workingIndex=0;
        this.tileToCurrentIndex=genTileToIndex(initState);
        this.tileToCorrectIndex=genTileToIndex(endState);
        this.rowToDoQueue=new LinkedList<>();
        this.colToDoQueue=new LinkedList<>();
        setUpRCtoDoList(this.dimension,this.rowToDoQueue,this.colToDoQueue);
        this.concernTile=new int[2];
        this.solution="";
        this.concernTile[0]=rowToDoQueue.remove();
        this.doneTile=new HashSet<>();
        //End of set up

//        this.doneTile.add(1);
//        this.doneTile.add(2);
//        this.doneTile.add(4);
//        this.concernTile[0]=3;
//        this.concernTile[1]=4;
        this.concernTile[0]=1;

        System.out.println("init");
        tool.prtsqArrInSq(workingBoard);
//        System.out.println();
        rotate(concernTile[0]);
        System.out.println("after");
        tool.prtsqArrInSq(workingBoard);
        System.out.println();
        System.out.println("Move:");
        System.out.println(this.solution);
    }




    // Will pushZero to specific (row,col) and add move to this.solution
    // warning: will not care about solved tiles!
    public void zeroPusher(int targetRow,int targetCol){
        int[] currZeroIndex=this.tileToCurrentIndex.get(0);
        int tileToMove;
        while(currZeroIndex[0]!=targetRow && currZeroIndex[1]!=targetCol){
            while(currZeroIndex[0]!=targetRow){
                if(targetRow<currZeroIndex[0]){
                    tileToMove=workingBoard[currZeroIndex[0]-1][currZeroIndex[1]];
                    this.tileToCurrentIndex.get(tileToMove)[0]=currZeroIndex[0];
                    this.workingBoard[currZeroIndex[0]][currZeroIndex[1]]=tileToMove;
                    this.workingBoard[currZeroIndex[0]-1][currZeroIndex[1]]=0;
                    currZeroIndex[0]--;
                    this.solution+=tileToMove+" D"+"\n";
                }
                else if(targetRow>currZeroIndex[0]){
                    tileToMove=workingBoard[currZeroIndex[0]+1][currZeroIndex[1]];
                    this.tileToCurrentIndex.get(tileToMove)[0]=currZeroIndex[0];
                    this.workingBoard[currZeroIndex[0]][currZeroIndex[1]]=tileToMove;
                    this.workingBoard[currZeroIndex[0]+1][currZeroIndex[1]]=0;
                    currZeroIndex[0]++;
                    this.solution+=tileToMove+" U"+"\n";
                }
            }

        }
        while(currZeroIndex[1]!=targetCol){
            if(targetCol<currZeroIndex[1]){
                tileToMove=workingBoard[currZeroIndex[0]][currZeroIndex[1]-1];
                this.tileToCurrentIndex.get(tileToMove)[1]=currZeroIndex[1];
                this.workingBoard[currZeroIndex[0]][currZeroIndex[1]]=tileToMove;
                this.workingBoard[currZeroIndex[0]][currZeroIndex[1]-1]=0;
                currZeroIndex[1]--;
                this.solution+=tileToMove+" R"+"\n";
            }
            else if(targetCol>currZeroIndex[1]){
                tileToMove=workingBoard[currZeroIndex[0]][currZeroIndex[1]+1];
                this.tileToCurrentIndex.get(tileToMove)[1]=currZeroIndex[1];
                this.workingBoard[currZeroIndex[0]][currZeroIndex[1]]=tileToMove;
                this.workingBoard[currZeroIndex[0]][currZeroIndex[1]+1]=0;
                currZeroIndex[1]++;
                this.solution+=tileToMove+" L"+"\n";
            }
        }
    }

    //will pushZero to specific (row,col) and add move to this.solution
    //will care about the solved tiles
    private void superZeroPusher(int[][]workingBoard,int targetRow,int targetCol){
        int[][] tempBoard=tool.copy2DArr(workingBoard);

    }

    private int manhattanDis(int i,int j,int targetRow,int targetCol){
        return Math.abs(i-targetRow)+Math.abs(j-targetCol);
    }

    //Could move tile to zero and add move to this.solution
    public int singleMove(char dir){
        int[] currZeroIndex=this.tileToCurrentIndex.get(0);
        int tileToMove=0;
        if(dir=='U'){
            tileToMove=workingBoard[currZeroIndex[0]+1][currZeroIndex[1]];
            this.tileToCurrentIndex.get(tileToMove)[0]=currZeroIndex[0];
            this.workingBoard[currZeroIndex[0]][currZeroIndex[1]]=tileToMove;
            this.workingBoard[currZeroIndex[0]+1][currZeroIndex[1]]=0;
            currZeroIndex[0]++;
            this.solution+=tileToMove+" U"+"\n";
        }
        else if (dir=='D') {
            tileToMove=workingBoard[currZeroIndex[0]-1][currZeroIndex[1]];
            this.tileToCurrentIndex.get(tileToMove)[0]=currZeroIndex[0];
            this.workingBoard[currZeroIndex[0]][currZeroIndex[1]]=tileToMove;
            this.workingBoard[currZeroIndex[0]-1][currZeroIndex[1]]=0;
            currZeroIndex[0]--;
            this.solution+=tileToMove+" D"+"\n";
        }
        else if (dir=='R'){
            tileToMove=workingBoard[currZeroIndex[0]][currZeroIndex[1]-1];
            this.tileToCurrentIndex.get(tileToMove)[1]=currZeroIndex[1];
            this.workingBoard[currZeroIndex[0]][currZeroIndex[1]]=tileToMove;
            this.workingBoard[currZeroIndex[0]][currZeroIndex[1]-1]=0;
            currZeroIndex[1]--;
            this.solution+=tileToMove+" R"+"\n";
        }
        else if(dir=='L'){
            tileToMove=workingBoard[currZeroIndex[0]][currZeroIndex[1]+1];
            this.tileToCurrentIndex.get(tileToMove)[1]=currZeroIndex[1];
            this.workingBoard[currZeroIndex[0]][currZeroIndex[1]]=tileToMove;
            this.workingBoard[currZeroIndex[0]][currZeroIndex[1]+1]=0;
            currZeroIndex[1]++;
            this.solution+=tileToMove+" L"+"\n";
        }
        return tileToMove;
    }

    //could rotate a tile to the blank space in min step and add move to this.solution
    public void rotate(int concernTile){
        int[] currZeroIndex=this.tileToCurrentIndex.get(0);
        int[] currTileIndex=this.tileToCurrentIndex.get(concernTile);
        int[] corner1=copyIndex(this.tileToCurrentIndex.get(0));
        int[] corner2=copyIndex(this.tileToCurrentIndex.get(concernTile));
        int[] corner3=new int[2];
        int[] corner4=new int[2];
        int targetRow=corner1[0];
        int targetCol=corner1[1];
        int movedTile;
        if(corner1[0]!=corner2[0] && corner1[1]!=corner2[1]){
            //1---3
            //|   |
            //4---2

            //corner3 has same row as corner1, same col as corner2
            //corner4 has same row as corner2, same col as corner1
            corner3[0]=corner1[0];
            corner3[1]=corner2[1];
            corner4[0]=corner2[0];
            corner4[1]=corner1[1];
            while(!(currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                while(currZeroIndex[1]!=corner3[1]){    //corner 1 to corner 3
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[1]<corner3[1]){
                        movedTile=singleMove('L');
                        if(movedTile==concernTile){
                            if(corner3[1]-corner1[1]>1){
                                corner3[1]--;
                                corner2[1]--;
                                break;
                            }
                        }
                    }
                    else{
                        movedTile=singleMove('R');
                        if(movedTile==concernTile){
                            if(corner1[1]-corner3[1]>1){
                                corner3[1]++;
                                corner2[1]++;
                                break;
                            }
                        }
                    }
                }   //end: corner 1 to corner 3
                while(currZeroIndex[0]!=corner2[0]){    //corner 3 to corner 2
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[0]<corner2[0]){
                        movedTile=singleMove('U');
                        if(movedTile==concernTile){
                            if(corner2[0]-corner3[0]>1){
                                corner2[0]--;
                                corner4[0]--;
                                break;
                            }
                        }
                    }
                    else{
                        movedTile=singleMove('D');
                        if(movedTile==concernTile){
                            if(corner3[0]-corner2[0]>1){
                                corner2[0]++;
                                corner4[0]++;
                                break;
                            }
                        }
                    }
                }//end: //corner 3 to corner 2

                while(currZeroIndex[1]!=corner4[1]){    //corner 2 to corner 4
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[1]<corner4[1]){
                        movedTile=singleMove('L');
                        if(movedTile==concernTile){
                            if(corner3[1]-corner1[1]>1){
                                corner3[1]--;
                                corner2[1]--;
                                break;
                            }
                        }
                    }
                    else{
                        movedTile=singleMove('R');
                        if(movedTile==concernTile){
                            if(corner1[1]-corner3[1]>1){
                                corner3[1]++;
                                corner2[1]++;
                                break;
                            }
                        }
                    }
                }   //end: corner 2 to corner 4

                while(currZeroIndex[0]!=corner1[0]){    //corner4 to corner 1
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[0]<corner1[0]){
                        movedTile=singleMove('U');
                        if(movedTile==concernTile){
                            if(corner2[0]-corner3[0]>1){
                                corner2[0]--;
                                corner4[0]--;
                                break;
                            }
                        }
                    }
                    else{
                        movedTile=singleMove('D');
                        if(movedTile==concernTile){
                            if(corner3[0]-corner2[0]>1){
                                corner2[0]++;
                                corner4[0]++;
                                break;
                            }
                        }
                    }
                }       //end: corner4 to corner 1

            }
        }

        else if(corner1[0]==corner2[0]){
            //1---2
            //|   |
            //4---3
            //corner 3 has same col as corner 2
            //corner 4 has same col as corner 1
            if(corner1[0]==0){
                corner3[0]=1;
                corner3[1]=corner2[1];
                corner4[0]=1;
                corner4[1]=corner1[1];
            }
            else if(corner1[0]==this.boundRC){
                corner3[0]=corner1[0]-1;
                corner3[1]=corner2[1];
                corner4[0]=corner1[0]-1;
                corner4[1]=corner1[1];
            }
            else{
                if(!containsDoneTile(corner1[0]-1,true,corner1[1],corner2[1])){
                    corner3[0]=corner1[0]-1;
                    corner3[1]=corner2[1];
                    corner4[0]=corner1[0]-1;
                    corner4[1]=corner1[1];
                }
                else{
                    corner3[0]=corner1[0]+1;
                    corner3[1]=corner2[1];
                    corner4[0]=corner1[0]+1;
                    corner4[1]=corner1[1];
                }
            }
            while(!(currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                while(currZeroIndex[1]!=corner2[1]){    //corner1 to corner2
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[1]<corner2[1]){    ///corner1[1]<corner2[1]
                        movedTile=singleMove('L');
                        if(movedTile==concernTile){
                            if(corner2[1]-corner1[1]>1){
                                corner2[1]--;
                                corner3[1]--;
                                break;
                            }
                        }
                    }
                    else{   //corner1[1]>corner2[1]
                        movedTile=singleMove('R');
                        if(movedTile==concernTile){
                            if(corner1[1]-corner2[1]>1){
                                corner2[1]++;
                                corner3[1]++;
                                break;
                            }
                        }
                    }
                }
                while(currZeroIndex[0]!=corner3[0]){    //corner2 to corner3
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[0]<corner3[0]){    //corner2[0]<corner3[0]
                        singleMove('U');
                    }
                    else{   //corner2[0]>corner3[0]
                        singleMove('D');
                    }
                }
                while(currZeroIndex[1]!=corner4[1]){    //corner3 to corner4
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[1]<corner4[1]){    //corner3[1]<corner4[1]
                        singleMove('L');
                    }
                    else{
                        singleMove('R');
                    }
                }
                while(currZeroIndex[0]!=corner1[0]){    //corner 4 to corner1
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        break;
                    }
                    if(currZeroIndex[0]<corner1[0]){
                        singleMove('U');
                    }
                    else{
                        singleMove('D');
                    }
                }
            }
        }
        else if (corner1[1]==corner2[1]){
            //1---4
            //|   |
            //2---3
            //corner3 has same row as corner2
            //corner4 has same row as corner1
            if(corner1[1]==0){
                corner3[0]=corner2[0];
                corner3[1]=1;
                corner4[0]=corner1[0];
                corner4[1]=1;
            }
            else if(corner1[1]==this.boundRC){
                corner3[0]=corner2[0];
                corner3[1]=corner1[1]-1;
                corner4[0]=corner1[0];
                corner4[1]=corner1[1]-1;
            }
            else{
                if(!containsDoneTile(corner1[1]-1,false,corner1[0],corner2[0])){
                    corner3[0]=corner2[0];
                    corner3[1]=corner1[1]-1;
                    corner4[0]=corner1[0];
                    corner4[1]=corner1[1]-1;
                }
                else{
                    corner3[0]=corner2[0];
                    corner3[1]=corner1[1]+1;
                    corner4[0]=corner1[0];
                    corner4[1]=corner1[1]+1;
                }
            }
            while(!(currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                while(currZeroIndex[0]!=corner2[0]){
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        break;
                    }
                    if(currZeroIndex[0]<corner2[0]){    //corner1[0]<corner2[0]
                        movedTile=singleMove('U');
                        if(movedTile==concernTile){
                            if(corner2[0]-corner1[0]>1){
                                corner2[0]--;
                                corner3[0]--;
                                break;
                            }
                        }
                    }
                    else{   //corner2[0]<corner1[0]
                        movedTile=singleMove('D');
                        if(movedTile==concernTile){
                            if(corner1[0]-corner2[0]>1){
                                corner2[0]++;
                                corner3[0]++;
                                break;
                            }
                        }
                    }
                }
                while(currZeroIndex[1]!=corner3[1]){
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[1]<corner3[1]){
                        singleMove('L');
                    }
                    else{
                        singleMove('R');
                    }
                }
                while(currZeroIndex[0]!=corner4[0]){
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[0]<corner4[0]){
                        singleMove('U');
                    }
                    else{
                        singleMove('D');
                    }
                }
                while(currZeroIndex[1]!=corner1[1]){
                    if((currTileIndex[0]==targetRow && currTileIndex[1]==targetCol)){
                        return;
                    }
                    if(currZeroIndex[1]<corner1[1]){
                        singleMove('L');
                    }
                    else{
                        singleMove('R');
                    }
                }
            }
        }
    }

    //Could make move by reading order likes URRD
    public void scriptMove(String script){
        int len=script.length();
        for(int k=0;k<len;k++){
            singleMove(script.charAt(k));
        }
    }

    //isRow: could check if the concern row contains any of the done tile
    //!isRow: could check if the cocern col contains any of the done tile
    private Boolean containsDoneTile(int toCheck,Boolean isRow,int bound1,int bound2){
        if(bound1>bound2){
            int temp=bound1;
            bound1=bound2;
            bound2=temp;
        }
        if(isRow){
            for(int k=bound1;k<=bound2;k++){
                if(this.doneTile.contains(this.workingBoard[toCheck][k])){
                    return true;
                }
            }
            return false;
        }
        else{
            for(int k=bound1;k<=bound2;k++){
                //System.out.println(this.workingBoard[k][toCheck]);
                if(this.doneTile.contains(this.workingBoard[k][toCheck])){
                    return true;
                }
            }
            return false;
        }
    }

    //Return a new int[] for index
    private int[] copyIndex(int[] index){
        int[] temp=new int[2];
        temp[0]=index[0];
        temp[1]=index[1];
        return temp;
    }

    //Return the correct ans[][]
    //Exp (4*4):
    //this.ansArr[][]
    // 1  2  3  4
    // 5  6  7  8
    // 9 10 11 12
    //13 14 15  0
    private int[][] GenCorrectAns(int dim){
        int[][] correct= new int[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                correct[i][j] = i * dim + j + 1;
            }
        }
        correct[this.dimension - 1][this.dimension - 1] = 0;
        return correct;
    }

    //Return the HashMap<tile,index[]> of tile and its {row,col} in the board;
    private HashMap<Integer,int[]> genTileToIndex(int[][] currentBoard){
        HashMap<Integer,int[]> temp=new HashMap<>();
        for(int i=0;i<currentBoard.length;i++){
            for(int j=0;j<currentBoard.length;j++){
                temp.put(currentBoard[i][j],new int[] {i,j});
            }
        }
        return temp;
    }

    //Set up both the row and col to do list
    private void setUpRCtoDoList(int dim,Queue<Integer> rowToDoListForPlug,Queue<Integer> colToDoListForPlug){
        int[][] tempRowOrder=new int[dim][dim];
        int[][] tempColOrder=new int[dim][dim];
        int num;
        for(int i=0;i<dim;i++){
            for(int j=0;j<dim;j++){
                num=i*dim+j+1;
                tempRowOrder[i][j]=num;
                tempColOrder[j][i]=num;
            }
        }
        tempRowOrder[dim-1][dim-1]=0;
        tempColOrder[dim-1][dim-1]=0;
        for(int i=0;i<dim;i++){
            for(int j=0;j<dim;j++){
                rowToDoListForPlug.add(tempRowOrder[i][j]);
                colToDoListForPlug.add(tempColOrder[i][j]);
            }
        }
    }
}
