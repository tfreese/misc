/*
 * Copyright (c) 2002-2007 JGoodies Karsten Lentzsch. All Rights Reserved. Redistribution and use in
 * source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met: o Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. o Redistributions in binary form must reproduce
 * the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. o Neither the name of
 * JGoodies Karsten Lentzsch nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.binding.tutorial.basics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.BoundedRangeAdapter;
import com.jgoodies.binding.adapter.SingleListSelectionAdapter;
import com.jgoodies.binding.adapter.SpinnerAdapterFactory;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.tutorial.Album;
import com.jgoodies.binding.tutorial.TutorialUtils;
import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.collect.ArrayListModel;
import com.jgoodies.common.collect.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Demonstrates how to bind different value types to Swing components.
 * 
 * @author Karsten Lentzsch
 * @version $Revision: 1.18 $
 * @see com.jgoodies.binding.adapter.BasicComponentFactory
 * @see com.jgoodies.binding.adapter.Bindings
 */

public final class ComponentsExample
{

	private static final class ChooseColorAction extends AbstractAction
	{

		private static final class Closer extends WindowAdapter implements Serializable
		{
			@Override
			public void windowClosing(final WindowEvent e)
			{
				Window w = e.getWindow();
				w.setVisible(false);
			}
		}

		private static final class DisposeOnClose extends ComponentAdapter implements Serializable
		{
			@Override
			public void componentHidden(final ComponentEvent e)
			{
				Window w = (Window) e.getComponent();
				w.dispose();
			}
		}

		private static final class OKHandler implements ActionListener
		{
			private final Trigger trigger;

			private OKHandler(final Trigger trigger)
			{
				this.trigger = trigger;
			}

			/**
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				this.trigger.triggerCommit();
			}
		}

		private final ValueModel bufferedColorModel;

		private final Component parent;

		private final Trigger trigger;

		private ChooseColorAction(final Component parent, final ValueModel colorModel)
		{
			super("\u2026");
			this.parent = parent;
			this.trigger = new Trigger();
			this.bufferedColorModel = new BufferedValueModel(colorModel, this.trigger);
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			JColorChooser colorChooser =
					BasicComponentFactory.createColorChooser(this.bufferedColorModel);
			ActionListener okHandler = new OKHandler(this.trigger);
			JDialog dialog =
					JColorChooser.createDialog(this.parent, "Choose Color", true, colorChooser,
							okHandler, null);
			dialog.addWindowListener(new Closer());
			dialog.addComponentListener(new DisposeOnClose());

			dialog.setVisible(true); // blocks until user brings dialog down...
		}

	}

	private final class ColorUpdateHandler implements PropertyChangeListener
	{
		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(final PropertyChangeEvent evt)
		{
			updatePreviewPanel();
		}
	}

	public static final class ExampleBean extends Model
	{

		// Names of the Bound Bean Properties *************************************

		public static final Object CENTER = ColumnSpec.CENTER;

		static final Integer[] INTEGER_CHOICES =
		{
				new Integer(0), new Integer(1), new Integer(2)
		};

		// An object based enumeration (using an enum from the JGoodies Forms)
		public static final Object LEFT = ColumnSpec.LEFT;

		private static final int NO_DATE = -1;

		static final Object[] OBJECT_CHOICES =
		{
				LEFT, CENTER, ColumnSpec.RIGHT
		};

		public static final String PROPERTYNAME_BOOLEAN_VALUE = "booleanValue";

		public static final String PROPERTYNAME_COLOR = "color";

		public static final String PROPERTYNAME_DATE = "date";

		public static final String PROPERTYNAME_FLOAT_VALUE = "floatValue";

		public static final String PROPERTYNAME_INT_CHOICE = "intChoice";

		// Constants **************************************************************

		public static final String PROPERTYNAME_INT_LIMITED = "intLimited";

		public static final String PROPERTYNAME_INT_VALUE = "intValue";

		public static final String PROPERTYNAME_LIST_SELECTION = "listSelection";

		public static final String PROPERTYNAME_LONG_VALUE = "longValue";

		public static final String PROPERTYNAME_OBJECT_CHOICE = "objectChoice";

		public static final String PROPERTYNAME_TEXT = "text";

		public static final Object RIGHT = ColumnSpec.RIGHT;

		// Fields *****************************************************************

		private boolean booleanValue;

		private Color color;

		private long date;

		private float floatValue;

		private int intChoice;

		private int intLimited; // for a spinner

		private int intValue;

		private ObservableList<Album> listModel;

		private Object listSelection;

		private long longValue;

		private Object objectChoice;

		private String text;

		// Instance Creation ******************************************************

		public ExampleBean()
		{
			this.booleanValue = true;
			this.color = Color.WHITE;
			this.date = new GregorianCalendar(1967, 11, 5).getTime().getTime();
			this.floatValue = 0.5f;
			this.intChoice = INTEGER_CHOICES[0].intValue();
			this.intLimited = 15;
			this.intValue = 42;
			this.longValue = 42L;
			this.objectChoice = LEFT;
			this.text = "Text";
			this.listModel = new ArrayListModel<Album>();
			this.listModel.addAll(Album.ALBUMS);
			this.listSelection = this.listModel.get(0);
		}

		// Accessors **************************************************************

		public boolean getBooleanValue()
		{
			return this.booleanValue;
		}

		public Color getColor()
		{
			return this.color;
		}

		public Date getDate()
		{
			return this.date == NO_DATE ? null : new Date(this.date);
		}

		public float getFloatValue()
		{
			return this.floatValue;
		}

		public int getIntChoice()
		{
			return this.intChoice;
		}

		public int getIntLimited()
		{
			return this.intLimited;
		}

		public int getIntValue()
		{
			return this.intValue;
		}

		public ListModel getListModel()
		{
			return this.listModel;
		}

		public Object getListSelection()
		{
			return this.listSelection;
		}

		public long getLongValue()
		{
			return this.longValue;
		}

		public Object getObjectChoice()
		{
			return this.objectChoice;
		}

		public String getText()
		{
			return this.text;
		}

		public void setBooleanValue(final boolean newBooleanValue)
		{
			boolean oldBooleanValue = getBooleanValue();
			this.booleanValue = newBooleanValue;
			firePropertyChange(PROPERTYNAME_BOOLEAN_VALUE, oldBooleanValue, newBooleanValue);
		}

		public void setColor(final Color newColor)
		{
			Color oldColor = getColor();
			this.color = newColor;
			firePropertyChange(PROPERTYNAME_COLOR, oldColor, newColor);
		}

		public void setDate(final Date newDate)
		{
			Date oldDate = getDate();
			this.date = newDate == null ? NO_DATE : newDate.getTime();
			firePropertyChange(PROPERTYNAME_DATE, oldDate, newDate);
		}

		public void setFloatValue(final float newFloatValue)
		{
			float oldFloatValue = getFloatValue();
			this.floatValue = newFloatValue;
			firePropertyChange(PROPERTYNAME_FLOAT_VALUE, oldFloatValue, newFloatValue);
		}

		public void setIntChoice(final int newIntChoice)
		{
			int oldIntChoice = getIntChoice();
			this.intChoice = newIntChoice;
			firePropertyChange(PROPERTYNAME_INT_CHOICE, oldIntChoice, newIntChoice);
		}

		public void setIntLimited(final int newIntLimited)
		{
			int oldIntLimited = getIntLimited();
			this.intLimited = newIntLimited;
			firePropertyChange(PROPERTYNAME_INT_LIMITED, oldIntLimited, newIntLimited);
		}

		public void setIntValue(final int newIntValue)
		{
			int oldIntValue = getIntValue();
			this.intValue = newIntValue;
			firePropertyChange(PROPERTYNAME_INT_VALUE, oldIntValue, newIntValue);
		}

		public void setListSelection(final Object newListSelection)
		{
			Object oldListSelection = getListSelection();
			this.listSelection = newListSelection;
			firePropertyChange(PROPERTYNAME_LIST_SELECTION, oldListSelection, newListSelection);
		}

		public void setLongValue(final long newLongValue)
		{
			long oldLongValue = getLongValue();
			this.longValue = newLongValue;
			firePropertyChange(PROPERTYNAME_LONG_VALUE, oldLongValue, newLongValue);
		}

		public void setObjectChoice(final Object newObjectChoice)
		{
			Object oldObjectChoice = getObjectChoice();
			this.objectChoice = newObjectChoice;
			firePropertyChange(PROPERTYNAME_OBJECT_CHOICE, oldObjectChoice, newObjectChoice);
		}

		public void setText(final String newText)
		{
			String oldText = getText();
			this.text = newText;
			firePropertyChange(PROPERTYNAME_TEXT, oldText, newText);
		}

	}

	// A custom PresentationModel that provides a SelectionInList
	// for the bean's ListModel and the bean's list selection.
	private static final class ExamplePresentationModel extends PresentationModel<ExampleBean>
	{

		/**
		 * Holds the bean's list model plus a selection.
		 */
		private final SelectionInList<ExampleBean> selectionInList;

		// Instance Creation -----------------------------------------

		private ExamplePresentationModel(final ExampleBean exampleBean)
		{
			super(exampleBean);
			this.selectionInList =
					new SelectionInList<ExampleBean>(exampleBean.getListModel(),
							getModel(ExampleBean.PROPERTYNAME_LIST_SELECTION));
		}

		// Custom Models ---------------------------------------------

		public SelectionInList<ExampleBean> getSelectionInList()
		{
			return this.selectionInList;
		}

	}

	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		}
		catch (Exception e)
		{
			// Likely PlasticXP is not in the class path; ignore.
		}
		JFrame frame = new JFrame();
		frame.setTitle("Binding Tutorial :: Components");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		ComponentsExample example = new ComponentsExample();
		JComponent panel = example.buildPanel();
		frame.getContentPane().add(panel);
		frame.pack();
		TutorialUtils.locateOnOpticalScreenCenter(frame);
		frame.setVisible(true);
	}

	private JComboBox alignmentIntCombo;

	private JComboBox alignmentObjectCombo;

	private JRadioButton centerIntRadio;

	private JRadioButton centerObjectRadio;

	// Misc
	private JCheckBox checkBox;

	private JPanel colorPreview;

	// Lists
	private JComboBox comboBox;

	// Formatted Input
	private JFormattedTextField dateField;

	private JLabel floatLabel;

	private JFormattedTextField integerField;

	// Choice
	private JRadioButton leftIntRadio;

	private JRadioButton leftObjectRadio;

	private JList list;

	private JFormattedTextField longField;

	private JPasswordField passwordField;

	// Holds an ExampleBean and vends ValueModels that adapt its properties.
	private final ExamplePresentationModel presentationModel;

	private JRadioButton rightIntRadio;

	private JRadioButton rightObjectRadio;

	private JSlider slider;

	// Launching **************************************************************

	private JSpinner spinner;

	// Instance Creation ******************************************************

	private JTable table;

	// Component Creation and Initialization **********************************

	private JTextArea textArea;

	// Text Components
	private JTextField textField;

	private JLabel textLabel;

	// Building ***************************************************************

	/**
	 * Constructs the 'Components' example on an instance of ExampleBean.
	 */
	public ComponentsExample()
	{
		this.presentationModel = new ExamplePresentationModel(new ExampleBean());
	}

	private JPanel buildChoicesPanel()
	{
		FormLayout layout =
				new FormLayout("right:max(50dlu;pref), 3dlu, p, 6dlu, p, 6dlu, p, 0:grow",
						"p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, p, 3dlu, p");

		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Integer Choice", cc.xyw(1, 1, 8));
		builder.addLabel("JRadioButton", cc.xy(1, 3));
		builder.add(this.leftIntRadio, cc.xy(3, 3));
		builder.add(this.centerIntRadio, cc.xy(5, 3));
		builder.add(this.rightIntRadio, cc.xy(7, 3));
		builder.addLabel("JComboBox", cc.xy(1, 5));
		builder.add(this.alignmentIntCombo, cc.xyw(3, 5, 3));

		builder.addSeparator("Object Choice", cc.xyw(1, 7, 8));
		builder.addLabel("JRadioButton", cc.xy(1, 9));
		builder.add(this.leftObjectRadio, cc.xy(3, 9));
		builder.add(this.centerObjectRadio, cc.xy(5, 9));
		builder.add(this.rightObjectRadio, cc.xy(7, 9));
		builder.addLabel("JComboBox", cc.xy(1, 11));
		builder.add(this.alignmentObjectCombo, cc.xyw(3, 11, 3));
		return builder.getPanel();
	}

	private JPanel buildFormattedPanel()
	{
		FormLayout layout =
				new FormLayout("right:max(50dlu;pref), 3dlu, 50dlu", "p, 3dlu, p, 3dlu, p");

		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);
		CellConstraints cc = new CellConstraints();
		builder.addLabel("Date", cc.xy(1, 1));
		builder.add(this.dateField, cc.xy(3, 1));
		builder.addLabel("Integer", cc.xy(1, 3));
		builder.add(this.integerField, cc.xy(3, 3));
		builder.addLabel("Long", cc.xy(1, 5));
		builder.add(this.longField, cc.xy(3, 5));
		return builder.getPanel();
	}

	private JPanel buildListPanel()
	{
		FormLayout layout =
				new FormLayout("right:max(50dlu;pref), 3dlu, 150dlu",
						"fill:60dlu, 6dlu, fill:60dlu, 6dlu, p");

		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);
		CellConstraints cc = new CellConstraints();
		builder.addLabel("JList", cc.xy(1, 1, "right, top"));
		builder.add(new JScrollPane(this.list), cc.xy(3, 1));
		builder.addLabel("JTable", cc.xy(1, 3, "right, top"));
		builder.add(new JScrollPane(this.table), cc.xy(3, 3));
		builder.addLabel("JComboBox", cc.xy(1, 5));
		builder.add(this.comboBox, cc.xy(3, 5));
		return builder.getPanel();
	}

	private JPanel buildMiscPanel()
	{
		FormLayout layout =
				new FormLayout("right:max(50dlu;pref), 3dlu, 50dlu, 3dlu, 50dlu",
						"p, 3dlu, p, 3dlu, p, 3dlu, p");
		layout.setRowGroups(new int[][]
		{
			{
					1, 3, 5
			}
		});

		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);

		Action chooseAction =
				new ChooseColorAction(builder.getPanel(),
						this.presentationModel.getModel(ExampleBean.PROPERTYNAME_COLOR));

		CellConstraints cc = new CellConstraints();
		builder.addLabel("JCheckBox", cc.xy(1, 1));
		builder.add(this.checkBox, cc.xy(3, 1));
		builder.addLabel("JSlider", cc.xy(1, 3));
		builder.add(this.slider, cc.xy(3, 3));
		builder.add(this.floatLabel, cc.xy(5, 3));
		builder.addLabel("JSpinner", cc.xy(1, 5));
		builder.add(this.spinner, cc.xy(3, 5));
		builder.addLabel("JColorChooser", cc.xy(1, 7));
		builder.add(this.colorPreview, cc.xy(3, 7, "fill, fill"));
		builder.add(new JButton(chooseAction), cc.xy(5, 7, "left, center"));
		return builder.getPanel();
	}

	/**
	 * Builds and returns the panel.
	 * 
	 * @return the built panel
	 */
	public JComponent buildPanel()
	{
		initComponents();
		initEventHandling();

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);

		tabbedPane.addTab("Text", buildTextPanel());
		tabbedPane.addTab("Formatted", buildFormattedPanel());
		tabbedPane.addTab("Choices", buildChoicesPanel());
		tabbedPane.addTab("List", buildListPanel());
		tabbedPane.addTab("Misc", buildMiscPanel());
		return tabbedPane;
	}

	// Helper Code ************************************************************

	private JPanel buildTextPanel()
	{
		FormLayout layout =
				new FormLayout("right:max(50dlu;pref), 3dlu, 50dlu",
						"p, 3dlu, p, 3dlu, p, 14dlu, 3dlu, p");
		layout.setRowGroups(new int[][]
		{
			{
					1, 3, 5
			}
		});

		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);
		CellConstraints cc = new CellConstraints();
		builder.addLabel("JTextField", cc.xy(1, 1));
		builder.add(this.textField, cc.xy(3, 1));
		builder.addLabel("JPasswordField", cc.xy(1, 3));
		builder.add(this.passwordField, cc.xy(3, 3));
		builder.addLabel("JTextArea", cc.xy(1, 5));
		builder.add(new JScrollPane(this.textArea), cc.xywh(3, 5, 1, 2));
		builder.addLabel("JLabel", cc.xy(1, 8));
		builder.add(this.textLabel, cc.xy(3, 8));
		return builder.getPanel();
	}

	/**
	 * Creates, binds and configures the UI components.
	 * <p>
	 * If possible, the components are created using the BasicComponentFactory, or the Bindings
	 * class.
	 */
	private void initComponents()
	{
		// Text Components
		this.textField =
				BasicComponentFactory.createTextField(this.presentationModel
						.getModel(ExampleBean.PROPERTYNAME_TEXT));
		this.textArea =
				BasicComponentFactory.createTextArea(this.presentationModel
						.getModel(ExampleBean.PROPERTYNAME_TEXT));
		this.passwordField =
				BasicComponentFactory.createPasswordField(this.presentationModel
						.getModel(ExampleBean.PROPERTYNAME_TEXT));
		this.textLabel =
				BasicComponentFactory.createLabel(this.presentationModel
						.getModel(ExampleBean.PROPERTYNAME_TEXT));

		// Formatted Input
		this.dateField =
				BasicComponentFactory.createDateField(this.presentationModel
						.getModel(ExampleBean.PROPERTYNAME_DATE));
		this.integerField =
				BasicComponentFactory.createIntegerField(this.presentationModel
						.getModel(ExampleBean.PROPERTYNAME_INT_VALUE));
		this.longField =
				BasicComponentFactory.createLongField(this.presentationModel
						.getModel(ExampleBean.PROPERTYNAME_LONG_VALUE));

		// Choice
		ValueModel intChoiceModel =
				this.presentationModel.getModel(ExampleBean.PROPERTYNAME_INT_CHOICE);
		this.leftIntRadio =
				BasicComponentFactory.createRadioButton(intChoiceModel,
						ExampleBean.INTEGER_CHOICES[0], "Left");
		this.centerIntRadio =
				BasicComponentFactory.createRadioButton(intChoiceModel,
						ExampleBean.INTEGER_CHOICES[1], "Center");
		this.rightIntRadio =
				BasicComponentFactory.createRadioButton(intChoiceModel,
						ExampleBean.INTEGER_CHOICES[2], "Right");
		this.alignmentIntCombo =
				BasicComponentFactory.createComboBox(new SelectionInList<Integer>(
						ExampleBean.INTEGER_CHOICES, intChoiceModel));

		ValueModel objectChoiceModel =
				this.presentationModel.getModel(ExampleBean.PROPERTYNAME_OBJECT_CHOICE);
		this.leftObjectRadio =
				BasicComponentFactory
						.createRadioButton(objectChoiceModel, ExampleBean.LEFT, "Left");
		this.centerObjectRadio =
				BasicComponentFactory.createRadioButton(objectChoiceModel, ExampleBean.CENTER,
						"Center");
		this.rightObjectRadio =
				BasicComponentFactory.createRadioButton(objectChoiceModel, ExampleBean.RIGHT,
						"Right");
		this.alignmentObjectCombo =
				BasicComponentFactory.createComboBox(new SelectionInList<Object>(
						ExampleBean.OBJECT_CHOICES, objectChoiceModel));

		// Lists
		this.comboBox =
				BasicComponentFactory.createComboBox(this.presentationModel.getSelectionInList(),
						TutorialUtils.createAlbumListCellRenderer());

		this.list =
				BasicComponentFactory.createList(this.presentationModel.getSelectionInList(),
						TutorialUtils.createAlbumListCellRenderer());

		this.table = new JTable();
		this.table.setModel(TutorialUtils.createAlbumTableModel(this.presentationModel
				.getSelectionInList()));
		this.table.setSelectionModel(new SingleListSelectionAdapter(this.presentationModel
				.getSelectionInList().getSelectionIndexHolder()));

		// Misc
		this.checkBox =
				BasicComponentFactory.createCheckBox(
						this.presentationModel.getModel(ExampleBean.PROPERTYNAME_BOOLEAN_VALUE),
						"available");
		this.colorPreview = new JPanel();
		this.colorPreview.setBorder(new LineBorder(Color.GRAY));
		updatePreviewPanel();

		ValueModel floatModel =
				this.presentationModel.getModel(ExampleBean.PROPERTYNAME_FLOAT_VALUE);
		this.slider = new JSlider();
		this.slider.setModel(new BoundedRangeAdapter(ConverterFactory
				.createFloatToIntegerConverter(floatModel, 100), 0, 0, 100));
		this.floatLabel =
				BasicComponentFactory.createLabel(ConverterFactory.createStringConverter(
						floatModel, NumberFormat.getPercentInstance()));
		this.spinner = new JSpinner();
		this.spinner.setModel(SpinnerAdapterFactory.createNumberAdapter(
				this.presentationModel.getModel(ExampleBean.PROPERTYNAME_INT_LIMITED), 0, // defaultValue
				0, // minValue
				100, // maxValue
				5)); // step
	}

	private void initEventHandling()
	{
		this.presentationModel.getModel(ExampleBean.PROPERTYNAME_COLOR).addValueChangeListener(
				new ColorUpdateHandler());
	}

	private void updatePreviewPanel()
	{
		this.colorPreview.setBackground((this.presentationModel.getBean()).getColor());
	}

}
