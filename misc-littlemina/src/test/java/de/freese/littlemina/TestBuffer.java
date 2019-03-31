// Created: 01.11.2016
package de.freese.littlemina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.littlemina.core.buffer.AbstractIoBuffer;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestBuffer
{
    /**
     * Erzeugt eine neue Instanz von {@link TestBuffer}
     */
    public TestBuffer()
    {
        super();
    }

    /**
    *
    */
    @Test
    public void text010NormalizeCapacity()
    {
        assertEquals(1024, AbstractIoBuffer.normalizeCapacity(0));

        assertEquals(1, AbstractIoBuffer.normalizeCapacity(1));
        assertEquals(2, AbstractIoBuffer.normalizeCapacity(2));
        assertEquals(4, AbstractIoBuffer.normalizeCapacity(3));
        assertEquals(16, AbstractIoBuffer.normalizeCapacity(12));
        assertEquals(32, AbstractIoBuffer.normalizeCapacity(20));
        assertEquals(128, AbstractIoBuffer.normalizeCapacity(112));
        assertEquals(1024, AbstractIoBuffer.normalizeCapacity(1000));
        assertEquals(1024, AbstractIoBuffer.normalizeCapacity(1024));
    }

    /**
     *
     */
    @Test
    public void text020Buffer()
    {
        ByteBuffer b1 = ByteBuffer.allocate(12);
        b1.putInt(1);
        b1.putInt(2);
        b1.mark(); // Index 8
        b1.putInt(3);

        ByteBuffer b2 = AbstractIoBuffer.createNewByteBuffer(b1, 20, 8);
        b2.putInt(4);
        b2.putInt(5);

        // Mark prüfen.
        b2.reset();
        assertEquals(3, b2.getInt());

        b1.flip();
        b2.flip();
        b2.limit(b2.capacity()); // Durch mark steht limit noch auf 12.

        assertEquals(12, b1.capacity());
        assertEquals(20, b2.capacity());

        assertEquals(1, b1.getInt());
        assertEquals(1, b2.getInt());
        assertEquals(2, b1.getInt());
        assertEquals(2, b2.getInt());
        assertEquals(3, b1.getInt());
        assertEquals(3, b2.getInt());
        assertEquals(4, b2.getInt());
        assertEquals(5, b2.getInt());
    }
}
