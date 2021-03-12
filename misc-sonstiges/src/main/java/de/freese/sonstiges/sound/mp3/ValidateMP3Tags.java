/**
 * Created: 28.09.2013
 */
package de.freese.sonstiges.sound.mp3;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.datatype.Artwork;

/**
 * @author Thomas Freese
 */
public class ValidateMP3Tags
{
    // private static final List<FieldKey> KEYS_UNUSED = Arrays.asList(FieldKey.COMMENT, FieldKey.COMPOSER, FieldKey.ORIGINAL_ARTIST,
    // FieldKey.URL_OFFICIAL_ARTIST_SITE, FieldKey.ENCODER, FieldKey.ORIGINAL_ARTIST);

    /**
     * @param reports {@link Map}
     * @param file {@link File}
     * @param text String
     */
    private static void addReport(final Map<File, Report> reports, final File file, final String text)
    {
        // Report report = reports.putIfAbsent(file, new Report(file));
        Report report = reports.get(file);

        if (report == null)
        {
            report = new Report(file);
            reports.put(file, report);
        }

        report.addMessage(text);
    }

    /**
     * Prüfen ob die Tags inhalte haben.
     *
     * @param audioFile {@link AudioFile}
     * @param reports {@link Map}
     * @param keys {@link List}
     */
    private static void containsFlag(final AudioFile audioFile, final Map<File, Report> reports, final List<FieldKey> keys)
    {
        Tag tag = audioFile.getTag();

        for (FieldKey key : keys)
        {
            for (TagField field : tag.getFields(key))
            {
                if (!(field instanceof TagTextField))
                {
                    continue;
                }

                String value = null;

                try
                {
                    TagTextField textField = (TagTextField) field;
                    value = textField.getContent();
                }
                catch (NullPointerException ex)
                {
                    // Ignore
                }

                if ((value == null) || value.isBlank())
                {
                    continue;
                }

                if (FieldKey.ENCODER.equals(key) && audioFile.getFile().getName().toLowerCase().endsWith("flac"))
                {
                    // Bei FLAC steht immer die Bibliothek drin.
                    continue;
                }

                addReport(reports, audioFile.getFile(), key.name());
            }
        }
    }

    /**
     * Prüft die Tags auf String-Vorkommen..
     *
     * @param directory {@link Path}
     * @param reports {@link Map}
     * @param fields {@link List}
     * @param text String[]
     * @throws Exception Falls was schief geht.
     */
    private static void containsText(final Path directory, final Map<File, Report> reports, final List<FieldKey> fields, final String...text) throws Exception
    {
        Predicate<String> textFilter = s -> {
            if ((s == null) || s.isEmpty())
            {
                return false;
            }

            for (String t : text)
            {
                if (s.contains(t))
                {
                    return true;
                }
            }

            return false;
        };

        // Predicate<TagField> filter = t -> false;
        Consumer<AudioFile> consumer = af -> {
            Tag tag = af.getTag();

            for (FieldKey field : fields)
            {
                for (TagField tagField : tag.getFields(field))
                {
                    if (!(tagField instanceof TagTextField))
                    {
                        continue;
                    }

                    String value = ((TagTextField) tagField).getContent();

                    if (textFilter.test(value))
                    {
                        addReport(reports, af.getFile(), "containsText");
                    }
                }
            }

        };

        // Predicate<AudioFile> filter = af -> false;
        // filter = filter.or(af -> af.getTag().getFields(FieldKey.ARTIST).c);
        walk(directory, audioFile -> {

            try
            {
                consumer.accept(audioFile);
            }
            catch (RuntimeException rex)
            {
                throw rex;
            }
            catch (Exception ex)
            {
                RuntimeException re = new RuntimeException(ex);
                re.setStackTrace(ex.getStackTrace());

                throw re;
            }
        });
    }

    /**
     * Prüfen ob Cover vorhanden sind.
     *
     * @param audioFile {@link AudioFile}
     * @param reports {@link Map}
     */
    static void existCovers(final AudioFile audioFile, final Map<File, Report> reports)
    {
        Tag tag = audioFile.getTag();
        List<Artwork> artworks = tag.getArtworkList();

        if ((artworks == null) || artworks.isEmpty())
        {
            return;
        }

        if (artworks.size() > 1)
        {
            addReport(reports, audioFile.getFile(), "mehrere cover");
        }

        // String value = tag.getFirst(FieldKey.COVER_ART);
        //
        // if (StringUtils.isBlank(value))
        // {
        // return;
        // }
        //
        // reports.add(new Report("cover", audioFile.getFile()));
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        LogManager.getLogManager().reset();

        Path rootDirectory = Paths.get(System.getProperty("user.home")).resolve("mediathek").resolve("musik");
        // rootDirectory = rootDirectory.resolve("Suede");

        List<FieldKey> fields = new ArrayList<>();
        fields.add(FieldKey.ALBUM);
        fields.add(FieldKey.ALBUM_ARTIST);
        fields.add(FieldKey.ALBUM_ARTIST_SORT);
        fields.add(FieldKey.ALBUM_SORT);
        fields.add(FieldKey.ARTIST);
        fields.add(FieldKey.ARTIST_SORT);
        fields.add(FieldKey.ORIGINAL_ARTIST);
        fields.add(FieldKey.TITLE);
        fields.add(FieldKey.TITLE_SORT);

        Map<File, Report> reports = new HashMap<>();

        try
        {
            // containsText(rootDirectory, reports, fields, " Feat", " Vs", " By ", " Van ", " Von ", " De ", " remix",
            // " dub", " mix");
            containsText(rootDirectory, reports, fields, " Feat", " Vs", " By ", " Van ", " De ", " La ", " With ", " version", " video", " remix", " dub",
                    " mix", " cut");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        // List<FieldKey> requiredKeys = Arrays.asList(FieldKey.ARTIST, FieldKey.ALBUM_ARTIST, FieldKey.TITLE, FieldKey.ALBUM, FieldKey.GENRE); //
        // List<FieldKey> unwantedKeys = new ArrayList<>(Arrays.asList(FieldKey.values()));
        // unwantedKeys.removeAll(requiredKeys);
        // unwantedKeys.remove(FieldKey.YEAR);
        // unwantedKeys.remove(FieldKey.TRACK_TOTAL);
        // unwantedKeys.remove(FieldKey.TRACK);
        // unwantedKeys.remove(FieldKey.RECORD_LABEL);
        // unwantedKeys.remove(FieldKey.ORIGINAL_YEAR);
        // // unwantedKeys.remove(FieldKey.LYRICS);
        // // unwantedKeys.remove(FieldKey.LANGUAGE);
        // unwantedKeys.remove(FieldKey.IS_COMPILATION);
        // unwantedKeys.remove(FieldKey.DISC_TOTAL);
        // unwantedKeys.remove(FieldKey.DISC_NO);
        // unwantedKeys.remove(FieldKey.COVER_ART);
        //
        // traverse(rootDirectory, reports, requiredKeys, unwantedKeys);
        System.out.println();

        int i = 1;

        //@formatter:off
//        reports.values()
//        .stream()
//        .sorted()
//        .forEach(report -> System.out.printf("%03d: %s%n", i++, report));
        //@formatter:on
        for (Report report : new TreeSet<>(reports.values()))
        {
            System.out.printf("%03d: %s%n", i++, report.toString(rootDirectory));
        }

        // reports.forEach((file, report) -> System.out.println(report.toString(rootDirectory)));
    }

    /**
     * @param directory {@link Path}s
     * @param reports {@link Map}
     * @param requiredKeys {@link List}
     * @param unwantedKeys {@link List}
     * @throws Exception Falls was schief geht.
     */
    protected static void traverse(final Path directory, final Map<File, Report> reports, final List<FieldKey> requiredKeys, final List<FieldKey> unwantedKeys)
        throws Exception
    {
        walk(directory, audioFile -> {
            try
            {
                // Song Titel mit gleichen Namen zählen um Duplikate zu finden.
                validateName(audioFile, reports, requiredKeys);
                containsFlag(audioFile, reports, unwantedKeys);
                // existCovers(audioFile, reports);
            }
            catch (RuntimeException rex)
            {
                throw rex;
            }
            catch (Exception ex)
            {
                RuntimeException re = new RuntimeException(ex);
                re.setStackTrace(ex.getStackTrace());

                throw re;
            }
        });
    }

    /**
     * Prüfen die Schreibweise der Tags.
     *
     * @param audioFile {@link AudioFile}
     * @param reports {@link Map}
     * @param keys {@link List}
     */
    private static void validateName(final AudioFile audioFile, final Map<File, Report> reports, final List<FieldKey> keys)
    {
        Tag tag = audioFile.getTag();

        String fileName = audioFile.getFile().getName();

        if (fileName.endsWith("MP3") || fileName.endsWith("WMA") || fileName.endsWith("FLAC"))
        {
            addReport(reports, audioFile.getFile(), "dateiname");
        }

        for (FieldKey key : keys)
        {
            for (TagField field : tag.getFields(key))
            {
                if (!(field instanceof TagTextField))
                {
                    continue;
                }

                TagTextField textField = (TagTextField) field;
                String value = textField.getContent();

                if ((value == null) || value.isBlank())
                {
                    continue;
                }

                if (StringUtils.containsAny(value, '`', '´', '"'))
                {
                    addReport(reports, audioFile.getFile(), "sonderzeichen");
                }

                if (value.startsWith(" ") || value.endsWith(" ") || value.contains("  "))
                {
                    addReport(reports, audioFile.getFile(), "leerzeichen");
                }

                if (value.toLowerCase().contains(" vs ") || value.toLowerCase().contains(" feat ") || value.toLowerCase().contains(" ft "))
                {
                    addReport(reports, audioFile.getFile(), "schreibweise");
                }

                value = value.replace("\\(", "");
                value = value.replace("\\)", "");
                value = value.replace("\\.", " ");
                value = value.replace("-", " ");

                String[] splits = value.split(" ");

                for (String split : splits)
                {
                    if ((split == null) || split.isBlank())
                    {
                        continue;
                    }

                    char c = split.charAt(0);

                    if (Character.isLetter(c) && !Character.isUpperCase(c))
                    {
                        addReport(reports, audioFile.getFile(), "schreibweise");
                    }
                }
            }
        }
    }

    /**
     * @param directory {@link Path}s
     * @param consumer {@link Consumer}
     * @throws Exception Falls was schief geht.
     */
    private static void walk(final Path directory, final Consumer<AudioFile> consumer) throws Exception
    {
        //@formatter:off
        Files.walk(directory)
            .filter(path -> !Files.isDirectory(path))
            .filter(p -> p.getParent().getParent().getFileName().toString().startsWith("A")) // Nur bestimmte Interpreten; p.getName(p.getNameCount() - 3).toString().startsWith("B")
            .filter(p -> !p.toString().toLowerCase().endsWith(".gif"))
            .filter(p -> !p.toString().toLowerCase().endsWith(".jpg"))
            .filter(p -> !p.toString().toLowerCase().endsWith(".png"))
            .filter(p -> !p.toString().toLowerCase().endsWith(".txt"))
            //                .filter(p -> !p.toString().toLowerCase().endsWith(".m4b"))
            .sorted()
            .peek(System.out::println)
            .forEach(path
                ->
            {
                try
                {
                    AudioFile audioFile = AudioFileIO.read(path.toFile());

                    consumer.accept(audioFile);
                }
                catch (RuntimeException rex)
                {
                    throw rex;
                }
                catch (Exception ex)
                {
                    RuntimeException rex = new RuntimeException(ex);
                    rex.setStackTrace(ex.getStackTrace());

                    throw rex;
                }
            });
        //@formatter:on
    }
}
