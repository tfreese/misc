package com.javacodegeeks.nio.large_file_transfer.test.remote;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import com.javacodegeeks.nio.large_file_transfer.Constants;
import com.javacodegeeks.nio.large_file_transfer.remote.FileReader;
import com.javacodegeeks.nio.large_file_transfer.remote.FileReceiverAsync;
import com.javacodegeeks.nio.large_file_transfer.remote.FileSender;
import com.javacodegeeks.nio.large_file_transfer.test.AbstractTest;

/**
 * @author Thomas Freese
 */
public class FileCopyAsyncTest extends AbstractTest
{
    /**
     * @author Thomas Freese
     */
    private final class TestAsyncClient implements Runnable
    {
        /**
         *
         */
        private final String fileName;

        /**
         *
         */
        private final FileReader reader;

        /**
         *
         */
        private final FileSender sender;

        /**
         *
         */
        private final long size;

        /**
         * Erstellt ein neues {@link TestAsyncClient} Object.
         *
         * @param sender {@link FileSender}
         * @param reader {@link FileReader}
         * @param fileName String
         * @param size long
         */
        private TestAsyncClient(final FileSender sender, final FileReader reader, final String fileName, final long size)
        {
            assert !Objects.isNull(sender) && !Objects.isNull(reader) && !Objects.isNull(fileName);

            this.sender = sender;
            this.reader = reader;
            this.size = size;
            this.fileName = fileName;
        }

        /**
         * @param response String
         * @throws IOException Falls was schief geht.
         */
        private void confirmAndGo(final String response) throws IOException
        {
            if (!response.contains(Constants.CONFIRMATION))
            {
                final ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
                final long bytesRead = this.sender.getChannel().read(buffer);

                if (bytesRead > 0)
                {
                    confirmAndGo(response + new String(buffer.array()));
                }
                else if (bytesRead < 0)
                {
                    this.reader.close();
                }
            }
            else
            {
                this.reader.read();
            }
        }

        /**
         * @throws IOException Falls was schief geht.
         */
        private void negotiate() throws IOException
        {
            final String message = this.fileName + Constants.MESSAGE_DELIMITTER + String.valueOf(this.size) + Constants.END_MESSAGE_MARKER;
            final ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

            while (buffer.hasRemaining())
            {
                this.sender.getChannel().write(buffer);
            }
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try
            {
                negotiate();
                confirmAndGo(StringUtils.EMPTY);
            }
            catch (IOException e)
            {
                throw new RuntimeException("unable to run", e);
            }
            finally
            {
                try
                {
                    this.reader.close();
                }
                catch (IOException e)
                {
                    throw new RuntimeException("unable to close reader", e);
                }
            }
        }
    }

    /**
     *
     */
    private static final String LARGE_FILE_ONE = "/tmp/input1.tar.gz";

    /**
     *
     */
    private static final String LARGE_FILE_TWO = "/tmp/input2.tar.gz";

    /**
     *
     */
    private static final int PORT = 9999;

    /**
     * @throws IOException Falls was schief geht.
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void copyTwoLargeFilesConcurrently() throws IOException, InterruptedException
    {
        final CountDownLatch serverReady = new CountDownLatch(1);
        final CountDownLatch jobsLatch = new CountDownLatch(2);
        final AtomicBoolean pass = new AtomicBoolean(Boolean.FALSE);

        final FileReceiverAsync fileReceiverAsync = new FileReceiverAsync(PORT, 2, TARGET, fileWriter -> {
            pass.set((fileWriter.done() && (LARGE_FILE_ONE.contains(fileWriter.getFileName()) || LARGE_FILE_TWO.contains(fileWriter.getFileName()))));
            jobsLatch.countDown();
        });

        new Thread()
        {
            @Override
            public void run()
            {
                fileReceiverAsync.start();
                serverReady.countDown();
            }
        }.start();

        serverReady.await();

        runClient(LARGE_FILE_ONE);
        runClient(LARGE_FILE_TWO);

        jobsLatch.await();
        fileReceiverAsync.stop(0l);

        assertTrue(pass.get());
    }

    /**
     * @param srcPath String
     */
    private void runClient(final String srcPath)
    {
        new Thread()
        {
            /**
             * @see java.lang.Thread#run()
             */
            @Override
            public void run()
            {
                try
                {
                    final File srcFile = new File(srcPath);

                    final FileSender sender = new FileSender(PORT);
                    final FileReader reader = new FileReader(sender, srcPath);
                    final TestAsyncClient helper = new TestAsyncClient(sender, reader, srcFile.getName(), srcFile.length());
                    helper.run();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    /**
     * @see com.javacodegeeks.nio.large_file_transfer.test.AbstractTest#setUp()
     */
    @Before
    @Override
    public void setUp() throws IOException
    {
        final File fileOne = new File(LARGE_FILE_ONE);
        final File fileTwo = new File(LARGE_FILE_TWO);

        final File targetFileOne = new File(TARGET + "/" + fileOne.getName());
        final File targetFileTwo = new File(TARGET + "/" + fileTwo.getName());

        Files.deleteIfExists(targetFileOne.toPath());
        Files.deleteIfExists(targetFileTwo.toPath());
    }
}
