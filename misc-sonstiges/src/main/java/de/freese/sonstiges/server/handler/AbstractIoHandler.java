/**
 * Created: 04.11.2018
 */

package de.freese.sonstiges.server.handler;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verarbeitet den Request und Response.<br>
 * Basis-Implementierung des {@link IoHandler}.
 *
 * @author Thomas Freese
 * @param <T> Type
 * @see IoHandler
 */
public abstract class AbstractIoHandler<T> implements IoHandler<T>
{
    /**
    *
    */
    private final ThreadLocal<CharsetDecoder> CHARSET_DECODER = ThreadLocal.withInitial(() -> {
        CharsetDecoder decoder = getCharset().newDecoder();

        return decoder;
    });

    /**
    *
    */
    private final ThreadLocal<CharsetEncoder> CHARSET_ENCODER = ThreadLocal.withInitial(() -> {
        CharsetEncoder encoder = getCharset().newEncoder();

        return encoder;
    });

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractIoHandler} Object.
     */
    protected AbstractIoHandler()
    {
        super();
    }

    /**
     * @return {@link CharsetEncoder}
     */
    protected CharsetDecoder getCharsetDecoder()
    {
        return this.CHARSET_DECODER.get().reset();
    }

    /**
     * @return {@link CharsetEncoder}
     */
    protected CharsetEncoder getCharsetEncoder()
    {
        return this.CHARSET_ENCODER.get().reset();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
