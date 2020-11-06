// Created: 14.06.2012
package de.freese.sonstiges.dsl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class NewsSnippet
{
    /**
     *
     */
    private String content;

    /**
     *
     */
    private String description;

    /**
     * Vorgänger
     */
    private NewsSnippet predecessor;

    /**
     * Nachfolger
     */
    private NewsSnippet successor;

    /**
     *
     */
    private List<String> tags = new ArrayList<>();

    /**
     *
     */
    private final String title;

    /**
     *
     */
    private Topic topic;

    /**
     *
     */
    private Date validFrom;

    /**
     *
     */
    private Date validTo;

    /**
     * Erstellt ein neues {@link NewsSnippet} Object.
     *
     * @param title String
     */
    public NewsSnippet(final String title)
    {
        super();

        this.title = title;
    }

    /**
     * @return String
     */
    public String getContent()
    {
        return this.content;
    }

    /**
     * @return String
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @return {@link NewsSnippet}
     */
    public NewsSnippet getPredecessor()
    {
        return this.predecessor;
    }

    /**
     * Nachfolger
     *
     * @return {@link NewsSnippet}
     */
    public NewsSnippet getSuccessor()
    {
        return this.successor;
    }

    /**
     * @return {@link List}<String>
     */
    public List<String> getTags()
    {
        return this.tags;
    }

    /**
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @return {@link Topic}
     */
    public Topic getTopic()
    {
        return this.topic;
    }

    /**
     * @return {@link Date}
     */
    public Date getValidFrom()
    {
        return this.validFrom;
    }

    /**
     * @return {@link Date}
     */
    public Date getValidTo()
    {
        return this.validTo;
    }

    /**
     * @param content String
     */
    void setContent(final String content)
    {
        this.content = content;
    }

    /**
     * @param description String
     */
    void setDescription(final String description)
    {
        this.description = description;
    }

    /**
     * Vorgänger
     *
     * @param predecessor {@link NewsSnippet}
     */
    void setPredecessor(final NewsSnippet predecessor)
    {
        this.predecessor = predecessor;
    }

    /**
     * Nachfolger
     *
     * @param successor {@link NewsSnippet}
     */
    void setSuccessor(final NewsSnippet successor)
    {
        this.successor = successor;
    }

    /**
     * @param topic {@link Topic}
     */
    void setTopic(final Topic topic)
    {
        this.topic = topic;
    }

    /**
     * @param validFrom {@link Date}
     */
    void setValidFrom(final Date validFrom)
    {
        this.validFrom = validFrom;
    }

    /**
     * @param validTo {@link Date}
     */
    void setValidTo(final Date validTo)
    {
        this.validTo = validTo;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String dateFormat = "%1$tY.%1$tm.%1$td"; // %1$tT

        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(" [topic=@");
        builder.append(this.topic.getName());
        builder.append(", title=");
        builder.append(this.title);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", content=");
        builder.append(this.content);
        builder.append(", validFrom=");
        builder.append(String.format(dateFormat, this.validFrom));
        builder.append(", validTo=");
        builder.append(String.format(dateFormat, this.validTo));
        builder.append(", tags=");
        builder.append(this.tags);

        if (this.predecessor != null)
        {
            builder.append(", predecessor=");
            builder.append(String.format(dateFormat, this.predecessor.validFrom));
            builder.append("-");
            builder.append(String.format(dateFormat, this.predecessor.validTo));
        }

        if (this.successor != null)
        {
            builder.append(", successor=");
            builder.append(String.format(dateFormat, this.successor.validFrom));
            builder.append("-");
            builder.append(String.format(dateFormat, this.successor.validTo));
        }

        builder.append("]");

        return builder.toString();
    }
}
