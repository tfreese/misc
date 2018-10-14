// Created: 05.04.2018
package de.freese.jsync.impl.receiver;

import java.net.URI;
import java.nio.file.Paths;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.Receiver;

/**
 * Factory für den {@link Receiver}.<br>
 * Liefert die konkrete Implementierung gemäß {@link URI}.
 *
 * @author Thomas Freese
 */
public class ReceiverFactory
{
    /**
     * Liefert die konkrete Implementierung gemäß {@link URI}.
     *
     * @param options {@link Options}
     * @param target {@link URI}
     * @return {@link Receiver}
     */
    public static Receiver createReceiverFromURI(final Options options, final URI target)
    {
        Receiver receiver = null;

        if (target.getScheme().startsWith("file"))
        {
            receiver = new LocalhostReceiver(options, Paths.get(target.getPath())); // .replaceFirst("/", "")
        }

        if (receiver == null)
        {
            throw new IllegalStateException("no receiver for URI: " + target);
        }

        return receiver;
    }
}
