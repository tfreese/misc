package state;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class CalendarEditor
{
	/**
	 * @author Thomas Freese
	 */
	private class CleanState implements State
	{
		/**
         * 
         */
		private State nextState = new DirtyState(this);

		/**
		 * @see state.State#edit()
		 */
		@Override
		public void edit()
		{
			CalendarEditor.this.currentState = this.nextState;
		}

		/**
         * 
         */
		@Override
		public void save()
		{
			// Empty
		}
	}

	/**
	 * @author Thomas Freese
	 */
	private class DirtyState implements State
	{
		/**
         * 
         */
		private State nextState;

		/**
		 * Creates a new {@link DirtyState} object.
		 * 
		 * @param nextState {@link State}
		 */
		public DirtyState(final State nextState)
		{
			this.nextState = nextState;
		}

		/**
		 * @see state.State#edit()
		 */
		@Override
		public void edit()
		{
			// Empty
		}

		/**
		 * @see state.State#save()
		 */
		@Override
		public void save()
		{
			FileLoader.storeData(CalendarEditor.this.appointmentFile,
					(ArrayList<Appointment>) CalendarEditor.this.appointments);
			CalendarEditor.this.currentState = this.nextState;
		}
	}

	/**
     * 
     */
	private static final String DEFAULT_APPOINTMENT_FILE = "appointments.ser";

	/**
     * 
     */
	private File appointmentFile;

	/**
     * 
     */
	private List<Appointment> appointments = new ArrayList<>();

	/**
     * 
     */
	private State currentState;

	/**
	 * Creates a new {@link CalendarEditor} object.
	 */
	public CalendarEditor()
	{
		this(DEFAULT_APPOINTMENT_FILE);
	}

	/**
	 * Creates a new {@link CalendarEditor} object.
	 * 
	 * @param appointmentFileName String
	 */
	@SuppressWarnings("unchecked")
	public CalendarEditor(final String appointmentFileName)
	{
		this.appointmentFile = new File(appointmentFileName);

		try
		{
			this.appointments = (ArrayList<Appointment>) FileLoader.loadData(this.appointmentFile);
		}
		catch (ClassCastException exc)
		{
			System.err
					.println("Unable to load information. The file does not contain a list of appointments.");
		}

		this.currentState = new CleanState();
	}

	/**
	 * @param appointment {@link Appointment}
	 */
	public void addAppointment(final Appointment appointment)
	{
		if (!this.appointments.contains(appointment))
		{
			this.appointments.add(appointment);
		}
	}

	/**
     * 
     */
	public void edit()
	{
		this.currentState.edit();
	}

	/**
	 * @return {@link List}
	 */
	public List<Appointment> getAppointments()
	{
		return this.appointments;
	}

	/**
	 * @param appointment {@link Appointment}
	 */
	public void removeAppointment(final Appointment appointment)
	{
		this.appointments.remove(appointment);
	}

	/**
     * 
     */
	public void save()
	{
		this.currentState.save();
	}
}
