// Created: 03.12.2020
package de.freese.jconky;

import de.freese.jconky.system.LinuxSystemMonitor;
import de.freese.jconky.system.SystemMonitor;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Thomas Freese
 */
public final class Settings
{
    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class SettingsHolder
    {
        /**
         *
         */
        private static final Settings INSTANCE = new Settings();

        /**
         * Erstellt ein neues {@link SettingsHolder} Object.
         */
        private SettingsHolder()
        {
            super();
        }
    }

    /**
     * @return {@link Settings}
     */
    public static Settings getInstance()
    {
        return SettingsHolder.INSTANCE;
    }

    /**
     *
     */
    private Font font;

    /**
     *
     */
    private final SystemMonitor systemMonitor;

    /**
     * Erstellt ein neues {@link Settings} Object.
     */
    private Settings()
    {
        super();

        this.systemMonitor = new LinuxSystemMonitor();
    }

    /**
     * @return {@link Color}
     */
    public Color getColorText()
    {
        return Color.LIGHTGRAY;
    }

    /**
     * @return {@link Font}
     */
    public Font getFont()
    {
        if (this.font == null)
        {
            // this.font = new Font(getFontName(), getFontSize());
            this.font = Font.font(getFontName(), getFontSize());
        }

        return this.font;
    }

    /**
     * @return int
     */
    public String getFontName()
    {
        return "DejaVu Sans Mono";
    }

    /**
     * @return int
     */
    public int getFontSize()
    {
        return 12;
    }

    /**
     * @return {@link SystemMonitor}
     */
    public SystemMonitor getSystemMonitor()
    {
        return this.systemMonitor;
    }

    /**
     * @return boolean
     */
    public boolean isDebug()
    {
        return true;
    }
}
