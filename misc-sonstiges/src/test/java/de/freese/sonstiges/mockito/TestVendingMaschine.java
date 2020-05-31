/**
 * 06.11.2013
 */
package de.freese.sonstiges.mockito;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author Thomas Freese
 */
public class TestVendingMaschine
{
    /**
     * Erstellt ein neues {@link TestVendingMaschine} Objekt.
     */
    public TestVendingMaschine()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testWithMockito() throws Exception
    {
        ICashBox cashBox = Mockito.mock(ICashBox.class);
        Mockito.when(cashBox.getCurrentAmount()).thenReturn(42);

        Mockito.doThrow(new IllegalArgumentException("Invalid value")).when(cashBox).withdraw(ArgumentMatchers.intThat(new NonNegativeIntegerMatcher()));

        IBox box = Mockito.mock(IBox.class);
        Mockito.when(box.isEmpty()).thenReturn(Boolean.FALSE);
        Mockito.when(box.getPrice()).thenReturn(42);

        IBox[] boxes = new IBox[]
        {
                box
        };
        VendingMaschine maschine = new VendingMaschine(cashBox, boxes);
        maschine.selectItem(0);

        // Sicherstellen das Methoden mit diesen Parametern einmal aufgerufen wurden.
        Mockito.verify(cashBox).withdraw(ArgumentMatchers.eq(42));
        Mockito.verify(box, Mockito.times(1)).releaseItem();
    }
}
