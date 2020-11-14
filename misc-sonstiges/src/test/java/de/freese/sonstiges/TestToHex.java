// Created: 04.11.2016
package de.freese.sonstiges;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestToHex
{
    /**
     *
     */
    private static final byte[] BYTES = new byte[1000000];

    /**
     *
     */
    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        Random random = new Random(System.currentTimeMillis());

        random.nextBytes(BYTES);
    }

    /**
     *
     */
    @Test
    void test010()
    {
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES)
        {
            int byteValue = element & 0xFF;

            sb.append(HEX_CODE[(byteValue >> 4) & 0xF]);
            sb.append(HEX_CODE[(byteValue & 0xF)]);
        }

        // System.out.println(sb);
        System.out.printf("010: %d ms%n", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    /**
    *
    */
    @Test
    void test020()
    {
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES)
        {
            int byteValue = element & 0xFF;

            sb.append(HEX_CODE[byteValue >> 4]);
            sb.append(HEX_CODE[byteValue & 0xF]);
        }

        // System.out.println(sb);
        System.out.printf("020: %d ms%n", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    /**
    *
    */
    @Test
    void test030()
    {
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(BYTES.length * 2);
        sb.append(DatatypeConverter.printHexBinary(BYTES));

        // System.out.println(sb);
        System.out.printf("030: %d ms%n", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    /**
    *
    */
    @Test
    void test040()
    {
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES)
        {
            sb.append(Integer.toString((element & 0xFF) + 0x100, 16).substring(1).toUpperCase());
        }

        // System.out.println(sb);
        System.out.printf("040: %d ms%n", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    /**
    *
    */
    @Test
    void test050()
    {
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES)
        {
            sb.append(String.format("%02X", element));
        }

        // System.out.println(sb);
        System.out.printf("050: %d ms%n", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    /**
    *
    */
    @Test
    void test060()
    {
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES)
        {
            String hex = Integer.toHexString(element).toUpperCase();

            if (hex.length() == 1)
            {
                sb.append("0");
            }

            sb.append(hex);
        }

        // System.out.println(sb);
        System.out.printf("060: %d ms%n", System.currentTimeMillis() - start);

        assertTrue(true);
    }
}
