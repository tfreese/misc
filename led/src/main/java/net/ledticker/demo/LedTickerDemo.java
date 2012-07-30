package net.ledticker.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.led.demo.elements.ticker.StockTickerElement;
import net.led.demo.provider.Stock;
import net.led.demo.provider.UpdateListener;
import net.led.demo.provider.YahooProvider;
import net.led.demo.util.ColorSelectorListener;
import net.led.demo.util.ColorSelectorPanel;
import net.ledticker.LedTicker;
import net.ledticker.LedTickerFactory;

public class LedTickerDemo implements ActionListener, ChangeListener, UpdateListener,
		ColorSelectorListener
{

	private JTextField stocksField = new JTextField(6);

	private JTextField dotWidth = new JTextField(6);

	private JTextField dotHeight = new JTextField(6);

	private JTextField hGap = new JTextField(6);

	private JTextField vGap = new JTextField(6);

	private JSlider slider = new JSlider(1, 10);

	private Color symbolColor = Color.YELLOW;

	private Color stockUpColor = Color.GREEN;

	private Color stockNeutralColor = Color.YELLOW;

	private Color stockDownColor = Color.RED;

	private LedTicker ledTicker;

	private Map elements = new HashMap();

	private JFrame tickerFrame;

	private YahooProvider yahooProvider;

	public LedTickerDemo()
	{
		createLedTickerComponent();
		createGUI();

		// Create the YahooProvider.
		this.yahooProvider = new YahooProvider();

		String[] initialSymbols =
		{
				"MSFT", "INTC", "DELL", "GOOG", "ORCL", "AMZN", "GE", "JNJ", "PG", "WMT", "HD"
		};
		for (int i = 0; i < initialSymbols.length; i++)
		{
			addSymbol(initialSymbols[i]);
		}

		this.yahooProvider.addUpdateListener(this);
		this.yahooProvider.start();

		// Start the ticker animation
		this.ledTicker.startAnimation();
	}

	/**
	 * Creates the LedTicker.
	 */
	private void createLedTickerComponent()
	{
		// STEP1 : create the component
		this.ledTicker = LedTickerFactory.createLedTicker();
		this.ledTicker.setElementGap(4);
		this.ledTicker.setTokenGap(2);
		this.ledTicker.setDotSize(2, 2);
		this.ledTicker.setDotGaps(1, 1);

	}

	private void centerFrame(final Window frame)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}

	private JPanel createScrollControlsPanel()
	{
		TitledBorder scrollControlsBorder =
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
		scrollControlsBorder.setTitle("Ticker Controls");

		JButton pause = new JButton("Pause");
		pause.setActionCommand("pause");
		pause.addActionListener(this);

		JButton start = new JButton("Start");
		start.setActionCommand("start");
		start.addActionListener(this);

		JButton stop = new JButton("Stop");
		stop.setActionCommand("stop");
		stop.addActionListener(this);

		this.slider.addChangeListener(this);
		this.slider.setPaintTicks(true);
		this.slider.setPaintLabels(true);
		this.slider.setMajorTickSpacing(3);
		this.slider.setMinorTickSpacing(1);
		Hashtable labels = new Hashtable();
		for (int i = this.slider.getMinimum(); i <= this.slider.getMaximum(); i++)
		{
			labels.put(new Integer(i), new JLabel(String.valueOf(i)));
		}
		this.slider.setLabelTable(labels);

		JPanel scrollControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		scrollControlsPanel.setBorder(scrollControlsBorder);
		scrollControlsPanel.add(start);
		scrollControlsPanel.add(stop);
		scrollControlsPanel.add(pause);
		scrollControlsPanel.add(this.slider);

		return scrollControlsPanel;
	}

	private JPanel createStocksControlsPanel()
	{
		TitledBorder stocksControlsBorder =
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
		stocksControlsBorder.setTitle("Stocks Controls");

		JButton addStock = new JButton("Add Stock");
		addStock.setActionCommand("addStock");
		addStock.addActionListener(this);

		this.stocksField.setActionCommand("addStock");
		this.stocksField.addActionListener(this);

		JButton removeStock = new JButton("Remove Stock");
		removeStock.setActionCommand("removeStock");
		removeStock.addActionListener(this);

		JButton removeAllStocks = new JButton("Remove All");
		removeAllStocks.setActionCommand("removeAll");
		removeAllStocks.addActionListener(this);

		JPanel stocksControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		stocksControlsPanel.setBorder(stocksControlsBorder);
		stocksControlsPanel.add(addStock);
		stocksControlsPanel.add(this.stocksField);
		stocksControlsPanel.add(removeStock);
		stocksControlsPanel.add(removeAllStocks);

		return stocksControlsPanel;
	}

	private JPanel createDotsControlsPanel()
	{
		TitledBorder dotsControlsBorder =
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
		dotsControlsBorder.setTitle("Dots Controls");

		JLabel dotWidthLabel = new JLabel("Dot width");
		JLabel dotHeightLabel = new JLabel("Dot height");
		JButton setDotSize = new JButton("Set Dot size");
		setDotSize.setActionCommand("setDotSize");
		setDotSize.addActionListener(this);

		JPanel dotsControlsPanel = new JPanel(new GridBagLayout());
		dotsControlsPanel.setBorder(dotsControlsBorder);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 0, 5, 5);

		dotsControlsPanel.add(this.dotWidth, gbc);

		gbc.gridx++;
		dotsControlsPanel.add(dotWidthLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		dotsControlsPanel.add(this.dotHeight, gbc);

		gbc.gridx++;
		dotsControlsPanel.add(dotHeightLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		dotsControlsPanel.add(setDotSize, gbc);

		return dotsControlsPanel;
	}

	private JPanel createGapsControlsPanel()
	{
		TitledBorder gapsControlsBorder =
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
		gapsControlsBorder.setTitle("Gaps Controls");

		JLabel hGapLabel = new JLabel("Horizontal gap");
		JLabel vGapLabel = new JLabel("Vertical gap");
		JButton setDotGap = new JButton("Set Dot Gap");
		setDotGap.setActionCommand("setDotGap");
		setDotGap.addActionListener(this);

		JPanel gapsControlsPanel = new JPanel(new GridBagLayout());
		gapsControlsPanel.setBorder(gapsControlsBorder);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 0, 5, 5);

		gapsControlsPanel.add(this.hGap, gbc);

		gbc.gridx++;
		gapsControlsPanel.add(hGapLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gapsControlsPanel.add(this.vGap, gbc);

		gbc.gridx++;
		gapsControlsPanel.add(vGapLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gapsControlsPanel.add(setDotGap, gbc);

		return gapsControlsPanel;
	}

	private JPanel createTickerPaneColors()
	{
		TitledBorder stocksColorsBorder =
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
		stocksColorsBorder.setTitle("Ticker Colors");

		Color gridColor = new Color(0x111111);
		Color bgColor = new Color(0x333333);

		ColorSelectorPanel backgroundSelectorPanel =
				new ColorSelectorPanel("Select background color", bgColor, "bgColor", this);
		ColorSelectorPanel gridSelectorPanel =
				new ColorSelectorPanel("Select turned-off led color", gridColor, "gridColor", this);
		ColorSelectorPanel symbolSelectorPanel =
				new ColorSelectorPanel("Select symbol color", this.symbolColor, "symbolColor", this);

		JPanel stocksColorsPanel = new JPanel(new GridLayout(3, 1));
		stocksColorsPanel.setBorder(stocksColorsBorder);
		stocksColorsPanel.add(backgroundSelectorPanel);
		stocksColorsPanel.add(gridSelectorPanel);
		stocksColorsPanel.add(symbolSelectorPanel);

		this.ledTicker.setBackgroundColor(bgColor);
		this.ledTicker.setDotOffColor(gridColor);

		return stocksColorsPanel;
	}

	private JPanel createTrendColorsPanel()
	{
		TitledBorder trendColorsBorder =
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
		trendColorsBorder.setTitle("Trend Colors");

		ColorSelectorPanel stockUpSelectorPanel =
				new ColorSelectorPanel("Select Stock Up Color", this.stockUpColor, "upColor", this);
		ColorSelectorPanel stockNeutralSelectorPanel =
				new ColorSelectorPanel("Select Stock Neutral Color", this.stockNeutralColor,
						"neutralColor", this);
		ColorSelectorPanel stockDownSelectorPanel =
				new ColorSelectorPanel("Select Stock Down Color", this.stockDownColor, "downColor",
						this);

		JPanel trendColorsPanel = new JPanel(new GridLayout(3, 1));
		trendColorsPanel.setBorder(trendColorsBorder);
		trendColorsPanel.add(stockUpSelectorPanel);
		trendColorsPanel.add(stockNeutralSelectorPanel);
		trendColorsPanel.add(stockDownSelectorPanel);

		return trendColorsPanel;
	}

	private void createGUI()
	{
		JPanel generalPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 0, 5, 5);
		gbc.gridwidth = 2;
		generalPanel.add(createScrollControlsPanel(), gbc);

		gbc.gridy = 1;
		gbc.gridheight = 2;
		generalPanel.add(createStocksControlsPanel(), gbc);

		gbc.gridx += 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.anchor = GridBagConstraints.NORTH;
		generalPanel.add(createTickerPaneColors(), gbc);
		gbc.anchor = GridBagConstraints.CENTER;

		gbc.weightx = 0.5;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridheight = 1;
		generalPanel.add(createDotsControlsPanel(), gbc);

		gbc.gridx++;
		generalPanel.add(createGapsControlsPanel(), gbc);

		gbc.gridx++;
		gbc.gridy = 2;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridheight = 2;
		gbc.anchor = GridBagConstraints.NORTH;
		generalPanel.add(createTrendColorsPanel(), gbc);

		// ADD TICKER COMPONENT
		this.tickerFrame = new JFrame("Led Ticker Component v2.0");
		this.tickerFrame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints tickerFrameConstarints = new GridBagConstraints();

		tickerFrameConstarints.gridx = 0;
		tickerFrameConstarints.gridy = 0;
		tickerFrameConstarints.weightx = 1;
		tickerFrameConstarints.weighty = 0;
		tickerFrameConstarints.insets = new Insets(5, 5, 0, 5);
		tickerFrameConstarints.fill = GridBagConstraints.HORIZONTAL;
		this.tickerFrame.getContentPane().add(this.ledTicker.getTickerComponent(),
				tickerFrameConstarints);

		// ADD THE MAIN PANEL OF THIS DEMO
		tickerFrameConstarints.fill = GridBagConstraints.NONE;
		tickerFrameConstarints.anchor = GridBagConstraints.NORTH;
		tickerFrameConstarints.gridy = 1;
		tickerFrameConstarints.gridx = 0;
		tickerFrameConstarints.weightx = 0;
		tickerFrameConstarints.weighty = 1;
		this.tickerFrame.getContentPane().add(generalPanel, tickerFrameConstarints);

		this.tickerFrame.pack();
		centerFrame(this.tickerFrame);
		this.tickerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.tickerFrame.setVisible(true);
	}

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

	private void addSymbol(final String symbol)
	{
		StockTickerElement ste = new StockTickerElement(symbol);
		ste.setStockUpColor(this.stockUpColor);
		ste.setStockNeutralColor(this.stockNeutralColor);
		ste.setStockDownColor(this.stockDownColor);
		ste.setSymbolColor(this.symbolColor);
		this.elements.put(symbol, ste);
		this.yahooProvider.addSymbol(symbol);
		this.ledTicker.addElement(ste);
	}

	public void actionPerformed(final ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals("addStock"))
		{
			String name = this.stocksField.getText().toUpperCase();
			this.stocksField.setText(name);
			addSymbol(name);
		}
		else if (command.equals("removeStock"))
		{
			String name = this.stocksField.getText().toUpperCase();
			this.stocksField.setText(name);
			StockTickerElement ste = (StockTickerElement) this.elements.get(name);
			if (ste != null)
			{
				this.yahooProvider.removeSymbol(name);
				this.ledTicker.removeElement(ste);
				this.elements.remove(name);
			}
		}
		else if (command.equals("removeAll"))
		{
			this.yahooProvider.removeAllElements();
			this.elements.clear();
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e1)
			{
				// Ignore
			}
			this.ledTicker.removeAll();
		}
		else if (command.equals("pause"))
		{
			this.ledTicker.pauseAnimation();
		}
		else if (command.equals("start"))
		{
			this.ledTicker.startAnimation();
		}
		else if (command.equals("stop"))
		{
			this.ledTicker.stopAnimation();
		}
		else if (command.equals("setSpeed"))
		{
			this.ledTicker.setSpeed(this.slider.getValue());
		}
		else if (command.equals("setDotSize"))
		{
			int width, height;
			try
			{
				width = Integer.parseInt(this.dotWidth.getText(), 10);
			}
			catch (Exception ex)
			{
				width = 1;
			}
			try
			{
				height = Integer.parseInt(this.dotHeight.getText(), 10);
			}
			catch (Exception ex)
			{
				height = 1;
			}
			this.ledTicker.setDotSize(width, height);
			this.tickerFrame.pack();
			centerFrame(this.tickerFrame);
		}
		else if (command.equals("setDotGap"))
		{
			int hg, vg;
			try
			{
				hg = Integer.parseInt(this.hGap.getText(), 10);
			}
			catch (Exception ex)
			{
				hg = 1;
			}
			try
			{
				vg = Integer.parseInt(this.vGap.getText(), 10);
			}
			catch (Exception ex)
			{
				vg = 1;
			}
			this.ledTicker.setDotGaps(hg, vg);
			this.tickerFrame.pack();
			centerFrame(this.tickerFrame);
		}
	}

	public void setColor(final String id, final Color color)
	{
		if (id.equals("bgColor"))
		{
			this.ledTicker.setBackgroundColor(color);
		}
		else if (id.equals("gridColor"))
		{
			this.ledTicker.setDotOffColor(color);
		}
		else if (id.equals("symbolColor"))
		{
			this.symbolColor = color;
			for (Iterator it = this.elements.values().iterator(); it.hasNext();)
			{
				StockTickerElement ste = (StockTickerElement) it.next();
				ste.setSymbolColor(color);
			}
			this.ledTicker.updateAll();
		}
		else if (id.equals("upColor"))
		{
			this.stockUpColor = color;
			for (Iterator it = this.elements.values().iterator(); it.hasNext();)
			{
				StockTickerElement ste = (StockTickerElement) it.next();
				ste.setStockUpColor(color);
			}
			this.ledTicker.updateAll();
		}
		else if (id.equals("neutralColor"))
		{
			this.stockNeutralColor = color;
			for (Iterator it = this.elements.values().iterator(); it.hasNext();)
			{
				StockTickerElement ste = (StockTickerElement) it.next();
				ste.setStockNeutralColor(color);
			}
			this.ledTicker.updateAll();
		}
		else if (id.equals("downColor"))
		{
			this.stockDownColor = color;
			for (Iterator it = this.elements.values().iterator(); it.hasNext();)
			{
				StockTickerElement ste = (StockTickerElement) it.next();
				ste.setStockDownColor(color);
			}
			this.ledTicker.updateAll();
		}
	}

	public void stateChanged(final ChangeEvent e)
	{
		this.ledTicker.setSpeed(((JSlider) e.getSource()).getValue());
	}

	public static void main(final String[] args)
	{
		LedTickerDemo ltd = new LedTickerDemo();
	}
}