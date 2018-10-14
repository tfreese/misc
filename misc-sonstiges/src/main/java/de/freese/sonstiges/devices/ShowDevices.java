/**
 * Created: 04.10.2018
 */

package de.freese.sonstiges.devices;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

/**
 * @author Thomas Freese
 */
public class ShowDevices
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        showFileStores();
        showRootDirectories();
        showRootsFromFileSystemView();
        showFileStoreFromPath();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    private static void showFileStoreFromPath() throws Exception
    {
        System.out.println();
        System.out.println("ShowDevices.showFileStoreFromPath()");

        Path path = Paths.get("pom.xml");
        FileStore fileStore = Files.getFileStore(path);

        System.out.printf("FileStore from %s: %s, %s%n", path, fileStore.name(), fileStore.type());
    }

    /**
     *
     */
    private static void showFileStores()
    {
        System.out.println();
        System.out.println("ShowDevices.showFileStores()");

        for (FileStore store : FileSystems.getDefault().getFileStores())
        {
            System.out.printf("%s: %s%n", store.name(), store.type());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    private static void showRootDirectories() throws Exception
    {
        System.out.println();
        System.out.println("ShowDevices.showRootDirectories()");

        for (Path root : FileSystems.getDefault().getRootDirectories())
        {
            FileStore fileStore = Files.getFileStore(root);
            // System.out.format("%s\t%s\n", root, fileStore.getAttribute("volume:isRemovable"));
            System.out.printf("%s: %s%n", fileStore.name(), fileStore.type());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    private static void showRootsFromFileSystemView() throws Exception
    {
        System.out.println();
        System.out.println("ShowDevices.showRootsFromFileSystemView()");

        FileSystemView fsv = FileSystemView.getFileSystemView();

        List<File> roots = new ArrayList<>();

        // roots.addAll(Arrays.asList(fsv.getRoots())); // Liefert nur lokale Partitionen.
        roots.addAll(Arrays.asList(File.listRoots())); // Liefert auch Netzlaufwerke.

        for (File path : roots)
        {
            // System.out.printf("Drive Name: %s, %s%n", path, fsv.getSystemTypeDescription(path));

            System.out.println("System Drive: " + path);
            System.out.println("TypeDescription: " + fsv.getSystemTypeDescription(path));
            System.out.println("Drive Display name: " + fsv.getSystemDisplayName(path));
            System.out.println("Is drive: " + fsv.isDrive(path));
            System.out.println("Is floppy: " + fsv.isFloppyDrive(path));
            System.out.println("Readable: " + path.canRead());
            System.out.println("Writable: " + path.canWrite());
            System.out.println();
        }
    }

    /**
     * Erstellt ein neues {@link ShowDevices} Object.
     */
    public ShowDevices()
    {
        super();
    }
}
