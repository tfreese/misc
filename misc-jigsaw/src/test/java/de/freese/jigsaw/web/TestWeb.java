/**
 * Created: 03.10.2018
 */

package de.freese.jigsaw.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.function.Supplier;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestWeb
{
    /**
     * Erstellt ein neues {@link TestWeb} Object.
     */
    public TestWeb()
    {
        super();
    }

    /**
     *
     */
    @Test
    public void testWeb()
    {
        Supplier<String> supplier = new HttpQuery();

        String result = supplier.get();

        System.out.println(result);

        assertNotNull(result);
        assertTrue(!result.isEmpty());
    }
}
