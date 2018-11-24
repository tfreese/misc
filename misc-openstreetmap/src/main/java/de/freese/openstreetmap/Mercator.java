package de.freese.openstreetmap;

/**
 * Konvertiert die Längen- und Breitengerade in kartesische XY Koordinaten.<br>
 * Basis ist der "Elliptical Mercator".<br>
 * Quelle: http://wiki.openstreetmap.org/wiki/Mercator<br>
 * Quelle: http://de.wikipedia.org/wiki/Mercator-Projektion
 * 
 * @author Thomas Freese
 */
public class Mercator
{
	/**
	 * Minimaler Erdradius in m.
	 */
	private final static double POLAR_RADIUS = 6356752.3142D;

	/**
	 * Maximaler Erdradius in m.
	 */
	private final static double AEQUATOR_RADIUS = 6378137.0D;

	/**
	 * Minimaler Erdradius in m.
	 */
	private final static double MITTLERER_RADIUS = 6371000.8D;

	/**
	 * POLAR_RADIUS / AEQUATOR_RADIUS
	 */
	private static final double FORMFAKTOR = POLAR_RADIUS / AEQUATOR_RADIUS;

	/**
	 * 1.0D - (FORMFAKTOR * FORMFAKTOR)
	 */
	private static final double ABPLATTUNG = 1.0D - (FORMFAKTOR * FORMFAKTOR);

	/**
	 * Erdumfang in Meter.
	 */
	private static final double ERD_UMFANG = 2.0D * MITTLERER_RADIUS * Math.PI;

	/**
	 * Math.sqrt(ABPLATTUNG)
	 */
	private static final double EXZENTRIZITAET = Math.sqrt(ABPLATTUNG);

	/**
	 * EXZENTRIZITAET / 2.0D
	 */
	@SuppressWarnings("unused")
	private static final double EXZENTRIZITAET_HALBE = EXZENTRIZITAET / 2.0D;

	/**
	 * Math.PI / 2.0D
	 */
	@SuppressWarnings("unused")
	private static final double PI_HALBE = Math.PI / 2.0D;

	/**
	 * Math.PI / 4.0D
	 */
	private static final double PI_VIERTEL = Math.PI / 4.0D;

	/**
	 * Rad = Winkel * (Math.PI / 180.0D) = Math.toRadians(Winkel)
	 */
	private static final double RAD = Math.PI / 180.0D;

	/**
	 * RAD * ERD_UMFANG
	 */
	@SuppressWarnings("unused")
	private static final double RAD_AEQUATOR_RADIUS = RAD * AEQUATOR_RADIUS;

	/**
	 * RAD * ERD_UMFANG
	 */
	private static final double RAD_ERD_UMFANG = RAD * ERD_UMFANG;

	/**
	 * RAD / 2.0D
	 */
	private static final double RAD_HALBE = RAD / 2.0D;

	/**
	 * Breitengrad.<br>
	 * Quelle: http://wiki.openstreetmap.org/wiki/Mercator
	 * 
	 * @param longitude double
	 * @return double
	 */
	public static double mercX(final double longitude)
	{
		double x = 0;

		// Formel nach OpenStreetMap.
		// x = longitude * RAD_AEQUATOR_RADIUS;

		// Formel nach Wiki.
		x = longitude * RAD_ERD_UMFANG;

		return x;
	}

	/**
	 * Längengrad.<br>
	 * Quelle: http://wiki.openstreetmap.org/wiki/Mercator
	 * 
	 * @param latitude double
	 * @return double
	 */
	public static double mercY(final double latitude)
	{
		double m_lat = latitude;

		if (m_lat > 89.5D)
		{
			m_lat = 89.5D;
		}
		if (m_lat < -89.5D)
		{
			m_lat = -89.5D;
		}

		double y = 0;

		// Formel nach OpenStreetMap.
		// double phi = m_lat * RAD;
		// double sinphi = Math.sin(phi);
		// double con = EXZENTRIZITAET * sinphi;
		// con = Math.pow(((1.0D - con) / (1.0D + con)), EXZENTRIZITAET_HALBE);
		// double ts = Math.tan(0.5D * (PI_HALBE - phi)) / con;
		// y = 0 - (AEQUATOR_RADIUS * Math.log(ts));

		// Formel nach Wiki.
		y = Math.log(Math.tan(PI_VIERTEL + (m_lat * RAD_HALBE))) * ERD_UMFANG;

		return y;
	}
}
