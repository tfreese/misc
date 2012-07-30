package facade;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class InternationalizationWizard
{
	/**
     * 
     */
	private Currency currency = new Currency();

	/**
     * 
     */
	private final Map<String, Nation> map;

	/**
     * 
     */
	private InternationalizedText propertyFile = new InternationalizedText();

	/**
	 * Creates a new {@link InternationalizationWizard} object.
	 */
	public InternationalizationWizard()
	{
		super();

		this.map = new HashMap<>();
		Nation[] nations =
				{
				new Nation("US", '$', "+1", "us.properties", NumberFormat.getInstance(Locale.US)),
						new Nation("The Netherlands", 'f', "+31", "dutch.properties",
								NumberFormat.getInstance(Locale.GERMANY)),
						new Nation("France", 'f', "+33", "french.properties",
								NumberFormat.getInstance(Locale.FRANCE))
				};

		for (Nation nation : nations)
		{
			this.map.put(nation.getName(), nation);
		}
	}

	/**
	 * @return char
	 */
	public char getCurrencySymbol()
	{
		return this.currency.getCurrencySymbol();
	}

	/**
	 * @param name String
	 * @return {@link Nation}
	 */
	public Nation getNation(final String name)
	{
		return this.map.get(name);
	}

	/**
	 * @return Object[]
	 */
	public Nation[] getNations()
	{
		return this.map.values().toArray(new Nation[0]);
	}

	/**
	 * @return {@link NumberFormat}
	 */
	public NumberFormat getNumberFormat()
	{
		return this.currency.getNumberFormat();
	}

	/**
	 * @return String
	 */
	public String getPhonePrefix()
	{
		return PhoneNumber.getSelectedInterPrefix();
	}

	/**
	 * @param key String
	 * @return String
	 */
	public String getProperty(final String key)
	{
		return this.propertyFile.getProperty(key);
	}

	/**
	 * @param key String
	 * @param defaultValue String
	 * @return String
	 */
	public String getProperty(final String key, final String defaultValue)
	{
		return this.propertyFile.getProperty(key, defaultValue);
	}

	/**
	 * @param name String
	 */
	public void setNation(final String name)
	{
		Nation nation = this.map.get(name);

		if (nation != null)
		{
			this.currency.setCurrencySymbol(nation.getSymbol());
			this.currency.setNumberFormat(nation.getNumberFormat());
			PhoneNumber.setSelectedInterPrefix(nation.getDialingPrefix());
			this.propertyFile.setFileName(nation.getPropertyFileName());
		}
	}
}
