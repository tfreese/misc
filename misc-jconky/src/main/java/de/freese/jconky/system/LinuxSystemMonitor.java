// Created: 01.12.2020
package de.freese.jconky.system;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.CpuTimes;
import de.freese.jconky.model.HostInfo;
import de.freese.jconky.model.ProcessInfo;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.util.JConkyUtils;

/**
 * @author Thomas Freese
 */
public class LinuxSystemMonitor extends AbstractSystemMonitor
{
    /**
     * /proc/stat: cpu\\s+(.*)
     */
    static final Pattern CPU_JIFFIES_PATTERN = Pattern.compile("cpu\\s+(.*)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    /**
     * /proc/cpuinfo: processor\\s+:\\s+(\\d+)
     */
    static final Pattern CPUINFO_NUM_CPU_PATTERN = Pattern.compile("processor\\s+:\\s+(\\d+)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    /**
     *
     */
    private static final Pattern PROC_DIR_PATTERN = Pattern.compile("([\\d]*)");

    /**
     *
     */
    private static final FilenameFilter PROCESS_DIRECTORY_FILTER = (dir, name) -> {
        File fileToTest = new File(dir, name);

        return fileToTest.isDirectory() && PROC_DIR_PATTERN.matcher(name).matches();
    };

    /**
     * sensors: Core\\s{1}\\d+:.*
     */
    private static final Pattern SENSORS_CORE_PATTERN = Pattern.compile("Core\\s{1}\\d+:.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    /**
     * sensors: Package\\s{1}id\\s{1}\\d+:.*
     */
    private static final Pattern SENSORS_PACKAGE_PATTERN = Pattern.compile("Package\\s{1}id\\s{1}\\d+:.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    /**
     * /proc/stat: cpu\\d+
     */
    protected static final Pattern STAT_NUM_CPU_PATTERN = Pattern.compile("cpu\\d+", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    /**
     * /proc/%s/status: Name:\\s+(\\w+)
     */
    private static final Pattern STATUS_NAME_MATCHER = Pattern.compile("Name:\\s+(\\w+)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    /**
     * /proc/%s/status: Uid:\\s+(\\d+)\\s.*
     */
    private static final Pattern STATUS_UID_MATCHER = Pattern.compile("Uid:\\s+(\\d+)\\s.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    /**
     * /proc/%s/status: VmRSS:\\s+(\\d+) kB<br>
     * residentBytes
     */
    private static final Pattern STATUS_VM_RSS_MATCHER = Pattern.compile("VmRSS:\\s+(\\d+) kB", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    // /**
    // * /proc/%s/status: VmSize:\\s+(\\d+) kB<br>
    // * totalBytes
    // */
    // private static final Pattern STATUS_VM_SIZE_MATCHER = Pattern.compile("VmSize:\\s+(\\d+) kB", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    // private static final Pattern TOTAL_MEMORY_PATTERN =
    // Pattern.compile("MemTotal:\\s+(\\d+) kB", Pattern.MULTILINE);
    // private static final Pattern FREE_MEMORY_PATTERN =
    // Pattern.compile("MemFree:\\s+(\\d+) kB", Pattern.MULTILINE);
    // private static final Pattern BUFFERS_PATTERN =
    // Pattern.compile("Buffers:\\s+(\\d+) kB", Pattern.MULTILINE);
    // private static final Pattern CACHED_PATTERN =
    // Pattern.compile("Cached:\\s+(\\d+) kB", Pattern.MULTILINE);
    // private static final Pattern TOTAL_SWAP_PATTERN =
    // Pattern.compile("SwapTotal:\\s+(\\d+) kB", Pattern.MULTILINE);
    // private static final Pattern FREE_SWAP_PATTERN =
    // Pattern.compile("SwapFree:\\s+(\\d+) kB", Pattern.MULTILINE);
    // private static final Pattern CPU_FREQ_PATTERN =
    // Pattern.compile("model name[^@]*@\\s+([0-9.A-Za-z]*)", Pattern.MULTILINE);
    // private static final Pattern UPTIME_PATTERN =
    // Pattern.compile("([\\d]*).*");
    // private static final Pattern PID_PATTERN =
    // Pattern.compile("([\\d]*).*");
    // private static final Pattern DISTRIBUTION =
    // Pattern.compile("DISTRIB_DESCRIPTION=\"(.*)\"", Pattern.MULTILINE);

    /**
     *
     */
    private final ProcessBuilder processBuilderSensors;

    /**
     *
     */
    private final ProcessBuilder processBuilderTop;

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

        // @formatter:off
        this.processBuilderTop = new ProcessBuilder()
                .command("/bin/sh", "-c", "top -b -n 1") // -u tommy
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

        CpuInfos cpuInfos = new CpuInfos(cpuInfoMap);

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

        // Package
        matcher = SENSORS_PACKAGE_PATTERN.matcher(output);

        if (matcher.find())
        {
            String line = matcher.group();

            String[] splits = SPACE_PATTERN.split(line);

            String temperatureString = splits[3];
            temperatureString = temperatureString.replace("+", "").replace("°C", "");
            double temperature = Double.parseDouble(temperatureString);

            temperatures.put(-1, temperature);
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
     * @see de.freese.jconky.system.SystemMonitor#getProcessInfos(double, long)
     */
    @Override
    public ProcessInfos getProcessInfos(final double uptimeInSeconds, final long totalSystemMemory)
    {
        return getProcessInfosByTop();
        // return getProcessInfosByProc(uptimeInSeconds, totalSystemMemory);
    }

    /**
     * @param uptimeInSeconds double
     * @param totalSystemMemory long
     * @return {@link ProcessInfos}
     */
    ProcessInfos getProcessInfosByProc(final double uptimeInSeconds, final long totalSystemMemory)
    {
        String[] pids = new File("/proc").list(PROCESS_DIRECTORY_FILTER);

        List<ProcessInfo> infos = new ArrayList<>(pids.length);

        for (String pid : pids)
        {
            // /proc/4543/stat
            // 4543 (cinnamon) S 4231 3355 3355 0 -1 4194304 159763 53230 432 4977 11873 3096 1461 181 20 0 12 0 3831 4350136320 79932 18446744073709551615
            // 94314044076032 94314044078533 140729316610576 0 0 0 0 16781312 82952 0 0 0 17 0 0 0 833 0 0 94314044087280 94314044088448 94314055774208
            // 140729316617080 140729316617099 140729316617099 140729316618214 0
            List<String> stat = readContent(String.format("/proc/%s/stat", pid));
            List<String> cmdLine = readContent(String.format("/proc/%s/cmdline", pid));
            List<String> status = readContent(String.format("/proc/%s/status", pid));

            if (stat.isEmpty() || cmdLine.isEmpty() || status.isEmpty())
            {
                // Prozess existiert nicht mehr.
                continue;
            }

            String lineStat = stat.get(0);

            String[] splitsStat = SPACE_PATTERN.split(lineStat);

            // String pid = splits[0];
            String state = splitsStat[2];
            int utimeJiffie = Integer.parseInt(splitsStat[13]); // CPU time spent in user code, measured in clock ticks.
            int stimeJiffie = Integer.parseInt(splitsStat[14]); // CPU time spent in kernel code, measured in clock ticks.
            int cutimeJiffie = Integer.parseInt(splitsStat[15]); // Waited-for children's CPU time spent in user code in clock ticks.
            int cstimeJiffie = Integer.parseInt(splitsStat[13]); // Waited-for children's CPU time spent in kernel code in clock ticks.
            int starttime = Integer.parseInt(splitsStat[21]); // Waited-for children's CPU time spent in kernel code in clock ticks.

            double totalTimeJiffie = (double) utimeJiffie + stimeJiffie;

            // Inklusive Child-Processes.
            totalTimeJiffie += cutimeJiffie + cstimeJiffie;

            double seconds = uptimeInSeconds - JConkyUtils.jiffieToSeconds(starttime);
            double cpuUsage = JConkyUtils.jiffieToSeconds(totalTimeJiffie) / seconds;

            String command = null;

            if (!cmdLine.isEmpty())
            {
                command = cmdLine.get(0);
            }
            else
            {
                command = splitsStat[1];
            }

            command = command.replace("(", "").replace(")", "").replace("\\r", "").replace("\\n", "");

            String statusOutput = status.stream().collect(Collectors.joining("\n"));

            Matcher matcher = STATUS_NAME_MATCHER.matcher(statusOutput);
            String name = null;

            if (matcher.find())
            {
                name = matcher.group(1);
            }
            else
            {
                name = command;
            }

            matcher = STATUS_VM_RSS_MATCHER.matcher(statusOutput);
            long residentBytes = 0L;

            if (matcher.find())
            {
                residentBytes = Long.parseLong(matcher.group(1));
            }

            // matcher = STATUS_VM_SIZE_MATCHER.matcher(status);
            // long totalBytes = 0L;
            //
            // if (matcher.find())
            // {
            // totalBytes = Long.parseLong(matcher.group(1));
            // }

            matcher = STATUS_UID_MATCHER.matcher(statusOutput);
            matcher.find();
            String uid = matcher.group(1);
            String owner = uid;

            ProcessInfo processInfo = new ProcessInfo(Integer.parseInt(pid), state, name, owner, cpuUsage, (double) residentBytes / totalSystemMemory);
            infos.add(processInfo);

            // TODO /etc/passwd auslesen für UIDs.
        }

        ProcessInfos processInfos = new ProcessInfos(infos);

        getLogger().debug(processInfos.toString());

        return processInfos;
    }

    /**
     * @return {@link ProcessInfos}
     */
    ProcessInfos getProcessInfosByTop()
    {
        List<ProcessInfo> infos = new ArrayList<>(300);

        // String output = readContent(this.processBuilderSensors).stream().collect(Collectors.joining("\n"));
        List<String> lines = readContent(this.processBuilderTop);

        // GiB Spch: 15,6 total, 12,4 free, 2,0 used, 1,1 buff/cache
        // GiB Swap: 14,4 total, 14,4 free, 0,0 used. 13,2 avail Spch

        // Bis zur ProzessLise gehen.
        int startIndex = 0;

        for (String line : lines)
        {
            startIndex++;

            if (line.trim().startsWith("PID USER"))
            {
                break;
            }
        }

        for (int i = startIndex; i < lines.size(); i++)
        {
            String line = lines.get(i);

            String[] splits = SPACE_PATTERN.split(line.trim());

            int pid = Integer.parseInt(splits[0]);
            String owner = splits[1];
            double cpuUsage = Double.parseDouble(splits[6].replace(",", "."));
            double memoryUsage = Double.parseDouble(splits[7].replace(",", "."));
            String state = splits[9];
            String name = splits[10];

            if (getMyPid() == pid)
            {
                // jConky wollen wir nicht.
                continue;
            }
            else if ("top".equals(name))
            {
                // top wollen wir nicht.
                continue;
            }
            else if ("java".equals(name))
            {
                // java wollen wir nicht.
                continue;
            }

            ProcessInfo processInfo = new ProcessInfo(pid, state, name, owner, cpuUsage / 100D, memoryUsage / 100D);
            infos.add(processInfo);
        }

        ProcessInfos processInfos = new ProcessInfos(infos);

        getLogger().debug(processInfos.toString());

        return processInfos;
    }

    /**
     * @see de.freese.jconky.system.SystemMonitor#getUptimeInSeconds()
     */
    @Override
    public double getUptimeInSeconds()
    {
        List<String> lines = readContent("/proc/uptime");
        String line = lines.get(0);

        // ArchLinux
        // 1147.04 8069.99

        String[] splits = SPACE_PATTERN.split(line);

        return Double.parseDouble(splits[0]);
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
