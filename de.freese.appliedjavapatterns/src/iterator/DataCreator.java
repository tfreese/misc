package iterator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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
		ToDoListCollection data = new ToDoListCollectionImpl();
		ToDoList listOne = new ToDoListImpl();
		ToDoList listTwo = new ToDoListImpl();
		ToDoList listThree = new ToDoListImpl();

		listOne.setListName("Daily Routine");
		listTwo.setListName("Programmer hair washing procedure");
		listThree.setListName("Reading List");
		listOne.add("Get up (harder some days than others)");
		listOne.add("Brew cuppa Java");
		listOne.add("Read JVM Times");
		listTwo.add("Lather");
		listTwo.add("Rinse");
		listTwo.add("Repeat");
		listTwo.add("(eventually throw a TooMuchHairConditioner exception)");
		listThree.add("The complete annotated aphorisms of Duke");
		listThree.add("How green was my Java");
		listThree.add("URL, sweet URL");
		data.add(listOne);
		data.add(listTwo);
		data.add(listThree);

		return data;
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
