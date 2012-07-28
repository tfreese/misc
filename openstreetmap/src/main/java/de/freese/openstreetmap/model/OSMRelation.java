// /**
// * Created: 06.11.2011
// */
//
// package de.freese.openstreetmap.model;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
// * Relationen gruppieren {@link OSMNode} und {@link OSMWay} zu größeren Entitäten.
// *
// * @author Thomas Freese
// */
// public class OSMRelation extends AbstractOSMModel
// {
// /**
// *
// */
// public List<OSMNode> nodes = null;
//
// /**
// *
// */
// public List<OSMWay> ways = null;
//
// /**
// * Erstellt ein neues {@link OSMRelation} Object.
// */
// public OSMRelation()
// {
// super();
// }
//
// /**
// * @return {@link List}<OSMNode>
// */
// public List<OSMNode> getNodes()
// {
// if (this.nodes == null)
// {
// this.nodes = new ArrayList<>();
// }
//
// return this.nodes;
// }
//
// /**
// * @return {@link List}<OSMWay>
// */
// public List<OSMWay> getWays()
// {
// if (this.ways == null)
// {
// this.ways = new ArrayList<>();
// }
//
// return this.ways;
// }
// }
