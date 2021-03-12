package sudoku5;

import java.util.ListResourceBundle;

/***
 * Default language resource (English)
 */
public class LanguageResource extends ListResourceBundle
{
    /**
     *
     */
    static Object[][] contents =
    {
            {
                    "BUTTON_NEW", "Create new"
            },
            {
                    "BUTTON_SOLUTION", "Show solution"
            },
            {
                    "BUTTON_CHECK", "Validate"
            },
            {
                    "BUTTON_HELP", "Give hint"
            },
            {
                    "TEXT_SHOW_VALIDS", "Show possibilities"
            },
            {
                    "TEXT_COPYRIGHT", "Copyright"
            },
            {
                    "TEXT_CREATING", "Creating new puzzle..."
            },
            {
                    "TEXT_CANCEL", "Cancel"
            }
    };

    /**
     * Erstellt ein neues {@link LanguageResource} Object.
     */
    public LanguageResource()
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