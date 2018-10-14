// Created: 05.04.2018
package de.freese.jsync.impl.sender;

import java.net.URI;
import java.nio.file.Paths;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.Sender;

/**
 * Factory für den {@link Sender}.<br>
 * Liefert die konkrete Implementierung gemäß {@link URI}.
 *
 * @author Thomas Freese
 */
public class SenderFactory
{
    /**
     * Liefert die konkrete Implementierung gemäß {@link URI}.
     *
     * @param options {@link Options}
     * @param source {@link URI}
     * @return {@link Sender}
     */
    public static Sender createSenderFromURI(final Options options, final URI source)
    {
        Sender sender = null;

        if (source.getScheme().startsWith("file"))
        {
            sender = new LocalhostSender(options, Paths.get(source.getPath())); // .replaceFirst("/", "")
        }

        if (sender == null)
        {
            throw new IllegalStateException("no sender for URI: " + source);
        }

        return sender;
    }
}
