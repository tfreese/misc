package com.javacodegeeks.nio.large_file_transfer.remote;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface OnComplete
{
    /**
     * @param fileWriter {@link FileWriterProxy}
     */
    void onComplete(FileWriterProxy fileWriter);
}
