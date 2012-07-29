package state;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class DataCreator
{
	/**
     * 
     */
	private static final String DEFAULT_FILE = "data.ser";

	/**
     * 
     */
	private static Calendar dateCreator = Calendar.getInstance();

	/**
	 * @return Serializable
	 */
	private static Serializable createData()
	{
		List<Appointment> appointments = new ArrayList<>();
		List<Contact> contacts = new ArrayList<>();

		contacts.add(new ContactImpl("Test", "Subject", "Volunteer", "United Patterns Consortium"));
		Location location1 = new LocationImpl("Punxsutawney, PA");

		appointments.add(new Appointment("Slowpokes anonymous", contacts, location1, createDate(
				2001, 1, 1, 12, 01), createDate(2001, 1, 1, 12, 02)));
		appointments.add(new Appointment("Java focus group", contacts, location1, createDate(2001,
				1, 1, 12, 30), createDate(2001, 1, 1, 14, 30)));
		appointments.add(new Appointment("Something else", contacts, location1, createDate(2001, 1,
				1, 12, 01), createDate(2001, 1, 1, 12, 02)));
		appointments.add(new Appointment("Yet another thingie", contacts, location1, createDate(
				2001, 1, 1, 12, 01), createDate(2001, 1, 1, 12, 02)));

		return (ArrayList<Appointment>) appointments;
	}

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
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		String fileName;

		if (args.length == 1)
		{
			fileName = args[0];
		}
		else
		{
			fileName = DEFAULT_FILE;
		}

		serialize(fileName);
	}

	/**
	 * @param fileName String
	 */
	public static void serialize(final String fileName)
	{
		try
		{
			serializeToFile(createData(), fileName);
		}
		catch (IOException exc)
		{
			exc.printStackTrace();
		}
	}

	/**
	 * @param content {@link Serializable}
	 * @param fileName String
	 * @throws IOException Falls was schief geht
	 */
	private static void serializeToFile(final Serializable content, final String fileName)
		throws IOException
	{
		ObjectOutputStream serOut = new ObjectOutputStream(new FileOutputStream(fileName));

		serOut.writeObject(content);
		serOut.close();
	}
}
