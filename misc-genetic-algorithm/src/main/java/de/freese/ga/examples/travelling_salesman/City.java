// Erzeugt: 26.08.2015
package de.freese.ga.examples.travelling_salesman;

/**
 * Genom Value
 *
 * @author Thomas Freese
 */
public class City
{
    /**
     *
     */
    private final String name;
    /**
     *
     */
    private final int x;

    /**
     *
     */
    private final int y;

    /**
     * Erstellt ein neues {@link City} Object.
     *
     * @param name String
     * @param x int
     * @param y int
     */
    public City(final String name, final int x, final int y)
    {
        super();

        this.name = name;
        this.x = x;
        this.y = y;
    }

    /**
     * @param city {@link City}
     * @return double
     */
    public double distanceTo(final City city)
    {
        double xDistance = Math.abs(getX() - city.getX());
        double yDistance = Math.abs(getY() - city.getY());
        double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

        return distance;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return int
     */
    public int getX()
    {
        return this.x;
    }

    /**
     * @return int
     */
    public int getY()
    {
        return this.y;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append("(").append(getX()).append(":").append(getY()).append(")");

        return sb.toString();
    }
}
