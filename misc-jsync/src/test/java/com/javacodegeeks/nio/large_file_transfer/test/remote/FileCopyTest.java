package com.javacodegeeks.nio.large_file_transfer.test.remote;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import com.javacodegeeks.nio.large_file_transfer.remote.FileReader;
import com.javacodegeeks.nio.large_file_transfer.remote.FileReceiver;
import com.javacodegeeks.nio.large_file_transfer.remote.FileSender;
import com.javacodegeeks.nio.large_file_transfer.remote.FileWriter;
import com.javacodegeeks.nio.large_file_transfer.test.AbstractTest;

/**
 * @author Thomas Freese
 */
public class FileCopyTest extends AbstractTest
{

    /**
     *
     */
    private static final int PORT = 9999;

    /**
     * @throws IOException Falls was schief geht.
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void copyLargeFile() throws IOException, InterruptedException
    {
        final CountDownLatch latch = new CountDownLatch(1);
        final FileReceiver receiver = new FileReceiver(PORT, new FileWriter(TARGET + "/" + super.srcFile.getName()), super.srcFile.length());

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
                    receiver.receive();
                }
                catch (IOException e)
                {

                }
                finally
                {
                    latch.countDown();
                }
            }
        }.start();

        final FileReader reader = new FileReader(new FileSender(PORT), SRC);
        reader.read();

        latch.await();
        compare();
    }
}
