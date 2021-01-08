package board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PuzzleState implements Comparable<PuzzleState> {
	
	public static final PuzzleState GOAL_STATE;
	private static final Random random;
	
	private static final int[] NINE_PRIMES = {17, 19, 23, 29, 31, 37, 41, 43, 47}; // used for hash codes

	/* static initializer to set the random object and the GOAL_STATE PuzzleState */
	static {
		ArrayList<Integer> goalPositions = new ArrayList<>();
		for (int i = 1; i < 9; i++) {
			goalPositions.add(i);
		}
		goalPositions.add(0);
		GOAL_STATE = new PuzzleState(goalPositions, -1, 0, null);
		
		random = new Random();
	}

	private ArrayList<Integer> puzzle; /* the locations of each number in the puzzle */
	private PuzzleState parent; /* reference to the "parent" puzzle */
	private int parentOperation; // will be -1 if the puzzle represents some "special state", like the
	// initial state or the goal state.
	private int gVal; // will be -1 for the "goal state".
	private int fVal; // Computed once since it never changes.

	/* Standard constructor.  Also computes the f-value of the PuzzleState and stores it in a 
	 * variable.
	 */
	public PuzzleState(ArrayList<Integer> puzzle, int parentOperation, int gVal, PuzzleState parent) {
		this.puzzle = puzzle;
		this.parentOperation = parentOperation;
		this.gVal = gVal;
		this.parent = parent;
		this.fVal = this.f();
	}

	/* Sets the current PuzzleState to have no parent and a g-value of 0. */
	public PuzzleState(ArrayList<Integer> puzzle) {
		this(puzzle, -1, 0, null);
	}
	
	/* No-arg constructor.  This creates a scrambled PuzzleState by applying 
	 * between 40 and 75 random moves to the puzzle.*/
	public PuzzleState() {
		this(GOAL_STATE);
		int numMoves = (random.nextInt(36) + 40);
		for (int i = 0; i < numMoves; i++) {
			ArrayList<Integer> operations = getPossibleOperationsExcludeInverse();
			int op = operations.get((int)(Math.random() * operations.size()));
			performOperation(this, op);
		}
		gVal = 0;
	}

	/* Copy Constructor.  The parents are aliased because it would be very
	 * time consuming to recursively copy the parent.  A deep copy of the puzzle
	 * is created.
	 */
	public PuzzleState(PuzzleState other) {
		parent = other.parent;
		puzzle = new ArrayList<>(other.puzzle);
		parentOperation = other.parentOperation;
		gVal = other.gVal;
	}

	/* This method examines the current object, finds the position of the blank tile,
	 * and based on the position of the blank, it determines which operations are 
	 * possible to make.  For example, if the blank is on the left side of the GUI,
	 * then it would be impossible to perform the operation MOVE_BLANK_LEFT, so
	 * that operation would not be added to the returned ArrayList.  Additionally,
	 * this method excludes the "inverse" operation, meaning the operation that was
	 * used to reach the current PuzzleState object.  This is used in places where it
	 * would be pointless to check the previous state again.
	 */
	public ArrayList<Integer> getPossibleOperationsExcludeInverse() {
		
		ArrayList<Integer> possibleOperations = getPossibleOperationsIncludeInverse();
		
		if (parentOperation == -1) { // the PuzzleState has no parent (it was just scrambled)
			return possibleOperations; // no operation to remove
		}
		
		/* otherwise, remove the inverse of the parent operation.  Integer.valueOf() is necessary
		 * since it would otherwise attempt to remove the index given by the argument */
		possibleOperations.remove(Integer.valueOf(BoardOperations.inverseOperation(parentOperation)));
		
		return possibleOperations;
	}
	
	/* This method examines the current object, finds the position of the blank tile,
	 * and based on the position of the blank, it determines which operations are 
	 * possible to make.  For example, if the blank is on the left side of the GUI,
	 * then it would be impossible to perform the operation MOVE_BLANK_LEFT, so
	 * that operation would not be added to the returned ArrayList.
	 */
	public ArrayList<Integer> getPossibleOperationsIncludeInverse() {
		
		int posOfBlank = puzzle.indexOf(0);
		ArrayList<Integer> possibleOperations = new ArrayList<>();
		
		/* based on the position of the blank, add all possible operations */
		switch(posOfBlank) {
		case 0:
			possibleOperations.add(BoardOperations.MOVE_BLANK_DOWN);
			possibleOperations.add(BoardOperations.MOVE_BLANK_RIGHT);
			break;
		case 1: 
			possibleOperations.add(BoardOperations.MOVE_BLANK_DOWN);
			possibleOperations.add(BoardOperations.MOVE_BLANK_RIGHT);
			possibleOperations.add(BoardOperations.MOVE_BLANK_LEFT);
			break;
		case 2:
			possibleOperations.add(BoardOperations.MOVE_BLANK_DOWN);
			possibleOperations.add(BoardOperations.MOVE_BLANK_LEFT);
			break;
		case 3:
			possibleOperations.add(BoardOperations.MOVE_BLANK_DOWN);
			possibleOperations.add(BoardOperations.MOVE_BLANK_RIGHT);
			possibleOperations.add(BoardOperations.MOVE_BLANK_UP);
			break;
		case 4:
			possibleOperations.add(BoardOperations.MOVE_BLANK_DOWN);
			possibleOperations.add(BoardOperations.MOVE_BLANK_RIGHT);
			possibleOperations.add(BoardOperations.MOVE_BLANK_UP);
			possibleOperations.add(BoardOperations.MOVE_BLANK_LEFT);
			break;
		case 5:
			possibleOperations.add(BoardOperations.MOVE_BLANK_DOWN);
			possibleOperations.add(BoardOperations.MOVE_BLANK_UP);
			possibleOperations.add(BoardOperations.MOVE_BLANK_LEFT);
			break;
		case 6:
			possibleOperations.add(BoardOperations.MOVE_BLANK_UP);
			possibleOperations.add(BoardOperations.MOVE_BLANK_RIGHT);
			break;
		case 7:
			possibleOperations.add(BoardOperations.MOVE_BLANK_UP);
			possibleOperations.add(BoardOperations.MOVE_BLANK_RIGHT);
			possibleOperations.add(BoardOperations.MOVE_BLANK_LEFT);
			break;
		case 8:
			possibleOperations.add(BoardOperations.MOVE_BLANK_UP);
			possibleOperations.add(BoardOperations.MOVE_BLANK_LEFT);
		}
		
		return possibleOperations;
		
	}

	/* getters */
	
	public ArrayList<Integer> getBoardState() {
		return new ArrayList<>(puzzle);
	}
	
	/* A copy is not made, so the caller must not modify the result of
	 * a call to this getter.
	 */
	public PuzzleState getParent() {
		return parent;
	}
	
	public int getParentOperation() {
		return parentOperation;
	}
	
	/* setters */
	
	public void setGValue(int gVal) {
		this.gVal = gVal;
	}
	
	public void setParentOperation(int op) {
		this.parentOperation = op;
	}
	
	public void setParent(PuzzleState parent) {
		this.parent = parent;
	}
	
	/* Performs the given operation on the given PuzzleState.  This method
	 * does not return anything, because it modifies the first parameter 
	 * instead.  This method is very similar to the getNewState methods, 
	 * except that instead of returning the new PuzzleState, this modifies
	 * the parameter.  The caller of this method must ensure that operation
	 * is valid given the position of the blank.
	 */
	private void performOperation(PuzzleState initialState, int operation) {
		/* find where the blank is */
		int posOfBlank = puzzle.indexOf(0);
		ArrayList<Integer> newPositions = new ArrayList<>();
		
		/* perform modifications based on the position of the blank */
		switch(operation) {
		case BoardOperations.MOVE_BLANK_UP:
			for (int i = 0; i < posOfBlank - 3; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			for (int i = (posOfBlank - 2); i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank - 3));
			for (int i = (posOfBlank + 1); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_DOWN:
			for (int i = 0; i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank + 3));
			for (int i = (posOfBlank + 1); i < posOfBlank + 3; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			for (int i = (posOfBlank + 4); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_LEFT:
			for (int i = 0; i < posOfBlank - 1; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			newPositions.add(puzzle.get(posOfBlank - 1));
			for (int i = (posOfBlank + 1); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_RIGHT:
			for (int i = 0; i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank + 1));
			newPositions.add(0);
			for (int i = (posOfBlank + 2); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		default:
			throw new IllegalArgumentException(operation + "is not a valid operation!");
		}
		
		/* Change the puzzle of the PuzzleState parameter to the modified one, and then
		 * update the parent operation and g-value of the new PuzzleState.
		 */
		initialState.puzzle = newPositions;
		initialState.parentOperation = operation;
		initialState.gVal++;
	}
	
	/* Returns a new PuzzleState object which is the result of applying the
	 * operation given as a parameter to the current object.  The current 
	 * PuzzleState will be returned if the operation passed to the method
	 * is not valid given the position of the blank.
	 */
	public PuzzleState getNewStateIncludeInverse(int operation) {
		
		/* make sure the operation is valid given the position of the blank */
		if (!getPossibleOperationsIncludeInverse().contains(operation)) {
			return this;
		}
		
		int posOfBlank = puzzle.indexOf(0);
		ArrayList<Integer> newPositions = new ArrayList<>();
		
		/* apply changes for each case */
		switch(operation) {
		case BoardOperations.MOVE_BLANK_UP:
			for (int i = 0; i < posOfBlank - 3; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			for (int i = (posOfBlank - 2); i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank - 3));
			for (int i = (posOfBlank + 1); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_DOWN:
			for (int i = 0; i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank + 3));
			for (int i = (posOfBlank + 1); i < posOfBlank + 3; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			for (int i = (posOfBlank + 4); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_LEFT:
			for (int i = 0; i < posOfBlank - 1; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			newPositions.add(puzzle.get(posOfBlank - 1));
			for (int i = (posOfBlank + 1); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_RIGHT:
			for (int i = 0; i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank + 1));
			newPositions.add(0);
			for (int i = (posOfBlank + 2); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		default:
			throw new IllegalArgumentException(operation + "is not a valid operation!");
		}
		return new PuzzleState(newPositions, operation, gVal + 1, this);
	}

	/* Returns a new PuzzleState object which is the result of applying the
	 * operation given as a parameter to the current object.  This excludes
	 * the inverse operation, meaning if the parameter is the inverse operation
	 * of whatever operation was last applied to reach the current PuzzleState,
	 * then the current PuzzleState will be returned.  The current PuzzleState 
	 * will also be returned if the operation passed to the method
	 * is not valid given the position of the blank.
	 */
	public PuzzleState getNewStateExcludeInverse(int operation) {
		
		/* make sure the operation is valid and not the inverse of the previous
		 * operation
		 */
		if (!(getPossibleOperationsExcludeInverse().contains(operation))) {
			return this;
		}
		
		int posOfBlank = puzzle.indexOf(0);
		ArrayList<Integer> newPositions = new ArrayList<>();
		
		/* apply changes based on the operation */
		switch(operation) {
		case BoardOperations.MOVE_BLANK_UP:
			for (int i = 0; i < posOfBlank - 3; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			for (int i = (posOfBlank - 2); i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank - 3));
			for (int i = (posOfBlank + 1); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_DOWN:
			for (int i = 0; i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank + 3));
			for (int i = (posOfBlank + 1); i < posOfBlank + 3; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			for (int i = (posOfBlank + 4); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_LEFT:
			for (int i = 0; i < posOfBlank - 1; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(0);
			newPositions.add(puzzle.get(posOfBlank - 1));
			for (int i = (posOfBlank + 1); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		case BoardOperations.MOVE_BLANK_RIGHT:
			for (int i = 0; i < posOfBlank; i++) {
				newPositions.add(puzzle.get(i));
			}
			newPositions.add(puzzle.get(posOfBlank + 1));
			newPositions.add(0);
			for (int i = (posOfBlank + 2); i < 9; i++) {
				newPositions.add(puzzle.get(i));
			}
			break;
		default:
			throw new IllegalArgumentException(operation + "is not a valid operation!");
		}
		
		return new PuzzleState(newPositions, operation, gVal + 1, this);
		
	}

	/* This method returns an ArrayList of all PuzzleState objects that are
	 * reachable from the current PuzzleState by making a single move.
	 * This excludes the inverse of whichever move was used to reach the 
	 * current PuzzleState, because the only place this method is used is
	 * in the algorithm to find the optimal solution, and it would be
	 * inefficient to examine some states multiple times.
	 */
	public ArrayList<PuzzleState> children() {
		ArrayList<PuzzleState> children = new ArrayList<>(3);
		for (int op : getPossibleOperationsExcludeInverse()) {
			children.add(getNewStateExcludeInverse(op));
		}
		return children;
	}
	
	/* returns the g-value (the number of steps that have been
	 * taken to reach the current PuzzleState).
	 */
	public int g() {
		return gVal;
	}

	/* Computes the heuristic value (h-value), which is calculated
	 * as the sum of the Manhattan Distances of all tiles on the board
	 * and the number of direct tile reversals.
	 */
	public int h() {
		return sumOfManhattanDistances() + numTileReversals();
	}

	/* Computes the f value, as the sum of the g-value (the number of
	 * steps that have been taken to reach the current PuzzleState)
	 * and the h-value (the heuristic)
	 */
	public int f() {
		return h() + gVal;
	}

	/* One part of the heuristic computed for each PuzzleState.  
	 * Tile Reversals are undesirable because they require
	 * longer cycles of moves to fix.
	 */
	public int numTileReversals() {
		
		int tileReversalCount = 0;
		Set<Integer> finishedSet = new HashSet<>();
		
		for (int i = 1; i < 8; i++) {
			
			if (finishedSet.contains(i)) {
				continue;
			}
			finishedSet.add(i); 
			
			int currPos = puzzle.indexOf(i);
			if (currPos == (i - 1)) {
				continue;
			}
			
			int currRow = currPos / 3;
			int currCol = currPos % 3;
			int goalRow = (i - 1) / 3;
			int goalCol = (i - 1) % 3;
			int horizDistance = Math.abs(currCol - goalCol);
			int vertDistance = Math.abs(currRow - goalRow);
			int manhattanDistance = (horizDistance + vertDistance);
			
			if (manhattanDistance > 1) {
				continue;
			}
			int shouldHave = (currPos + 1);
			if (puzzle.get(i - 1) == shouldHave) {
				tileReversalCount += 2;
				finishedSet.add(shouldHave);
			}
		}
		return tileReversalCount;
		
	}

	/* One part of the heuristic computed for each PuzzleState.  This
	 * method adds up the Manhattan Distances (described in a comment
	 * above the method manhattanDistance()) for every tile on the
	 * grid, including the blank.  
	 */
	private int sumOfManhattanDistances() {
		
		int sum = 0;
		for (int i = 0; i < 9; i++) {
			sum += manhattanDistance(i);
		}
		return sum;
		
	}

	/* Computes the Manhattan Distance for a single digit on
	 * the grid.  The Manhattan Distance is the number of moves
	 * that a single tile on the grid is away from its solved
	 * location.  This is the sum of the horizontal distance
	 * and the vertical distance between the tile and its 
	 * destination.
	 */
	private int manhattanDistance(int digit) {
		
		/* compute the actual position of the tile corresponding to 
		 * the parameter digit
		 */
		int position = puzzle.indexOf(digit);
		int row = position / 3;
		int col = position % 3;
		
		/* Determine where the tile corresponding to the parameter
		 * digit will be in the solved state.
		 */
		int goalCol;
		int goalRow;
		
		if (digit == 0) { // the blank goes in the last spot on the grid
			goalRow = 2;
			goalCol = 2;
		} else { // normal (non-blank) tiles
			goalRow = (digit - 1) / 3;
			goalCol = (digit - 1) % 3;
		}
		
		/* compute the vertical and horizontal distances from the actual
		 * location to the "goal" location, and add them together.
		 */
		int vertDistance = Math.abs(row - goalRow);
		int horizDistance = Math.abs(col - goalCol);
		
		return vertDistance + horizDistance;
		
	}

	/* Two PuzzleState objects are considered equal if every location on
	 * the puzzle is the same.  That means that the f-value of each 
	 * PuzzleState is not involved in the check for equality.  This is a 
	 * rare case where equals and compareTo will not check for the same
	 * things.  The compareTo method will be used when ordering the 
	 * PuzzleStates according to how "promising" they are within the
	 * PuzzleSolver.solve() method.  The equals method will be used when
	 * checking if a given PuzzleState has already been examined or not,
	 * and in that case we want to check for puzzles that look the same.
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if ( ! (other instanceof PuzzleState)) {
			return false;
		}
		PuzzleState state = (PuzzleState) other;
		return puzzle.equals(state.puzzle);
	}

	/* 9 unique primes (defined at the top of the file) are multiplied by
	 * each location on the grid in order to avoid collisions in the hash
	 * table as much as possible.
	 */
	public int hashCode() {
		int hashCode = 0;
		for (int i = 0; i < 9; i++) {
			hashCode += (NINE_PRIMES[i] * puzzle.get(i));
		}
		return hashCode;
	}

	/* This toString method was used earlier in the testing process before
	 * the GUI was created.
	 */
	public String toString() {
		int blankLocation = puzzle.indexOf(0);
		String s = "[";
		s += ((blankLocation == 0)? " " : (puzzle.get(0))) + " ";
		s += ((blankLocation == 1)? " " : (puzzle.get(1))) + " ";
		s += ((blankLocation == 2)? " " : (puzzle.get(2))) + "]\n";
		s += "[" + ((blankLocation == 3)? " " : (puzzle.get(3))) + " ";
		s += ((blankLocation == 4)? " " : (puzzle.get(4))) + " ";
		s += ((blankLocation == 5)? " " : (puzzle.get(5))) + "]\n";
		s += "[" + ((blankLocation == 6)? " " : (puzzle.get(6))) + " ";
		s += ((blankLocation == 7)? " " : (puzzle.get(7))) + " ";
		s += ((blankLocation == 8)? " " : (puzzle.get(8))) + "]";
		return s;
	}

	/* Two PuzzleStates will be compared according to how "promising" each one
	 * is.  This metric is calculated for a given PuzzleState object as the 
	 * sum of its h-value (which estimates how close it is to the solved state) 
	 * and its g-value (which is the number of moves that have been performed on 
	 * it so far).
	 */
	@Override
	public int compareTo(PuzzleState other) {
		return (fVal - other.fVal);
	}
	
}
