/**
 * Created: 06.11.2011
 */

package de.freese.openstreetmap.model;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

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
	public final TLongObjectMap<OSMNode> nodeMap;

	/**
	 * 
	 */
	public final TLongObjectMap<OSMWay> wayMap;

	/**
	 * Erstellt ein neues {@link OSMModel} Object.
	 */
	public OSMModel()
	{
		super();

		this.nodeMap = new TLongObjectHashMap<>();
		this.wayMap = new TLongObjectHashMap<>();
	}

	/**
	 * Leeren der Maps.
	 */
	public void clear()
	{
		this.nodeMap.clear();
		this.wayMap.clear();
	}

	/**
	 * @return {@link TLongObjectMap}
	 */
	public TLongObjectMap<OSMNode> getNodeMap()
	{
		return this.nodeMap;
	}

	/**
	 * @return {@link TLongObjectMap}
	 */
	public TLongObjectMap<OSMWay> getWayMap()
	{
		return this.wayMap;
	}
}
