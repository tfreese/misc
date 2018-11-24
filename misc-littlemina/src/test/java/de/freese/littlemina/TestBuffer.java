// Created: 01.11.2016
package de.freese.littlemina;

import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import de.freese.littlemina.core.buffer.AbstractIoBuffer;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
        Assert.assertEquals(1024, AbstractIoBuffer.normalizeCapacity(0));

        Assert.assertEquals(1, AbstractIoBuffer.normalizeCapacity(1));
        Assert.assertEquals(2, AbstractIoBuffer.normalizeCapacity(2));
        Assert.assertEquals(4, AbstractIoBuffer.normalizeCapacity(3));
        Assert.assertEquals(16, AbstractIoBuffer.normalizeCapacity(12));
        Assert.assertEquals(32, AbstractIoBuffer.normalizeCapacity(20));
        Assert.assertEquals(128, AbstractIoBuffer.normalizeCapacity(112));
        Assert.assertEquals(1024, AbstractIoBuffer.normalizeCapacity(1000));
        Assert.assertEquals(1024, AbstractIoBuffer.normalizeCapacity(1024));
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
        Assert.assertEquals(3, b2.getInt());

        b1.flip();
        b2.flip();
        b2.limit(b2.capacity()); // Durch mark steht limit noch auf 12.

        Assert.assertEquals(12, b1.capacity());
        Assert.assertEquals(20, b2.capacity());

        Assert.assertEquals(1, b1.getInt());
        Assert.assertEquals(1, b2.getInt());
        Assert.assertEquals(2, b1.getInt());
        Assert.assertEquals(2, b2.getInt());
        Assert.assertEquals(3, b1.getInt());
        Assert.assertEquals(3, b2.getInt());
        Assert.assertEquals(4, b2.getInt());
        Assert.assertEquals(5, b2.getInt());
    }
}
