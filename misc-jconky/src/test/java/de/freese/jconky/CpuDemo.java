// Created: 30.11.2020
package de.freese.jconky;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import de.freese.jconky.model.CpuTimes;

/**
 * @author Thomas Freese
 */
public class CpuDemo
{
    /**
     *
     */
    private static final com.sun.management.OperatingSystemMXBean operatingSystemMXBean =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /**
     *
     */
    private static CpuTimes prev = new CpuTimes();

    /**
     * @return {@link CpuTimes}
     */
    private static CpuTimes getCpuTimes()
    {
        // Files.lines(path, cs)
        // Files.readAllLines(path, cs)

        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/stat", StandardCharsets.UTF_8)))
        {
            String line = reader.readLine();

            // "[ ]" = "\\s+" = Whitespace: einer oder mehrere
            String[] splits = line.split("\\s+");

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
        catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // cat /proc/stat
        // cpu 247721 450 70350 2534219 43469 8434 2372 0 0 0
        // cpu0 32043 74 7737 310311 11885 570 673 0 0 0
        // cpu1 26344 48 11872 312233 6875 5683 232 0 0 0
        // [...]
        // Relevant sind jeweils die ersten vier Zahlen, die für User, Nice, System und Idle stehen.
        // Zusammengerechnet geben sie im Beispiel 198.083, wovon der Idle-Wert 158.570 ausmacht, was etwa 80 % des Gesamtwerts entspricht.
        // Die effektive CPU-Auslastung seit Systemstart liegt also bei gerade 20 %.
        //
        // Effektive Auslastung: user nice system idle iowait irq softirq steal guest guest_nice
        //
        // PrevIdle = previdle + previowait
        // Idle = idle + iowait
        //
        // PrevNonIdle = prevuser + prevnice + prevsystem + previrq + prevsoftirq + prevsteal
        // NonIdle = user + nice + system + irq + softirq + steal
        //
        // PrevTotal = PrevIdle + PrevNonIdle
        // Total = Idle + NonIdle
        //
        // # differentiate: actual value minus the previous one
        // totald = Total - PrevTotal
        // idled = Idle - PrevIdle
        //
        // CPU_Percentage = (totald - idled)/totald

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(CpuDemo::showCpuLoad, 1, 1, TimeUnit.SECONDS);
    }

    /**
     *
     */
    private static void showCpuLoad()
    {
        System.out.println("CPU-Load [%] - OperatingSystemMXBean: " + (operatingSystemMXBean.getCpuLoad() * 100D));

        CpuTimes cpuTimes = getCpuTimes();
        System.out.println("CPU-Load [%] - Vorgänger-Rechnung: " + (cpuTimes.getCpuUsage(prev) * 100D));
        prev = cpuTimes;

        System.out.println();
    }
}
