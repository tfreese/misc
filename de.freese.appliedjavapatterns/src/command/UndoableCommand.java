package command;

/**
 * @author Thomas Freese
 */
public interface UndoableCommand extends Command
{
	/**
     * 
     */
	public void redo();

	/**
     * 
     */
	public void undo();
}
