package strategy;

/**
 * @author Thomas Freese
 */
public interface SummarizingStrategy
{
	/**
     * 
     */
	public static final String EOL_STRING = System.getProperty("line.separator");

	/**
     * 
     */
	public static final String DELIMITER = ":";

	/**
     * 
     */
	public static final String COMMA = ",";

	/**
     * 
     */
	public static final String SPACE = " ";

	/**
	 * @param contactList {@link Contact}[]
	 * @return String[]
	 */
	public String[] makeSummarizedList(Contact[] contactList);

	/**
	 * @param contactList {@link Contact}[]
	 * @return String
	 */
	public String summarize(Contact[] contactList);
}
