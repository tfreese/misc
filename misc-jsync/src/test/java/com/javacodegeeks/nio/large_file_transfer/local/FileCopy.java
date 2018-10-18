package com.javacodegeeks.nio.large_file_transfer.local;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import com.javacodegeeks.nio.large_file_transfer.Constants;

/**
 * @author Thomas Freese
 */
public final class FileCopy
{
    /**
     * @param src String
     * @param target String
     * @throws IOException Falls was schief geht.
     */
    public static void copy(final String src, final String target) throws IOException
    {
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty(target))
        {
            throw new IllegalArgumentException("src and target required");
        }

        final String fileName = getFileName(src);

        try (FileChannel from = (FileChannel.open(Paths.get(src), StandardOpenOption.READ));
             FileChannel to = (FileChannel.open(Paths.get(target + "/" + fileName), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)))
        {
            transfer(from, to, 0l, from.size());
        }
    }

    /**
     * @param src String
     * @return String
     */
    private static String getFileName(final String src)
    {
        assert StringUtils.isNotEmpty(src);

        final File file = new File(src);

        if (file.isFile())
        {
            return file.getName();
        }

        throw new RuntimeException("src is not a valid file");
    }

    /**
     * @param from {@link FileChannel}
     * @param to {@link FileChannel}
     * @param position long
     * @param size long
     * @throws IOException Falls was schief geht.
     */
    private static void transfer(final FileChannel from, final FileChannel to, long position, final long size) throws IOException
    {
        assert !Objects.isNull(from) && !Objects.isNull(to);

        while (position < size)
        {
            position += from.transferTo(position, Constants.TRANSFER_MAX_SIZE, to);
        }
    }

    /**
     * Erstellt ein neues {@link FileCopy} Object.
     */
    private FileCopy()
    {
        throw new IllegalStateException(Constants.INSTANTIATION_NOT_ALLOWED);
    }
}
