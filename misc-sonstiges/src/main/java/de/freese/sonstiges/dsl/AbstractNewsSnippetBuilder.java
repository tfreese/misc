//Created: 14.06.2012
package de.freese.sonstiges.dsl;

/**
 * @param <T> Konkreter Objekttyp
 *
 * @author Thomas Freese
 */
public abstract class AbstractNewsSnippetBuilder<T extends AbstractNewsSnippetBuilder<?>>
{
    /**
     *
     */
    private final NewsSnippet objectUnderConstruction;

    /**
     *
     */
    private final Topic topic;

    /**
     * Erstellt ein neues {@link AbstractNewsSnippetBuilder} Object.
     *
     * @param topic                   {@link Topic}
     * @param objectUnderConstruction {@link NewsSnippet}
     */
    protected AbstractNewsSnippetBuilder(final Topic topic,
                                         final NewsSnippet objectUnderConstruction)
    {
        super();

        this.topic = topic;
        this.objectUnderConstruction = objectUnderConstruction;
    }

    /**
     * Erstellt ein neues {@link AbstractNewsSnippetBuilder} Object.
     *
     * @param topic {@link Topic}
     * @param title String
     */
    protected AbstractNewsSnippetBuilder(final Topic topic, final String title)
    {
        super();

        this.topic = topic;

        this.objectUnderConstruction = new NewsSnippet(title);
        this.objectUnderConstruction.setTopic(topic);
    }

    /**
     *
     */
    public abstract void add();

    /**
     * @param content String
     *
     * @return {@link AbstractNewsSnippetBuilder}
     */
    public T containing(final String content)
    {
        getObjectUnderConstruction().setContent(content);

        return (T) this;
    }

    /**
     * @param description String
     *
     * @return {@link AbstractNewsSnippetBuilder}
     */
    public T describe(final String description)
    {
        getObjectUnderConstruction().setDescription(description);

        return (T) this;
    }

    /**
     * @param tags String[]
     *
     * @return {@link AbstractNewsSnippetBuilder}
     */
    public T taggedBy(final String... tags)
    {
        for (String tag : tags)
        {
            if (!getObjectUnderConstruction().getTags().contains(tag))
            {
                getObjectUnderConstruction().getTags().add(tag);
            }
        }

        return (T) this;
    }

    /**
     * @return {@link NewsSnippet}
     */
    protected NewsSnippet getObjectUnderConstruction()
    {
        return this.objectUnderConstruction;
    }

    /**
     * @return {@link Topic}
     */
    protected Topic getTopic()
    {
        return this.topic;
    }
}
