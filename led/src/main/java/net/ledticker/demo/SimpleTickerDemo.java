package net.ledticker.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.led.demo.elements.ticker.StockTickerElement;
import net.led.demo.provider.Stock;
import net.led.demo.provider.UpdateListener;
import net.led.demo.provider.YahooProvider;
import net.led.demo.util.OptionsDialog;
import net.led.demo.util.OptionsListener;
import net.ledticker.LedTicker;
import net.ledticker.LedTickerFactory;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class SimpleTickerDemo implements ActionListener, OptionsListener, UpdateListener
{

	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		}
		catch (UnsupportedLookAndFeelException ex)
		{
			// Ignore
		}

		SimpleTickerDemo std = new SimpleTickerDemo();
	}

	private LedTicker ledTicker;

	private Map elements = new HashMap();

	private JFrame tickerFrame;

	private YahooProvider yahooProvider;

	private JPopupMenu menu;

	public SimpleTickerDemo()
	{
		createLedTickerComponent();
		createPopUpMenu();
		createGUI();

		// Create a YahooProvider.
		this.yahooProvider = new YahooProvider();

		for (Iterator it = this.elements.keySet().iterator(); it.hasNext();)
		{
			this.yahooProvider.addSymbol((String) it.next());
		}

		this.yahooProvider.addUpdateListener(this);
		this.yahooProvider.start();
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals("menuOptions"))
		{
			this.menu.setVisible(false);
			String[] symbols = new String[this.elements.size()];
			int i = 0;
			for (Iterator it = this.elements.keySet().iterator(); it.hasNext();)
			{
				symbols[i++] = (String) it.next();
			}
			OptionsDialog od = new OptionsDialog(this.tickerFrame, this, symbols);
		}
		else if (command.equals("moveUp"))
		{
			this.menu.setVisible(false);
			// Move the ticker to the top of the screen.
			this.tickerFrame.setLocation(0, 0);
		}
		else if (command.equals("moveDown"))
		{
			this.menu.setVisible(false);
			// Move the ticker 100 pixels above the bottom of the screen.
			// We added 100 pixels to avoid moving the ticker under the Windows taskbar.
			this.tickerFrame.setLocation(0,
					Toolkit.getDefaultToolkit().getScreenSize().height - 100);
		}
		else if (command.equals("exit"))
		{
			System.exit(0);
		}
	}

	/**
	 * @see net.ledticker.demo.OptionsListener#addSymbol(java.lang.String)
	 */
	@Override
	public void addSymbol(final String symbol)
	{
		StockTickerElement ste = new StockTickerElement(symbol);
		this.elements.put(symbol, ste);
		this.ledTicker.addElement(ste);
		this.yahooProvider.addSymbol(symbol);
	}

	private void createGUI()
	{
		// ADD TICKER COMPONENT
		this.tickerFrame = new JFrame("Led Ticker Component v2.0");

		this.tickerFrame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints tickerFrameConstarints = new GridBagConstraints();
		tickerFrameConstarints.gridx = 0;
		tickerFrameConstarints.gridy = 0;
		tickerFrameConstarints.weightx = 1;
		tickerFrameConstarints.weighty = 0;
		tickerFrameConstarints.insets = new Insets(0, 0, 0, 0);
		tickerFrameConstarints.fill = GridBagConstraints.HORIZONTAL;
		this.tickerFrame.getContentPane().add(this.ledTicker.getTickerComponent(),
				tickerFrameConstarints);

		this.tickerFrame.setUndecorated(true);
		this.tickerFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,
				this.tickerFrame.getPreferredSize().height);
		this.tickerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.tickerFrame.setVisible(true);
		this.ledTicker.startAnimation();
	}

	/**
	 * Creates a LedTicker based on an array of symbols.
	 */
	private void createLedTickerComponent()
	{
		// STEP1 : create the component
		this.ledTicker = LedTickerFactory.createLedTicker();
		this.ledTicker.setElementGap(12);
		this.ledTicker.setTokenGap(6);
		this.ledTicker.setDotSize(3, 3);
		this.ledTicker.setDotGaps(1, 1);
		String[] initialSymbols =
		{
				"MSFT", "INTC", "DELL", "GOOG", "ORCL", "AMZN", "GE", "JNJ", "PG", "WMT", "HD"
		};
		for (String initialSymbol : initialSymbols)
		{
			StockTickerElement ste = new StockTickerElement(initialSymbol);
			this.elements.put(ste.getSymbol(), ste);
			this.ledTicker.addElement(ste);
		}
	}

	/**
	 * Creates a popup menu which will be displayed when the user right clicks on the ticker.
	 */
	private void createPopUpMenu()
	{
		JMenuItem optionsMenuItem = new JMenuItem("Options");
		optionsMenuItem.setActionCommand("menuOptions");
		optionsMenuItem.addActionListener(this);

		JMenuItem moveUpMenuItem = new JMenuItem("Move to screen top");
		moveUpMenuItem.setActionCommand("moveUp");
		moveUpMenuItem.addActionListener(this);

		JMenuItem moveDownMenuItem = new JMenuItem("Move to screen bottom");
		moveDownMenuItem.setActionCommand("moveDown");
		moveDownMenuItem.addActionListener(this);

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setActionCommand("exit");
		exitMenuItem.addActionListener(this);

		this.menu = new JPopupMenu();

		this.menu.add(optionsMenuItem);
		this.menu.addSeparator();
		this.menu.add(moveUpMenuItem);
		this.menu.add(moveDownMenuItem);
		this.menu.addSeparator();
		this.menu.add(exitMenuItem);

		this.ledTicker.getTickerComponent().addMouseListener((new MouseAdapter()
		{
			@Override
			public void mousePressed(final MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					SimpleTickerDemo.this.menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					SimpleTickerDemo.this.menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}));
	}

	/**
	 * @see net.ledticker.demo.OptionsListener#removeSymbol(java.lang.String)
	 */
	@Override
	public void removeSymbol(final String symbol)
	{
		StockTickerElement ste = (StockTickerElement) this.elements.get(symbol);
		if (ste != null)
		{
			this.yahooProvider.removeSymbol(symbol);
			this.ledTicker.removeElement(ste);
			this.elements.remove(symbol);
		}
	}

	/**
	 * @see net.ledticker.demo.provider.UpdateListener#updateStock(demo.Stock)
	 */
	@Override
	public void update(final Object newValue)
	{
		Stock stock = (Stock) newValue;

		StockTickerElement ste = (StockTickerElement) this.elements.get(stock.getID());
		if (ste != null)
		{
			ste.setLast(stock.getLast());
			ste.setChangePercent(stock.getChangePercent());
			this.ledTicker.update(ste);
		}
	}
}