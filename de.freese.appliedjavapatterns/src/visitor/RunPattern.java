package visitor;

import java.io.File;
import java.util.Iterator;

/**
 * @author Thomas Freese
 */
public class RunPattern
{
	/**
	 * @param arguments String[]
	 */
	public static void main(final String[] arguments)
	{
		System.out.println("Example for the Visitor pattern");
		System.out.println();
		System.out.println("This sample will use a ProjectCostVisitor to calculate");
		System.out.println(" the total amount required to complete a Project.");
		System.out.println();

		System.out.println("Deserializing a test Project for Visitor pattern");
		System.out.println();

		if (!(new File("data.ser").exists()))
		{
			DataCreator.serialize("data.ser");
		}

		Project project = (Project) (DataRetriever.deserializeData("data.ser"));

		System.out
				.println("Creating a ProjectCostVisitor, to calculate the total cost of the project.");
		ProjectCostVisitor visitor = new ProjectCostVisitor();

		visitor.setHourlyRate(100);

		System.out.println("Moving throuhg the Project, calculating total cost");
		System.out.println(" by passing the Visitor to each of the ProjectItems.");
		visitProjectItems(project, visitor);
		System.out.println("The total cost for the project is: " + visitor.getTotalCost());
	}

	/**
	 * @param item {@link ProjectItem}
	 * @param visitor {@link ProjectVisitor}
	 */
	private static void visitProjectItems(final ProjectItem item, final ProjectVisitor visitor)
	{
		item.accept(visitor);

		if (item.getProjectItems() != null)
		{
			Iterator<ProjectItem> subElements = item.getProjectItems().iterator();

			while (subElements.hasNext())
			{
				visitProjectItems(subElements.next(), visitor);
			}
		}
	}
}
