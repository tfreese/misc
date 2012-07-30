package facade;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Thomas Freese
 */
public class FacadeGui implements ActionListener, ItemListener
{
	/**
	 * @author Thomas Freese
	 */
	private class WindowCloseManager extends WindowAdapter
	{
		/**
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosing(final WindowEvent evt)
		{
			exitApplication();
		}
	}

	/**
     * 
     */
	private static final String GUI_TITLE = "title";

	/**
     * 
     */
	private static final String EXIT_CAPTION = "exit";

	/**
     * 
     */
	private static final String COUNTRY_LABEL = "country";

	/**
     * 
     */
	private static final String CURRENCY_LABEL = "currency";

	/**
     * 
     */
	private static final String PHONE_LABEL = "phone";

	/**
     * 
     */
	private JPanel controlPanel = null;

	/**
     * 
     */
	private JPanel displayPanel = null;

	/**
     * 
     */
	private JComboBox<Nation> countryChooser = null;

	/**
     * 
     */
	private JLabel countryLabel = null;

	/**
     * 
     */
	private JLabel currencyLabel = null;

	/**
     * 
     */
	private JLabel phoneLabel = null;

	/**
     * 
     */
	private JTextField currencyTextField = null;

	/**
     * 
     */
	private JTextField phoneTextField = null;

	/**
     * 
     */
	private JButton exit = null;

	/**
     * 
     */
	private JFrame mainFrame = null;

	/**
     * 
     */
	private InternationalizationWizard nationalityFacade = null;

	/**
	 * Creates a new {@link FacadeGui} object.
	 * 
	 * @param wizard {@link InternationalizationWizard}
	 */
	public FacadeGui(final InternationalizationWizard wizard)
	{
		super();

		this.nationalityFacade = wizard;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent evt)
	{
		Object originator = evt.getSource();

		if (originator == this.exit)
		{
			exitApplication();
		}
	}

	/**
     * 
     */
	public void createGui()
	{
		this.mainFrame = new JFrame(this.nationalityFacade.getProperty(GUI_TITLE));
		Container content = this.mainFrame.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		this.displayPanel = new JPanel();
		this.displayPanel.setLayout(new GridLayout(3, 2));

		this.countryLabel = new JLabel(this.nationalityFacade.getProperty(COUNTRY_LABEL));
		this.countryChooser = new JComboBox<>(this.nationalityFacade.getNations());
		this.currencyLabel = new JLabel(this.nationalityFacade.getProperty(CURRENCY_LABEL));
		this.currencyTextField = new JTextField();
		this.phoneLabel = new JLabel(this.nationalityFacade.getProperty(PHONE_LABEL));
		this.phoneTextField = new JTextField();

		this.currencyTextField.setEditable(false);
		this.phoneTextField.setEditable(false);

		this.displayPanel.add(this.countryLabel);
		this.displayPanel.add(this.countryChooser);
		this.displayPanel.add(this.currencyLabel);
		this.displayPanel.add(this.currencyTextField);
		this.displayPanel.add(this.phoneLabel);
		this.displayPanel.add(this.phoneTextField);
		content.add(this.displayPanel);

		this.controlPanel = new JPanel();
		this.exit = new JButton(this.nationalityFacade.getProperty(EXIT_CAPTION));
		this.controlPanel.add(this.exit);
		content.add(this.controlPanel);

		this.exit.addActionListener(this);
		this.countryChooser.addItemListener(this);

		this.mainFrame.addWindowListener(new WindowCloseManager());
		this.mainFrame.pack();
		this.mainFrame.setVisible(true);
	}

	/**
     * 
     */
	private void exitApplication()
	{
		System.exit(0);
	}

	/**
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(final ItemEvent evt)
	{
		Object originator = evt.getSource();

		if (originator == this.countryChooser)
		{
			updateGui();
		}
	}

	/**
	 * @param nation {@link Nation}
	 */
	public void setNation(final Nation nation)
	{
		this.countryChooser.setSelectedItem(nation);
	}

	/**
     * 
     */
	private void updateGui()
	{
		this.nationalityFacade.setNation(this.countryChooser.getSelectedItem().toString());
		this.mainFrame.setTitle(this.nationalityFacade.getProperty(GUI_TITLE));
		this.countryLabel.setText(this.nationalityFacade.getProperty(COUNTRY_LABEL));
		this.currencyLabel.setText(this.nationalityFacade.getProperty(CURRENCY_LABEL));
		this.phoneLabel.setText(this.nationalityFacade.getProperty(PHONE_LABEL));
		this.exit.setText(this.nationalityFacade.getProperty(EXIT_CAPTION));

		this.currencyTextField.setText(this.nationalityFacade.getCurrencySymbol() + " "
				+ this.nationalityFacade.getNumberFormat().format(5280.50));
		this.phoneTextField.setText(this.nationalityFacade.getPhonePrefix());

		this.mainFrame.invalidate();
		this.countryLabel.invalidate();
		this.currencyLabel.invalidate();
		this.phoneLabel.invalidate();
		this.exit.invalidate();
		this.mainFrame.validate();
	}
}
