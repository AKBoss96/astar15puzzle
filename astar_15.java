import java.util.*;
import java.io.*;

public  class astar_15{
    private static class State{                 //class to represent each state 
        int level = 0;
        int [][] order = new int [4][4];        //configuration of tiles
        int blank_pos_row;                      //row in which empty tile is located
        int blank_pos_col;                      //column in which empty tile is located
        String move = "";
        State parent = null;                    //parent state
        State up = null;                        //states
        State down = null;                      //for
        State left = null;                      //tiles
        State right = null;                     //located in each of the 4 directions
    }

    private static class solutionStateInfo{    //separate class to represent the solution state
        int expandCount = 0;                   //number of nodes expanded
        State solutionState = null;
        long memoryUsage;                      //RAM usage of program
        long runningTime;                      //time taken to execute the program
        String moves = "";                     //path to be taken from initial state to goal state
        String startingBoard = "";
    }


    public static boolean goalTest(State curState){        //checking for the goal state
        int [][] goalState = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}};
        return Arrays.deepEquals(curState.order, goalState);    
    }

    public static void checkNextStates(State curState){             //method to check whether current state can be expanded in a particular direction
        if(curState.blank_pos_col > 0){
            curState.left = new State();
            curState.left.move = "L";
            curState.left.level = curState.level + 1;
            curState.left.blank_pos_col = curState.blank_pos_col - 1;
            curState.left.blank_pos_row = curState.blank_pos_row;
            curState.left.parent = curState;
            curState.left.order = performMove(curState.order, curState.blank_pos_row, curState.blank_pos_col, 'L');
        }
        if(curState.blank_pos_col < 3){
            curState.right = new State();
            curState.right.move = "R";
            curState.right.level = curState.level + 1;
            curState.right.blank_pos_col = curState.blank_pos_col + 1;
            curState.right.blank_pos_row = curState.blank_pos_row;
            curState.right.parent = curState;     
            curState.right.order = performMove(curState.order, curState.blank_pos_row, curState.blank_pos_col, 'R');

        }
        if(curState.blank_pos_row > 0){
            curState.up = new State();
            curState.up.move = "U";
            curState.up.level = curState.level + 1;
            curState.up.blank_pos_col = curState.blank_pos_col;
            curState.up.blank_pos_row = curState.blank_pos_row - 1;
            curState.up.parent = curState;
            curState.up.order = performMove(curState.order, curState.blank_pos_row, curState.blank_pos_col, 'U');
        
        }
         if(curState.blank_pos_row < 3){
            curState.down = new State();
            curState.down.move = "D";
            curState.down.level = curState.level + 1;
            curState.down.blank_pos_col = curState.blank_pos_col;
            curState.down.blank_pos_row = curState.blank_pos_row + 1;
            curState.down.parent = curState;
            curState.down.order = performMove(curState.order, curState.blank_pos_row, curState.blank_pos_col, 'D');
        }       
    }
    

    public static int[][] performMove(int[][] curState, int blankY, int blankX, char move){  //method to perform a move from current state to next state
        int [][] newState = new int [4][];
        for(int i = 0; i < 4; i++)
            newState[i] = curState[i].clone();

        if(move == 'U'){
            newState[blankY][blankX] = newState[blankY - 1][blankX];
            newState[blankY - 1][blankX] = 0;
        }
        else if(move == 'D'){
            newState[blankY][blankX] = newState[blankY + 1][blankX];
            newState[blankY + 1][blankX] = 0;
        }
        else if(move == 'L'){
            newState[blankY][blankX] = newState[blankY][blankX - 1];
            newState[blankY][blankX - 1] = 0;
        }
        else{
            newState[blankY][blankX] = newState[blankY][blankX + 1];
            newState[blankY][blankX + 1] = 0;
        }
        return newState;
    }

    
    public static String getPath(State searchResult){      //retrieves the sequence of moves from initial state to goal state
        LinkedList<State> solutionPath = new LinkedList<State>();
        State current = searchResult;
        String moves = "";
        while(current != null){
            solutionPath.addFirst(current);
            current = current.parent;
        }
        while(!solutionPath.isEmpty()){
            State tmp = solutionPath.removeFirst();
            if(tmp.move != "Start")
                moves = moves + tmp.move;
        }
        return moves;
    }
 



    public static int countNumMisplaced(State curState){            //method to compute the number of misplaced tiles in the board
        int [][] goalState = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}};
        int misplacedCount = 0;
        for (int row = 0; row<4; row++){
            for (int col = 0; col < 4; col++){
                if(goalState[row][col] != curState.order[row][col])
                    misplacedCount++;
            }
        }
        return misplacedCount;
    }

    public static solutionStateInfo AStarMisplacedTiles(State startState, String board){   //method to perform A* search with misplaced tiles heuristic
        ArrayList<State> nextStates = new ArrayList<State>();
        nextStates.add(startState);

        solutionStateInfo testSolution = new solutionStateInfo();
        int expandCount = 0;
        long startTime = System.currentTimeMillis();    //timestep before A* search starts

        State current = null;
        while(expandCount < Integer.MAX_VALUE){
            //identifying the state with the least cost function f = g + h. Here g is the level of search and h is the value of heuristic function
            int minVal = Integer.MAX_VALUE;
            for (int i = 0; i < nextStates.size() ; i++) {
                State tmp = nextStates.get(i);
                if(countNumMisplaced(tmp) + tmp.level < minVal){
                    minVal = countNumMisplaced(tmp) + tmp.level;
                    current = tmp;
                }
            }
            nextStates.remove(current);

            if (countNumMisplaced(current) == 0){         //goal state is reached when the number of misplaed tiles is 0
                testSolution.expandCount = expandCount;
                testSolution.solutionState = current;
                testSolution.startingBoard = board;
                testSolution.moves = getPath(current);

                Runtime runTime = Runtime.getRuntime();       //getting the current runtime associated with this process
                testSolution.memoryUsage = runTime.totalMemory() - runTime.freeMemory();   //difference between total memory and free memory gives the memory used by the program
                long endTime = System.currentTimeMillis();    //timestep after A* search ends
                testSolution.runningTime = endTime - startTime;   //difference between end time and start time gives the running time of the program.
                
                return testSolution;
            }
            else{                                      //if goal state has not been reached, expand the current state and move to the next states
                checkNextStates(current);
                expandCount++;

                if(current.left != null)
                    nextStates.add(current.left);
                if(current.right != null)
                    nextStates.add(current.right);
                if(current.up != null)
                    nextStates.add(current.up);
                if(current.down != null)
                    nextStates.add(current.down);
            }
        }
        return null;
    }


    public static int computeManhattanDistanceSum(State curState){     //method to compute the sum of Manhattan distances of all tiles on the board
        int distance = 0;
        for (int row = 0; row < 4 ; row++) {
            for (int col = 0; col < 4 ; col++ ) {
                int x;
                int y;
                int val = curState.order[row][col];
                
                if(val >= 1 && val <= 4){
                    y = 0;
                    x = val - 1;
                }
                else if(val >= 5 && val <= 8){
                    y = 1;
                    x = val - 5;
                }
                else if(val >= 9 && val <= 12){
                    y = 2;
                    x = val - 9;
                }
                else if(val >= 13 && val <= 15){
                    y = 3;
                    x = val - 13;
                }
                else{
                    y = 3;
                    x = 3;
                }

                distance = distance + Math.abs(row - y) + Math.abs(col - x);
            }
        }
        return distance;
    }

    
    public static solutionStateInfo AStarManhattanDistance(State startState, String board){    //method to perform A* search with Manhattan distance heuristic
        ArrayList<State> nextStates = new ArrayList<State>();
        nextStates.add(startState);

        solutionStateInfo testSolution = new solutionStateInfo();
        int expandCount = 0;
        long startTime = System.currentTimeMillis(); //timestep before A* search starts

        State current = null;
        while(expandCount < Integer.MAX_VALUE){
            //identifying the state with the least cost function f = g + h. Here g is the level of search and h is the value of heuristic function
            int minVal = Integer.MAX_VALUE;
            for (int i = 0; i < nextStates.size() ; i++) {
                State tmp = nextStates.get(i);
                if(computeManhattanDistanceSum(tmp) + tmp.level < minVal){
                    minVal = computeManhattanDistanceSum(tmp) + tmp.level;
                    current = tmp;
                }
            }
            nextStates.remove(current);

            if (computeManhattanDistanceSum(current) == 0){     //goal state is reached when sum of Manhattan distances is 0
                testSolution.expandCount = expandCount;
                testSolution.solutionState = current;
                testSolution.startingBoard = board;
                testSolution.moves = getPath(current);

                Runtime runTime = Runtime.getRuntime();     //getting the current runtime associated with this process
                testSolution.memoryUsage = runTime.totalMemory() - runTime.freeMemory(); //difference between total memory and free memory gives the memory used by the program
                long endTime = System.currentTimeMillis();   //timestep after A* search ends
                testSolution.runningTime = endTime - startTime;   //difference between end time and start time gives the running time of the program.
                
                return testSolution;
            }
            else{                                            //if goal state has not been reached, expand the current state and move to the next states
                checkNextStates(current);
                expandCount++;

                if(current.left != null)
                    nextStates.add(current.left);
                if(current.right != null)
                    nextStates.add(current.right);
                if(current.up != null)
                    nextStates.add(current.up);
                if(current.down != null)
                    nextStates.add(current.down);
            }
        }
        return null;
    }

    

    public static void main(String [] args){
        if(args.length != 16){      //checking for input
            System.out.println("Enter exactly 16 numbers!");
            return;
        }
 
        System.gc();
        
        try{
            //initializing the start state with the initial configuration
            String board = "";
            State startState = new State();
            startState.move = "Start";
            for (int i =0; i < 4; i++) {
                for (int q = 0; q < 4; q++) {
                    startState.order[i][q] = Integer.parseInt(args[4*i + q]);
                    board = board + startState.order[i][q] +" ";
                    if(startState.order[i][q] == 0){            //empty tile is represented by 0
                            startState.blank_pos_row = i;
                            startState.blank_pos_col = q;
                    }
                }
            }
            
            int inversions = 0;     
            for (int i = 0; i < args.length-1 ; i++){
                for(int j = i+1; j < args.length ; j++)
                {
                    //inversion occurs when i < j, but array[i] > a[j]
                    if(Integer.parseInt(args[j])!=0 && Integer.parseInt(args[i])!=0 && Integer.parseInt(args[i]) > Integer.parseInt(args[j]))
                        inversions++;
                }
            }
            
            int row_from_below = 4 - startState.blank_pos_row; //identifying the row position of the empty tile counting from the bottom
            
            if((row_from_below%2==0 && inversions%2==0) || (row_from_below%2!=0 && inversions%2!=0)) //unsolvable if both row position and inversion count are of same parity
            {
                System.out.println("\nSolution cannot be found");
                return;
            }
            
            System.out.println("\nA* search using heuristic 1 - Number of misplaced tiles\n-------------------------------------------------------");

            solutionStateInfo searchResult = AStarMisplacedTiles(startState, board); //calling A* search with misplaced tiles heuristic
            
            if(searchResult != null)
                System.out.println("Moves: " + searchResult.moves+ "\nNumber of nodes expanded: "+ searchResult.expandCount + "\nTime taken: "+ searchResult.runningTime+" ms\nMemory used: "+searchResult.memoryUsage+" bytes");
            
        }catch(OutOfMemoryError e){
            System.out.println("Program ran out of memory!");
        }

        System.gc();
        
        try{
            //initializing the start state with the initial configuration
            String board = "";
            State startState = new State();
            startState.move = "Start";
            for (int i =0; i < 4; i++) {
                for (int q = 0; q < 4; q++) {
                    startState.order[i][q] = Integer.parseInt(args[4*i + q]);
                    board = board + startState.order[i][q] +" ";
                    if(startState.order[i][q] == 0){             //empty tile is represented by 0
                            startState.blank_pos_row = i;
                            startState.blank_pos_col = q;
                    }
                }
            }

            System.out.println("\nA* search using heuristic 2 - Manhattan distance\n------------------------------------------------");
            solutionStateInfo searchResult = AStarManhattanDistance(startState, board); //calling A* search with Manhattan distance heuristic
            
            if(searchResult != null)
                System.out.println("Moves: " + searchResult.moves+ "\nNumber of nodes expanded: "+ searchResult.expandCount + "\nTime taken: "+ searchResult.runningTime+" ms\nMemory used: "+searchResult.memoryUsage+" bytes"); //printing the result
            
        }catch(OutOfMemoryError e){
            System.out.println("Program ran out of memory!");
        }
    }


}