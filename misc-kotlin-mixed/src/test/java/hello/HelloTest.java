package hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import hello.JavaHello;

/**
 * @author Thomas Freese
 */
class HelloTest
{
    /**
     *
     */
    @Test
    void testAssert()
    {
        Assertions.assertEquals("Hello from Kotlin!", JavaHello.getHelloStringFromKotlin());
        Assertions.assertEquals("Hello from Java!", hello.KotlinHelloKt.getHelloStringFromJava());

        System.out.println(hello.KotlinHelloKt.getHelloStringFromJava());
    }
}
