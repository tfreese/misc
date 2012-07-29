package builder;

import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Scheduler
{
	/**
	 * Erstellt ein neues {@link Scheduler} Object.
	 */
	Scheduler()
	{
		super();
	}

	/**
	 * @param builder {@link AppointmentBuilder}
	 * @param startDate {@link Date}
	 * @param endDate {@link Date}
	 * @param description String
	 * @param location {@link Location}
	 * @param attendees {@link List}
	 * @return {@link Appointment}
	 * @throws InformationRequiredException Falls was schief geht
	 */
	public Appointment createAppointment(final AppointmentBuilder builder, final Date startDate,
											final Date endDate, final String description,
											final Location location, final List<Contact> attendees)
		throws InformationRequiredException
	{
		AppointmentBuilder b = builder;

		if (b == null)
		{
			b = new AppointmentBuilder();
		}

		builder.buildAppointment();
		builder.buildDates(startDate, endDate);
		builder.buildDescription(description);
		builder.buildAttendees(attendees);
		builder.buildLocation(location);

		return builder.getAppointment();
	}
}
