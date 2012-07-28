/**
 * Created: 06.11.2011
 */

package de.freese.openstreetmap;

import de.freese.openstreetmap.model.OSMModel;

/**
 * @author Thomas Freese
 */
public class Main
{
	/**
	 * @param args String[]
	 */
	@SuppressWarnings("boxing")
	public static void main(final String[] args)
	{
		try
		{
			OSMParser parser = new OSMParser();

			OSMModel model = parser.parse("braunschweig.zip", "braunschweig.osm");
			System.out.printf("Nodes = %d, Ways = %d\n", model.getNodeMap().size(), model
					.getWayMap().size());

			new MyFrame(model);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
