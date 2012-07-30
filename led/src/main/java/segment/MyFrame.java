package segment;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyFrame extends Frame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyFrame()
	{
		super("7-Segment-Anzeige");
		setBackground(Color.lightGray);
		setLayout(new FlowLayout());

		// Dialog-Button
		Button button = new Button("Dialog");
		button.addActionListener(this);
		add(button);
		// Ende-Button
		button = new Button("Ende");
		button.addActionListener(this);
		add(button);
		// Window-Ereignisse
		addWindowListener(new WindowClosingAdapter(true));
	}

	public static void main(final String[] args)
	{
		MyFrame wnd = new MyFrame();
		wnd.setSize(300, 200);
		wnd.setVisible(true);
	}

	public void actionPerformed(final ActionEvent event)
	{
		String cmd = event.getActionCommand();

		if (cmd.equals("Dialog"))
		{
			MyDialog dlg = new MyDialog(this);
			dlg.setVisible(true);
		}
		else if (cmd.equals("Ende"))
		{
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}
}
