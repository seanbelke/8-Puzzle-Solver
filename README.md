# 8-Puzzle-Solver
An algorithm that finds the optimal solution to any scrambled 8 puzzle, along with a GUI and a function to generate scrambles.

## 8-Puzzle Overview
An 8-Puzzle is a sliding tile game on a 3x3 grid.  There are 8 tiles on the grid and one blank space, allowing the player
to slide the tiles around the grid using the blank space.  The goal is to permute the tiles on the grid such that
they form the sequence 1, 2, 3, 4, 5, 6, 7, 8, with the blank at the end, when reading the tiles in row-major order.

Here is a visual representation of what the tiles should look like at the end:

    +-------+-------+-------+
    |       |       |       |
    |   1   |   2   |   3   |
    |       |       |       |
    +-------+-------+-------+
    |       |       |       |
    |   4   |   5   |   6   |
    |       |       |       |
    +-------+-------+-------+
    |       |       |       |
    |   7   |   8   |       |
    |       |       |       |
    +-------+-------+-------+

And here is an example of a scrambled 8-Puzzle: 

    +-------+-------+-------+
    |       |       |       |
    |   8   |   6   |   3   |
    |       |       |       |
    +-------+-------+-------+
    |       |       |       |
    |   2   |   7   |       |
    |       |       |       |
    +-------+-------+-------+
    |       |       |       |
    |   4   |   5   |   1   |
    |       |       |       |
    +-------+-------+-------+

## GUI
When the user runs this program, the GUI will display a solved 8-Puzzle.  The user can use the bottom-left button
to scramble the 8-Puzzle.  The user can also, at any time (other than when the puzzle is being solved by the 
program), use the arrow keys to move the tiles around.  This is the mechanism by which the user can solve the
puzzle.  For any given puzzle position, it is possible that some of the arrow keys will have no effect.  Each arrow
key corresponds to moving a certain tile *into* the location where the blank is.  Unless the blank is in the 
center of the puzzle, not all of the arrow keys will be valid because there will be fewer than 4 different tiles
that could be moved into the blank space.  Here are some examples: 

    +-------+-------+-------+
    |       |       |       |
    |   5   |   8   |   4   |
    |       |       |       |
    +-------+-------+-------+
    |       |       |       |
    |   2   |   7   |   3   |
    |       |       |       |
    +-------+-------+-------+
    |       |       |       |
    |   1   |       |   6   |
    |       |       |       |
    +-------+-------+-------+

In the above configuration, there are 3 tiles surrounding the blank space.  In this case, the *down*, *right*, and
*left* arrow keys will all have effects.  Specifically, the *down* arrow key would move tile number 7 *down* into the
location that currently contains the blank space.  The *right* arrow key would move tile number 1 *right* into the 
location that currently contains the blank space.  Finally, the *left* arrow key would move tile number 6 *left*
into the location that currently contains the blank space.  The *up* arrow key would have no effect, because there
is no tile below the blank space that could be moved upwars to take its position.  

The other button on the GUI reads "Solve Puzzle."  It will display the steps to achieve an optimal solution to the
current configuration of the puzzle.  

## Finding Solutions and the A* Algorithm
The heart of this program is its ability to find an optimal solution to any scrambled 8-Puzzle.  In order to do this,
an informed search algorithm known as the A* algorithm is used.  To use the A* algorithm on the 8-Puzzle, each state
of the puzzle is examined to find its "neighbors" - the states that could be reached with a single move.  Each of those
states is added to a running list of "discovered" puzzle states.  This list is kept ordered by how "promising" each state
looks.  How "promising" a particular state of the puzzle looks is determined by summing together two metrics: its g-value,
which is the number of moves performed on the puzzle so far, and its h-value, which is the estimated cost left to reach
the solved state.

The cost is calculated as the sum of two heuristic measures: the sum of the Manhattan Distances, and the number of direct
tile reversals.  Manhattan Distance is a common metric used in these kinds of puzzles, and is the (horizontal + vertical)
distance that each puzzle piece is away from its location in the solved puzzle state.  Direct tile reversals are also 
considered in the calculation of the cost because they generally make the puzzle more difficult to solve.  

Essentially, a loop continues examining the most promising "discovered" puzzle state and adding its neighbors to the 
discovered list, until the state that it is examining is the solved state.  At this point, the algorithm ends, and a
list of moves is sent to the front end so that it can display the solution for the user.  The front end will display
each step of the solution in order with a small delay between each one.

## Optimality
The A* algorithm is guaranteed to find an optimal solution if the heuristic being used is "admissible."  This property
of admissibility means that the heuristic never overestimates the remaining cost to solve the puzzle.  This is the case
for the Manhattan Distance + Tile Reversal heuristic, so the solution displayed will always be optimal.

## Compatability Note
This program was written and tested on a Windows PC, so it is possible that running it on a Mac or Linux machine will
produce some minor alignment issues around the border of the JFrame.  If this is the case, it would be resolved by 
modifying the arguments to the initializeAndDisplayGUI method called in the Driver as well as the constants added to
the gridSize and bottomPanelSize arguments in the setSize method called in the GuiStarter.initializeAndDisplayGUI
method.

## References

### A* Algorithm Explanation: 
http://theory.stanford.edu/~amitp/GameProgramming/AStarComparison.html
https://en.wikipedia.org/wiki/A*_search_algorithm#Optimal_efficiency
https://en.wikipedia.org/wiki/Admissible_heuristic

### Referred to this source when writing the GUI: 
https://ssaurel.medium.com/developing-a-15-puzzle-game-of-fifteen-in-java-dfe1359cc6e3

## Build Instructions

Start the program by running the main method in Driver.java
