/*
 * Copyright (C) 2008-11 Bernhard Hobiger This file is part of HoDoKu. HoDoKu is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. HoDoKu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with HoDoKu. If not, see <http://www.gnu.org/licenses/>.
 */

package sudoku;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

/**
 * @author hobiwan
 */
public class WriteAsPNGDialog extends javax.swing.JDialog
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(final String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new WriteAsPNGDialog(new javax.swing.JFrame(), true, null, 0, 0, 0)
						.setVisible(true);
			}
		});
	}

	private File bildFile;

	private int auflösung;

	private double bildSize;

	private int einheit;

	private boolean ok = false;

	private JRadioButton[] einheiten;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton abbrechenButton;

	private javax.swing.JLabel auflösungLabel;

	private javax.swing.JTextField auflösungTextField;

	private javax.swing.JButton bildSpeichernButton;

	private javax.swing.JLabel dateiNameLabel;

	private javax.swing.JTextField dateiNameTextField;

	private javax.swing.JButton durchsuchenButton;

	private javax.swing.ButtonGroup einheitButtonGroup;

	private javax.swing.JPanel einheitPanel;

	private javax.swing.JLabel größeLabel;

	private javax.swing.JPanel größePanel;

	private javax.swing.JTextField größeTextField;

	private javax.swing.JRadioButton inchRadioButton;

	private javax.swing.JRadioButton mmRadioButton;

	private javax.swing.JRadioButton pixelRadioButton;

	// End of variables declaration//GEN-END:variables
	/** Creates new form WriteAsPNGDialog */
	public WriteAsPNGDialog(final java.awt.Frame parent, final boolean modal, final File bildFile,
			final double size, final int auflösung, final int einheit)
	{
		super(parent, modal);
		initComponents();

		this.auflösung = auflösung;
		this.bildSize = size;
		this.einheit = einheit;
		this.bildFile = bildFile;

		this.einheiten = new JRadioButton[3];
		this.einheiten[0] = this.mmRadioButton;
		this.einheiten[1] = this.inchRadioButton;
		this.einheiten[2] = this.pixelRadioButton;

		if (bildFile != null)
		{
			this.dateiNameTextField.setText(bildFile.getAbsolutePath());
			this.dateiNameTextField.setCaretPosition(this.dateiNameTextField.getText().length());
		}
		this.auflösungTextField.setText(Integer.toString(auflösung));
		this.größeTextField.setText(Double.toString(size));
		this.einheiten[einheit].setSelected(true);

		getRootPane().setDefaultButton(this.bildSpeichernButton);
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				setVisible(false);
			}
		};
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		getRootPane().getActionMap().put("ESCAPE", escapeAction);
	}

	private void abbrechenButtonActionPerformed(final java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_abbrechenButtonActionPerformed
		setVisible(false);
	}// GEN-LAST:event_abbrechenButtonActionPerformed

	private void bildSpeichernButtonActionPerformed(final java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_bildSpeichernButtonActionPerformed
		String fileName = this.dateiNameTextField.getText();
		if (fileName.length() > 0)
		{
			try
			{
				this.bildFile = new File(fileName);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(
						this,
						java.util.ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString(
								"WriteAsPNGDialog.invalid_filename"),
						java.util.ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString(
								"WriteAsPNGDialog.error"), JOptionPane.ERROR_MESSAGE);
			}
		}
		if (this.bildFile == null)
		{
			durchsuchenButtonActionPerformed(null);
		}
		if (this.bildFile == null)
		{
			return;
		}
		try
		{
			this.auflösung = Integer.parseInt(this.auflösungTextField.getText());
			this.bildSize = Double.parseDouble(this.größeTextField.getText());
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(
					this,
					java.util.ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString(
							"WriteAsPNGDialog.invalid_input_format"),
					java.util.ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString(
							"WriteAsPNGDialog.error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (int i = 0; i < this.einheiten.length; i++)
		{
			if (this.einheiten[i].isSelected())
			{
				this.einheit = i;
				break;
			}
		}
		this.ok = true;
		setVisible(false);
	}// GEN-LAST:event_bildSpeichernButtonActionPerformed

	private void durchsuchenButtonActionPerformed(final java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_durchsuchenButtonActionPerformed
		JFileChooser chooser = new JFileChooser(getBildFile());
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
		{
			@Override
			public boolean accept(final File f)
			{
				if (f.isDirectory())
				{
					return true;
				}
				String[] parts = f.getName().split("\\.");
				if (parts.length > 1)
				{
					String ext = parts[parts.length - 1];
					if (ext.equalsIgnoreCase("png"))
					{
						return true;
					}
				}
				return false;
			}

			@Override
			public String getDescription()
			{
				return "*.png "
						+ java.util.ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString(
								"WriteAsPNGDialog.files");
			}
		});

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			this.bildFile = chooser.getSelectedFile();
			String name = getBildFile().getAbsolutePath();
			if (!name.toLowerCase().endsWith(".png"))
			{
				name += ".png";
				this.bildFile = new File(name);
			}
			this.dateiNameTextField.setText(getBildFile().getAbsolutePath());
			this.dateiNameTextField.setCaretPosition(this.dateiNameTextField.getText().length());
		}
		this.bildSpeichernButton.requestFocusInWindow();
	}// GEN-LAST:event_durchsuchenButtonActionPerformed

	public int getAuflösung()
	{
		return this.auflösung;
	}

	public File getBildFile()
	{
		return this.bildFile;
	}

	public double getBildSize()
	{
		return this.bildSize;
	}

	public int getEinheit()
	{
		return this.einheit;
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT
	 * modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		this.einheitButtonGroup = new javax.swing.ButtonGroup();
		this.größePanel = new javax.swing.JPanel();
		this.größeLabel = new javax.swing.JLabel();
		this.auflösungLabel = new javax.swing.JLabel();
		this.größeTextField = new javax.swing.JTextField();
		this.auflösungTextField = new javax.swing.JTextField();
		this.einheitPanel = new javax.swing.JPanel();
		this.mmRadioButton = new javax.swing.JRadioButton();
		this.inchRadioButton = new javax.swing.JRadioButton();
		this.pixelRadioButton = new javax.swing.JRadioButton();
		this.dateiNameLabel = new javax.swing.JLabel();
		this.dateiNameTextField = new javax.swing.JTextField();
		this.durchsuchenButton = new javax.swing.JButton();
		this.bildSpeichernButton = new javax.swing.JButton();
		this.abbrechenButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		java.util.ResourceBundle bundle =
				java.util.ResourceBundle.getBundle("intl/WriteAsPNGDialog"); // NOI18N
		setTitle(bundle.getString("WriteAsPNGDialog.title")); // NOI18N

		this.größePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle
				.getString("WriteAsPNGDialog.größePanel.border.title"))); // NOI18N

		this.größeLabel.setDisplayedMnemonic(java.util.ResourceBundle
				.getBundle("intl/WriteAsPNGDialog")
				.getString("WriteAsPNGDialog.größeLabel.mnemonic").charAt(0));
		this.größeLabel.setLabelFor(this.größeTextField);
		this.größeLabel.setText(bundle.getString("WriteAsPNGDialog.größeLabel.text")); // NOI18N

		this.auflösungLabel.setDisplayedMnemonic(java.util.ResourceBundle
				.getBundle("intl/WriteAsPNGDialog")
				.getString("WriteAsPNGDialog.auflösungLabel.mnemonic").charAt(0));
		this.auflösungLabel.setLabelFor(this.auflösungTextField);
		this.auflösungLabel.setText(bundle.getString("WriteAsPNGDialog.auflösungLabel.text")); // NOI18N

		javax.swing.GroupLayout größePanelLayout = new javax.swing.GroupLayout(this.größePanel);
		this.größePanel.setLayout(größePanelLayout);
		größePanelLayout.setHorizontalGroup(größePanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				größePanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								größePanelLayout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(this.größeLabel)
										.addComponent(this.auflösungLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								größePanelLayout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(this.auflösungTextField,
												javax.swing.GroupLayout.DEFAULT_SIZE, 170,
												Short.MAX_VALUE)
										.addComponent(this.größeTextField,
												javax.swing.GroupLayout.DEFAULT_SIZE, 170,
												Short.MAX_VALUE)).addContainerGap()));
		größePanelLayout.setVerticalGroup(größePanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				größePanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								größePanelLayout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE, false)
										.addComponent(this.größeLabel)
										.addComponent(this.größeTextField,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								größePanelLayout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE, false)
										.addComponent(this.auflösungLabel)
										.addComponent(this.auflösungTextField,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		this.einheitPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle
				.getString("WriteAsPNGDialog.einheitPanel.border.title"))); // NOI18N

		this.einheitButtonGroup.add(this.mmRadioButton);
		this.mmRadioButton.setMnemonic(java.util.ResourceBundle.getBundle("intl/WriteAsPNGDialog")
				.getString("WriteAsPNGDialog.mmRadioButton.mnemonic").charAt(0));
		this.mmRadioButton.setSelected(true);
		this.mmRadioButton.setText(bundle.getString("WriteAsPNGDialog.mmRadioButton.text")); // NOI18N
		this.mmRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.mmRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

		this.einheitButtonGroup.add(this.inchRadioButton);
		this.inchRadioButton.setMnemonic(java.util.ResourceBundle
				.getBundle("intl/WriteAsPNGDialog")
				.getString("WriteAsPNGDialog.inchRadioButton.mnemonic").charAt(0));
		this.inchRadioButton.setText(bundle.getString("WriteAsPNGDialog.inchRadioButton.text")); // NOI18N
		this.inchRadioButton.setToolTipText(bundle
				.getString("WriteAsPNGDialog.inchRadioButton.toolTipText")); // NOI18N
		this.inchRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.inchRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

		this.einheitButtonGroup.add(this.pixelRadioButton);
		this.pixelRadioButton.setMnemonic(java.util.ResourceBundle
				.getBundle("intl/WriteAsPNGDialog")
				.getString("WriteAsPNGDialog.pixelRadioButton.mnemonic").charAt(0));
		this.pixelRadioButton.setText(bundle.getString("WriteAsPNGDialog.pixelRadioButton.text")); // NOI18N
		this.pixelRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.pixelRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

		javax.swing.GroupLayout einheitPanelLayout = new javax.swing.GroupLayout(this.einheitPanel);
		this.einheitPanel.setLayout(einheitPanelLayout);
		einheitPanelLayout.setHorizontalGroup(einheitPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				einheitPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								einheitPanelLayout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(this.mmRadioButton)
										.addComponent(this.inchRadioButton)
										.addComponent(this.pixelRadioButton))
						.addContainerGap(45, Short.MAX_VALUE)));
		einheitPanelLayout.setVerticalGroup(einheitPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				einheitPanelLayout.createSequentialGroup().addComponent(this.mmRadioButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.inchRadioButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.pixelRadioButton)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		this.dateiNameLabel.setDisplayedMnemonic(java.util.ResourceBundle
				.getBundle("intl/WriteAsPNGDialog")
				.getString("WriteAsPNGDialog.dateiNameLabel.mnemonic").charAt(0));
		this.dateiNameLabel.setLabelFor(this.dateiNameTextField);
		this.dateiNameLabel.setText(bundle.getString("WriteAsPNGDialog.dateiNameLabel.text")); // NOI18N

		this.durchsuchenButton.setMnemonic('.');
		this.durchsuchenButton.setText("..."); // NOI18N
		this.durchsuchenButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt)
			{
				durchsuchenButtonActionPerformed(evt);
			}
		});

		this.bildSpeichernButton.setMnemonic(java.util.ResourceBundle
				.getBundle("intl/WriteAsPNGDialog")
				.getString("WriteAsPNGDialog.bildSpeichernButton.mnemonic").charAt(0));
		this.bildSpeichernButton.setText(bundle
				.getString("WriteAsPNGDialog.bildSpeichernButton.text")); // NOI18N
		this.bildSpeichernButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt)
			{
				bildSpeichernButtonActionPerformed(evt);
			}
		});

		this.abbrechenButton.setMnemonic(java.util.ResourceBundle
				.getBundle("intl/WriteAsPNGDialog")
				.getString("WriteAsPNGDialog.abbrechenButton.mnemonic").charAt(0));
		this.abbrechenButton.setText(bundle.getString("WriteAsPNGDialog.abbrechenButton.text")); // NOI18N
		this.abbrechenButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt)
			{
				abbrechenButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		this.größePanel,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		this.einheitPanel,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(this.dateiNameLabel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		this.dateiNameTextField,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		315, Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		this.durchsuchenButton))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		this.bildSpeichernButton)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(this.abbrechenButton)))
								.addContainerGap()));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]
		{
				this.abbrechenButton, this.bildSpeichernButton
		});

		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(
										javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(this.größePanel,
												javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(this.einheitPanel,
												javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
						.addGap(14, 14, 14)
						.addGroup(
								layout.createParallelGroup(
										javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(this.dateiNameLabel)
										.addComponent(this.durchsuchenButton)
										.addComponent(this.dateiNameTextField,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(14, 14, 14)
						.addGroup(
								layout.createParallelGroup(
										javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(this.bildSpeichernButton)
										.addComponent(this.abbrechenButton)).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	public boolean isOk()
	{
		return this.ok;
	}

}
