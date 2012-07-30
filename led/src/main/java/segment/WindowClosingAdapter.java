package segment;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowClosingAdapter extends WindowAdapter
{
	private boolean exitSystem;

	/**
	 * Erzeugt einen WindowClosingAdapter zum Schliessen des Fensters. Das Programm wird nicht
	 * beendet.
	 */
	public WindowClosingAdapter()
	{
		this(false);
	}

	/**
	 * Erzeugt einen WindowClosingAdapter zum Schliessen des Fensters. Ist exitSystem true, wird das
	 * komplette Programm beendet.
	 */
	public WindowClosingAdapter(final boolean exitSystem)
	{
		this.exitSystem = exitSystem;
	}

	public void windowClosing(final WindowEvent event)
	{
		event.getWindow().setVisible(false);
		event.getWindow().dispose();

		if (this.exitSystem)
		{
			System.exit(0);
		}
	}
}
