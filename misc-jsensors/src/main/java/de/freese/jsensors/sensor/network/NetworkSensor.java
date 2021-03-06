// Created: 01.06.2017
package de.freese.jsensors.sensor.network;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.LifeCycle;
import de.freese.jsensors.utils.Utils;

/**
 * Sensor für die Netzwerk Auslastung.<br>
 * Gemessen werden die Anzahl von empfangenen und gesendeten Bytes.<br>
 * Wird bei Linux kein Intrface angegeben, werden alle Interfaces außer "lo" zusammengefasst (ls /sys/class/net).
 *
 * @author Thomas Freese
 */
public class NetworkSensor extends AbstractSensor implements LifeCycle
{
    /**
     *
     */
    private static final Pattern PATTERN_BYTES = Pattern.compile(" bytes (.+?) ", Pattern.UNICODE_CHARACTER_CLASS);

    /**
     * "[ ]" = "\\s+" = Whitespace: einer oder mehrere
     */
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);

    /**
     *
     */
    private final List<String> interfaces = new ArrayList<>();

    /**
     * Erstellt ein neues {@link NetworkSensor} Object.
     */
    public NetworkSensor()
    {
        super();
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getNames()
     */
    @Override
    public List<String> getNames()
    {
        return List.of("network.in", "network.out");
    }

    /**
     * Ausgabe von "ifconfig".<br>
     * /proc/net/dev<br>
     * /sys/class/net/$1/statistics/rx_bytes<br>
     * ifstat<br>
     *
     * @return String[]; Index 0=Input, 1=Output
     * @throws Exception Falls was schief geht.
     */
    protected String[] getNetUsageLinux() throws Exception
    {
        // Utils.executeCommand("ifconfig", "-a");
        long input = 0;
        long output = 0;

        for (String iFace : this.interfaces)
        {
            List<String> lines = Utils.executeCommand("ifconfig", iFace);

            // lines.stream().forEach(System.out::println);
            // lines.stream().map(l -> l.trim()).filter(l -> l.startsWith("RX packets")).forEach(System.out::println);
            // lines.stream().map(l -> l.trim()).filter(l -> l.startsWith("TX packets")).forEach(System.out::println);

            // ArchLinux:
            // RX packets 32997 bytes 46685918 (44.5 MiB)
            // TX packets 15894 bytes 1288395 (1.2 MiB)
            input += lines.stream().map(String::trim).filter(l -> l.startsWith("RX packets")).mapToLong(l -> {
                Matcher matcher = PATTERN_BYTES.matcher(l);

                matcher.find();
                long value = Long.parseLong(matcher.group(1));
                return value;
            }).findFirst().orElse(0L);

            output += lines.stream().map(String::trim).filter(l -> l.startsWith("TX packets")).mapToLong(l -> {
                Matcher matcher = PATTERN_BYTES.matcher(l);

                matcher.find();
                long value = Long.parseLong(matcher.group(1));
                return value;
            }).findFirst().orElse(0L);

            // matcher.find();
            // input += Long.valueOf(matcher.group(1));
            //
            // matcher.find();
            // output += Long.valueOf(matcher.group(1));
        }

        return new String[]
        {
                Long.toString(input), Long.toString(output)
        };
    }

    /**
     * Ausgabe von "netstat -e".<br>
     *
     * @return String[]; Index 0=Input, 1=Output
     * @throws Exception Falls was schief geht.
     */
    protected String[] getNetUsageWindows() throws Exception
    {
        List<String> lines = Utils.executeCommand("netstat", "-e");

        String line = lines.stream().filter(l -> l.startsWith("Bytes") || l.startsWith("Octets")).findFirst().get();
        line = Utils.trimAndStripWhitespaces(line);

        String[] splits = SPACE_PATTERN.split(line);
        String[] values = new String[]
        {
                splits[1], splits[2]
        };

        return values;
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#measureImpl()
     */
    @Override
    protected void measureImpl() throws Exception
    {
        String[] values = null;

        if (Utils.isWindows())
        {
            values = getNetUsageWindows();
        }
        else if (Utils.isLinux())
        {
            values = getNetUsageLinux();
        }
        else
        {
            throw new IllegalStateException("unsupported operation system");
        }

        String bytesInput = values[0];
        String bytesOutput = values[1];

        long timeStamp = System.currentTimeMillis();

        store("network.in", bytesInput, timeStamp);
        store("network.out", bytesOutput, timeStamp);
    }

    /**
     * Setzt das zu beobachtende Netzwerk-Interfrace.<br>
     * Nur für Linux relevant.
     *
     * @param iFace String; optional
     */
    public void setInterface(final String iFace)
    {
        this.interfaces.add(iFace);
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (Utils.isLinux() && this.interfaces.isEmpty())
        {
            // Kein Interface angegeben -> alle ermitteln ausser "lo".
            // Beispiel: em1 lo wlp6so
            List<String> lines = Utils.executeCommand("ls", "/sys/class/net");

            lines.stream().limit(1).map(String::trim).flatMap(l -> Stream.of(SPACE_PATTERN.split(l))).filter(s -> !"lo".equals(s))
                    .forEach(this.interfaces::add);
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        // Empty
    }
}
