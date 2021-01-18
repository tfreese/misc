/**
 * Created: 17.07.2011
 */

package de.freese.sonstiges.serviceloader;

/**
 * Beispielservice.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface IService
{
    /**
     * @return String
     */
    public String getText();
}
