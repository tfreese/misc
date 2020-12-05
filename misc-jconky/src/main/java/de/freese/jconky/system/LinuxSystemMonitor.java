// Created: 01.12.2020
package de.freese.jconky.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.CpuTimes;
import de.freese.jconky.model.HostInfo;

/**
 * @author Thomas Freese
 */
public class LinuxSystemMonitor extends AbstractSystemMonitor
{
    /**
    *
    */
    private final ProcessBuilder processBuilderSensors;

    /**
     *
     */
    private final ProcessBuilder processBuilderUname;

    /**
     * Erstellt ein neues {@link LinuxSystemMonitor} Object.
     */
    public LinuxSystemMonitor()
    {
        super();

        // @formatter:off
        this.processBuilderUname = new ProcessBuilder()
                .command("/bin/sh", "-c", "uname -a")
                //.redirectErrorStream(true); // Gibt Fehler auf dem InputStream aus.
                ;
        // @formatter:on

        // @formatter:off
        this.processBuilderSensors = new ProcessBuilder()
                .command("/bin/sh", "-c", "sensors")
                ;
        // @formatter:on
    }

    /**
     * /sys/devices/system/cpu/cpu<N>/cpufreq/scaling_cur_freq
     *
     * @param numCpus int
     * @return {@link Map}
     */
    private Map<Integer, Integer> getCpuFrequencies(final int numCpus)
    {
        Map<Integer, Integer> frequencies = new HashMap<>();

        for (int i = 0; i < numCpus; i++)
        {
            String file = String.format("/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", i);
            List<String> lines = readContent(file);

            // Nur eine Zeile erwartet.
            String line = lines.get(0);

            int frequency = Integer.parseInt(line);

            frequencies.put(i, frequency);
        }

        return frequencies;
    }

    /**
     * @see de.freese.jconky.system.SystemMonitor#getCpuInfos()
     */
    @Override
    public CpuInfos getCpuInfos()
    {
        // String output = readContent("/proc/cpuinfo").stream().collect(Collectors.joining("\n"));
        //
        // int numCpus = 0;
        //
        // Matcher matcher = CPUINFO_NUM_CPU_PATTERN.matcher(output);
        //
        // while (matcher.find())
        // {
        // numCpus++;
        // }

        List<String> lines = readContent("/proc/stat");
        // String output = lines.stream().collect(Collectors.joining("\n"));
        //
        // int numCpus = 0;
        //
        // Matcher matcher = STAT_NUM_CPU_PATTERN.matcher(output);
        //
        // while (matcher.find())
        // {
        // numCpus++;
        // }

        int numCpus = Runtime.getRuntime().availableProcessors();

        // Temperaturen
        Map<Integer, Double> temperatures = getCpuTemperatures();

        // Frequenzen
        Map<Integer, Integer> frequencies = getCpuFrequencies(numCpus);

        Map<Integer, CpuInfo> cpuInfoMap = new HashMap<>();

        // Total Jiffies
        String line = lines.get(0);
        CpuTimes cpuTimes = parseCpuTimes(line);
        CpuInfo cpuInfo = new CpuInfo(-1, temperatures.getOrDefault(-1, 0D), 0, cpuTimes);
        cpuInfoMap.put(cpuInfo.getCore(), cpuInfo);

        // Core Jiffies
        for (int i = 0; i < numCpus; i++)
        {
            line = lines.get(i + 1);

            cpuTimes = parseCpuTimes(line);
            double temperature = temperatures.getOrDefault(i, 0D);
            int frequency = frequencies.getOrDefault(i, 0);

            cpuInfo = new CpuInfo(i, temperature, frequency, cpuTimes);
            cpuInfoMap.put(cpuInfo.getCore(), cpuInfo);
        }

        CpuInfos cpuInfos = new CpuInfos(numCpus, cpuInfoMap);

        getLogger().debug(cpuInfos.get(-1).toString());

        return cpuInfos;
    }

    /**
     * @see de.freese.jconky.system.SystemMonitor#getCpuLoadAvg()
     */
    @Override
    public CpuLoadAvg getCpuLoadAvg()
    {
        List<String> lines = readContent("/proc/loadavg");

        // Nur eine Zeile erwartet.
        String line = lines.get(0);

        // ArchLinux
        // 0.40 0.91 1.09 1/999 73841

        // String[] splits = line.split(SPACE_PATTERN.pattern());
        String[] splits = SPACE_PATTERN.split(line);

        CpuLoadAvg loadAvg = new CpuLoadAvg(Double.valueOf(splits[0]), Double.valueOf(splits[1]), Double.valueOf(splits[2]));

        getLogger().debug(loadAvg.toString());

        return loadAvg;
    }

    /**
     * @return {@link Map}
     */
    private Map<Integer, Double> getCpuTemperatures()
    {
        Map<Integer, Double> temperatures = new HashMap<>();

        String output = readContent(this.processBuilderSensors).stream().collect(Collectors.joining("\n"));

        Matcher matcher = SENSORS_CORE_PATTERN.matcher(output);

        while (matcher.find())
        {
            String line = matcher.group();

            String[] splits = SPACE_PATTERN.split(line);

            String coreString = splits[1];
            coreString = coreString.replace(":", "");
            int core = Integer.parseInt(coreString);

            String temperatureString = splits[2];
            temperatureString = temperatureString.replace("+", "").replace("°C", "");
            double temperature = Double.parseDouble(temperatureString);

            temperatures.put(core, temperature);
        }

        return temperatures;
    }

    /**
     * @see de.freese.jconky.system.SystemMonitor#getHostInfo()
     */
    @Override
    public HostInfo getHostInfo()
    {
        List<String> lines = readContent(this.processBuilderUname);

        // Nur eine Zeile erwartet.
        String line = lines.get(0);

        // ArchLinux
        // Linux mainah 5.9.11-arch2-1 #1 SMP PREEMPT Sat, 28 Nov 2020 02:07:22 +0000 x86_64 GNU/Linux

        // String[] splits = line.split(SPACE_PATTERN.pattern());
        String[] splits = SPACE_PATTERN.split(line);

        HostInfo hostInfo = new HostInfo(splits[1], splits[2], splits[12] + " " + splits[13]);

        getLogger().debug(hostInfo.toString());

        return hostInfo;
    }

    /**
     * @param line String
     * @return {@link CpuTimes}
     */
    private CpuTimes parseCpuTimes(final String line)
    {
        String[] splits = SPACE_PATTERN.split(line);

        long user = Long.parseLong(splits[1]);
        long nice = Long.parseLong(splits[2]);
        long system = Long.parseLong(splits[3]);
        long idle = Long.parseLong(splits[4]);
        long ioWait = Long.parseLong(splits[5]);
        long irq = Long.parseLong(splits[6]);
        long softIrq = Long.parseLong(splits[7]);
        long steal = Long.parseLong(splits[8]);
        long guest = Long.parseLong(splits[9]);
        long guestNice = Long.parseLong(splits[10]);

        CpuTimes cpuTimes = new CpuTimes(user, nice, system, idle, ioWait, irq, softIrq, steal, guest, guestNice);

        return cpuTimes;
    }
}
