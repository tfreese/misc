package decorator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class SupportedProjectItem extends ProjectDecorator
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1977145658602654089L;

	/**
     * 
     */
	private List<File> supportingDocuments = new ArrayList<>();

	/**
	 * Creates a new {@link SupportedProjectItem} object.
	 */
	public SupportedProjectItem()
	{
		super();
	}

	/**
	 * Creates a new {@link SupportedProjectItem} object.
	 * 
	 * @param newSupportingDocument {@link File}
	 */
	public SupportedProjectItem(final File newSupportingDocument)
	{
		super();

		addSupportingDocument(newSupportingDocument);
	}

	/**
	 * @param document {@link File}
	 */
	public void addSupportingDocument(final File document)
	{
		if (!this.supportingDocuments.contains(document))
		{
			this.supportingDocuments.add(document);
		}
	}

	/**
	 * @return {@link List}
	 */
	public List<File> getSupportingDocuments()
	{
		return this.supportingDocuments;
	}

	/**
	 * @param document {@link File}
	 */
	public void removeSupportingDocument(final File document)
	{
		this.supportingDocuments.remove(document);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getProjectItem().toString() + EOL_STRING + "\tSupporting Documents: "
				+ this.supportingDocuments;
	}
}
