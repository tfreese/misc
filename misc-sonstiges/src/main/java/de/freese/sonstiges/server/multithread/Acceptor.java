// Created: 08.09.2020
package de.freese.sonstiges.server.multithread;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Der {@link Acceptor} nimmt die neuen Client-Verbindungen entgegen und übergibt sie einem {@link Reactor}.<br>
 *
 * @author Thomas Freese
 */
class Acceptor extends AbstractNioProcessor
{
    /**
     *
     */
    private final Supplier<Reactor> reactorSupplier;
    /**
    *
    */
    private final ServerSocketChannel serverSocketChannel;

    /**
     * Erstellt ein neues {@link Acceptor} Object.
     *
     * @param selector {@link Selector}
     * @param serverSocketChannel {@link ServerSocketChannel}
     * @param reactorSupplier {@link Supplier}
     */
    public Acceptor(final Selector selector, final ServerSocketChannel serverSocketChannel, final Supplier<Reactor> reactorSupplier)
    {
        super(selector);

        this.serverSocketChannel = Objects.requireNonNull(serverSocketChannel, "serverSocketChannel required");
        this.reactorSupplier = Objects.requireNonNull(reactorSupplier, "reactorSupplier required");
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#beforeSelectorWhile()
     */
    @Override
    protected void beforeSelectorWhile() throws Exception
    {
        this.serverSocketChannel.register(getSelector(), SelectionKey.OP_ACCEPT);
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#onAcceptable(java.nio.channels.SelectionKey)
     */
    @Override
    protected void onAcceptable(final SelectionKey selectionKey)
    {
        try
        {
            // Verbindung mit Client herstellen.
            SocketChannel socketChannel = this.serverSocketChannel.accept();

            if (socketChannel == null)
            {
                // Falls sich schon ein anderer Acceptor den Channel geschnappt hat.
                // Dewegen ist es auch Blödsinn mehrere zu registrieren, da immer alle regaieren, aber nur einer den Channel hat.
                return;
            }

            getLogger().info("{}: Connection Accepted", socketChannel.getRemoteAddress());

            // Socket dem Reactor übergeben.
            this.reactorSupplier.get().addSession(socketChannel);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }
}
