package hello;

/**
 * @author Thomas Freese
 */
public class JavaHello
{
    /**
     *
     */
    public static String JavaHelloString = "Hello from Java!";

    /**
     * @return String
     */
    public static String getHelloStringFromKotlin()
    {
        return KotlinHelloKt.getKotlinHelloString();
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        System.out.println(getHelloStringFromKotlin());
        System.out.println(KotlinHelloKt.getHelloStringFromJava());
    }
}
