package de.freese.sonstiges;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

/**
 * @author Thomas Freese
 */
public class Misc
{
    /**
     * @throws Exception Falls was schief geht.
     */
    static void artistsWithOnlyOneSubdir() throws Exception
    {
        final Path basePath = Paths.get(System.getProperty("user.home"), "mediathek", "musik");

        try (Stream<Path> stream = Files.list(basePath))
        {
            //@formatter:off
            stream.filter(p -> Files.isDirectory(p))
                .filter(p -> {
                    long subFolder = 0 ;

                    try(Stream<Path> subStream = Files.list(p))
                    {
                        subFolder = subStream
                                .filter(p2 -> Files.isDirectory(p2))
                                .count();
                    }
                    catch (Exception ex)
                    {
                        // NOOP
                    }

                    return subFolder == 1;
                })
            .sorted()
            .forEach(System.out::println);
            //@formatter:on
        }
    }

    /**
    *
    */
    static void dateTime()
    {
        System.out.println("01: " + Instant.now()); // UTC time-zone
        System.out.println("02: " + Instant.ofEpochMilli(System.currentTimeMillis()) + "; " + new Date()); // UTC time-zone
        System.out.println("03: " + Clock.system(ZoneId.of("Europe/Berlin")).instant()); // UTC time-zone

        System.out.println("04: " + ZonedDateTime.now());
        System.out.println("05: " + ZonedDateTime.now().toLocalDate());
        System.out.println("06: " + ZonedDateTime.now().toLocalDateTime());
        System.out.println("07: " + ZonedDateTime.now().toLocalTime());

        System.out.println("08: " + Date.from(ZonedDateTime.now().toInstant()));
        System.out.println("09: " + Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        System.out.println("10: " + Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(+2))));
        System.out.println("11: " + ZoneId.systemDefault());
        System.out.println("12: " + ZoneId.of("Europe/Berlin"));

        System.out.println("13: " + LocalTime.now());
        System.out.println("13: " + LocalDate.now());
        System.out.println("14a: " + LocalDateTime.now());
        System.out.println("14b: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()));
        System.out.println("14c: " + LocalDateTime.ofEpochSecond(System.currentTimeMillis() / 1000, 0, ZoneOffset.ofHours(+2)));

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = LocalDate.of(2016, Month.JANUARY, 1).get(weekFields.weekOfWeekBasedYear());
        System.out.println("15: 2016-01-01 - weekOfWeekBasedYear = " + weekNumber);
        weekNumber = LocalDate.of(2016, Month.JANUARY, 1).get(weekFields.weekOfYear());
        System.out.println("16: 2016-01-01 - weekOfYear = " + weekNumber);
        weekNumber = LocalDate.of(2014, 12, 31).get(weekFields.weekOfWeekBasedYear());
        System.out.println("17: 2014-12-31 - weekOfWeekBasedYear = " + weekNumber);
        weekNumber = LocalDate.of(2014, 12, 31).get(weekFields.weekOfYear());
        System.out.println("18: 2014-12-31 - weekOfYear = " + weekNumber);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    static void fileWalker() throws Exception
    {
        final Path path = Paths.get(System.getProperty("user.home"), "mediathek", "musik", "ATC");

        System.out.println("Files.walk");

        //@formatter:off
        Files.walk(path)
//                .filter(p -> !Files.isDirectory(p))
                .filter(p -> !p.toString().toLowerCase().endsWith(".jpg"))
                .filter(p -> !p.toString().toLowerCase().endsWith(".m4b"))
                .sorted()
                .skip(6)
                .limit(100)
                .forEach(System.out::println);
        //@formatter:on
        // .filter(p -> !p.endsWith(".m4b"))

        System.out.printf("%nFiles.list%n");
        Files.list(path).sorted().forEach(System.out::println);

        System.out.printf("%nFiles.newDirectoryStream%n");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path))
        {
            Iterator<Path> iter = stream.iterator();

            while (iter.hasNext())
            {
                Path p = iter.next();
                System.out.println(p);
            }
        }

        System.out.printf("%nFiles.walkFileTree%n");
        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            String indent = "";

            /**
             * @see java.nio.file.SimpleFileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
             */
            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
            {
                if (StringUtils.isNotBlank(this.indent))
                {
                    this.indent = this.indent.substring(0, this.indent.length() - 3);
                }

                return FileVisitResult.CONTINUE;
            }

            /**
             * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
             */
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException
            {
                this.indent = this.indent + "   ";

                // System.out.println(dir);
                // System.out.println(path.relativize(dir));
                //
                // Path target = Paths.get(System.getProperty("user.dir"), "mediathek");
                // System.out.println(target.resolve(path.relativize(dir)));

                return FileVisitResult.CONTINUE;
            }

            /**
             * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
             */
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
            {
                System.out.printf("%s%s%n", this.indent, file);

                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Analog KeePass Passwort-Generator: Generiert ein Passwort mit gegebenen Pattern.<br>
     * <ul>
     * <li>l = lower-case Letters</li>
     * <li>U = upper-case Letters</li>
     * <li>d = digits</li>
     * </ul>
     * Example:<br>
     * Pattern "lllll_UUUUU_dddddd." returns "vrifa_EMFCQ_399671."<br>
     * <br>
     *
     * @param random {@link Random}
     * @param pattern String
     * @return String
     */
    static String generatePW(final Random random, final String pattern)
    {
        Objects.requireNonNull(random, () -> "random is required");
        Objects.requireNonNull(pattern, () -> "pattern is required");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < pattern.length(); i++)
        {
            char c = pattern.charAt(i);

            switch (c)
            {
                case 'l':
                    // Kleinbuchstaben
                    sb.append((char) (97 + random.nextInt(26)));
                    break;

                case 'U':
                    // Großbuchstaben
                    sb.append((char) (65 + random.nextInt(26)));
                    break;

                case 'd':
                    // Zahlen
                    sb.append(random.nextInt(10));
                    break;

                default:
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    static void listDirectories() throws Exception
    {
        Path base = Paths.get(System.getProperty("user.dir"));

        // Liefert alles im Verzeichnis, nicht rekursiv.
        System.out.println();
        DirectoryStream.Filter<Path> filter = (path) -> {
            return Files.isDirectory(path) && !path.getFileName().toString().startsWith(".");
        };

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(base, filter))
        {
            for (Path path : stream)
            {
                System.out.println(path);
            }
        }

        // Liefert alles rekursiv wenn definiert, auch den Root Path.
        System.out.println();
        Files.walk(base, 1).collect(Collectors.toList()).parallelStream().filter(Files::isDirectory).forEach(System.out::println);

        // Liefert alles im Verzeichnis, nicht rekursiv.
        System.out.println();
        Predicate<Path> isDirectory = Files::isDirectory;
        Predicate<Path> isHidden = (p) -> p.getFileName().toString().startsWith(".");

        Files.list(base).filter(isDirectory.and(isHidden.negate())).forEach(System.out::println);
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        // SimpleNamingContextBuilder builder =
        // SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        // builder.bind("java:comp/env/bla", "BlaBla");
        // // builder.activate();
        //
        // Context context = new InitialContext();
        // Object object = context.lookup("java:comp/env/bla");
        // System.out.println(object);
        //
        // builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        // builder.bind("java:comp/env/blo", "BloBlo");
        // object = context.lookup("java:comp/env/blo");
        // System.out.println(object);
        // Collator collator = Collator.getInstance(Locale.GERMAN);
        // collator.setStrength(Collator.PRIMARY);
        //
        // System.out.println("compare: " + collator.compare("4.9", "4.11"));
        // System.out.println((int) '■');
        //
        // System.out.println(InetAddress.getByName("5.157.15.6").getHostName());
        // ***********************************************************************************
        // systemMXBean();

        securityProviders();
    }

    /**
    *
    */
    static void printCharsets()
    {
        System.out.printf("Charsets: Default=%s", Charset.defaultCharset());
        Set<String> sets = Charset.availableCharsets().keySet();
        // Arrays.sort(ids);

        for (String set : sets)
        {
            System.out.println(set);
        }
    }

    /**
    *
    */
    static void printTimeZones()
    {
        System.out.printf("TimeZones: Default=%s", TimeZone.getDefault());
        String[] ids = TimeZone.getAvailableIDs();
        Arrays.sort(ids);

        for (String id : ids)
        {
            System.out.println(id);
        }
    }

    /**
     *
     */
    static void processBuilder()
    {
        try
        {
            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            // Process process = Runtime.getRuntime().exec("ps -ef");
            // Process process = Runtime.getRuntime().exec("ping -c5 weg.de");
            // Process process = new ProcessBuilder().command("df -hT").start();
            final Process process = new ProcessBuilder().command("/bin/sh", "-c", "df | grep lvroot | awk '{print $4}'").start();
            // .directory(directory);
            // .redirectErrorStream(true);

            // p.waitFor();
            final BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            stdInput.lines().forEach(System.out::println);

            // read any errors from the attempted command
            System.out.println("\nHere is the standard error of the command (if any):\n");
            stdError.lines().forEach(System.out::println);

            System.exit(0);
        }
        catch (final IOException ex)
        {
            System.out.println("exception happened - here's what I know: ");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    static void processStartCommand() throws Exception
    {
        // new ProcessBuilder().directory(pomPath.toFile());

        // @formatter:off
        Process process = new ProcessBuilder()
                //.command("ipconfig", "/all")
                .command("netstat", "-a")
                .redirectErrorStream(true)
                .start();
        // @formatter:on

        // Charset charset = Charset.forName("windows-1252");
        Charset charset = StandardCharsets.UTF_8;
        List<String> output = null;

        // try (InputStreamReader isr = new InputStreamReader(process.getInputStream()))
        // {
        // System.out.println(isr.getEncoding());
        // }
        try (BufferedReader readerIn = new BufferedReader(new InputStreamReader(process.getInputStream(), charset)))
        {
            // @formatter:off
            output = readerIn.lines().collect(Collectors.toList());
            // @formatter:on
        }

        process.destroy();

        output.forEach(System.out::println);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    static void react() throws Exception
    {
        Mono.just("Test").map(s -> s + s).subscribe(System.out::println);
        Mono.just("").map(v -> null).onErrorReturn("null value").subscribe(System.out::println);// .onErrorReturn("null value")

        Thread.sleep(200);
        System.out.println();

        // Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        Scheduler scheduler = Schedulers.elastic();
        // Scheduler scheduler = Schedulers.parallel();
        // subscribeOn(Scheduler scheduler)
        Flux.just("Test1", "Test2", "Test3", "Test4").parallel().runOn(scheduler).map(s -> s + s).subscribe(v -> {
            System.out.println(Thread.currentThread().getName() + ": " + v);
        });

        Thread.sleep(200);
        System.out.println();

        Flux.just("Test1", "Test2", "Test3").parallel(2).runOn(scheduler).map(v -> v.endsWith("1") ? null : v).map(s -> s + s).subscribe(v -> {
            System.out.println(Thread.currentThread().getName() + ": " + v);
        }, th -> System.out.println("Exception: " + th)); // , th -> System.out.println("Exception: " + th)

        Thread.sleep(200);
        System.out.println();

        // Hooks.onOperatorDebug();
        Flux.just("Test1", "Test2", "Test3", null).parallel().runOn(scheduler).filter(StringUtils::isNotBlank).map(s -> s + s).doOnError(System.out::println)
                .subscribe(v -> {
                    System.out.println(Thread.currentThread().getName() + ": " + v);
                }); // .doOnError(System.out::println)

        Thread.sleep(200);
        System.out.println();

        // Test mit StepVerifier (io.projectreactor:reactor-test)
        Flux<String> source = Flux.just("foo", "bar");
        source = source.concatWith(Mono.error(new IllegalArgumentException("boom")));

        StepVerifier.create(source).expectNext("foo").expectNext("bar").expectErrorMessage("boom").verify();
    }

    /**
     *
     */
    static void regEx()
    {
        System.out.printf("102.112.207.net: %s%n", "102.112.207.net".matches(".*2[0oO]7\\.net"));
        System.out.printf("102.112.2o7.net: %s%n", "102.112.2o7.net".matches(".*2(0|o|O)7\\.net"));
        System.out.printf("102.122.2O7.net: %s%n", "102.122.2O7.net".matches(".*2(0|o|O)7\\.net"));
    }

    /**
     *
     */
    static void replace()
    {
        String text = "ab\"cd'ef \\ ";

        text = text.replaceAll("[\"]", "\\\""); // " -> \"
        System.out.println(text);

        text = text.replace("\"", "\\\""); // " -> \"
        System.out.println(text);

        text = text.replace("\'", "\\\'"); // ' -> \'
        System.out.println(text);

        text = text.replace(" \\ ", ""); // ' \ ' -> ''
        System.out.println(text);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    static void rrd() throws Exception
    {
        // try (RandomAccessFile raf = new RandomAccessFile("file.dat", "rw"))
        // {
        // // Erstellt leere Datei fester Größe.
        // raf.setLength(8 * 1024);
        // }

        // String file = System.getProperty("user.home") + File.separator + "mapped.dat";
        Path path = Paths.get(System.getProperty("user.home"), "mapped.dat");

        // try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
        // FileChannel fileChannel = raf.getChannel())
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE))
        {
            long fileSize = 8 * 1024; // 8 kB

            // Bereich der Datei im Buffer mappen.
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);

            buffer.putInt(1305); // Erster Eintrag
            buffer.putInt(8, 1305); // Dritter Eintrag, absolute Position

            buffer.position(0); // An den Anfang setzen

            // while (buffer.hasRemaining())
            // {
            // // Würde den kompletten Buffer (8 kB) auslesen.
            // System.out.println(buffer.getInt());
            // }
            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());

            buffer.force();
            fileChannel.force(true);
        }

        System.out.println();

        // Einzel int-Read mit DataInputStream.
        try (DataInputStream dis = new DataInputStream(Files.newInputStream(path)))
        {
            System.out.println(dis.readInt());
            dis.skip(4);
            System.out.println(dis.readInt());
        }

        System.out.println();

        // Multi int-Read mit MappedByteBuffer.
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ))
        {
            // Bereich der Datei im Buffer mappen, nur die ersten 12 Bytes = 3 Integers.
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, 12);

            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
        }

        System.out.println();

        // Einzel int-Read mit ByteBuffer (allocate).
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ))
        {
            ByteBuffer buffer = ByteBuffer.allocate(4);

            fileChannel.read(buffer);
            buffer.flip();
            System.out.println(buffer.getInt());

            buffer.clear();
            fileChannel.read(buffer);
            buffer.flip();
            System.out.println(buffer.getInt());

            buffer.clear();
            fileChannel.read(buffer);
            buffer.flip();
            System.out.println(buffer.getInt());
        }

        System.out.println();

        // Multi int-Read mit ByteBuffer (allocate).
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ))
        {
            // Nur die ersten 12 Bytes = 3 Integers.
            ByteBuffer buffer = ByteBuffer.allocate(12);

            fileChannel.read(buffer);
            buffer.flip();

            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
        }
    }

    /**
     *
     */
    static void securityProviders()
    {
        for (Provider provider : Security.getProviders())
        {
            System.out.printf(" --- Provider %s, version %s --- %n", provider.getName(), provider.getVersionStr());

            Set<Service> services = provider.getServices();

            for (Service service : services)
            {
                if (service.getType().equalsIgnoreCase(MessageDigest.class.getSimpleName()))
                {
                    System.out.printf("Algorithm name: \"%s\"%n", service.getAlgorithm());
                }
            }

            System.out.println();
        }
    }

    /**
    *
    */
    static void splitList()
    {
        List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Map<Integer, List<Integer>> groups = intList.stream().collect(Collectors.groupingBy(s -> (s - 1) / 3));
        List<List<Integer>> subSets = new ArrayList<>(groups.values());

        subSets.forEach(list -> {
            System.out.println("\nSub-List:");
            list.forEach(System.out::println);
        });
    }

    // TODO
    /**
     * @throws Exception Falls was schief geht.
     */
    static void systemMXBean() throws Exception
    {
        System.out.println("\nOperatingSystemMXBean");

        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) operatingSystemMXBean;

        System.out.println("\tArch: " + os.getArch());
        System.out.println("\tName: " + os.getName());
        System.out.println("\tVersion: " + os.getVersion());
        System.out.println("\tAvailableProcessors: " + os.getAvailableProcessors());
        System.out.println("\tCommittedVirtualMemorySize: " + os.getCommittedVirtualMemorySize());
        System.out.println("\tFreePhysicalMemorySize(: " + os.getFreePhysicalMemorySize());
        System.out.println("\tFreeSwapSpaceSize: " + os.getFreeSwapSpaceSize());
        System.out.println("\tProcessCpuLoad: " + os.getProcessCpuLoad());
        System.out.println("\tProcessCpuTime: " + os.getProcessCpuTime());
        System.out.println("\tSystemCpuLoad: " + os.getSystemCpuLoad());
        System.out.println("\tSystemLoadAverage: " + os.getSystemLoadAverage());
        System.out.println("\tTotalPhysicalMemorySize: " + os.getTotalPhysicalMemorySize());
        System.out.println("\tTotalSwapSpaceSize: " + os.getTotalSwapSpaceSize());

        long lastSystemTime = 0;
        long lastProcessCpuTime = 0;

        long systemTime = System.nanoTime();
        long processCpuTime = os.getProcessCpuTime();
        double cpuUsage = ((double) (processCpuTime - lastProcessCpuTime)) / ((double) (systemTime - lastSystemTime));
        System.out.println("\tcpuUsage: " + cpuUsage);

        lastSystemTime = systemTime;
        lastProcessCpuTime = processCpuTime;

        Thread.sleep(5000);

        systemTime = System.nanoTime();
        processCpuTime = os.getProcessCpuTime();
        cpuUsage = ((double) (processCpuTime - lastProcessCpuTime)) / ((double) (systemTime - lastSystemTime));
        System.out.println("\tcpuUsage: " + cpuUsage);
    }
}
