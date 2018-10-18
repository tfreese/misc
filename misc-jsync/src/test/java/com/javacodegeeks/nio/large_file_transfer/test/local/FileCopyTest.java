package com.javacodegeeks.nio.large_file_transfer.test.local;

import java.io.IOException;
import org.junit.Test;
import com.javacodegeeks.nio.large_file_transfer.local.FileCopy;
import com.javacodegeeks.nio.large_file_transfer.test.AbstractTest;

/**
 * @author Thomas Freese
 */
public class FileCopyTest extends AbstractTest
{
    /**
     * Erstellt ein neues {@link FileCopyTest} Object.
     */
    public FileCopyTest()
    {
        super();
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    public void copyLargeFile() throws IOException
    {
        FileCopy.copy(SRC, TARGET);

        compare();
    }
}
