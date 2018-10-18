package com.javacodegeeks.nio.large_file_transfer;

/**
 * @author Thomas Freese
 */
public final class Constants
{
    /**
     *
     */
    public static final int BUFFER_SIZE = 2048;

    /**
     *
     */
    public static final String CONFIRMATION = "OK";

    /**
     *
     */
    public static final String END_MESSAGE_MARKER = ":END";

    /**
     *
     */
    public static final String INSTANTIATION_NOT_ALLOWED = "Instantiation not allowed";

    /**
     *
     */
    public static final String MESSAGE_DELIMITTER = "#";

    /**
     *
     */
    public static final long TRANSFER_MAX_SIZE = (1024 * 1024);

    /**
     *
     */
    private Constants()
    {
        throw new IllegalStateException(INSTANTIATION_NOT_ALLOWED);
    }
}
