// Erzeugt: 21.09.2016
package de.freese.sonstiges.imap;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.PasswordAuthentication;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.sql.DataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hsqldb.jdbc.JDBCPool;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.imap.analyze.FunctionNormalizeGerman;
import de.freese.sonstiges.imap.analyze.FunctionStemmer;
import de.freese.sonstiges.imap.analyze.FunctionStripNotLetter;
import de.freese.sonstiges.imap.analyze.FunctionStripSameChar;
import de.freese.sonstiges.imap.analyze.FunctionStripStopWords;

/**
 * imap.1und1.de:143 (STARTTLS); pop.1und1.de:110 (STARTTLS); smtp.1und1.de:587 (STARTTLS)<br>
 * imap.1und1.de:993 (SSL); pop.1und1.de:995 (SSL); smtp.1und1.de:465 (STARTTLS)
 *
 * @author Thomas Freese
 */
public class ReadImapMails
{
    /**
     * @author Thomas Freese
     */
    private abstract static class AbstractTextPart
    {
        /**
         *
         */
        private final String text;

        /**
         * Erstellt ein neues {@link AbstractTextPart} Object.
         *
         * @param text String
         */
        private AbstractTextPart(final String text)
        {
            super();

            this.text = text;
        }

        /**
         * @return String
         */
        public String getText()
        {
            return this.text;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return getText();
        }
    }

    /**
     * Generisches Callback-Interface für einen Mail-{@link Folder}.
     *
     * @author Thomas Freese
     * @param <T> Konkreter Return-Typ
     */
    private interface FolderCallback<T>
    {
        /**
         * @param folder {@link Folder}
         * @return Object
         * @throws Exception Falls was schief geht.
         */
        public T doInFolder(Folder folder) throws Exception;
    }

    /**
     * @author Thomas Freese
     */
    private static final class HTMLTextPart extends AbstractTextPart
    {
        /**
         * Erstellt ein neues {@link HTMLTextPart} Object.
         *
         * @param text String
         */
        private HTMLTextPart(final String text)
        {
            super(text);
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class PlainTextPart extends AbstractTextPart
    {
        /**
         * Erstellt ein neues {@link PlainTextPart} Object.
         *
         * @param text String
         */
        private PlainTextPart(final String text)
        {
            super(text);
        }
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadImapMails.class);

    /**
     *
     */
    public static final Function<List<String>, List<String>> PRE_FILTER = token -> {
        // String linkRegEx = "^((http[s]?|ftp|file):\\/)?\\/?([^:\\/\\s]+)(:([^\\/]*))?((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(\\?([^#]*))?(#(.*))?$";
        String linkRegEx = "^((http[s]?|ftp|file):.*)|(^(www.).*)";
        String mailRegEx = "^(.+)@(.+).(.+)$"; // ^[A-Za-z0-9+_.-]+@(.+)$

        // @formatter:off
        List<String> list = token.stream()
                .map(t -> t.replace("\n", " ").replace("\r", " ")) // LineBreaks entfernen
                // .peek(System.out::println)
                .map(t -> t.split(" "))
                .flatMap(Arrays::stream)
                .parallel()
                .filter(StringUtils::isNotBlank) // .filter(t -> !t.isEmpty())
                .map(StringUtils::trim)
                .map(StringUtils::lowerCase)
                .filter(t -> !t.matches(linkRegEx)) // URLs entfernen
                .filter(t -> !t.matches(mailRegEx)) // Mails entfernen
                //.filter(t -> !t.startsWith("http:"))
                //.filter(t -> !t.startsWith("https:"))
                //.filter(t -> !t.startsWith("ftp:"))
                //.filter(t -> !t.startsWith("file:"))
                //.filter(t -> !t.contains("@"))
                .map(FunctionStripNotLetter.INSTANCE)
                .map(t -> t.split(" "))
                .flatMap(Arrays::stream)
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim)
                .map(FunctionStripSameChar.INSTANCE)
                .filter(t -> t.length() > 2)
                .sorted()
                .collect(Collectors.toList());
        // @formatter:on

        return list;
    };

    /**
    *
    */
    public static final BiFunction<List<String>, Locale, Map<String, Integer>> STEMMER_FILTER = (token, locale) -> {
        Function<String, String> functionStemmer = FunctionStemmer.get(locale);

       // @formatter:off
       // parallelStream wegen Stemmer nicht möglich.
        Map<String, Integer> map = token.stream()
                .map(t -> Locale.GERMAN.equals(locale) ? FunctionNormalizeGerman.INSTANCE.apply(t) : t)
                .map(FunctionStripStopWords.INSTANCE)
                .map(functionStemmer)
                .filter(t -> t.length() > 2)
                .sorted()
                //.peek(System.out::println)
                //.collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting())); // long
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.summingInt(e -> 1))); // integer
       // @formatter:on

        return map;
    };

    /**
     * Aktiviert die Proxy-Kommunikation.
     */
    static void enableProxy()
    {
        // DNS-Auflösung konfigurieren.
        // System.setProperty("sun.net.spi.nameservice.nameservers", "192.168.155.23");
        // System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
        // System.setProperty("sun.net.spi.nameservice.domain", "DOMAIN");

        // Use the default network proxies configured in the sytem.
        // System.setProperty("java.net.useSystemProxies", "true");
        // Proxy konfigurieren.
        System.setProperty("proxySet", "true");

        System.setProperty("socksProxyHost", "194.114.63.23");
        System.setProperty("socksProxyPort", "8080");

        System.setProperty("http.proxyHost", "194.114.63.23");
        System.setProperty("http.proxyPort", "8080");
        // System.setProperty("http.proxyUser", "USER");
        // System.setProperty("http.proxyPassword", "PASSWORD");
        // System.setProperty("http.auth.ntlm.domain", "DOMAIN");
        // System.setProperty("http.nonProxyHosts", "localhost|host1|host2");

        System.setProperty("https.proxyHost", "194.114.63.23");
        System.setProperty("https.proxyPort", "8080");
        // System.setProperty("https.proxyUser", "USER");
        // System.setProperty("https.proxyPassword", "PASSWORD");
        // System.setProperty("https.auth.ntlm.domain", "DOMAIN");
        // System.setProperty("https.nonProxyHosts", "localhost|host1|host2");

        java.net.Authenticator.setDefault(new java.net.Authenticator()
        {
            /**
             * @see java.net.Authenticator#getPasswordAuthentication()
             */
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("USER", "PASSWORD".toCharArray());
            }
        });

        try
        {
            // InetAddress address = InetAddress.getByName("www.web.de"); // 82.165.230.17
            // LOGGER.info(address.toString());
            // InetAddress address = InetAddress.getByName("imap.1und1.de"); // 212.227.15.171
            // InetAddress address = InetAddress.getByName("212.227.15.171");
            // LOGGER.info(address.toString());
        }
        catch (Exception ex)
        {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // enableProxy();

        DataSource dataSource = null;

        JDBCPool jdbcPool = new JDBCPool();
        // jdbcPool.setUrl("jdbc:hsqldb:mem:mails");
        jdbcPool.setUrl("jdbc:hsqldb:file:/tmp/mails/mails;shutdown=true");
        jdbcPool.setUser("sa");
        jdbcPool.setPassword(null);
        dataSource = jdbcPool;

        // SingleConnectionDataSource scds = new SingleConnectionDataSource();
        // scds.setDriverClassName("org.mariadb.jdbc.Driver");
        // scds.setUrl("jdbc:mariadb://localhost:3306/tommy?user=...&password=...");
        // scds.setSuppressClose(true);
        // scds.setAutoCommit(false);
        // dataSource = scds;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("schema_hsqldb.sql"), StandardCharsets.UTF_8)))
        {
            // @formatter:off
               String sql = reader.lines()
                       .filter(Objects::nonNull)
                       .map(String::trim)
                       .filter(line -> !line.isEmpty())
                       .filter(line -> !line.startsWith("#"))
                       .filter(line -> !line.startsWith("--"))
                       .map(line -> line.replace("\n", " ").replace("\r", " "))
                       .collect(Collectors.joining(" "));
               // @formatter:on

            statement.executeUpdate(sql);
        }

        String host = args[0];
        String user = args[1];
        String password = args[2];

        Properties props = new Properties(System.getProperties());
        props.put("mail.debug", Boolean.FALSE.toString());
        props.put("mail.event.executor", ForkJoinPool.commonPool());
        props.put("mail.host", host);

        String protocol = "imaps";
        props.put("mail.store.protocol", protocol);

        if (protocol.equals("imaps"))
        {
            props.put("mail.imaps.port", "993");
            props.put("mail.imaps.auth", "true");
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.ssl.checkserveridentity", "true");
        }
        else
        {
            // imap = ohne SSL
            props.put("mail.imap.host", host);
            props.put("mail.imap.auth", "true");
            props.put("mail.imap.starttls.enable", "true");
        }

        Authenticator authenticator = new MailAuthenticator(user, password);

        Session session = Session.getInstance(props, authenticator);
        // session.setDebug(false); // Wird mit "mail.debug" gesteuert.

        try (Store store = session.getStore()) // Wird mit "mail.store.protocol" gesteuert.
        {
            // store.connect(null, null, null);
            store.connect();
            LOGGER.info("Connection established with IMAP server.");

            ReadImapMails readMail = new ReadImapMails(dataSource);
            readMail.processMail(store, "Spam", true);
            readMail.processMail(store, "INBOX", false);
        }

        // if (dataSource instanceof DisposableBean)
        // {
        // ((DisposableBean) dataSource).destroy();
        // }
        // else
        if (dataSource instanceof AutoCloseable)
        {
            ((AutoCloseable) dataSource).close();
        }
        else if (dataSource instanceof Closeable)
        {
            ((Closeable) dataSource).close();
        }
        else if (dataSource instanceof JDBCPool)
        {
            ((JDBCPool) dataSource).close(1);
        }
    }

    /**
    *
    */
    private final DataSource dataSource;

    /**
     * Erzeugt eine neue Instanz von {@link ReadImapMails}
     *
     * @param dataSource {@link DataSource}
     */
    public ReadImapMails(final DataSource dataSource)
    {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    /**
     * @param spam boolean
     * @return {@link FolderCallback}
     */
    @SuppressWarnings("unused")
    private FolderCallback<Void> getFolderCallback(final boolean spam)
    {
        return folder -> {
            List<String> messageIDs = getMessageIDs();

            // Messages laden.
            Message[] messages = null;

            // Alle Mails, älteste zuerst.
            messages = folder.getMessages();

            // Die aktuellsten 2 Mails.
            int n = folder.getMessageCount();
            // messages = folder.getMessages(n - 1, n);

            // SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            // messages = folder.search(searchTerm);
            for (Message message : messages)
            {
                // Enumeration<String> headers = message.getAllHeaders();
                if (message.getHeader("Message-ID") == null)
                {
                    continue;
                }

                String messageID = message.getHeader("Message-ID")[0];
                int messageNumber = message.getMessageNumber();
                Date receivedDate = message.getReceivedDate();
                String subject = message.getSubject();
                String from = null;

                if (message.getFrom() != null)
                {
                    from = ((InternetAddress) message.getFrom()[0]).getAddress();
                }

                if (messageIDs.contains(messageID))
                {
                    continue;
                }

                LOGGER.info(String.format("%02d \t %s \t %tc \t %s \t %s", messageNumber, messageID, receivedDate, subject, from));

                List<AbstractTextPart> textParts = getTextParts(message);

                if (CollectionUtils.isEmpty(textParts))
                {
                    continue;
                }

                // @formatter:off
                List<String> token = textParts.stream()
                    .map(AbstractTextPart::getText)
                    .map(t -> Jsoup.parse(t).text()) // HTML-Text extrahieren
                    .map(t -> t.split(" "))
                    .flatMap(Arrays::stream)
                     // peek(System.out::println)
                    .collect(Collectors.toList());
                // @formatter:on

                Locale locale = FunctionStripStopWords.guessLocale(token);

                token = PRE_FILTER.apply(token);
                Map<String, Integer> wordCount = STEMMER_FILTER.apply(token, locale);

                LOGGER.info("");

                // Header ändern, ist bei IMAPMessages nicht erlaubt !
                // message.addHeader("MY-HEADER", "Test");
                // message.saveChanges();
                try
                {
                    insertMessage(messageID, subject, spam, receivedDate, from, wordCount);
                }
                catch (SQLException sex)
                {
                    LOGGER.error(null, sex);
                }
            }

            return null;
        };
    }

    /**
     * Liefert die vorhandenen MessageIDs aus der DB.
     *
     * @return {@link List}
     * @throws SQLException Falls was schief geht.
     */
    private List<String> getMessageIDs() throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct MESSAGE_ID from MESSAGE");

        List<String> result = new ArrayList<>();

        try (Connection connection = this.dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString()))
        {
            while (resultSet.next())
            {
                String messageId = resultSet.getString("MESSAGE_ID");
                result.add(messageId);
            }
        }

        return result;
    }

    /**
     * Liefert alle vorhandenen Text Parts einer {@link Message}.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link List}
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    private List<AbstractTextPart> getTextParts(final Part part) throws MessagingException, IOException
    {
        List<AbstractTextPart> textParts = new ArrayList<>();

        if (part.isMimeType("text/*"))
        {
            if (!(part.getContent() instanceof String))
            {
                return null;
            }

            String text = (String) part.getContent();

            if (part.isMimeType("text/plain"))
            {
                textParts.add(new PlainTextPart(text));
            }
            else if (part.isMimeType("text/html"))
            {
                textParts.add(new HTMLTextPart(text));
            }
        }
        else if (part.isMimeType("multipart/*"))
        {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);

                List<AbstractTextPart> tp = getTextParts(bp);

                if (CollectionUtils.isNotEmpty(tp))
                {
                    textParts.addAll(tp);
                }
            }
        }

        return textParts;
    }

    /**
     * @param messageID String
     * @param subject String
     * @param spam boolean
     * @param receivedDate {@link Date}
     * @param from String
     * @param wordCount {@link Map}
     * @throws SQLException Falls was schief geht
     */
    private void insertMessage(final String messageID, final String subject, final boolean spam, final Date receivedDate, final String from,
                               final Map<String, Integer> wordCount)
        throws SQLException
    {
        try (Connection connection = this.dataSource.getConnection())
        {
            connection.setAutoCommit(false);

            StringBuilder sqlMessage = new StringBuilder();
            sqlMessage.append("insert into message (message_id, subject, is_spam, received_date, sender)");
            sqlMessage.append(" values (?, ?, ?, ?, ?)");

            StringBuilder sqlToken = new StringBuilder("INSERT INTO token (token, ham_count, spam_count) VALUES");

            if (spam)
            {
                sqlToken.append(" (?, 0, 1) ON DUPLICATE KEY UPDATE spam_count = spam_count + 1");
            }
            else
            {
                sqlToken.append(" (?, 1, 0) ON DUPLICATE KEY UPDATE ham_count = ham_count + 1");
            }

            StringBuilder sqlMessageToken = new StringBuilder();
            sqlMessageToken.append("insert into message_token (message_id, token, count) values (?, ?, ?)");

            try (PreparedStatement preparedStatementMsg = connection.prepareStatement(sqlMessage.toString());
                 PreparedStatement preparedStatementToken = connection.prepareStatement(sqlToken.toString());
                 PreparedStatement preparedStatementMessageToken = connection.prepareStatement(sqlMessageToken.toString()))
            {
                preparedStatementMsg.setString(1, messageID);
                preparedStatementMsg.setString(2, subject);
                preparedStatementMsg.setBoolean(3, spam);
                preparedStatementMsg.setDate(4, new java.sql.Date(receivedDate.getTime()));
                preparedStatementMsg.setString(5, from);
                preparedStatementMsg.executeUpdate();

                for (Entry<String, Integer> entry : wordCount.entrySet())
                {
                    String token = entry.getKey();
                    int count = entry.getValue();

                    // Token
                    preparedStatementToken.setString(1, token);
                    preparedStatementToken.executeUpdate();

                    // Message_Token
                    preparedStatementMessageToken.setString(1, messageID);
                    preparedStatementMessageToken.setString(2, token);
                    preparedStatementMessageToken.setInt(3, count);
                    preparedStatementMessageToken.addBatch();
                    // preparedStatementMessageToken.executeUpdate();
                }

                preparedStatementMessageToken.executeBatch();

                connection.commit();
            }
            catch (SQLException sex)
            {
                connection.rollback();
                throw sex;
            }
        }
    }

    /**
     * @param store {@link Store}
     * @param folderName String
     * @param spam boolean
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    private void processMail(final Store store, final String folderName, final boolean spam) throws SQLException
    {
        Folder folder = null;

        try
        {
            LOGGER.info("processing mails for {}", folderName);

            // folder = store.getDefaultFolder();
            // LOGGER.info("Getting the default " + folder.getFullName() + " folder.");

            // Alle Folder anzeigen.
            // for (Folder f : folder.list("*"))
            // {
            // if ((f.getType() & Folder.HOLDS_MESSAGES) != 0)
            // {
            // LOGGER.info(f.getFullName() + ": " + f.getMessageCount());
            // }
            // }
            folder = store.getFolder(folderName);

            if (folder == null)
            {
                LOGGER.warn("Folder {} not exist", folderName);

                return;
            }

            if ((folder.getType() & Folder.HOLDS_MESSAGES) == 0)
            {
                LOGGER.warn("Folder {} can not contain messges", folderName);
                folder.close();

                return;
            }

            // checkRead
            if (!folder.isOpen())
            {
                folder.open(Folder.READ_ONLY);
            }

            FolderCallback<Void> folderCallback = getFolderCallback(spam);

            folderCallback.doInFolder(folder);
        }
        catch (Exception ex)
        {
            LOGGER.error(null, ex);
        }
        finally
        {
            try
            {
                if ((folder != null) && folder.isOpen())
                {
                    folder.close(false);
                }
            }
            catch (Exception ex2)
            {
                LOGGER.error(null, ex2);
            }
        }
    }
}
