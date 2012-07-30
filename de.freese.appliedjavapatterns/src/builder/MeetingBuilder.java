package builder;

/**
 * @author Thomas Freese
 */
public class MeetingBuilder extends AppointmentBuilder
{
	/**
	 * Erstellt ein neues {@link MeetingBuilder} Object.
	 */
	MeetingBuilder()
	{
		super();
	}

	/**
	 * @see builder.AppointmentBuilder#getAppointment()
	 */
	@Override
	public Appointment getAppointment() throws InformationRequiredException
	{
		try
		{
			super.getAppointment();
		}
		finally
		{
			if (this.appointment.getEndDate() == null)
			{
				this.requiredElements += END_DATE_REQUIRED;
			}

			if (this.requiredElements > 0)
			{
				throw new InformationRequiredException(this.requiredElements);
			}
		}

		return this.appointment;
	}
}
