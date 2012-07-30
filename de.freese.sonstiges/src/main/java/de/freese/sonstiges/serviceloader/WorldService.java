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
	 * Erstellt ein neues {@link WorldService} Object.
	 */
	public WorldService()
	{
		super();
	}

	/**
	 * @see de.freese.sonstiges.serviceloader.IService#getText()
	 */
	@Override
	public String getText()
	{
		return "World";
	}
}
