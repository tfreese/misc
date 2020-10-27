/**
 * Created: 26.04.2019
 */

package de.freese.jsensors.lifecycle;

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
     * @return boolean
     */
    public boolean isStopped();

    /**
     *
     */
    public void start();

    /**
     *
     */
    public void stop();
}
