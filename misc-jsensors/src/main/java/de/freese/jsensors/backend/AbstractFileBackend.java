// Created: 02.06.2017
package de.freese.jsensors.backend;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Basis-implementierung eines Backends für Dateien.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFileBackend extends AbstractBackend
{
    /**
    *
    */
    private Path basePath = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFileBackend}.
     */
    public AbstractFileBackend()
    {
        super();
    }

    /**
     * Setzt das Basis-Verzeichnis.
     *
     * @param basePath String
     */
    public void setBasePath(final String basePath)
    {
        Objects.requireNonNull(basePath, "basePath required");

        if (basePath.isEmpty())
        {
            throw new IllegalStateException("basePath is empty");
        }

        this.basePath = Paths.get(basePath);
    }

    /**
     * Liefert das Basis-Verzeichnis.
     *
     * @return {@link Path}
     */
    protected Path getBasePath()
    {
        return this.basePath;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#initialize()
     */
    @Override
    protected void initialize() throws Exception
    {
        super.initialize();

        if (getBasePath() == null)
        {
            throw new IllegalStateException("basePath required");
        }

        Files.createDirectories(getBasePath());
    }
}
