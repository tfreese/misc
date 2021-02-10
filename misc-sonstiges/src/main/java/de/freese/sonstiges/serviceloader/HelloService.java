/**
 * Created: 17.07.2011
 */

package de.freese.sonstiges.serviceloader;

/**
 * BeispielService.
 *
 * @author Thomas Freese
 */
public class HelloService implements IService
{
    /**
     * @see de.freese.sonstiges.serviceloader.IService#getText()
     */
    @Override
    public String getText()
    {
        return "Hello";
    }
}
