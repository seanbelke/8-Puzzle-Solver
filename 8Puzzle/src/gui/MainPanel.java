package gui;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import board.BoardOperations;
import board.PuzzleSolver;
import board.PuzzleState;

/* This class represents the main container inside of the JFrame for the GUI.  It
 * contains two other containers, the GridPanel and the BottomPanel, which are
 * defined as classes within this class.
 */
public class MainPanel extends JPanel {

	
	private static final long serialVersionUID = 1L;
	
	/* define the Fonts for the GUI */
	private static final Font GRID_FONT = new Font("Cambria Math", Font.BOLD, 50);
	private static final Font BOTTOM_PANEL_FONT = new Font("Georgia", Font.PLAIN, 17);

	/* define the colors for the GUI */
	private static final Color TILE_COLOR = Color.LIGHT_GRAY,
			TILE_BORDER_COLOR = Color.BLACK, NUMBER_COLOR = Color.BLACK, 
			GRID_BACKGROUND_COLOR = new Color(235, 235, 235);
	
	/* private instance variables */
	private int margin, curvAmt, tileSize;
	private PuzzleState currentState;
		
	/* this boolean will be used to prevent the user from interrupting the optimal 
	 * solver, which would cause problems. */
	boolean isSolving = false;

	/* Constructor */
	public MainPanel(int gridSize, int margin, int bottomPanelSize, int curvAmt, 
			PuzzleState currentState) {
		
		this.margin = margin;
		this.curvAmt = curvAmt;
		this.currentState = currentState;
		
		int tempSize = gridSize - (2 * margin);
		tileSize = tempSize / 3;
		
		setLayout(new BorderLayout());
		
		GridPanel grid = new GridPanel();
		add(grid, BorderLayout.CENTER);
		BottomPanel bottomPanel = new BottomPanel(grid);
		bottomPanel.setPreferredSize(new Dimension(gridSize, bottomPanelSize));
		add(bottomPanel, BorderLayout.SOUTH);
		
		repaintMainPanel();
	}

	/* Repaints the entire main panel.  This is a wrapper around the repaint() method
	 * for the MainPanel.
	 */
	public void repaintMainPanel() {
		repaint();
	}

	/* Private class that handles the portion of the GUI that displays the current
	 * state of the puzzle, and contains listeners to allow the user to attempt
	 * to solve the puzzle manually.
	 */
	private class GridPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public GridPanel() {
			this.setBackground(GRID_BACKGROUND_COLOR);

			this.setFont(GRID_FONT);
			this.setFocusable(true);
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					requestFocusInWindow();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}

			});
			this.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub

				}

				/* Handles adjusting the grid if one of the arrow keys is pressed.
				 * If an arrow key is pressed that does not correspond to a valid
				 * move (such as pressing the right arrow key when the blank is on
				 * the left side of the board) then nothing happens.
				 */
				@Override
				public void keyPressed(KeyEvent e) {
					
					if (isSolving) {
						return;
					}
					
					if (!e.isActionKey()) {
						return;
					} 
					
					int keyCode = e.getKeyCode();
					switch(keyCode) {
					case 38:
						currentState = currentState.getNewStateIncludeInverse(BoardOperations.MOVE_BLANK_DOWN);
						break;
					case 40:
						currentState = currentState.getNewStateIncludeInverse(BoardOperations.MOVE_BLANK_UP);
						break;
					case 39:
						currentState = currentState.getNewStateIncludeInverse(BoardOperations.MOVE_BLANK_LEFT);
						break;
					case 37:
						currentState = currentState.getNewStateIncludeInverse(BoardOperations.MOVE_BLANK_RIGHT);
					}
					
					currentState.setGValue(0);
					currentState.setParentOperation(-1);
					currentState.setParent(null);
					repaintMainPanel();
					
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}

			});
		}

		/* This method displays each step of an optimal solution to the current
		 * state of the puzzle.  Each step is displayed with delays in between.
		 */
		private void displaySolution() {
			new Thread() {
				public void run() {
					
					isSolving = true;
					
					List<PuzzleState> solution = PuzzleSolver.solve(currentState);
					int num = 0; // used to skip the scrambled state
					
					for (PuzzleState step : solution) {
						if (num++ == 0) {
							continue;
						}
						currentState = step;
						if (currentState.equals(PuzzleState.GOAL_STATE)) {
							isSolving = false;
						}
						Thread paintingThread = new Thread() {
							public void run() {
								setPriority(MAX_PRIORITY);
								repaint();
							}
						};
						paintingThread.start();
						while(true) {

							try {
								paintingThread.join();
								break;
							} catch (InterruptedException e) {

							}
						}
						try {
							Thread.sleep(680); // delay between steps
						} catch (InterruptedException e) {

						}
					}
				}
			}.start();
			
			isSolving = false;
			
		}

		/* Draws the tiles on the grid.  They are each shown as rounded rectangles
		 * with the number on the tile drawn in the center.
		 */
		private void drawGrid(Graphics2D g) {
			for (int i = 0; i < 9; i++) {
				int r = i / 3;
				int c = i % 3;

				int x = margin + (c * tileSize);
				int y = margin + (r * tileSize);

				int number = currentState.getBoardState().get(i);
				if (number == 0) {
					continue;
				}
				g.setColor(TILE_COLOR);
				g.fillRoundRect(x, y, tileSize, tileSize, curvAmt, curvAmt);
				g.setColor(TILE_BORDER_COLOR);
				g.drawRoundRect(x, y, tileSize, tileSize, curvAmt, curvAmt);

				g.setColor(NUMBER_COLOR);
				drawCenteredString(g, String.valueOf(number), x , y);
			}
		}

		/* Draws the given String in the given Graphics2D context, such that
		 * it is centered within the tile whose top left corner is given by 
		 * the parameters x and y.
		 */
		private void drawCenteredString(Graphics2D g, String s, int x, int y) {
			
			FontMetrics fm = g.getFontMetrics();
			int asc = fm.getAscent();
			int desc = fm.getDescent();
			g.drawString(s,  x + (tileSize - fm.stringWidth(s)) / 2, 
					y + (asc + (tileSize - (asc + desc)) / 2));
			
		}

		
		/* Renders the current state of the puzzle on the GUI. */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2D = (Graphics2D) g;
			
			/* Avoids pixelations around the curved parts of the numbers on each
			 * tile. */
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			drawGrid(g2D);
		}
	}

	/* private class that handles the bottom panel of the GUI which contains the two
	 * buttons available to the user.  These buttons allow the user to scramble
	 * the puzzle, or to view an optimal solution of the puzzle.  
	 */
	private class BottomPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private GridPanel grid;
		
		/* Constructor for the BottomPanel.  Sets the LayoutManager to GridLayout and
		 * then adds the ScrambleButton and SolveButton on the left and right hand sides
		 * of the BottomPanel, respectively.
		 */
		private BottomPanel(GridPanel grid) {
			this.grid = grid;
			setLayout(new GridLayout());
			add(new ScrambleButton());
			add(new SolveButton());
		}

		/* Private class that will handle the lower left portion of the GUI which is a 
		 * button that, if pressed, will generate a new scrambled 8-Puzzle and display
		 * that for the user.  
		 */
		private class ScrambleButton extends JButton {

			private static final long serialVersionUID = 1L;
			private boolean mousePressed = false;
			private boolean mousePressedThenExited = false;

			public ScrambleButton() {
				super("Scramble Puzzle");
				this.setFocusPainted(false);
				this.setFocusable(false);
				this.setFont(BOTTOM_PANEL_FONT);
				this.addMouseListener(new MouseListener() {

					/* The boolean variables mousePressed and mousePressedThenExited are
					 * being used to make the button act like buttons usually do.  That is,
					 * if the button is clicked directly, it will scramble the puzzle.  
					 * If the mouse is pressed, then moved slightly, but released 
					 * while it is still on the button, it will also scramble the puzzle.
					 * If the mouse is pressed, moved outside of the region of the button,
					 * then moved back inside the region of the button and released, it will
					 * scramble the puzzle.  However, if the mouse is pressed, moved outside
					 * of the region of the button, and then released, it will not do anything.
					 */
					
					@Override
					public void mouseClicked(MouseEvent e) {
						/* Using mousePressed() and mouseReleased() instead so that
						 * it still works if the mouse moves slightly in between 
						 * pressing and releasing, in which case it's technically not a 
						 * "mouse click"
						 */
					}

					@Override
					public void mousePressed(MouseEvent e) {
						mousePressed = true;

					}

					@Override
					public void mouseReleased(MouseEvent e) {
						if (mousePressed) {
							if (isSolving) {
								return;
							} else {
								currentState = new PuzzleState();
								repaintMainPanel();
								mousePressed = false;
							}
						}
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						if (mousePressedThenExited) {
							mousePressedThenExited = false;
							mousePressed = true;
						}

					}

					@Override
					public void mouseExited(MouseEvent e) {
						if (mousePressed) {
							mousePressedThenExited = true;
							mousePressed = false;
						}
					}
				});
			}
		}

		/* Private class that handles the button on the lower right part of the GUI,
		 * which, if pressed, will display, at a moderate speed, an optimal solution
		 * to the scrambled puzzle.
		 */
		private class SolveButton extends JButton {

			private static final long serialVersionUID = 1L;
			
			private boolean mousePressed = false;
			private boolean mousePressedThenExited = false;

			public SolveButton() {
				super("Solve Puzzle");
				this.setFocusPainted(false);
				this.setFocusable(false);
				this.setFont(BOTTOM_PANEL_FONT);
				addMouseListener(new MouseListener() {
					
					/* The boolean variables mousePressed and mousePressedThenExited are
					 * being used to make the button act like buttons usually do.  That is,
					 * if the button is clicked directly, it will solve the puzzle.  
					 * If the mouse is pressed, then moved slightly, but released 
					 * while it is still on the button, it will also solve the puzzle.
					 * If the mouse is pressed, moved outside of the region of the button,
					 * then moved back inside the region of the button and released, it will
					 * solve the puzzle.  However, if the mouse is pressed, moved outside
					 * of the region of the button, and then released, it will not do anything.
					 */

					@Override
					public void mouseClicked(MouseEvent e) {
						/* Using mousePressed() and mouseReleased() instead so that
						 * it still works if the mouse moves slightly in between 
						 * pressing and releasing, in which case it's technically not a 
						 * "mouse click"
						 */
					}

					@Override
					public void mousePressed(MouseEvent e) {
						mousePressed = true;

					}

					@Override
					public void mouseReleased(MouseEvent e) {
						if (mousePressed && !isSolving) {
							grid.displaySolution();
						}

					}

					@Override
					public void mouseEntered(MouseEvent e) {
						if (mousePressedThenExited) {
							mousePressedThenExited = false;
							mousePressed = true;
						}

					}

					@Override
					public void mouseExited(MouseEvent e) {
						if (mousePressed) {
							mousePressedThenExited = true;
							mousePressed = false;
						}
					}

				});
			}
		}

	}

}
