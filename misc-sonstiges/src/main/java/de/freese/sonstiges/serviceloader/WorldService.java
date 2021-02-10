/**
 * Created: 17.07.2011
 */

package de.freese.sonstiges.serviceloader;

/**
 * BeispielService.
 *
 * @author Thomas Freese
 */
public class WorldService implements IService
{
    /**
     * @see de.freese.sonstiges.serviceloader.IService#getText()
     */
    @Override
    public String getText()
    {
        return "World";
    }
}
