package de.freese.misc.junit5;

import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
public class SharedResourcesDemo
{
    /**
     *
     */
    private Properties backup = null;

    /**
     * Erstellt ein neues {@link SharedResourcesDemo} Object.
     */
    public SharedResourcesDemo()
    {
        super();
    }

    /**
     *
     */
    @BeforeEach
    void backup()
    {
        this.backup = new Properties();
        this.backup.putAll(System.getProperties());
    }

    /**
     *
     */
    @Test
    @ResourceLock(value = "system.properties", mode = ResourceAccessMode.READ_WRITE)
    void canSetCustomPropertyToBar()
    {
        System.setProperty("my.prop", "bar");
        Assertions.assertEquals("bar", System.getProperty("my.prop"));
    }

    /**
     *
     */
    @Test
    @ResourceLock(value = "system.properties", mode = ResourceAccessMode.READ_WRITE)
    void canSetCustomPropertyToFoo()
    {
        System.setProperty("my.prop", "foo");
        Assertions.assertEquals("foo", System.getProperty("my.prop"));
    }

    /**
     *
     */
    @Test
    @ResourceLock(value = "system.properties", mode = ResourceAccessMode.READ)
    void customPropertyIsNotSetByDefault()
    {
        Assertions.assertNull(System.getProperty("my.prop"));
    }

    /**
     *
     */
    @AfterEach
    void restore()
    {
        System.setProperties(this.backup);
    }
}
