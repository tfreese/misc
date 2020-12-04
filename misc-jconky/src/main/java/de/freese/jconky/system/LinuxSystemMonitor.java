// Created: 01.12.2020
package de.freese.jconky.system;

import java.util.List;
import de.freese.jconky.model.HostInfo;

/**
 * @author Thomas Freese
 */
public class LinuxSystemMonitor extends AbstractSystemMonitor
{
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
}
