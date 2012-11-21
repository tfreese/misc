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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import com.jgoodies.binding.tutorial.Album;
import com.jgoodies.binding.tutorial.TutorialUtils;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Builds an editor that copies data from the domain back and forth. This approach is known as the
 * "copying" approach or "push/pull".
 * <p>
 * The event handling used to enable and disable the composer text field is invoked by a
 * ChangeListener that hooks into the classical check box. Note that this lacks the domain logic,
 * where the composer is set to <code>null</code> if the classical property is set to false. This
 * logic is deferred until the component values are written to the edited Album via
 * <code>#updateModel</code> when OK is pressed.
 * 
 * @author Karsten Lentzsch
 * @version $Revision: 1.12 $
 */

public final class EditorCopyingExample
{

	private final class CancelAction extends AbstractAction
	{

		private CancelAction()
		{
			super("Cancel");
		}

		@Override
		public void actionPerformed(final ActionEvent e)
		{
			// Just ignore the current content.
			System.out.println(EditorCopyingExample.this.editedAlbum);
			System.exit(0);
		}
	}

	/**
	 * Updates the composer field's enablement and text.
	 */
	private final class ClassicalChangeHandler implements ChangeListener
	{

		/**
		 * The selection state of the classical check box has changed. Updates the enablement and
		 * contents of the composer field.
		 */
		@Override
		public void stateChanged(final ChangeEvent evt)
		{
			updateComposerField();
		}
	}

	private final class OKAction extends AbstractAction
	{

		private OKAction()
		{
			super("OK");
		}

		@Override
		public void actionPerformed(final ActionEvent e)
		{
			updateModel();
			System.out.println(EditorCopyingExample.this.editedAlbum);
			System.exit(0);
		}
	}

	private final class ResetAction extends AbstractAction
	{

		private ResetAction()
		{
			super("Reset");
		}

		@Override
		public void actionPerformed(final ActionEvent e)
		{
			updateView();
			System.out.println(EditorCopyingExample.this.editedAlbum);
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
		frame.setTitle("Binding Tutorial :: Editor (Copying)");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		EditorCopyingExample example = new EditorCopyingExample();
		JComponent panel = example.buildPanel();
		example.updateView();
		frame.getContentPane().add(panel);
		frame.pack();
		TutorialUtils.locateOnOpticalScreenCenter(frame);
		frame.setVisible(true);
	}

	private JTextComponent artistField;

	private JButton cancelButton;

	private JCheckBox classicalBox;

	// Launching **************************************************************

	private JTextComponent composerField;

	// Instance Creation ******************************************************

	/**
	 * Refers to the Album that is to be edited by this example editor.
	 */
	private final Album editedAlbum;

	private JButton okButton;

	// Initialization *********************************************************

	private JButton resetButton;

	private JTextComponent titleField;

	// Copying Data Back and Forth ********************************************

	/**
	 * Constructs an editor for an example Album.
	 */
	public EditorCopyingExample()
	{
		this(Album.ALBUM1);
	}

	/**
	 * Constructs an editor for an Album to be edited.
	 * 
	 * @param album the Album to be edited
	 */
	public EditorCopyingExample(final Album album)
	{
		this.editedAlbum = album;
	}

	// Building ***************************************************************

	private JComponent buildButtonBar()
	{
		return new ButtonBarBuilder().addButton(this.okButton, this.cancelButton, this.resetButton)
				.build();
	}

	/**
	 * Builds and returns the editor panel.
	 * 
	 * @return the built panel
	 */
	public JComponent buildPanel()
	{
		initComponents();
		initEventHandling();

		FormLayout layout =
				new FormLayout("right:pref, 3dlu, 150dlu:grow",
						"p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, p");

		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);
		CellConstraints cc = new CellConstraints();

		builder.addLabel("Artist", cc.xy(1, 1));
		builder.add(this.artistField, cc.xy(3, 1));
		builder.addLabel("Title", cc.xy(1, 3));
		builder.add(this.titleField, cc.xy(3, 3));
		builder.add(this.classicalBox, cc.xy(3, 5));
		builder.addLabel("Composer", cc.xy(1, 7));
		builder.add(this.composerField, cc.xy(3, 7));
		builder.add(buildButtonBar(), cc.xyw(1, 9, 3));

		return builder.getPanel();
	}

	// Event Handling *********************************************************

	/**
	 * Creates and intializes the UI components.
	 */
	private void initComponents()
	{
		this.titleField = new JTextField();
		this.artistField = new JTextField();
		this.classicalBox = new JCheckBox("Classical");
		this.composerField = new JTextField();
		this.okButton = new JButton(new OKAction());
		this.cancelButton = new JButton(new CancelAction());
		this.resetButton = new JButton(new ResetAction());

		updateComposerField();
	}

	/**
	 * Observes the classical check box to update the composer field's enablement and contents. For
	 * demonstration purposes a listener is registered that writes all changes to the console.
	 */
	private void initEventHandling()
	{
		this.classicalBox.addChangeListener(new ClassicalChangeHandler());

		// Report changes in all bound Album properties.
		this.editedAlbum.addPropertyChangeListener(TutorialUtils
				.createDebugPropertyChangeListener());
	}

	// Actions ****************************************************************

	/**
	 * Updates the composer field's enablement and contents. Sets the enablement according to the
	 * selection state of the classical check box. If the composer is not enabled, we copy the
	 * domain logic and clear the composer field's text.
	 */
	private void updateComposerField()
	{
		boolean composerEnabled = this.classicalBox.isSelected();
		this.composerField.setEnabled(composerEnabled);
		if (!composerEnabled)
		{
			this.composerField.setText("");
		}
	}

	/**
	 * Reads the values from this editor's components and set the associated Album properties.
	 */
	private void updateModel()
	{
		this.editedAlbum.setTitle(this.titleField.getText());
		this.editedAlbum.setArtist(this.artistField.getText());
		this.editedAlbum.setClassical(this.classicalBox.isSelected());
		this.editedAlbum.setComposer(this.composerField.getText());
	}

	/**
	 * Reads the property values from the edited Album and sets them in this editor's components.
	 */
	private void updateView()
	{
		this.titleField.setText(this.editedAlbum.getTitle());
		this.artistField.setText(this.editedAlbum.getArtist());
		this.classicalBox.setSelected(this.editedAlbum.isClassical());
		this.composerField.setText(this.editedAlbum.getComposer());
	}

}
