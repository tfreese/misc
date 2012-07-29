package mvc;

/**
 * @author Thomas Freese
 */
public interface ContactView
{
	/**
	 * @param firstName String
	 * @param lastName String
	 * @param title String
	 * @param organization String
	 */
	public void refreshContactView(String firstName, String lastName, String title,
									String organization);
}
