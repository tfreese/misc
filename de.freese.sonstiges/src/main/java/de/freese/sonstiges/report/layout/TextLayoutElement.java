/**
 * 15.04.2008
 */
package de.freese.sonstiges.report.layout;

/**
 * Implementierung eines LayoutElementes fuer Text.
 * 
 * @author Thomas Freese
 */
public class TextLayoutElement extends AbstractLayoutElement
{
	/**
	 * Creates a new TextLayoutElement object.
	 */
	public TextLayoutElement()
	{
		super("");
	}

	/**
	 * Text des Elementes.
	 * 
	 * @param text String
	 */
	public void setText(final String text)
	{
		setName(text);
	}

	/**
	 * Text des Elementes.
	 * 
	 * @return String
	 */
	public String getText()
	{
		return getName();
	}
}