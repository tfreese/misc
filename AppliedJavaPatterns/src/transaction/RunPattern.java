package transaction;

import java.io.IOException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Thomas Freese
 */
public class RunPattern
{
	/**
     * 
     */
	private static Calendar dateCreator = Calendar.getInstance();

	/**
	 * @param year int
	 * @param month int
	 * @param day int
	 * @param hour int
	 * @param minute int
	 * @return {@link Date}
	 */
	public static Date createDate(final int year, final int month, final int day, final int hour,
									final int minute)
	{
		dateCreator.set(year, month, day, hour, minute);

		return dateCreator.getTime();
	}

	/**
	 * @param year int
	 * @param month int
	 * @param day int
	 * @param hour int
	 * @param increment int
	 * @return {@link Date}[]
	 */
	private static Date[] getDates(final int year, final int month, final int day, final int hour,
									final int increment)
	{
		Date[] returnDates = new Date[increment];

		for (int i = 0; i < increment; i++)
		{
			returnDates[i] = createDate(year, month, day, hour + i, 0);
		}

		return returnDates;
	}

	/**
	 * @param remoteUrls String[]
	 * @return {@link AppointmentTransactionParticipant}[]
	 */
	private static AppointmentTransactionParticipant[] lookUpParticipants(final String[] remoteUrls)
	{
		AppointmentTransactionParticipant[] returnValues =
				new AppointmentTransactionParticipant[remoteUrls.length];

		for (int i = 0; i < remoteUrls.length; i++)
		{
			try
			{
				returnValues[i] = (AppointmentTransactionParticipant) Naming.lookup(remoteUrls[i]);
			}
			catch (Exception exc)
			{
				System.out.println("Error using RMI to look up a transaction participant");
			}
		}

		return returnValues;
	}

	/**
	 * @param arguments String[]
	 */
	public static void main(final String[] arguments)
	{
		System.out.println("Example for the Transaction pattern");
		System.out.println("This code example shows how a Transaction can");
		System.out.println(" be applied to support change across a distributed");
		System.out.println(" system. In ths case, a distributed transaction");
		System.out.println(" is used to coordinate the change of dates in");
		System.out.println(" appointment books.");

		System.out.println("Running the RMI compiler (rmic)");
		System.out.println();

		try
		{
			Process p1 = Runtime.getRuntime().exec("rmic AppointmentBook");

			p1.waitFor();
		}
		catch (IOException exc)
		{
			System.err.println("Unable to run rmic utility. Exiting application.");
			System.exit(1);
		}
		catch (InterruptedException exc)
		{
			System.err.println("Threading problems encountered while using the rmic utility.");
		}

		System.out.println("Starting the rmiregistry");
		System.out.println();

		try
		{
			// Process rmiProcess =
			Runtime.getRuntime().exec("rmiregistry");

			Thread.sleep(15000);
		}
		catch (IOException exc)
		{
			System.err.println("Unable to start the rmiregistry. Exiting application.");
			System.exit(1);
		}
		catch (InterruptedException exc)
		{
			System.err.println("Threading problems encountered when starting the rmiregistry.");
		}

		System.out.println("Creating three appointment books");
		System.out.println();
		AppointmentBook apptBookOne = new AppointmentBook();
		AppointmentBook apptBookTwo = new AppointmentBook();
		AppointmentBook apptBookThree = new AppointmentBook();

		System.out.println("Creating appointments");
		System.out.println();
		Appointment apptOne =
				new AppointmentImpl("Swim relay to Kalimantan (or Java)", new ArrayList<Contact>(),
						new LocationImpl("Sidney, Australia"), createDate(2001, 11, 5, 11, 0));
		Appointment apptTwo =
				new AppointmentImpl("Conference on World Patternization", new ArrayList<Contact>(),
						new LocationImpl("London, England"), createDate(2001, 11, 5, 14, 0));
		Appointment apptThree =
				new AppointmentImpl("Society for the Preservation of Java - Annual Outing",
						new ArrayList<Contact>(), new LocationImpl("Kyzyl, Tuva"), createDate(2001,
								11, 5, 10, 0));

		System.out.println("Adding appointments to the appointment books");
		System.out.println();
		apptBookOne.addAppointment(apptThree);
		apptBookTwo.addAppointment(apptOne);
		apptBookOne.addAppointment(apptTwo);
		apptBookTwo.addAppointment(apptTwo);
		apptBookThree.addAppointment(apptTwo);

		System.out.println("AppointmentBook contents:");
		System.out.println();
		System.out.println(apptBookOne);
		System.out.println(apptBookTwo);
		System.out.println(apptBookThree);
		System.out.println();

		System.out.println("Rescheduling an appointment");
		System.out.println();
		System.out.println();
		boolean result =
				apptBookThree.changeAppointment(apptTwo, getDates(2001, 11, 5, 10, 3),
						lookUpParticipants(new String[]
						{
								apptBookOne.getUrl(), apptBookTwo.getUrl(), apptBookThree.getUrl()
						}), 20000L);

		System.out.println("Result of rescheduling was " + result);
		System.out.println("AppointmentBook contents:");
		System.out.println();
		System.out.println(apptBookOne);
		System.out.println(apptBookTwo);
		System.out.println(apptBookThree);
	}
}
