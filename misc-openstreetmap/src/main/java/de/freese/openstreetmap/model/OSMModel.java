/**
 * Created: 06.11.2011
 */
package de.freese.openstreetmap.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model mit allen Entitäten.
 *
 * @author Thomas Freese
 */
public class OSMModel
{

    /**
     *
     */
    public final Map<Long, OSMNode> nodeMap;

    /**
     *
     */
    public final Map<Long, OSMWay> wayMap;

    /**
     *
     */
    public final Map<Long, OSMRelation> relationMap;

    /**
     * Erstellt ein neues {@link OSMModel} Object.
     */
    public OSMModel()
    {
        super();

        this.nodeMap = new HashMap<>();
        this.wayMap = new HashMap<>();
        this.relationMap = new HashMap<>();
    }

    /**
     * Leeren der Maps.
     */
    public void clear()
    {
        this.nodeMap.clear();
        this.wayMap.clear();
        this.relationMap.clear();
    }

    /**
     * @return {@link Map}
     */
    public Map<Long, OSMNode> getNodeMap()
    {
        return this.nodeMap;
    }

    /**
     * @return {@link Map}
     */
    public Map<Long, OSMRelation> getRelationMap()
    {
        return this.relationMap;
    }

    /**
     * @return {@link Map}
     */
    public Map<Long, OSMWay> getWayMap()
    {
        return this.wayMap;
    }
}
