// Created: 16.03.2009
package de.freese.sonstiges.preferences.property;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * PropertyImplementierung einer PreferencesFactory.
 *
 * @author Thomas Freese
 */
public final class PropertyPreferencesFactory implements PreferencesFactory
{
    /**
     *
     */
    private static Preferences SYSTEM_ROOT;

    /**
     *
     */
    private static Preferences USER_ROOT;

    /**
     * @param args String[]
     * @throws BackingStoreException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public static void main(final String[] args) throws BackingStoreException, IOException
    {
        System.setProperty("java.util.prefs.PreferencesFactory", PropertyPreferencesFactory.class.getName());

        Preferences p = Preferences.userRoot();// Preferences.userNodeForPackage(List.class);

        for (String s : p.keys())
        {
            System.out.println("p[" + s + "]=" + p.get(s, null));
        }

        p.putBoolean("hi", true);
        p.put("Number", String.valueOf(System.currentTimeMillis()));

        p = p.node("test");
        System.out.println(p.get("user", null));
        p.put("user", "freese");
        System.out.println(new String(p.getByteArray("test", "null".getBytes())));
        p.putByteArray("test", new String("Thomas Freese").getBytes());

        System.out.println("UserRoot = " + p.isUserNode());

        Preferences.userRoot().exportSubtree(System.out);
    }

    /**
     * Erstellt ein neues {@link PropertyPreferencesFactory} Object.
     */
    public PropertyPreferencesFactory()
    {
        super();
    }

    /**
     * @see java.util.prefs.PreferencesFactory#systemRoot()
     */
    @Override
    public Preferences systemRoot()
    {
        if (SYSTEM_ROOT == null)
        {
            SYSTEM_ROOT = new PropertyPreferences();
        }

        return SYSTEM_ROOT;
    }

    /**
     * @see java.util.prefs.PreferencesFactory#userRoot()
     */
    @Override
    public Preferences userRoot()
    {
        if (USER_ROOT == null)
        {
            USER_ROOT = new PropertyPreferences();
        }

        return USER_ROOT;
    }
}
