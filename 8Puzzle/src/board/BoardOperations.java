package board;

/* A simple class that defines the possible operations for the 8 Puzzle
 * Game along with a function to give the inverse of an operation.
 */
public class BoardOperations {
	
	/* Symbolic constants for the 4 possible moves. */
	public static final int MOVE_BLANK_UP = 0;
	public static final int MOVE_BLANK_DOWN = 1;
	public static final int MOVE_BLANK_LEFT = 2;
	public static final int MOVE_BLANK_RIGHT = 3;

	/* Simple function that is just a wrapper around a switch statement.  
	 * This function takes in an integer representing the original operation
	 * code, and returns an integer representing the inverse of that
	 * operation.
	 */
	public static int inverseOperation(int operation) {
		switch(operation) {
		case MOVE_BLANK_UP:
			return MOVE_BLANK_DOWN;
		case MOVE_BLANK_DOWN:
			return MOVE_BLANK_UP;
		case MOVE_BLANK_LEFT:
			return MOVE_BLANK_RIGHT;
		case MOVE_BLANK_RIGHT:
			return MOVE_BLANK_LEFT;
		default:
			throw new IllegalArgumentException(operation + " is not a valid operation");
		}
	}

}
