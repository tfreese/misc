package strategy;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Thomas Freese
 */
public class OrganizationSummarizer implements SummarizingStrategy
{
	/**
	 * @author Thomas Freese
	 */
	private class OrganizationComparator implements Comparator<Contact>
	{
		/**
         * 
         */
		private Collator textComparator = Collator.getInstance();

		/**
		 * Erstellt ein neues {@link OrganizationComparator} Object.
		 */
		private OrganizationComparator()
		{
			super();
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final Contact o1, final Contact o2)
		{
			int compareResult =
					this.textComparator.compare(o1.getOrganization(), o2.getOrganization());

			if (compareResult == 0)
			{
				compareResult = this.textComparator.compare(o1.getLastName(), o2.getLastName());
			}

			return compareResult;
		}
	}

	/**
     * 
     */
	private Comparator<Contact> comparator = new OrganizationComparator();

	/**
	 * Erstellt ein neues {@link OrganizationSummarizer} Object.
	 */
	OrganizationSummarizer()
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
					contactList[i].getOrganization() + DELIMITER + SPACE
							+ contactList[i].getFirstName() + SPACE + contactList[i].getLastName()
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
			product.append(element.getOrganization());
			product.append(DELIMITER);
			product.append(SPACE);
			product.append(element.getFirstName());
			product.append(SPACE);
			product.append(element.getLastName());
			product.append(EOL_STRING);
		}

		return product.toString();
	}
}
