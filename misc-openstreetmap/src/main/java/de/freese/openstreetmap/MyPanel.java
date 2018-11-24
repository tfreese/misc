package de.freese.openstreetmap;

import de.freese.openstreetmap.model.OSMModel;
import de.freese.openstreetmap.model.OSMWay;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 * @author Thomas Freese
 */
public class MyPanel extends JPanel
{

    /**
     *
     */
    private static final long serialVersionUID = -1375597059859723042L;

    /**
     *
     */
    private final OSMModel model;

    /**
     *
     */
    private Matrix myZTFMatrix = null;

    /**
     *
     */
    private Rectangle mapBounds = null;

    /**
     * Erstellt ein neues {@link MyPanel} Object.
     *
     * @param model {@link OSMModel}
     */
    public MyPanel(final OSMModel model)
    {
        super();

        this.model = model;
    }

    /**
     * Ermittelt die gemeinsame BoundingBox der uebergebenen Polygone.
     *
     * @return Die BoundingBox
     */
    public Rectangle getMapBounds()
    {
        if (this.mapBounds == null)
        {
            for (OSMWay osmWay : this.model.getWayMap().values())
            {
                if (this.mapBounds == null)
                {
                    this.mapBounds = new Rectangle(osmWay.getBounds());
                    continue;
                }

                this.mapBounds = this.mapBounds.union(osmWay.getBounds());
            }
            // for (OSMRelation osmRelation : this.model.getRelationMap().valueCollection())
            // {
            // if (this.mapBounds == null)
            // {
            // this.mapBounds = new Rectangle(osmRelation.getBounds());
            // continue;
            // }
            //
            // this.mapBounds = this.mapBounds.union(osmRelation.getBounds());
            // }
        }

        return this.mapBounds;
    }

    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        if (this.model.getWayMap().isEmpty())
        {
            return;
        }

        if (this.myZTFMatrix == null)
        {
            return;
        }

        for (OSMWay osmWay : this.model.getWayMap().values())
        {
            Polygon polyToDraw = osmWay.getDrawablePolygon(this.myZTFMatrix);
            g.drawPolygon(polyToDraw);
        }
        // for (OSMRelation osmRelation : this.model.getRelationMap().valueCollection())
        // {
        // Polygon polyToDraw = osmRelation.getDrawablePolygon(this.myZTFMatrix);
        // g.drawPolygon(polyToDraw);
        // }
    }

    /**
     * Veraendert die interne Transformationsmatrix so, dass die zu zeichnenden Objekt horizontal verschoben werden.
     *
     * @param _delta Die Strecke, um die verschoben werden soll
     */
    public void scrollHorizontal(final int _delta)
    {
        Matrix transMat = Matrix.translate(_delta, 0);
        this.myZTFMatrix = transMat.multiply(this.myZTFMatrix);
    }

    /**
     * Veraendert die interne Transformationsmatrix so, dass die zu zeichnenden Objekt horizontal verschoben werden.
     *
     * @param _delta Die Strecke, um die verschoben werden soll
     */
    public void scrollVertical(final int _delta)
    {
        Matrix transMat = Matrix.translate(0, _delta);
        this.myZTFMatrix = transMat.multiply(this.myZTFMatrix);
    }

    /**
     * Veraendert die interne Transformationsmatrix so, dass in das Zentrum des Anzeigebereiches herein- bzw.
     * herausgezoomt wird
     *
     * @param factor Der Faktor um den herein- bzw. herausgezoomt wird
     */
    public void zoom(final double factor)
    {
        Point center = new Point(getWidth() / 2, getHeight() / 2);
        this.myZTFMatrix = Matrix.zoomPoint(this.myZTFMatrix, center, factor);

        // Matrix scaleMat = Matrix.scale(factor);
        // this.myZTFMatrix = scaleMat.multiply(this.myZTFMatrix);
    }

    /**
     * Stellt intern eine Transformationsmatrix zur Verfuegung, die so skaliert, verschiebt und spiegelt, dass die zu
     * zeichnenden Polygone komplett in den
     * Anzeigebereich passen
     */
    public void zoomToFit()
    {
        Rectangle bounds = getMapBounds();
        this.myZTFMatrix = Matrix.zoomToFit(bounds, new Rectangle(getWidth() - 2, getHeight() - 2));
    }
}
