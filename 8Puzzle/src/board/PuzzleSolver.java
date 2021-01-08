package board;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/* This class contains a single static method called solve() that will find an optimal
 * solution to a given 8-Puzzle object.
 */
public class PuzzleSolver {
	
	/* This method takes in a (usually scrambled) PuzzleState as its only parameter.
	 * It will then use the A* algorithm to find an optimal solution to the puzzle. 
	 * The returned value of the method is a List of PuzzleStates that represents
	 * the sequence of PuzzleStates that make up the solution that this method
	 * found.  This will be used to display the solution on the GUI.  
	 * 
	 * The A* algorithm works as follows: The PuzzleState is examined to 
	 * find its neighbors, excluding its "parent."  Then, each of those neighbors,
	 * now that they have been "discovered", are added to a queue called the "open
	 * list," which is ordered based on how promising each state looks.  This is 
	 * determined by the f-values of the states.  A lower f-value indicates a more 
	 * promising state, which means it is more likely to lead to an optimal solution.
	 * On each pass through a loop, the most promising puzzle state in the open list
	 * is examined, and this repeats until the solved state is found.
	 */
	public static List<PuzzleState> solve(PuzzleState initialState) {
		
		PuzzleState scrambledOne = new PuzzleState(initialState.getBoardState());
		PriorityQueue<PuzzleState> openList = new PriorityQueue<>();
		openList.add(scrambledOne);
		PuzzleState currentState = scrambledOne;
		
		while (true) {
			
			currentState = openList.remove();
			/* It the solved state has been found, exit the loop */
			if (currentState.equals(PuzzleState.GOAL_STATE)) {
				break;
			}
			
			/* otherwise, add all of this state's children to the open list */
			List<PuzzleState> children = currentState.children();
			for (PuzzleState child : children) {
				if ((openList.contains(child))) {
					continue;
				}
				openList.add(child);
			}
			
		}
		
		LinkedList<PuzzleState> operationsToSolve = new LinkedList<>();
		PuzzleState curr = currentState;
		while (curr != null) {
			operationsToSolve.addFirst(curr);
			curr = curr.getParent();
		}
		
		return operationsToSolve;
		
	}
}
