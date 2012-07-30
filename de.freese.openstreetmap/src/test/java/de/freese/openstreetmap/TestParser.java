/**
 * Created: 06.11.2011
 */

package de.freese.openstreetmap;

import de.freese.openstreetmap.OSMParser;
import de.freese.openstreetmap.model.OSMModel;

/**
 * @author Thomas Freese
 */
public class TestParser
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	@SuppressWarnings("boxing")
	public static void main(final String[] args) throws Exception
	{
		OSMParser parser = new OSMParser();

		OSMModel model = parser.parse("braunschweig.zip", "braunschweig.osm");
		System.out.printf("Nodes = %d, Ways = %d\n", model.getNodeMap().size(), model.getWayMap()
				.size());
		model.clear();

		model = parser.parse("xapibeispiel.zip", "xapibeispiel.osm");
		System.out.printf("Nodes = %d, Ways = %d\n", model.getNodeMap().size(), model.getWayMap()
				.size());
		model.clear();
	}
}
