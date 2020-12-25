// Created: 24.12.2020
package de.freese.jconky;

import com.sun.javafx.application.PlatformImpl;

/**
 * @author Thomas Freese
 */
public class JConkyLauncher
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Kein Taskbar Icon.
        PlatformImpl.setTaskbarApplication(false);
        // System.setProperty("apple.awt.UIElement", "true");
        // System.setProperty("apple.awt.headless", "true");
        // System.setProperty("java.awt.headless", "true");
        // System.setProperty("javafx.macosx.embedded", "true");
        // java.awt.Toolkit.getDefaultToolkit();

        JConky.main(args);
        // Application.launch(JConky.class, args);
    }
}
