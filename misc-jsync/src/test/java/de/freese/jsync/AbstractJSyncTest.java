// Created: 06.04.2018
package de.freese.jsync;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.BeforeClass;

/**
 * @author Thomas Freese
 */
public abstract class AbstractJSyncTest
{
    /**
     *
     */
    private static final Path PATH_BASE = Paths.get(System.getProperty("user.dir"), "target");

    /**
     *
     */
    protected static final Path PATH_QUELLE = PATH_BASE.resolve("quelle");

    /**
     *
     */
    protected static final Path PATH_ZIEL = PATH_BASE.resolve("ziel");

    /**
     * Verzeichnis-Struktur zum Testen aufbauen.
     *
     * @throws Exception Falls was schief geht.
     */
    @BeforeClass
    public static void beforeclass() throws Exception
    {
        deleteDirectoryRecursiv(PATH_QUELLE);
        deleteDirectoryRecursiv(PATH_ZIEL);

        // Quell-Dateien anlegen
        Path pathQuelleV1 = PATH_QUELLE.resolve("v1");

        Files.createDirectories(pathQuelleV1);

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(PATH_QUELLE.resolve("file1.txt").toFile())))
        {
            writer.print("file1.txt");
        }

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(pathQuelleV1.resolve("file2.txt").toFile())))
        {
            writer.print("file1.txt");
        }

        // Etwas warten, damit Timestamps im Zeil unterschiedlich sind.
        Thread.sleep(1000L);

        // Ziel-Datei anlegen
        Files.createDirectories(PATH_ZIEL);

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(PATH_ZIEL.resolve("file1.txt").toFile())))
        {
            writer.print("file1.txt");
        }
    }

    /**
     * Löscht das Verzeichnis rekursiv inklusive Dateien und Unterverzeichnisse.
     *
     * @param path {@link Path}
     * @throws IOException Falls was schief geht.
     */
    public static void deleteDirectoryRecursiv(final Path path) throws IOException
    {
        if (!Files.exists(path))
        {
            return;
        }

        if (!Files.isDirectory(path))
        {
            throw new IllegalArgumentException("path is not a dirctory: " + path);
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            /**
             * @see java.nio.file.SimpleFileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
             */
            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
            {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            /**
             * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
             */
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Erzeugt eine neue Instanz von {@link AbstractJSyncTest}.
     */
    public AbstractJSyncTest()
    {
        super();
    }

}
