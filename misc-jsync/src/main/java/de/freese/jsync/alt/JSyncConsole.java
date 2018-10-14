/**
 * Created: 23.10.2016
 */
package de.freese.jsync.alt;

import java.io.Console;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import de.freese.jsync.alt.api.Client;
import de.freese.jsync.alt.impl.ClientImpl;

/**
 * Console-Client für jsync.<br>
 *
 * @author Thomas Freese
 */
public class JSyncConsole
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        if (args.length == 0)
        {
            usage();
        }

        CommandLine line = null;

        try
        {
            CommandLineParser parser = new DefaultParser();
            line = parser.parse(getCommandOptions(), args);
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());

            usage();
        }

        de.freese.jsync.alt.api.Options options = new de.freese.jsync.alt.api.Options();
        options.setChecksum(false);
        options.setDelete(line.hasOption("delete"));
        options.setDryRun(line.hasOption("dry-run"));
        options.setChecksum(line.hasOption("checksum"));

        Console console = System.console();

        if (console != null)
        {
            options.setPrintWriter(console.writer());
        }

        String source = line.getOptionValue("src");
        String destination = line.getOptionValue("dst");

        Client client = new ClientImpl(options);
        client.sync(source, destination);

        System.exit(0);
    }

    /**
     * Liefert die möglichen Optionen der Kommandozeile.<br>
     * Dies sind die JRE Programm Argumente.
     *
     * @return {@link Options}
     */
    private static Options getCommandOptions()
    {
        Options options = new Options();

        OptionGroup groupParams = new OptionGroup();
        groupParams.addOption(Option.builder().longOpt("delete").hasArg(false).desc("Empfänger löscht Dateien vor dem Transfer").build());
        options.addOptionGroup(groupParams);

        options.addOption(Option.builder("n").longOpt("dry-run").desc("Synchronisation nur Simulieren").build());
        options.addOption(Option.builder("c").longOpt("checksum").desc("Zusätzlich Prüfsumme für Vergleich berechnen").build());

        options.addOption(Option.builder("src").longOpt("source").hasArg().argName("DIR").desc("Quell-Verzeichnis").required().build());
        options.addOption(Option.builder("dst").longOpt("destination").hasArg().argName("DIR").desc("Ziel-Verzeichnis").required().build());

        return options;
    }

    /**
     *
     */
    private static void usage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        // formatter.setWidth(120);
        // formatter.printHelp("Addressbook\n", getCommandOptions(), true);

        StringBuilder footer = new StringBuilder();
        footer.append("\n@Thomas Freese");

        formatter.printHelp(120, "jsync [OPTIONS] SOURCE DESTINATION\n", "\nParameter:", getCommandOptions(), footer.toString(), true);

        System.exit(-1);
    }

    /**
     * Erstellt ein neues {@link JSyncConsole} Object.
     */
    public JSyncConsole()
    {
        super();
    }
}
