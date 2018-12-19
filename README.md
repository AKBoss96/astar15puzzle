This is the Java implementation of the 15-puzzle using A* search with two heuristics - number of misplaced tiles and Manhattan distance. The performance of the two heuristics are compared.

Execution instructions:
1. The initial tile configuration is passed as individual numbers in the command line separated by a whitespace character, where the numbers from 1 to 15 are entered in some order and the empty tile is denoted by 0.

2. The goal state is represented as 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 0.

3. Compile the program using javac command.
	E.g., $ javac astar_15.java

4. Run the bytecode using the java command with input state as command-line argument.
	E.g., $ java astar_15 1 0 3 4 5 2 6 8 9 10 7 11 13 14 15 12
