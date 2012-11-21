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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.tutorial.TutorialUtils;
import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Demonstrates how to modify the enabled/editable/visible state of a Swing component in a
 * Presentation Model using the new ComponentValueModel that has been introduced in the Binding 1.1.
 * The advantage of this approach is, that a PresentationModel can now easily operate on frequently
 * used GUI state.
 * <p>
 * See the JavaDoc method comment for #initEventHandling in the ExamplePresentationModel.
 * 
 * @author Karsten Lentzsch
 * @version $Revision: 1.3 $
 * @see com.jgoodies.binding.adapter.BasicComponentFactory
 * @see com.jgoodies.binding.adapter.Bindings
 * @since 1.1
 */
public final class ComponentValueModelExample
{

	public static final class ExampleBean extends Model
	{

		// Names of the Bound Bean Properties *************************************

		public static final String PROPERTYNAME_EDITABLE = "editable";

		public static final String PROPERTYNAME_ENABLED = "enabled";

		public static final String PROPERTYNAME_TEXT1 = "text1";

		public static final String PROPERTYNAME_TEXT2 = "text2";

		public static final String PROPERTYNAME_TEXT3 = "text3";

		public static final String PROPERTYNAME_VISIBLE = "visible";

		// Fields *****************************************************************

		private boolean editable;

		private boolean enabled;

		private String text1;

		private String text2;

		private String text3;

		private boolean visible;

		// Instance Creation ******************************************************

		public ExampleBean()
		{
			this.enabled = true;
			this.editable = false;
			this.visible = true;
			this.text1 = "Sample text1";
			this.text2 = "Sample text2";
			this.text3 = "Sampel text3";
		}

		// Accessors **************************************************************

		public String getText1()
		{
			return this.text1;
		}

		public String getText2()
		{
			return this.text2;
		}

		public String getText3()
		{
			return this.text3;
		}

		public boolean isEditable()
		{
			return this.editable;
		}

		public boolean isEnabled()
		{
			return this.enabled;
		}

		public boolean isVisible()
		{
			return this.visible;
		}

		public void setEditable(final boolean newEditable)
		{
			boolean oldEditable = isEditable();
			this.editable = newEditable;
			firePropertyChange(PROPERTYNAME_EDITABLE, oldEditable, newEditable);
		}

		public void setEnabled(final boolean newEnabled)
		{
			boolean oldEnabled = isEnabled();
			this.enabled = newEnabled;
			firePropertyChange(PROPERTYNAME_ENABLED, oldEnabled, newEnabled);
		}

		public void setText1(final String newText)
		{
			String oldText = getText1();
			this.text1 = newText;
			firePropertyChange(PROPERTYNAME_TEXT1, oldText, newText);
		}

		public void setText2(final String newText)
		{
			String oldText = getText2();
			this.text2 = newText;
			firePropertyChange(PROPERTYNAME_TEXT2, oldText, newText);
		}

		public void setText3(final String newText)
		{
			String oldText = getText3();
			this.text3 = newText;
			firePropertyChange(PROPERTYNAME_TEXT3, oldText, newText);
		}

		public void setVisible(final boolean newVisible)
		{
			boolean oldVisible = isVisible();
			this.visible = newVisible;
			firePropertyChange(PROPERTYNAME_VISIBLE, oldVisible, newVisible);
		}

	}

	// A custom PresentationModel that provides a SelectionInList
	// for the bean's ListModel and the bean's list selection.
	private static final class ExamplePresentationModel extends PresentationModel<ExampleBean>
	{

		// Instance Creation -----------------------------------------

		private ExamplePresentationModel(final ExampleBean exampleBean)
		{
			super(exampleBean);
			initEventHandling();
		}

		// Event Handling ---------------------------------------------

		/**
		 * Initializes the event handling. The three domain properties enabled, editable, and
		 * visible are bound to the related properties of the ComponentValueModels for text1, text2,
		 * text3.
		 * <p>
		 * The first approach demonstrates how to register a hand-made value change handler with the
		 * domain's enabled model. This handler updates the enabled state of the ComponentValueModel
		 * whenever the domain changes. The ComponentValueModel change will in turn update all
		 * components bound to it.
		 * <p>
		 * A shorter way to write the above is to use a PropertyConnector. Whenever the editable or
		 * visible domain model changes, the PropertyConnector will update the connected property in
		 * the ComponentValueModel, which in turn will update all components bound to it.
		 * <p>
		 * Both approaches require to synchronize the ComponentValueModel state with the domain
		 * state at initialization time. In this example the initial domain state for enabled and
		 * visible is true, but the initial editable domain state is false, so the view bound to
		 * text 2 shall be non-editable first.
		 */
		private void initEventHandling()
		{
			// Observe changes in the domain's enabled property
			// to update the enablement of the text1 views.
			getModel(ExampleBean.PROPERTYNAME_ENABLED).addValueChangeListener(
					new PropertyChangeListener()
					{
						/**
						 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
						 */
						@Override
						public void propertyChange(final PropertyChangeEvent evt)
						{
							updateText1ViewsEnablement();
						}
					});
			// Update the enablement of all views bound to text1 now.
			updateText1ViewsEnablement();

			// Observe changes in the domain's editable property
			// to update the editable state of text2 views.
			PropertyConnector editableConnector =
					PropertyConnector.connect(getModel(ExampleBean.PROPERTYNAME_EDITABLE), "value",
							getComponentModel(ExampleBean.PROPERTYNAME_TEXT2),
							ComponentModel.PROPERTY_EDITABLE);
			// Update the editable state of all views bound to text2 now.
			editableConnector.updateProperty2();

			PropertyConnector visibleConnector =
					PropertyConnector.connect(getModel(ExampleBean.PROPERTYNAME_VISIBLE), "value",
							getComponentModel(ExampleBean.PROPERTYNAME_TEXT3),
							ComponentModel.PROPERTY_EDITABLE);
			visibleConnector.updateProperty2();
		}

		private void updateText1ViewsEnablement()
		{
			boolean enabled = getModel(ExampleBean.PROPERTYNAME_ENABLED).booleanValue();
			getComponentModel(ExampleBean.PROPERTYNAME_TEXT1).setEnabled(enabled);
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
		frame.setTitle("Binding Tutorial :: ComponentValueModel");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		ComponentValueModelExample example = new ComponentValueModelExample();
		JComponent panel = example.buildPanel();
		frame.getContentPane().add(panel);
		frame.pack();
		TutorialUtils.locateOnOpticalScreenCenter(frame);
		frame.setVisible(true);
	}

	private JCheckBox editableBox;

	private JCheckBox enabledBox;

	// Holds an ExampleBean and vends ValueModels that adapt its properties.
	private final ExamplePresentationModel presentationModel;

	private JTextField text1Field;

	// Launching **************************************************************

	private JTextField text2Field;

	// Instance Creation ******************************************************

	private JTextField text3Field;

	// Component Creation and Initialization **********************************

	private JCheckBox visibleBox;

	// Building ***************************************************************

	/**
	 * Constructs the 'Components' example on an instance of ExampleBean.
	 */
	public ComponentValueModelExample()
	{
		this.presentationModel = new ExamplePresentationModel(new ExampleBean());
	}

	// Helper Code ************************************************************

	/**
	 * Builds and returns the panel.
	 * 
	 * @return the built panel
	 */
	public JComponent buildPanel()
	{
		initComponents();

		FormLayout layout =
				new FormLayout("right:pref, 3dlu, 50dlu, 3dlu, pref", "p, 3dlu, p, 3dlu, p");

		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);
		CellConstraints cc = new CellConstraints();
		builder.addLabel("Text1", cc.xy(1, 1));
		builder.add(this.text1Field, cc.xy(3, 1));
		builder.add(this.enabledBox, cc.xy(5, 1));
		builder.addLabel("Text2", cc.xy(1, 3));
		builder.add(this.text2Field, cc.xy(3, 3));
		builder.add(this.editableBox, cc.xy(5, 3));
		builder.addLabel("Text3", cc.xy(1, 5));
		builder.add(this.text3Field, cc.xy(3, 5));
		builder.add(this.visibleBox, cc.xy(5, 5));
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
		this.text1Field =
				BasicComponentFactory.createTextField(this.presentationModel
						.getComponentModel(ExampleBean.PROPERTYNAME_TEXT1));
		this.text2Field =
				BasicComponentFactory.createTextField(this.presentationModel
						.getComponentModel(ExampleBean.PROPERTYNAME_TEXT2));
		this.text3Field =
				BasicComponentFactory.createTextField(this.presentationModel
						.getComponentModel(ExampleBean.PROPERTYNAME_TEXT3));

		this.enabledBox =
				BasicComponentFactory.createCheckBox(
						this.presentationModel.getModel(ExampleBean.PROPERTYNAME_ENABLED),
						"enabled");
		this.editableBox =
				BasicComponentFactory.createCheckBox(
						this.presentationModel.getModel(ExampleBean.PROPERTYNAME_EDITABLE),
						"editable");
		this.visibleBox =
				BasicComponentFactory.createCheckBox(
						this.presentationModel.getModel(ExampleBean.PROPERTYNAME_VISIBLE),
						"visible");
	}

}
