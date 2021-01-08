package gui;
import javax.swing.*;

/* Start the GUI by running the main method in this class. */
public class Driver {

	public static void main(String[] args) {
		/* Makes a request to the EDT (Event Dispatching Thread) and then
		 * the main thread dies, delegating the rest of the work to the EDT.
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				GuiStarter.initializeAndDisplayGUI(530, 37);
			}
		});
	}

}
