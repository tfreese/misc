// Created: 05.04.2018
package de.freese.jsync.impl.sender;

import java.nio.file.Path;
import java.util.Objects;

import de.freese.jsync.api.Options;
import de.freese.jsync.api.Sender;

/**
 * Basis-Implementierung des {@link Sender}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSender implements Sender
{
    /**
    *
    */
    private final Path base;

    /**
    *
    */
    private final Options options;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractSender}.
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public AbstractSender(final Options options, final Path base)
    {
        super();

        this.options = Objects.requireNonNull(options, "options required");
        this.base = Objects.requireNonNull(base, "base required");
    }

    /**
     * Liefert das Basis-Verzeichnis.
     *
     * @return base {@link Path}
     */
    protected Path getBase()
    {
        return this.base;
    }

    /**
     * @return {@link Options}
     */
    protected Options getOptions()
    {
        return this.options;
    }
}
