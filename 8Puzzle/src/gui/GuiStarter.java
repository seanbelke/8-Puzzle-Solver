package gui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import board.PuzzleState;

/* This class contains a single static method that is called by the driver.
 * It initializes the GUI for this project to display a solved 8-Puzzle.
 */
public class GuiStarter {
	
	/* This method is called by the driver.  It initializes the GUI to display
	 * a solved 8-Puzzle so that the user understands what the solved state should
	 * look like.  It also sets the Look and Feel of the GUI to be the system look
	 * and feel, since it generally looks more sleek than the cross-platform look
	 * and feel.  
	 */
	public static void initializeAndDisplayGUI(int gridSize, int bottomPanelSize) {
		/* set look and feel */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		/* create frame, set its size, and add a MainPanel */
		JFrame frame = new JFrame("8-Puzzle");
		frame.setSize(gridSize + 16, (gridSize + bottomPanelSize + 39));
		frame.setContentPane(new MainPanel(gridSize, 10, bottomPanelSize, 10, PuzzleState.GOAL_STATE));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
	}
}
