package de.freese.sonstiges.dnd.color;

/*
 * DragColorDemo.java is a 1.4 example that requires the following file: ColorTransferHandler.java
 */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Example code that shows any JComponent can be customized to allow dropping of a dnd.color.
 */
public class DragColorDemo extends JPanel implements ActionListener
{
    /**
     *
     */
    private static final long serialVersionUID = 7463976633344385765L;

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("DragColorDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and setup the content pane.
        JComponent newContentPane = new DragColorDemo();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(DragColorDemo::createAndShowGUI);
    }

    /**
     *
     */
    private final ColorTransferHandler colorHandler;

    /**
     *
     */
    private final JCheckBox toggleForeground;

    /**
     * Creates a new DragColorDemo object.
     */
    public DragColorDemo()
    {
        super(new BorderLayout());

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JColorChooser chooser = new JColorChooser();
        chooser.setDragEnabled(true);
        add(chooser, BorderLayout.PAGE_START);

        // Create the dnd.color transfer handler.
        this.colorHandler = new ColorTransferHandler();

        // Create a matrix of 9 buttons.
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3));

        for (int i = 0; i < 9; i++)
        {
            JButton tmp = new JButton("Button " + i);
            tmp.setTransferHandler(this.colorHandler);
            buttonPanel.add(tmp);
        }

        add(buttonPanel, BorderLayout.CENTER);

        // Create a check box.
        this.toggleForeground = new JCheckBox("Change the foreground dnd.color.");
        this.toggleForeground.setSelected(true);
        this.toggleForeground.addActionListener(this);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textPanel.add(this.toggleForeground, BorderLayout.PAGE_START);

        // Create a label.
        JLabel label = new JLabel("Change the dnd.color of any button or this label by dropping a dnd.color.");
        label.setTransferHandler(this.colorHandler);
        label.setOpaque(true); // So the background dnd.color can be changed.
        textPanel.add(label, BorderLayout.PAGE_END);
        add(textPanel, BorderLayout.PAGE_END);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        this.colorHandler.setChangesForegroundColor(this.toggleForeground.isSelected());
    }
}
