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
     * @return boolean
     */
    public boolean isStarted();

    /**
     *
     */
    public void start();

    /**
     *
     */
    public void stop();
}
