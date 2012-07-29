/**
 * Created: 11.12.2011
 */

package de.freese.spring.config.service;

import org.springframework.transaction.annotation.Transactional;

import de.freese.spring.config.dao.IUserDAO;

/**
 * @author Thomas Freese
 */
@Transactional
public class UserService implements IUserService
{
	/**
	 * 
	 */
	private IUserDAO userDAO = null;

	/**
	 * Erstellt ein neues {@link UserService} Object.
	 */
	public UserService()
	{
		super();
	}

	/**
	 * @see de.freese.spring.config.service.IUserService#getUser()
	 */
	@Override
	public String getUser()
	{
		return this.userDAO.loadUser();
	}

	/**
	 * @param userDAO {@link IUserDAO}
	 */
	public void setUserDAO(final IUserDAO userDAO)
	{
		this.userDAO = userDAO;
	}
}
