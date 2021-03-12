package sudoku5;

import java.util.ListResourceBundle;

/***
 * Dutch language resource
 */
public class LanguageResource_nl_NL extends ListResourceBundle
{
    /**
     *
     */
    static Object[][] contents =
    {
            {
                    "BUTTON_NEW", "Nieuw"
            },
            {
                    "BUTTON_SOLUTION", "Oplossing"
            },
            {
                    "BUTTON_CHECK", "Controleer"
            },
            {
                    "BUTTON_HELP", "Geef hint"
            },
            {
                    "TEXT_SHOW_VALIDS", "Toon mogelijkheden"
            },
            {
                    "TEXT_COPYRIGHT", "Copyright"
            },
            {
                    "TEXT_CREATING", "Bezig met creren nieuwe puzzel"
            },
            {
                    "TEXT_CANCEL", "Annuleren"
            }
    };

    /**
     * Erstellt ein neues {@link LanguageResource_nl_NL} Object.
     */
    public LanguageResource_nl_NL()
    {
        super();
    }

    /**
     * @see java.util.ListResourceBundle#getContents()
     */
    @Override
    public Object[][] getContents()
    {
        return contents;
    }
}