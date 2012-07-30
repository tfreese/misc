package strategy;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Thomas Freese
 */
public class NameSummarizer implements SummarizingStrategy
{
	/**
	 * @author Thomas Freese
	 */
	private class NameComparator implements Comparator<Contact>
	{
		/**
         * 
         */
		private Collator textComparator = Collator.getInstance();

		/**
		 * Erstellt ein neues {@link NameComparator} Object.
		 */
		private NameComparator()
		{
			super();
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final Contact o1, final Contact o2)
		{
			int compareResult = this.textComparator.compare(o1.getLastName(), o2.getLastName());

			if (compareResult == 0)
			{
				compareResult = this.textComparator.compare(o1.getFirstName(), o2.getFirstName());
			}

			return compareResult;
		}
	}

	/**
     * 
     */
	private Comparator<Contact> comparator = new NameComparator();

	/**
	 * Erstellt ein neues {@link NameSummarizer} Object.
	 */
	NameSummarizer()
	{
		super();
	}

	/**
	 * @see strategy.SummarizingStrategy#makeSummarizedList(strategy.Contact[])
	 */
	@Override
	public String[] makeSummarizedList(final Contact[] contactList)
	{
		Arrays.sort(contactList, this.comparator);
		String[] product = new String[contactList.length];

		for (int i = 0; i < contactList.length; i++)
		{
			product[i] =
					contactList[i].getLastName() + COMMA + SPACE + contactList[i].getFirstName()
							+ EOL_STRING;
		}

		return product;
	}

	/**
	 * @see strategy.SummarizingStrategy#summarize(strategy.Contact[])
	 */
	@Override
	public String summarize(final Contact[] contactList)
	{
		StringBuilder product = new StringBuilder();

		Arrays.sort(contactList, this.comparator);

		for (Contact element : contactList)
		{
			product.append(element.getLastName());
			product.append(COMMA);
			product.append(SPACE);
			product.append(element.getFirstName());
			product.append(EOL_STRING);
		}

		return product.toString();
	}
}
