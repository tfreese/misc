/**
 * Created: 03.10.2018
 */

package de.freese.jigsaw.web;

import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

        Assert.assertNotNull(result);
        Assert.assertTrue(!result.isEmpty());
    }
}
