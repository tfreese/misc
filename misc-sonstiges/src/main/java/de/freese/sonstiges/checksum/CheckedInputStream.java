package de.freese.sonstiges.checksum;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Checksum;

/**
 * @author Thomas Freese
 */
public class CheckedInputStream extends FilterInputStream
{
    /**
     *
     */
    private Checksum cksum;

    /**
     * Creates a new {@link CheckedInputStream} object.
     *
     * @param in {@link InputStream}
     * @param cksum {@link Checksum}
     */
    public CheckedInputStream(final InputStream in, final Checksum cksum)
    {
        super(in);

        this.cksum = cksum;
    }

    /**
     * @return {@link Checksum}
     */
    public Checksum getChecksum()
    {
        return this.cksum;
    }

    /**
     * @see java.io.FilterInputStream#read()
     */
    @Override
    public int read() throws IOException
    {
        int b = this.in.read();

        if (b != -1)
        {
            this.cksum.update(b);
        }

        return b;
    }

    /**
     * @see java.io.FilterInputStream#read(byte[])
     */
    @Override
    public int read(final byte[] b) throws IOException
    {
        int bytesRead = this.in.read(b, 0, b.length);

        if (bytesRead != -1)
        {
            this.cksum.update(b, 0, bytesRead);
        }

        return bytesRead;
    }

    /**
     * @see java.io.FilterInputStream#read(byte[], int, int)
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        int bytesRead = this.in.read(b, off, len);

        if (bytesRead != -1)
        {
            this.cksum.update(b, off, bytesRead);
        }

        return bytesRead;
    }
}
