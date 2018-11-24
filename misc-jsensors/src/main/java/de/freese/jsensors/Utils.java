// Created: 01.06.2017
package de.freese.jsensors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Util-Klasse.
 *
 * @author Thomas Freese
 */
public class Utils
{
    /**
     *
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     *
     */
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Ausführen eines OS-Commands über {@link Process}.<br>
     * Leerzeilen werden bei der Ausgabe entfernt.
     *
     * @param command String[]
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public static List<String> executeCommand(final String... command) throws Exception
    {
        // @formatter:off
        Process process = new ProcessBuilder()
                //.command("ipconfig", "/all")
                .command(command)
                .redirectErrorStream(true)
                .start();
        // @formatter:on

        Charset charset = StandardCharsets.UTF_8;
        List<String> list = null;

        // try (InputStreamReader isr = new InputStreamReader(process.getInputStream()))
        // {
        // System.out.println(isr.getEncoding());
        // }
        try (BufferedReader readerIn = new BufferedReader(new InputStreamReader(process.getInputStream(), charset)))
        {
            list = readerIn.lines().filter(l -> !l.isEmpty()).collect(Collectors.toList());
        }

        // list.forEach(System.out::println);

        process.waitFor();
        process.destroy();

        return list;
    }

    /**
     * Formatiert den Wert als String.
     *
     * @param value double
     * @return String
     */
    public static String format(final double value)
    {
        // English: Dezimalzeichen = Punkt
        String formatted = String.format(Locale.ENGLISH, "%3.2f", value);

        return formatted;
    }

    /**
     * Formatiert den Wert als String.
     *
     * @param value float
     * @return String
     */
    public static String format(final float value)
    {
        // English: Dezimalzeichen = Punkt
        String formatted = String.format(Locale.ENGLISH, "%3.2f", value);

        return formatted;
    }

    /**
     * Formatiert den Sensornamen als SQL-Tabellenname.<br>
     * Format:<br>
     * <ul>
     * <li>Großbuchstaben
     * <li>' ', '-' werden durch '_' ersetzt
     * </ul>
     *
     * @param sensorName String
     * @return String
     */
    public static String formatSensorTableName(final String sensorName)
    {
        String tableName = sensorName.toUpperCase();
        tableName = tableName.replace("-", "_");
        tableName = tableName.replace(" ", "_");

        return tableName;
    }

    /**
     * @return boolean
     */
    public static boolean isLinux()
    {
        return OS.contains("linux");
    }

    /**
     * @return boolean
     */
    public static boolean isUnix()
    {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    /**
     * @return boolean
     */
    public static boolean isWindows()
    {
        return OS.startsWith("win");
    }

    /**
     * Trimmt den String und entfernt mehrfach vorhanden Leerzeichen.
     *
     * @param value String
     * @return String
     */
    public static String trimAndStripWhitespaces(final String value)
    {
        String s = value.replace("          ", " ");
        s = s.replace("     ", " ");
        s = s.replace("     ", " ");
        s = s.replace("  ", " ");
        s = s.replace("  ", " ");
        s = s.replace("  ", " ");

        return s;
    }

    /**
     * Erzeugt eine neue Instanz von {@link Utils}.
     */
    private Utils()
    {
        super();
    }
}