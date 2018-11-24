// Created: 10.01.2010
/**
 * 10.01.2010
 */
package de.freese.littlemina.core.processor;

import java.util.function.Consumer;
import java.util.function.Supplier;
import de.freese.littlemina.core.RoundRobinPool;
import de.freese.littlemina.core.session.IoSession;
import de.freese.littlemina.core.session.NioSocketSession;

/**
 * Ein {@link IoProcessor} Pool, welche {@link IoSession}s auf einen oder mehrere<br>
 * {@link IoProcessor}s verteilt und somit bessere Unterstützung für<br>
 * Multi-Prozessor Architekturen bietet.
 *
 * @author Thomas Freese
 */
public final class NioSocketProcessorPool extends RoundRobinPool<NioSocketProcessor> implements IoProcessor<NioSocketSession>
{
    /**
     * Erstellt ein neues {@link NioSocketProcessorPool} Object.
     *
     * @param creator {@link Supplier}
     * @param disposer {@link Consumer}; optional
     */
    public NioSocketProcessorPool(final Supplier<NioSocketProcessor> creator, final Consumer<NioSocketProcessor> disposer)
    {
        super(creator, disposer, DEFAULT_SIZE);
    }

    /**
     * Liefert den {@link IoProcessor} einer {@link IoSession}.
     *
     * @param session {@link IoSession}
     * @return {@link IoProcessor}
     */
    private NioSocketProcessor getProcessor(final IoSession session)
    {
        NioSocketProcessor p = (NioSocketProcessor) session.getAttribute("processor");

        if (p == null)
        {
            p = nextObject();
            session.setAttribute("processor", p);
        }

        return p;
    }

    /**
     * @see de.freese.littlemina.core.processor.IoProcessor#scheduleAdd(de.freese.littlemina.core.session.IoSession)
     */
    @Override
    public void scheduleAdd(final NioSocketSession session)
    {
        getProcessor(session).scheduleAdd(session);
    }

    /**
     * @see de.freese.littlemina.core.processor.IoProcessor#scheduleRemove(de.freese.littlemina.core.session.IoSession)
     */
    @Override
    public void scheduleRemove(final NioSocketSession session)
    {
        getProcessor(session).scheduleRemove(session);
    }

    /**
     * @see de.freese.littlemina.core.processor.IoProcessor#scheduleWrite(de.freese.littlemina.core.session.IoSession)
     */
    @Override
    public void scheduleWrite(final NioSocketSession session) throws Exception
    {
        getProcessor(session).scheduleWrite(session);
    }
}
