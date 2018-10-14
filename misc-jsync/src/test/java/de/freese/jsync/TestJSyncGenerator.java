/**
 * Created: 22.10.2016
 */

package de.freese.jsync;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.RunnableFuture;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import de.freese.jsync.api.Generator;
import de.freese.jsync.api.Group;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.SyncItem;
import de.freese.jsync.api.User;
import de.freese.jsync.impl.generator.DefaultGenerator;
import de.freese.jsync.impl.generator.FileSyncItem;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJSyncGenerator extends AbstractJSyncTest
{
    /**
     * @author Thomas Freese
     */
    public static class TestGenerator extends DefaultGenerator
    {
        /**
         * Erzeugt eine neue Instanz von {@link DefaultGenerator}.
         *
         * @param options {@link Options}
         * @param base {@link Path}
         */
        public TestGenerator(final Options options, final Path base)
        {
            super(options, base);
        }

        /**
         * @see de.freese.jsync.impl.generator.DefaultGenerator#toItem(java.nio.file.Path)
         */
        @Override
        public SyncItem toItem(final Path path)
        {
            return super.toItem(path);
        }
    };

    /**
     * Erstellt ein neues {@link TestJSyncGenerator} Object.
     */
    public TestJSyncGenerator()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010GeneratorQuelle() throws Exception
    {
        Path base = PATH_QUELLE;
        Options options = new Options();
        options.setChecksum(false);

        Generator generator = new DefaultGenerator(options, base);

        RunnableFuture<Map<String, SyncItem>> future = generator.createSyncItems();
        // future.run();
        options.getExecutor().execute(future);

        Map<String, SyncItem> fileMap = future.get();

        Assert.assertNotNull(fileMap);
        Assert.assertEquals(4, fileMap.size());

        fileMap.forEach((key, value) -> System.out.printf("%s%n", key));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020GeneratorZiel() throws Exception
    {
        Path base = PATH_ZIEL;
        Options options = new Options();
        options.setChecksum(false);

        Generator generator = new DefaultGenerator(options, base);

        RunnableFuture<Map<String, SyncItem>> future = generator.createSyncItems();
        // future.run();
        options.getExecutor().execute(future);

        Map<String, SyncItem> fileMap = future.get();

        Assert.assertNotNull(fileMap);
        Assert.assertEquals(2, fileMap.size());

        fileMap.forEach((key, value) -> System.out.printf("%s%n", key));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030FileAttributes() throws Exception
    {
        Path base = Paths.get(System.getProperty("user.dir"));

        TestGenerator generator = new TestGenerator(new Options(), base);

        FileSyncItem fileSyncItem = (FileSyncItem) generator.toItem(base.resolve("pom.xml"));

        Assert.assertTrue(fileSyncItem.getLastModifiedTime() > 0);
        Assert.assertTrue(fileSyncItem.getSize() > 0);

        if (Options.IS_LINUX)
        {
            Assert.assertNotNull(fileSyncItem.getPermissions());

            Assert.assertNotNull(fileSyncItem.getGroup());
            Assert.assertNotNull(fileSyncItem.getGroup().getName());
            Assert.assertTrue(fileSyncItem.getGroup().getGid() > Group.ROOT.getGid() - 1);
            Assert.assertTrue(fileSyncItem.getGroup().getGid() < Group.ID_MAX + 1);
            Assert.assertEquals(fileSyncItem.getGroup().getName(), "tommy");
            Assert.assertEquals(fileSyncItem.getGroup().getGid(), 1000); // tommy

            Assert.assertNotNull(fileSyncItem.getUser());
            Assert.assertNotNull(fileSyncItem.getUser().getName());
            Assert.assertTrue(fileSyncItem.getUser().getUid() > User.ROOT.getUid() - 1);
            Assert.assertTrue(fileSyncItem.getUser().getUid() < User.ID_MAX + 1);
            Assert.assertEquals(fileSyncItem.getUser().getName(), "tommy");
            Assert.assertEquals(fileSyncItem.getUser().getUid(), 1000); // tommy
        }
    }
}
