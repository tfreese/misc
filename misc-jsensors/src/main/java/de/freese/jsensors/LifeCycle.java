/**
 * Created: 26.04.2019
 */

package de.freese.jsensors;

/**
 * @author Thomas Freese
 */
public interface LifeCycle
{
    /**
     * Is Component already started ?
     *
     * @return boolean
     */
    public boolean isStarted();

    /**
     * Start the Component.
     */
    public void start();

    /**
     * Stop the Component.
     */
    public void stop();
}
