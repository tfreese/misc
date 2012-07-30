package router;

/**
 * @author Thomas Freese
 */
public interface Receiver
{
	/**
	 * @param message {@link Message}
	 */
	public void receiveMessage(Message message);
}
