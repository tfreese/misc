package com.javacodegeeks.nio.large_file_transfer.test;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Thomas Freese
 */
public abstract class AbstractTest
{

    /**
     *
     */
    protected static final long AWAIT_TEST_COMPLETE = 20000l;

    /**
     *
     */
    protected static final String SRC = "/tmp/input.tar.gz";

    /**
     *
     */
    protected static final String TARGET = "/tmp/output";

    /**
     *
     */
    @AfterClass
    public static void destroy()
    {
        Thread.currentThread().getContextClassLoader().setDefaultAssertionStatus(false);
    }

    /**
     *
     */
    @BeforeClass
    public static void init()
    {
        Thread.currentThread().getContextClassLoader().setDefaultAssertionStatus(true);
    }

    /**
     *
     */
    protected File srcFile = null;

    /**
     *
     */
    protected File targetFile = null;

    /**
     * Erstellt ein neues {@link AbstractTest} Object.
     */
    public AbstractTest()
    {
        super();
    }

    /**
     *
     */
    protected final void compare()
    {
        assertEquals("file did not copy completely", this.srcFile.length(), this.targetFile.length());
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Before
    public void setUp() throws IOException
    {
        this.srcFile = new File(SRC);
        this.targetFile = new File(TARGET + "/" + this.srcFile.getName());

        Files.deleteIfExists(this.targetFile.toPath());
    }
}
