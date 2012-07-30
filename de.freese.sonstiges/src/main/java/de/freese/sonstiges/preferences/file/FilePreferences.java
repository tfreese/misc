// Created: 27.10.2009
/**
 * 27.10.2009
 */
package de.freese.sonstiges.preferences.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * Preferences implementation that stores to a user-defined file. See FilePreferencesFactory.
 * 
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @version $Id: FilePreferences.java,v 1.2 2009-12-09 16:10:48 tommy Exp $
 */
final class FilePreferences extends AbstractPreferences
{
	/**
	 *
	 */
	private static final Logger log = Logger.getLogger(FilePreferences.class.getName());

	/**
	 *
	 */
	private Map<String, FilePreferences> children;

	/**
	 *
	 */
	private boolean isRemoved = false;

	/**
	 *
	 */
	private Map<String, String> root;

	/**
	 * Erstellt ein neues {@link FilePreferences} Object.
	 * 
	 * @param parent {@link AbstractPreferences}
	 * @param name String
	 */
	FilePreferences(final AbstractPreferences parent, final String name)
	{
		super(parent, name);

		log.finest("Instantiating node " + name);

		this.root = new TreeMap<>();
		this.children = new TreeMap<>();

		try
		{
			sync();
		}
		catch (BackingStoreException ex)
		{
			log.log(Level.SEVERE, "Unable to sync on creation of node " + name, ex);
		}
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#childrenNamesSpi()
	 */
	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException
	{
		Set<String> keys = this.children.keySet();

		return keys.toArray(new String[keys.size()]);
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#childSpi(java.lang.String)
	 */
	@Override
	protected AbstractPreferences childSpi(final String name)
	{
		FilePreferences child = this.children.get(name);

		if ((child == null) || child.isRemoved())
		{
			child = new FilePreferences(this, name);
			this.children.put(name, child);
		}

		return child;
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#flushSpi()
	 */
	@Override
	protected void flushSpi() throws BackingStoreException
	{
		final File file = FilePreferencesFactory.getPreferencesFile();

		synchronized (file)
		{
			Properties p = new Properties();

			try
			{

				StringBuilder sb = new StringBuilder();
				getPath(sb);
				String path = sb.toString();

				if (file.exists())
				{
					p.load(new FileInputStream(file));

					List<String> toRemove = new ArrayList<>();

					// Make a list of all direct children of this node to be removed
					final Enumeration<?> pnen = p.propertyNames();

					while (pnen.hasMoreElements())
					{
						String propKey = (String) pnen.nextElement();

						if (propKey.startsWith(path))
						{
							String subKey = propKey.substring(path.length());

							// Only do immediate descendants
							if (subKey.indexOf('.') == -1)
							{
								toRemove.add(propKey);
							}
						}
					}

					// Remove them now that the enumeration is done with
					for (String propKey : toRemove)
					{
						p.remove(propKey);
					}
				}

				// If this node hasn't been removed, add back in any values
				if (!this.isRemoved)
				{
					for (String s : this.root.keySet())
					{
						p.setProperty(path + s, this.root.get(s));
					}
				}

				p.store(new FileOutputStream(file), "FilePreferences");
			}
			catch (IOException ex)
			{
				throw new BackingStoreException(ex);
			}
		}
	}

	/**
	 * @param sb {@link StringBuilder}
	 */
	private void getPath(final StringBuilder sb)
	{
		final FilePreferences parent = (FilePreferences) parent();

		if (parent == null)
		{
			return;
		}

		parent.getPath(sb);
		sb.append(name()).append('.');
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#getSpi(java.lang.String)
	 */
	@Override
	protected String getSpi(final String key)
	{
		return this.root.get(key);
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#keysSpi()
	 */
	@Override
	protected String[] keysSpi() throws BackingStoreException
	{
		return this.root.keySet().toArray(new String[this.root.keySet().size()]);
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#putSpi(java.lang.String, java.lang.String)
	 */
	@Override
	protected void putSpi(final String key, final String value)
	{
		this.root.put(key, value);

		try
		{
			flush();
		}
		catch (BackingStoreException e)
		{
			log.log(Level.SEVERE, "Unable to flush after putting " + key, e);
		}
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#removeNodeSpi()
	 */
	@Override
	protected void removeNodeSpi() throws BackingStoreException
	{
		this.isRemoved = true;
		flush();
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#removeSpi(java.lang.String)
	 */
	@Override
	protected void removeSpi(final String key)
	{
		this.root.remove(key);

		try
		{
			flush();
		}
		catch (BackingStoreException ex)
		{
			log.log(Level.SEVERE, "Unable to flush after removing " + key, ex);
		}
	}

	/**
	 * @see java.util.prefs.AbstractPreferences#syncSpi()
	 */
	@Override
	protected void syncSpi() throws BackingStoreException
	{
		if (isRemoved())
		{
			return;
		}

		final File file = FilePreferencesFactory.getPreferencesFile();

		if (!file.exists())
		{
			return;
		}

		synchronized (file)
		{
			Properties p = new Properties();

			try
			{
				p.load(new FileInputStream(file));

				StringBuilder sb = new StringBuilder();
				getPath(sb);
				String path = sb.toString();

				final Enumeration<?> pnen = p.propertyNames();

				while (pnen.hasMoreElements())
				{
					String propKey = (String) pnen.nextElement();

					if (propKey.startsWith(path))
					{
						String subKey = propKey.substring(path.length());

						// Only load immediate descendants
						if (subKey.indexOf('.') == -1)
						{
							this.root.put(subKey, p.getProperty(propKey));
						}
					}
				}
			}
			catch (IOException ex)
			{
				throw new BackingStoreException(ex);
			}
		}
	}
}
