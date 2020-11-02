/**
 * Created: 28.08.2015
 */

package de.freese.sonstiges.proxy;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang3.StringUtils;

/**
 * Generator für eine Proxy Blacklist.
 *
 * @author Thomas Freese
 */
public class ProxyBlacklist
{
    /**
     *
     */
    private static class HostComparator implements Comparator<String>
    {
        /**
         *
         */
        public HostComparator()
        {
            super();
        }

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final String o1, final String o2)
        {
            // String[] splits1 = o1.split("[.]");
            // String[] splits2 = o2.split("[.]");
            //
            // int size = Math.min(splits1.length, splits2.length);
            // int comp = 0;
            //
            // String[] s1 = Arrays.copyOfRange(splits1, splits1.length - size, splits1.length);
            // String[] s2 = Arrays.copyOfRange(splits2, splits2.length - size, splits2.length);
            //
            // for (int i = size - 1; i >= 0; i--)
            // {
            // comp = s1[i].compareTo(s2[i]);
            //
            // if (comp != 0)
            // {
            // break;
            // }
            // }
            String s1 = StringUtils.reverse(o1);
            String s2 = StringUtils.reverse(o2);
            int comp = s1.compareTo(s2);

            return comp;
        }
    }

    /**
     *
     */
    private static final File FILE_ADBLOCK_2_PRIVOXY = new File("/home/tommy/dokumente/linux/proxy/adblockplus2privoxy.sh");

    /**
     *
     */
    private static final File FILE_BL_ADBLOCK_DOWNLOAD = new File("/tmp/blacklist-adblock-download.txt");

    /**
     *
     */
    private static final File FILE_BL_COMMON_DOWNLOAD = new File("/tmp/blacklist-download.txt");

    /**
     *
     */
    private static final File FILE_BL_DEFAULT_DOMAIN = new File("/home/tommy/dokumente/linux/proxy/blacklist-domain.txt");

    /**
     *
     */
    private static final File FILE_BL_DEFAULT_REGEX = new File("/home/tommy/dokumente/linux/proxy/blacklist-regex.txt");

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // System.setProperty("proxyHost", "10.252.76.110");
        // System.setProperty("proxyPort", "8080");
        // System.setProperty("http.proxyUser", "...");
        // System.setProperty("http.proxyPassword", "...");

        ProxyBlacklist bl = new ProxyBlacklist();
        bl.buildCommonBlacklist();
        bl.buildPrivoxyBlacklist();
    }

    /**
     * Erstellt ein neues {@link ProxyBlacklist} Object.
     */
    public ProxyBlacklist()
    {
        super();
    }

    /**
     * Erstellt die allgemeine BlackList.
     *
     * @throws Exception Falls was schief geht.
     */
    public void buildCommonBlacklist() throws Exception
    {
        Set<String> blackList = new TreeSet<>(new HostComparator());

        if (FILE_BL_COMMON_DOWNLOAD.exists() && FILE_BL_COMMON_DOWNLOAD.canRead())
        {
            System.out.printf("Load %s%n", FILE_BL_COMMON_DOWNLOAD.getAbsoluteFile());
            load(blackList, FILE_BL_COMMON_DOWNLOAD.toURI().toURL());
        }
        else
        {
            // http://hosts-file.net/
            load(blackList, new URL("http://someonewhocares.org/hosts/hosts"));
            load(blackList, new URL("http://winhelp2002.mvps.org/hosts.txt"));
            load(blackList, new URL("http://blog.murawski.ch/wp-content/uploads/2010/08/blocked.domains.acl"));
            load(blackList, new URL("http://pgl.yoyo.org/adservers/serverlist.php?hostformat=nohtml&showintro=0"));

            // Enthält nur IPs
            // downloadPlain(blackList, "http://myip.ms/files/blacklist/general/latest_blacklist.txt");

            // Mit diesen beiden wächst die Blacklist auf ca. 2,6 Mio. !
            // loadTGZ(blackList, "http://www.shallalist.de/Downloads/shallalist.tar.gz");
            // loadTGZ(blackList, "http://urlblacklist.com/cgi-bin/commercialdownload.pl?type=download&file=bigblacklist");

            writeBlacklist(blackList, FILE_BL_COMMON_DOWNLOAD);
        }

        System.out.printf("BlackList Size: %d%n", blackList.size());
        blackList = filter(blackList);
        System.out.printf("BlackList Size: %d%n", blackList.size());
        writeBlacklist(blackList, new File(FILE_BL_COMMON_DOWNLOAD.getAbsolutePath() + ".filter")); // "/tmp/blacklist-download-filter.txt"
        blackList = ipToHostname(blackList);
        System.out.printf("BlackList Size: %d%n", blackList.size());
        writeBlacklist(blackList, new File(FILE_BL_COMMON_DOWNLOAD.getAbsolutePath() + ".ip2host")); // "/tmp/blacklist-download-ip2host.txt"

        // Default-BlackList dazu laden.
        load(blackList, FILE_BL_DEFAULT_DOMAIN.toURI().toURL());

        // Regex-Liste laden
        Set<String> regexList = new TreeSet<>();
        load(regexList, FILE_BL_DEFAULT_REGEX.toURI().toURL());

        // BlackList mit Regex ausdünnen.
        blackList = validateRegex(blackList, regexList);
        System.out.printf("BlackList Size: %d%n", blackList.size());
        writeBlacklist(blackList, new File(FILE_BL_COMMON_DOWNLOAD.getAbsolutePath() + ".regex"));

        // Neue BlackList schreiben
        writeBlacklist(blackList, FILE_BL_DEFAULT_DOMAIN);
    }

    /**
     * Erstellt die BlackList von AdBlockPlus.
     *
     * @throws Exception Falls was schief geht.
     */
    public void buildPrivoxyBlacklist() throws Exception
    {
        Set<String> easyList = new TreeSet<>();

        if (FILE_BL_ADBLOCK_DOWNLOAD.exists() && FILE_BL_ADBLOCK_DOWNLOAD.canRead())
        {
            System.out.printf("Load %s%n", FILE_BL_ADBLOCK_DOWNLOAD.getAbsoluteFile());
            load(easyList, FILE_BL_ADBLOCK_DOWNLOAD.toURI().toURL());
        }
        else
        {
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/easylist.txt"));
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/easylistgermany.txt"));
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/easyprivacy.txt"));
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/antiadblockfilters.txt"));
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/easyprivacy_nointernational.txt"));
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/easyprivacy.txt"));
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/malwaredomains_full.txt"));
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/fanboy-social.txt"));
            load(easyList, new URL("https://easylist-downloads.adblockplus.org/fanboy-annoyance.txt"));

            writeBlacklist(easyList, FILE_BL_ADBLOCK_DOWNLOAD);
        }

        new ProcessBuilder(FILE_ADBLOCK_2_PRIVOXY.getAbsolutePath(), FILE_BL_ADBLOCK_DOWNLOAD.getAbsolutePath()).start();

        // Blacklist Domain
        Set<String> blackListDomain = new TreeSet<>(new HostComparator());
        load(blackListDomain, FILE_BL_DEFAULT_DOMAIN.toURI().toURL());
        load(blackListDomain, FILE_BL_DEFAULT_REGEX.toURI().toURL());
        load(blackListDomain, new URL("/tmp/privoxy-blacklist-domain.txt"));

        // Blacklist HTTP-Elements
        Set<String> blackListElements = new TreeSet<>();
        load(blackListElements, new URL("/tmp/privoxy-blacklist-elements.txt"));

        // Whitelist Domain
        Set<String> whiteListDomain = new TreeSet<>();
        load(blackListElements, new URL("/tmp/privoxy-whitelist-domain.txt"));

        // Whitelist Images
        Set<String> whiteListImages = new TreeSet<>();
        load(blackListElements, new URL("/tmp/privoxy-whitelist-images.txt"));

        Charset charset = StandardCharsets.UTF_8;

        // Privoxy Filter
        try (PrintWriter writer = new PrintWriter("/tmp/generated.filter", charset))
        {
            writer.println("FILTER: generated Tag Filter for HTML Elements");

            for (String element : blackListElements)
            {
                writer.println(element);
            }
        }

        // Privoxy Action
        try (PrintWriter writer = new PrintWriter("/tmp/generated.action", charset))
        {
            writer.println("{ +block{generated} }");

            for (String domain : blackListDomain)
            {
                writer.println(domain);
            }

            writer.println();
            writer.println("{ +filter{generated} }");
            writer.println("*");
            writer.println();
            writer.println("{ -block }");

            for (String domain : whiteListDomain)
            {
                writer.println(domain);
            }

            writer.println();
            writer.println("{ -block +handle-as-image }");

            for (String image : whiteListImages)
            {
                writer.println(image);
            }
        }
    }

    /**
     * Ausgabe die BlackList.
     *
     * @param blackList {@link Set}
     */
    void dumpBlacklist(final Set<String> blackList)
    {
        int i = 0;

        for (String host : blackList)
        {
            System.out.printf("%4d. %s%n", i++, host);
        }
    }

    /**
     * Filtert die geladene Blacklist.
     *
     * @param blackList {@link Set}
     * @return {@link Set}
     */
    private Set<String> filter(final Set<String> blackList)
    {
        System.out.println("Filter BlackList");
        Set<String> set1 = new HashSet<>();

        // Alles raus was nicht rein soll.
        for (String host : blackList)
        {
            host = StringUtils.chomp(host);
            host = StringUtils.trim(host);

            if (StringUtils.startsWith(host, "#"))
            {
                host = "";
            }
            else if (StringUtils.startsWith(host, "&"))
            {
                host = "";
            }
            else if (StringUtils.startsWith(host, "-"))
            {
                host = "";
            }
            else if (StringUtils.contains(host, "localhost"))
            {
                host = "";
            }
            else if (StringUtils.contains(host, " htpc"))
            {
                host = "";
            }
            else if (StringUtils.contains(host, " raspi"))
            {
                host = "";
            }
            else if (StringUtils.contains(host, "255.255.255.255"))
            {
                host = "";
            }

            set1.add(host);
        }

        blackList.clear();
        set1.remove("");
        Set<String> set2 = new HashSet<>();

        // Bestimmte Inhalte entfernen.
        for (String host : set1)
        {
            host = StringUtils.substringBeforeLast(host, "#");
            host = StringUtils.substringBeforeLast(host, "#");

            if (StringUtils.startsWith(host, "0.0.0.0"))
            {
                host = StringUtils.substringAfter(host, "0.0.0.0");
            }
            else if (StringUtils.startsWith(host, "127.0.0.1"))
            {
                host = StringUtils.substringAfter(host, "127.0.0.1");
            }

            host = StringUtils.replace(host, "\\.", ".");

            host = StringUtils.trim(host);
            set2.add(host);
        }

        set1.clear();
        set2.remove("");

        // Bestimmte Inhalte entfernen.
        for (String host : set2)
        {
            if (StringUtils.startsWith(host, "www."))
            {
                host = StringUtils.substringAfter(host, "www.");
            }

            host = StringUtils.removeStart(host, ".");
            set1.add(host);
        }

        set2.clear();
        set1.remove("");

        Set<String> bl = new TreeSet<>(new HostComparator());
        bl.addAll(set1);

        set1.clear();
        set1 = null;
        set2.clear();
        set2 = null;

        return bl;
    }

    /**
     * Wandelt IP-Addressen in Hostnamen um.<br>
     * Falls das fehl schlägt, wird die IP entfernt.
     *
     * @param blackList {@link Set}
     * @return {@link Set}
     */
    private Set<String> ipToHostname(final Set<String> blackList)
    {
        System.out.println("IP -> Hostname");
        Set<String> bl = new TreeSet<>(new HostComparator());

        for (String host : blackList)
        {
            if (StringUtils.containsOnly(host, ".0123456789"))
            {
                String ip = host;

                try
                {
                    host = InetAddress.getByName(ip).getHostName();
                }
                catch (UnknownHostException ex)
                {
                    // Ignore
                }

                if (StringUtils.isNotBlank(host) && !StringUtils.containsOnly(host, ".0123456789"))
                {
                    System.out.printf("%s -> %s%n", ip, host);
                    bl.add(host.trim());
                }
            }
            else
            {
                bl.add(host.trim());
            }
        }

        blackList.clear();

        return bl;
    }

    /**
     * Laden einer Plaintext Liste.
     *
     * @param set {@link Set}
     * @param url {@link URL}
     * @throws Exception Falls was schief geht.
     */
    private void load(final Set<String> set, final URL url) throws Exception
    {
        System.out.printf("Download %s%n", url);

        try (InputStream is = url.openStream())
        {
            List<String> lines = Files.readAllLines(Paths.get(url.toURI()));
            set.addAll(lines);
        }
        catch (EOFException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Laden einer TGZ komprimierten Datei.
     *
     * @param set {@link Set}
     * @param urlPath String
     * @throws Exception Falls was schief geht.
     */
    void loadTGZ(final Set<String> set, final String urlPath) throws Exception
    {
        System.out.printf("Download %s%n", urlPath);

        URL url = new URL(urlPath);

        try (InputStream is = url.openStream();
             GZIPInputStream gzipIs = new GZIPInputStream(is);
             TarArchiveInputStream tarIs = new TarArchiveInputStream(gzipIs);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(tarIs, StandardCharsets.UTF_8)))
        {
            TarArchiveEntry entry = null;

            try
            {
                while ((entry = (TarArchiveEntry) tarIs.getNextEntry()) != null)
                {
                    if (entry.isFile() && tarIs.canReadEntryData(entry) && entry.getName().endsWith("domains"))
                    {
                        // List<String> lines = IOUtils.readLines(tarIs, StandardCharsets.UTF_8);
                        // set.addAll(lines);
                        bufferedReader.lines().forEach(set::add);
                    }
                }
            }
            catch (EOFException ex)
            {
                System.err.println(ex.getMessage());
            }
        }
    }

    /**
     * Entfernt Hosts, welche durch die Regex-Liste schon erfasst werden.
     *
     * @param blackList {@link Set}
     * @param regexList {@link Set}
     * @return {@link Set}
     */
    private Set<String> validateRegex(final Set<String> blackList, final Set<String> regexList)
    {
        System.out.print("Validate Regex: ");
        Set<String> bl = new TreeSet<>(new HostComparator());

        for (String host : blackList)
        {
            boolean match = false;

            for (String regex : regexList)
            {
                if (!StringUtils.startsWith(host, ".*"))
                {
                    regex = ".*" + regex;
                }

                if (host.matches(regex))
                {
                    match = true;
                    break;
                }
            }

            if (!match)
            {
                bl.add(host);
            }
        }

        blackList.clear();

        return bl;
    }

    /**
     * Speichert die BlackList.
     *
     * @param blackList {@link Set}
     * @param file {@link File}
     * @throws Exception Falls was schief geht.
     */
    private void writeBlacklist(final Set<String> blackList, final File file) throws Exception
    {
        System.out.printf("Write %s%n", file.getAbsoluteFile());

        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8))
        {
            for (String host : blackList)
            {
                writer.printf("%s%n", host);
            }
        }
    }
}
