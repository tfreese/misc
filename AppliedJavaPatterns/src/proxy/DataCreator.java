package proxy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
	 * @return {@link Serializable}
	 */
	private static Serializable createData()
	{
		List<Address> items = new ArrayList<>();

		items.add(new AddressImpl("Home address", "1418 Appian Way", "Pleasantville", "NH", "27415"));
		items.add(new AddressImpl("Resort", "711 Casino Ave.", "Atlantic City", "NJ", "91720"));
		items.add(new AddressImpl("Vacation spot", "90 Ka'ahanau Cir.", "Haleiwa", "HI", "41720"));

		return (ArrayList<Address>) items;
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
	 * @param data {@link Serializable}
	 * @param fileName String
	 * @throws IOException Falls was schief geht
	 */
	private static void serializeToFile(final Serializable data, final String fileName)
		throws IOException
	{
		ObjectOutputStream serOut = new ObjectOutputStream(new FileOutputStream(fileName));

		serOut.writeObject(data);
		serOut.close();
	}
}
