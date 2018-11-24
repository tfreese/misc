/**
 * 06.11.2013
 */
package de.freese.sonstiges.mockito;

/**
 * @author Thomas Freese
 */
public class VendingMaschine
{
	/**
	 * 
	 */
	private final ICashBox cashBox;

	/**
	 * 
	 */
	private final IBox[] boxes;

	/**
	 * Erstellt ein neues {@link VendingMaschine} Objekt.
	 * 
	 * @param cashBox {@link ICashBox}
	 * @param boxes {@link IBox}
	 */
	public VendingMaschine(final ICashBox cashBox, final IBox[] boxes)
	{
		super();

		this.cashBox = cashBox;
		this.boxes = boxes;
	}

	/**
	 * @param boxIndex int
	 * @throws Exception Falls was schief geht.
	 */
	public void selectItem(final int boxIndex) throws Exception
	{
		IBox box = this.boxes[boxIndex];

		if (box.isEmpty())
		{
			throw new Exception("box is empty");
		}

		int amountRequired = box.getPrice();

		if (amountRequired > this.cashBox.getCurrentAmount())
		{
			throw new Exception("not enough money");
		}

		box.releaseItem();
		this.cashBox.withdraw(amountRequired);
	}
}
