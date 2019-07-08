import hello.JavaHello;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelloTest
{
    @Test
    public void testAssert()
    {
        Assertions.assertEquals("Hello from Kotlin!", JavaHello.getHelloStringFromKotlin());
        Assertions.assertEquals("Hello from Java!", hello.KotlinHelloKt.getHelloStringFromJava());

        System.out.println(hello.KotlinHelloKt.getHelloStringFromJava());
    }
}
