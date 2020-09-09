// Created: 09.09.2020
package de.freese.sonstiges.server.multithread;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der {@link Reactor} kümmert sich asynchron um das weitere Connection-Handling für nur einen Client.<br>
 * Dafür muss der Server mit einem ThreadPool arbeiten !!!<br>
 * Der {@link IoHandler} übernimmt das Lesen und Schreiben von Request und Response,<br>
 * und beendet die Session durch den Aufruf von:<br>
 *
 * <pre>
 * <code>
 * socketChannel.close();
 * selectionKey.cancel();
 * selectionKey.selector().wakeup();
 * </code>
 * </pre>
 *
 * @author Thomas Freese
 */
class ReactorSingleClient extends Reactor
{
    /**
     * Erstellt ein neues {@link ReactorSingleClient} Object.
     *
     * @param selector {@link Selector}
     * @param ioHandler {@link IoHandler}
     */
    public ReactorSingleClient(final Selector selector, final IoHandler<SelectionKey> ioHandler)
    {
        super(selector, ioHandler);
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#exitCondition(int)
     */
    @Override
    protected boolean exitCondition(final int readyChannels)
    {
        // Abbruch, sobald der Channel keine Aktion ausführen will und keine neuen Sessions mehr vorhanden sind.
        return ((readyChannels == 0) && getNewSessions().isEmpty()) || !getSelector().isOpen();
    }
}
