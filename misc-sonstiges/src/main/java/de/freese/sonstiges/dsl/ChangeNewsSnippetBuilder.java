//Created: 14.06.2012
package de.freese.sonstiges.dsl;

import java.util.Date;

/**
 * @author Thomas Freese
 */
public class ChangeNewsSnippetBuilder extends AbstractNewsSnippetBuilder<ChangeNewsSnippetBuilder>
{
    /**
     *
     */
    private NewsSnippet oldNewsSnippet;

    /**
     * Erstellt ein neues {@link ChangeNewsSnippetBuilder} Object.
     *
     * @param topic          {@link Topic}
     * @param oldNewsSnippet {@link NewsSnippet}
     */
    public ChangeNewsSnippetBuilder(final Topic topic, final NewsSnippet oldNewsSnippet)
    {
        super(topic, oldNewsSnippet.getTitle());

        this.oldNewsSnippet = oldNewsSnippet;

        changeTo();
    }

    /**
     * @see de.freese.sonstiges.dsl.AbstractNewsSnippetBuilder#add()
     */
    @Override
    public void add()
    {
        // Vorgänger/Nachfolger setzen.
        getObjectUnderConstruction().setPredecessor(this.oldNewsSnippet);
        this.oldNewsSnippet.setSuccessor(getObjectUnderConstruction());

        getTopic().getNewsSnippets().remove(this.oldNewsSnippet);
        getTopic().getNewsSnippets().add(getObjectUnderConstruction());
    }

    /**
     * @param to {@link Date}
     *
     * @return {@link AbstractNewsSnippetBuilder}
     */
    public ChangeNewsSnippetBuilder validTo(final Date to)
    {
        getObjectUnderConstruction().setValidTo(to);

        return this;
    }

    /**
     * Daten kopieren.
     *
     * @return {@link ChangeNewsSnippetBuilder}
     */
    private ChangeNewsSnippetBuilder changeTo()
    {
        getObjectUnderConstruction().setContent(this.oldNewsSnippet.getContent());
        getObjectUnderConstruction().setDescription(this.oldNewsSnippet.getDescription());
        getObjectUnderConstruction().getTags().addAll(this.oldNewsSnippet.getTags());
        getObjectUnderConstruction().setValidFrom(this.oldNewsSnippet.getValidFrom());
        getObjectUnderConstruction().setValidTo(this.oldNewsSnippet.getValidTo());

        return this;
    }
}
