// Created: 01.06.2017
package de.freese.jsensors.sensor.network;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.Utils;

/**
 * Sensor für die Netzwerk Auslastung.<br>
 * Gemessen werden die Anzahl von empfangenen und gesendeten Bytes.<br>
 * Wird bei Linux kein Intrface angegeben, werden alle Interfaces außer "lo" zusammengefasst (ls /sys/class/net).
 *
 * @author Thomas Freese
 */
public class NetworkUsage extends AbstractSensor
{
    /**
     *
     */
    private static final Pattern PATTERN_BYTES = Pattern.compile(" bytes (.+?) ");

    /**
     *
     */
    private final List<String> interfaces = new ArrayList<>();

    /**
     * @see de.freese.jsensors.lifecycle.AbstractLifeCycle#onStart()
     */
    @Override
    protected void onStart() throws Exception
    {
        if (Utils.isLinux() && this.interfaces.isEmpty())
        {
            // Kein Interface angegeben -> alle ermitteln ausser "lo".
            // Beispiel: em1 lo wlp6so
            List<String> lines = Utils.executeCommand("ls", "/sys/class/net");

            lines.stream().limit(1).map(l -> l.trim()).flatMap(l -> Stream.of(l.split("[ ]"))).filter(s -> !s.equals("lo")).forEach(this.interfaces::add);
        }
    }

    /**
     * Ausgabe von "ifconfig".<br>
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
            input += lines.stream().map(l -> l.trim()).filter(l -> l.startsWith("RX packets")).mapToLong(l -> {
                Matcher matcher = PATTERN_BYTES.matcher(l);

                matcher.find();
                long value = Long.parseLong(matcher.group(1));
                return value;
            }).findFirst().orElse(0L);

            output += lines.stream().map(l -> l.trim()).filter(l -> l.startsWith("TX packets")).mapToLong(l -> {
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

        String[] splits = line.split("[ ]");
        String[] values = new String[]
        {
                splits[1], splits[2]
        };

        return values;
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#scanValue()
     */
    @Override
    protected void scanValue() throws Exception
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

        save(bytesInput, timeStamp, getName() + "-in");
        save(bytesOutput, timeStamp, getName() + "-out");
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
}
