package session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class SessionServerDelegate
{
	/**
	 * 
	 */
	private static final long NO_SESSION_ID = 0;

	/**
	 * 
	 */
	private static long nextSessionID = 1;

	/**
	 * 
	 */
	private static List<Contact> contacts = new ArrayList<>();

	/**
	 * 
	 */
	private static List<Address> addresses = new ArrayList<>();

	/**
	 * 
	 */
	private static Map<Long, Contact> editContacts = new HashMap<>();

	/**
	 * @param address {@link Address}
	 * @param sessionID long
	 * @return long
	 * @throws SessionException Falls was schief geht
	 */
	public static long addAddress(final Address address, final long sessionID)
		throws SessionException
	{
		if (sessionID <= NO_SESSION_ID)
		{
			throw new SessionException("A valid session ID is required to add an address",
					SessionException.SESSION_ID_REQUIRED);
		}

		Contact contact = editContacts.get(Long.valueOf(sessionID));

		if (contact == null)
		{
			throw new SessionException("You must select a contact before adding an address",
					SessionException.CONTACT_SELECT_REQUIRED);
		}

		if (addresses.indexOf(address) == -1)
		{
			addresses.add(address);
		}

		contact.addAddress(address);

		return sessionID;
	}

	/**
	 * @param contact {@link Contact}
	 * @param sessionID long
	 * @return long
	 * @throws SessionException Falls was schief geht
	 */
	public static long addContact(final Contact contact, final long sessionID)
		throws SessionException
	{
		long session = sessionID;

		if (session <= NO_SESSION_ID)
		{
			session = getSessionID();
		}

		if (contacts.indexOf(contact) != -1)
		{
			if (!editContacts.containsValue(contact))
			{
				editContacts.put(Long.valueOf(session), contact);
			}
			else
			{
				throw new SessionException(
						"This contact is currently being edited by another user.",
						SessionException.CONTACT_BEING_EDITED);
			}
		}
		else
		{
			contacts.add(contact);
			editContacts.put(Long.valueOf(session), contact);
		}

		return sessionID;
	}

	/**
	 * @param sessionID long
	 * @return long
	 * @throws SessionException Falls was schief geht
	 */
	public static long finalizeContact(final long sessionID) throws SessionException
	{
		if (sessionID <= NO_SESSION_ID)
		{
			throw new SessionException("A valid session ID is required to finalize a contact",
					SessionException.SESSION_ID_REQUIRED);
		}

		Contact contact = editContacts.get(Long.valueOf(sessionID));

		if (contact == null)
		{
			throw new SessionException(
					"You must select and edit a contact before committing changes",
					SessionException.CONTACT_SELECT_REQUIRED);
		}

		editContacts.remove(Long.valueOf(sessionID));

		return NO_SESSION_ID;
	}

	/**
	 * @return {@link List}
	 */
	public static List<Address> getAddresses()
	{
		return addresses;
	}

	/**
	 * @return {@link List}
	 */
	public static List<Contact> getContacts()
	{
		return contacts;
	}

	/**
	 * @return {@link List}
	 */
	public static List<Contact> getEditContacts()
	{
		return new ArrayList<>(editContacts.values());
	}

	/**
	 * @return long
	 */
	private static long getSessionID()
	{
		return nextSessionID++;
	}

	/**
	 * @param address {@link Address}
	 * @param sessionID long
	 * @return long
	 * @throws SessionException Falls was schief geht
	 */
	public static long removeAddress(final Address address, final long sessionID)
		throws SessionException
	{
		if (sessionID <= NO_SESSION_ID)
		{
			throw new SessionException("A valid session ID is required to remove an address",
					SessionException.SESSION_ID_REQUIRED);
		}

		Contact contact = editContacts.get(Long.valueOf(sessionID));

		if (contact == null)
		{
			throw new SessionException("You must select a contact before removing an address",
					SessionException.CONTACT_SELECT_REQUIRED);
		}

		if (addresses.indexOf(address) == -1)
		{
			throw new SessionException("There is no record of this address",
					SessionException.ADDRESS_DOES_NOT_EXIST);
		}

		contact.removeAddress(address);

		return sessionID;
	}
}
