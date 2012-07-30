package iterator;

import java.io.PrintStream;
import java.util.Iterator;

/**
 * @author Thomas Freese
 */
public class ListPrinter
{
	/**
	 * @param element {@link Iterating}
	 * @param output {@link PrintStream}
	 */
	public static void printIteratingElement(final Iterating element, final PrintStream output)
	{
		output.println("Printing the element " + element);
		Iterator<?> elements = element.getIterator();

		while (elements.hasNext())
		{
			Object currentElement = elements.next();

			if (currentElement instanceof Iterating)
			{
				printIteratingElement((Iterating) currentElement, output);
				output.println();
			}
			else
			{
				output.println(currentElement);
			}
		}
	}

	/**
	 * @param list {@link ToDoList}
	 * @param output {@link PrintStream}
	 */
	public static void printToDoList(final ToDoList list, final PrintStream output)
	{
		Iterator<?> elements = list.getIterator();

		output.println("  List - " + list + ":");

		while (elements.hasNext())
		{
			output.println("\t" + elements.next());
		}
	}

	/**
	 * @param lotsOfLists {@link ToDoListCollection}
	 * @param output {@link PrintStream}
	 */
	public static void printToDoListCollection(final ToDoListCollection lotsOfLists,
												final PrintStream output)
	{
		Iterator<?> elements = lotsOfLists.getIterator();

		output.println("\"To Do\" List Collection:");

		while (elements.hasNext())
		{
			printToDoList((ToDoList) elements.next(), output);
		}
	}
}
