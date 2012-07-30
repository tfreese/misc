package de.freese.sonstiges.preferences.property;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.apache.commons.codec.binary.Base64;

/**
 * Vereinfachte Implementierung der Preferences API OHNE Zugriff auf Systemspezifische Ablagen. Die
 * Nutzung erfolgt durch Import/Export des XML-Dokuments.
 * 
 * @author Thomas Freese
 */
public final class SimplePreferences extends Preferences
{
	/**
	 *
	 */
	private final String absolutePath;

	/**
	 *
	 */
	private final Map<String, SimplePreferences> childs = new HashMap<>();

	/**
	 *
	 */
	private final String name;

	/**
     *
     */
	private NodeChangeListener[] nodeListeners = new NodeChangeListener[0];

	/**
	 *
	 */
	private final SimplePreferences parent;

	/**
     *
     */
	private PreferenceChangeListener[] prefListeners = new PreferenceChangeListener[0];

	/**
	 *
	 */
	private final Map<String, String> properties = new HashMap<>();

	/**
     *
     */
	private boolean removed = false;

	/**
	 * Relativ zu diesem Knoten
	 */
	private final SimplePreferences root;

	/**
	 * Erstellt ein neues {@link SimplePreferences} Object.
	 * 
	 * @param parent {@link SimplePreferences}
	 * @param name {@link String}
	 */
	SimplePreferences(final SimplePreferences parent, final String name)
	{
		super();

		if (parent == null)
		{
			if (!name.equals(""))
			{
				throw new IllegalArgumentException("Root name '" + name + "' must be \"\"");
			}
			this.absolutePath = "/";
			this.root = this;
		}
		else
		{
			if (name.indexOf('/') != -1)
			{
				throw new IllegalArgumentException("Name '" + name + "' contains '/'");
			}
			if (name.equals(""))
			{
				throw new IllegalArgumentException("Illegal name: empty string");
			}

			this.root = parent.root;
			this.absolutePath =
					(parent == this.root ? "/" + name : parent.absolutePath() + "/" + name);
		}

		this.name = name;
		this.parent = parent;
	}

	/**
	 * @see java.util.prefs.Preferences#absolutePath()
	 */
	@Override
	public String absolutePath()
	{
		return this.absolutePath;
	}

	/**
	 * @see java.util.prefs.Preferences#addNodeChangeListener(java.util.prefs.NodeChangeListener)
	 */
	@Override
	public void addNodeChangeListener(final NodeChangeListener ncl)
	{
		if (ncl == null)
		{
			return;
		}

		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		NodeChangeListener[] old = this.nodeListeners;
		this.nodeListeners = new NodeChangeListener[old.length + 1];
		System.arraycopy(old, 0, this.nodeListeners, 0, old.length);
		this.nodeListeners[old.length] = ncl;
	}

	/**
	 * @see java.util.prefs.Preferences#addPreferenceChangeListener(java.util.prefs.PreferenceChangeListener)
	 */
	@Override
	public void addPreferenceChangeListener(final PreferenceChangeListener pcl)
	{
		if (pcl == null)
		{
			return;
		}

		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		PreferenceChangeListener[] old = this.prefListeners;
		this.prefListeners = new PreferenceChangeListener[old.length + 1];
		System.arraycopy(old, 0, this.prefListeners, 0, old.length);
		this.prefListeners[old.length] = pcl;
	}

	/**
	 * @see java.util.prefs.Preferences#childrenNames()
	 */
	@Override
	public String[] childrenNames() throws BackingStoreException
	{
		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		synchronized (this.childs)
		{
			Set<String> childNames = new TreeSet<>(this.childs.keySet());
			return childNames.toArray(new String[0]);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#clear()
	 */
	@Override
	public void clear() throws BackingStoreException
	{
		synchronized (this.properties)
		{
			this.properties.clear();
		}
	}

	/**
	 * @see java.util.prefs.Preferences#exportNode(java.io.OutputStream)
	 */
	@Override
	public void exportNode(final OutputStream os) throws IOException, BackingStoreException
	{
		throw new UnsupportedOperationException("TODO IMPL");
		// XmlSupport.export(os, this, false);
	}

	/**
	 * @see java.util.prefs.Preferences#exportSubtree(java.io.OutputStream)
	 */
	@Override
	public void exportSubtree(final OutputStream os) throws IOException, BackingStoreException
	{
		throw new UnsupportedOperationException("TODO IMPL");
		// XmlSupport.export(os, this, true);
	}

	/**
	 * @param child {@link SimplePreferences}
	 */
	private void fireNodeAddedEvent(final SimplePreferences child)
	{
		NodeChangeEvent event = new NodeChangeEvent(this, child);

		for (NodeChangeListener nodeListener : this.nodeListeners)
		{
			nodeListener.childAdded(event);
		}
	}

	/**
	 * @param child {@link SimplePreferences}
	 */
	private void fireNodeRemovedEvent(final SimplePreferences child)
	{
		NodeChangeEvent event = new NodeChangeEvent(this, child);

		for (NodeChangeListener nodeListener : this.nodeListeners)
		{
			nodeListener.childRemoved(event);
		}
	}

	/**
	 * @param key String
	 * @param newValue String
	 */
	private void firePreferenceChangeEvent(final String key, final String newValue)
	{
		PreferenceChangeEvent event = new PreferenceChangeEvent(this, key, newValue);

		for (PreferenceChangeListener prefListener : this.prefListeners)
		{
			prefListener.preferenceChange(event);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#flush()
	 */
	@Override
	public void flush() throws BackingStoreException
	{
		synchronized (this.childs)
		{
			for (SimplePreferences child : this.childs.values())
			{
				child.flush();
			}
		}
	}

	/**
	 * @see java.util.prefs.Preferences#get(java.lang.String, java.lang.String)
	 */
	@Override
	public String get(final String key, final String def)
	{
		if (key == null)
		{
			throw new NullPointerException("Null key");
		}
		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		synchronized (this.properties)
		{
			String result = null;

			try
			{
				result = this.properties.get(key);
			}
			catch (Exception ex)
			{
				// Ignore
			}

			return (result == null ? def : result);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#getBoolean(java.lang.String, boolean)
	 */
	@Override
	public boolean getBoolean(final String key, final boolean def)
	{
		boolean result = def;

		String value = get(key, null);

		if (value != null)
		{
			if (value.equalsIgnoreCase("true"))
			{
				result = true;
			}
			else if (value.equalsIgnoreCase("false"))
			{
				result = false;
			}
		}

		return result;
	}

	/**
	 * @see java.util.prefs.Preferences#getByteArray(java.lang.String, byte[])
	 */
	@Override
	public byte[] getByteArray(final String key, final byte[] def)
	{
		byte[] result = def;
		String value = get(key, null);

		try
		{
			if (value != null)
			{
				result = Base64.decodeBase64(value);
			}
		}
		catch (Exception ex)
		{
			// Ignore
		}

		return result;
	}

	/**
	 * @see java.util.prefs.Preferences#getDouble(java.lang.String, double)
	 */
	@Override
	public double getDouble(final String key, final double def)
	{
		double result = def;

		try
		{
			String value = get(key, null);

			if (value != null)
			{
				result = Double.parseDouble(value);
			}
		}
		catch (NumberFormatException ex)
		{
			// Ignore
		}

		return result;
	}

	/**
	 * @see java.util.prefs.Preferences#getFloat(java.lang.String, float)
	 */
	@Override
	public float getFloat(final String key, final float def)
	{
		float result = def;

		try
		{
			String value = get(key, null);

			if (value != null)
			{
				result = Float.parseFloat(value);
			}
		}
		catch (NumberFormatException ex)
		{
			// Ignore
		}

		return result;
	}

	/**
	 * @see java.util.prefs.Preferences#getInt(java.lang.String, int)
	 */
	@Override
	public int getInt(final String key, final int def)
	{
		int result = def;

		try
		{
			String value = get(key, null);

			if (value != null)
			{
				result = Integer.parseInt(value);
			}
		}
		catch (NumberFormatException ex)
		{
			// Ignore
		}

		return result;
	}

	/**
	 * @see java.util.prefs.Preferences#getLong(java.lang.String, long)
	 */
	@Override
	public long getLong(final String key, final long def)
	{
		long result = def;

		try
		{
			String value = get(key, null);

			if (value != null)
			{
				result = Long.parseLong(value);
			}
		}
		catch (NumberFormatException ex)
		{
			// Ignore
		}

		return result;
	}

	/**
	 * @see java.util.prefs.Preferences#isUserNode()
	 */
	@Override
	public boolean isUserNode()
	{
		return this.root == Preferences.userRoot();
	}

	/**
	 * @see java.util.prefs.Preferences#keys()
	 */
	@Override
	public String[] keys() throws BackingStoreException
	{
		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		synchronized (this.properties)
		{
			Set<String> childNames = new TreeSet<>(this.properties.keySet());
			return childNames.toArray(new String[0]);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#name()
	 */
	@Override
	public String name()
	{
		return this.name;
	}

	/**
	 * @see java.util.prefs.Preferences#node(java.lang.String)
	 */
	@Override
	public Preferences node(final String pathName)
	{
		synchronized (this.childs)
		{
			if (this.removed)
			{
				throw new IllegalStateException("Node has been removed.");
			}

			if (pathName.equals(""))
			{
				return this;
			}

			if (pathName.equals("/"))
			{
				return this.root;
			}
			if (pathName.charAt(0) != '/')
			{
				return node(new StringTokenizer(pathName, "/", true));
			}
		}

		return this.root.node(new StringTokenizer(pathName.substring(1), "/", true));
	}

	/**
	 * @param path {@link StringTokenizer}
	 * @return {@link SimplePreferences}
	 */
	private SimplePreferences node(final StringTokenizer path)
	{
		String token = path.nextToken();

		if (token.equals("/"))
		{
			throw new IllegalArgumentException("Consecutive slashes in path");
		}

		synchronized (this.childs)
		{
			SimplePreferences child = this.childs.get(token);

			if (child == null)
			{
				child = new SimplePreferences(this, token);

				fireNodeAddedEvent(child);

				this.childs.put(token, child);
			}

			if (!path.hasMoreTokens())
			{
				return child;
			}

			path.nextToken(); // Consume slash

			if (!path.hasMoreTokens())
			{
				throw new IllegalArgumentException("Path ends with slash");
			}

			return child.node(path);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#nodeExists(java.lang.String)
	 */
	@Override
	public boolean nodeExists(final String pathName) throws BackingStoreException
	{
		synchronized (this.childs)
		{
			if (pathName.equals(""))
			{
				return !this.removed;
			}

			if (this.removed)
			{
				throw new IllegalStateException("Node has been removed.");
			}

			if (pathName.equals("/"))
			{
				return true;
			}

			if (pathName.charAt(0) != '/')
			{
				return nodeExists(new StringTokenizer(pathName, "/", true));
			}
		}

		return this.root.nodeExists(new StringTokenizer(pathName.substring(1), "/", true));
	}

	/**
	 * @param path {@link StringTokenizer}
	 * @return boolean
	 * @throws BackingStoreException Falls was schief geht.
	 */
	private boolean nodeExists(final StringTokenizer path) throws BackingStoreException
	{
		String token = path.nextToken();

		if (token.equals("/"))
		{
			throw new IllegalArgumentException("Consecutive slashes in path");
		}

		synchronized (this.childs)
		{
			SimplePreferences child = this.childs.get(token);

			if (child == null)
			{
				return false;
			}

			if (!path.hasMoreTokens())
			{
				return true;
			}
			path.nextToken(); // Consume slash

			if (!path.hasMoreTokens())
			{
				throw new IllegalArgumentException("Path ends with slash");
			}

			return child.nodeExists(path);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#parent()
	 */
	@Override
	public Preferences parent()
	{
		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		return parent();
	}

	/**
	 * @see java.util.prefs.Preferences#put(java.lang.String, java.lang.String)
	 */
	@Override
	public void put(final String key, final String value)
	{
		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		synchronized (this.properties)
		{
			this.properties.put(key, value);
			firePreferenceChangeEvent(key, value);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#putBoolean(java.lang.String, boolean)
	 */
	@Override
	public void putBoolean(final String key, final boolean value)
	{
		put(key, String.valueOf(value));
	}

	/**
	 * @see java.util.prefs.Preferences#putByteArray(java.lang.String, byte[])
	 */
	@Override
	public void putByteArray(final String key, final byte[] value)
	{
		put(key, Base64.encodeBase64String(value));
	}

	/**
	 * @see java.util.prefs.Preferences#putDouble(java.lang.String, double)
	 */
	@Override
	public void putDouble(final String key, final double value)
	{
		put(key, Double.toString(value));
	}

	/**
	 * @see java.util.prefs.Preferences#putFloat(java.lang.String, float)
	 */
	@Override
	public void putFloat(final String key, final float value)
	{
		put(key, Float.toString(value));
	}

	/**
	 * @see java.util.prefs.Preferences#putInt(java.lang.String, int)
	 */
	@Override
	public void putInt(final String key, final int value)
	{
		put(key, Integer.toString(value));
	}

	/**
	 * @see java.util.prefs.Preferences#putLong(java.lang.String, long)
	 */
	@Override
	public void putLong(final String key, final long value)
	{
		put(key, Long.toString(value));
	}

	/**
	 * @see java.util.prefs.Preferences#remove(java.lang.String)
	 */
	@Override
	public void remove(final String key)
	{
		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		synchronized (this.properties)
		{
			this.properties.remove(key);
			firePreferenceChangeEvent(key, null);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#removeNode()
	 */
	@Override
	public void removeNode() throws BackingStoreException
	{
		if (this == this.root)
		{
			throw new UnsupportedOperationException("Can't remove the root!");
		}

		synchronized (this.parent.childs)
		{
			this.parent.childs.remove(this.name);
			this.removed = true;
			this.parent.fireNodeRemovedEvent(this);
		}
	}

	/**
	 * @see java.util.prefs.Preferences#removeNodeChangeListener(java.util.prefs.NodeChangeListener)
	 */
	@Override
	public void removeNodeChangeListener(final NodeChangeListener ncl)
	{
		if (ncl == null)
		{
			return;
		}

		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		NodeChangeListener[] newNl = new NodeChangeListener[this.nodeListeners.length - 1];
		int i = 0;

		while ((i < this.nodeListeners.length) && (this.nodeListeners[i] != ncl))
		{
			newNl[i] = this.nodeListeners[i++];
		}

		if (i == this.nodeListeners.length)
		{
			throw new IllegalArgumentException("Listener not registered.");
		}

		while (i < newNl.length)
		{
			newNl[i] = this.nodeListeners[++i];
		}

		this.nodeListeners = newNl;
	}

	/**
	 * @see java.util.prefs.Preferences#removePreferenceChangeListener(java.util.prefs.PreferenceChangeListener)
	 */
	@Override
	public void removePreferenceChangeListener(final PreferenceChangeListener pcl)
	{
		if (pcl == null)
		{
			return;
		}

		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed.");
		}

		PreferenceChangeListener[] newPl =
				new PreferenceChangeListener[this.prefListeners.length - 1];
		int i = 0;

		while ((i < newPl.length) && (this.prefListeners[i] != pcl))
		{
			newPl[i] = this.prefListeners[i++];
		}

		if ((i == newPl.length) && (this.prefListeners[i] != pcl))
		{
			throw new IllegalArgumentException("Listener not registered.");
		}

		while (i < newPl.length)
		{
			newPl[i] = this.prefListeners[++i];
		}

		this.prefListeners = newPl;
	}

	/**
	 * @see java.util.prefs.Preferences#sync()
	 */
	@Override
	public void sync() throws BackingStoreException
	{
		if (this.removed)
		{
			throw new IllegalStateException("Node has been removed");
		}

		synchronized (this.childs)
		{
			for (SimplePreferences child : this.childs.values())
			{
				child.sync();
			}
		}
	}

	/**
	 * @see java.util.prefs.Preferences#toString()
	 */
	@Override
	public String toString()
	{
		return (isUserNode() ? "User" : "System") + " Preference Node: " + absolutePath();
	}
}
