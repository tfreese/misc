package command;

/**
 * @author Thomas Freese
 */
public class ChangeLocationCommand implements UndoableCommand
{
	/**
     * 
     */
	private Appointment appointment;

	/**
     * 
     */
	private LocationEditor editor;

	/**
     * 
     */
	private Location newLocation;

	/**
     * 
     */
	private Location oldLocation;

	/**
	 * Erstellt ein neues {@link ChangeLocationCommand} Object.
	 */
	ChangeLocationCommand()
	{
		super();
	}

	/**
	 * @see command.Command#execute()
	 */
	@Override
	public void execute()
	{
		this.oldLocation = this.appointment.getLocation();
		this.newLocation = this.editor.getNewLocation();
		this.appointment.setLocation(this.newLocation);
	}

	/**
	 * @return {@link Appointment}
	 */
	public Appointment getAppointment()
	{
		return this.appointment;
	}

	/**
	 * @see command.UndoableCommand#redo()
	 */
	@Override
	public void redo()
	{
		this.appointment.setLocation(this.newLocation);
	}

	/**
	 * @param appointment {@link Appointment}
	 */
	public void setAppointment(final Appointment appointment)
	{
		this.appointment = appointment;
	}

	/**
	 * @param locationEditor {@link LocationEditor}
	 */
	public void setLocationEditor(final LocationEditor locationEditor)
	{
		this.editor = locationEditor;
	}

	/**
	 * @see command.UndoableCommand#undo()
	 */
	@Override
	public void undo()
	{
		this.appointment.setLocation(this.oldLocation);
	}
}
