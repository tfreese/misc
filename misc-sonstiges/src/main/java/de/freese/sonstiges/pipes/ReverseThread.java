package de.freese.sonstiges.pipes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Thomas Freese
 */
public class ReverseThread extends Thread
{
    /**
     *
     */
    private BufferedReader in = null;

    /**
     *
     */
    private PrintWriter out = null;

    /**
     * Creates a new {@link ReverseThread} object.
     *
     * @param out {@link PrintWriter}
     * @param in {@link BufferedReader}
     */
    public ReverseThread(final PrintWriter out, final BufferedReader in)
    {
        super();

        this.out = out;
        this.in = in;
    }

    /**
     * @param source String
     * @return String
     */
    private String reverseIt(final String source)
    {
        int i = 0;
        int len = source.length();
        StringBuffer dest = new StringBuffer(len);

        for (i = (len - 1); i >= 0; i--)
        {
            dest.append(source.charAt(i));
        }

        return dest.toString();
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {
        if ((this.out != null) && (this.in != null))
        {
            try
            {
                String input = null;

                while ((input = this.in.readLine()) != null)
                {
                    this.out.println(reverseIt(input));
                    this.out.flush();
                }

                this.out.close();
            }
            catch (IOException ex)
            {
                System.err.println("ReverseThread run: " + ex);
            }
        }
    }
}
