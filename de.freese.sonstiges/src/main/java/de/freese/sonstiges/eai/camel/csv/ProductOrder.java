/**
 * Created: 13.07.2012
 */

package de.freese.sonstiges.eai.camel.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

/**
 * @author Thomas Freese
 */
@CsvRecord(separator = ",", skipFirstLine = true)
public class ProductOrder
{
	/**
	 * 
	 */
	@DataField(pos = 1, trim = true)
	private String customer = null;

	/**
	 * 
	 */
	@DataField(pos = 3, trim = true, pattern = "0")
	private int price = 0;

	/**
	 * 
	 */
	@DataField(pos = 2, trim = true, pattern = "0")
	private int quantity = 0;

	/**
	 * 
	 */
	@DataField(pos = 4, trim = true, pattern = "0")
	private int total = 0;

	/**
	 * Erstellt ein neues {@link ProductOrder} Object.
	 */
	public ProductOrder()
	{
		super();
	}

	/**
	 * @return String
	 */
	public String getCustomer()
	{
		return this.customer;
	}

	/**
	 * @return int
	 */
	public int getPrice()
	{
		return this.price;
	}

	/**
	 * @return int
	 */
	public int getQuantity()
	{
		return this.quantity;
	}

	/**
	 * @return int
	 */
	public int getTotal()
	{
		return this.total;
	}

	/**
	 * @param total int
	 */
	public void setTotal(final int total)
	{
		this.total = total;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [customer=");
		builder.append(this.customer);
		builder.append(", quantity=");
		builder.append(this.quantity);
		builder.append(", price=");
		builder.append(this.price);
		builder.append(", total=");
		builder.append(this.total);
		builder.append("]");

		return builder.toString();
	}
}
