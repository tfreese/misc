/**
 * Created: 06.11.2011
 */

package de.freese.openstreetmap;

import javax.swing.SwingUtilities;
import de.freese.openstreetmap.io.IOSMParser;
import de.freese.openstreetmap.io.XMLStreamOSMParser;
import de.freese.openstreetmap.model.OSMModel;

/**
 * @author Thomas Freese
 */
public class OpenStreetMapMain
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        try
        {
            // IOSMParser parser = new JdomOSMParser();
            // IOSMParser parser = new SaxOSMParser();
            IOSMParser parser = new XMLStreamOSMParser();

            OSMModel model = parser.parse("braunschweig.zip", "braunschweig.osm");
            // OSMModel model = parser.parse("xapibeispiel.zip", "xapibeispiel.osm");
            System.out.printf("Nodes = %d, Ways = %d, Realtions = %d%n", model.getNodeMap().size(), model.getWayMap().size(), model.getRelationMap().size());

            MyFrame myFrame = new MyFrame(model);
            myFrame.initGui();
            myFrame.setSize(800, 800);
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);

            SwingUtilities.invokeLater(myFrame::zoomToFit);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
