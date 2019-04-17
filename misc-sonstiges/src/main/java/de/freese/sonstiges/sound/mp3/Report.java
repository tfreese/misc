/**
 * Created: 28.09.2013
 */
package de.freese.sonstiges.sound.mp3;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 */
final class Report implements Comparable<Report>
{
    /**
     *
     */
    private final File file;

    /**
     *
     */
    private final Set<String> messages = new TreeSet<>();

    /**
     * Erstellt ein neues {@link Report} Object.
     *
     * @param file {@link File}
     */
    Report(final File file)
    {
        super();

        this.file = file;
    }

    /**
     * @param text String
     */
    public void addMessage(final String text)
    {
        this.messages.add(text);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Report o)
    {
        int comp = this.file.compareTo(o.file);

        // if (comp == 0)
        // {
        // comp = this.messages.toString().compareTo(o.messages.toString());
        // }
        return comp;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        Report other = (Report) obj;

        if (this.file == null)
        {
            if (other.file != null)
            {
                return false;
            }
        }
        else if (!this.file.equals(other.file))
        {
            return false;
        }

        if (this.messages == null)
        {
            if (other.messages != null)
            {
                return false;
            }
        }
        else if (!this.messages.equals(other.messages))
        {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = (prime * result) + ((this.file == null) ? 0 : this.file.hashCode());
        result = (prime * result) + ((this.messages == null) ? 0 : this.messages.hashCode());

        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.file.getAbsolutePath());
        sb.append(": ");
        sb.append(this.messages);

        return sb.toString();
    }

    /**
     * @param rootDirectory {@link Path}
     * @return String
     */
    public String toString(final Path rootDirectory)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(rootDirectory.relativize(this.file.toPath()));
        sb.append(": ");
        sb.append(this.messages);

        return sb.toString();
    }
}
