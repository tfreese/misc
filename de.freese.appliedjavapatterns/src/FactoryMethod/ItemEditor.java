package FactoryMethod;

import javax.swing.JComponent;

/**
 * @author Thomas Freese
 */
public interface ItemEditor
{
	/**
     * 
     */
	public void commitChanges();

	/**
	 * @return {@link JComponent}
	 */
	public JComponent getGUI();
}
