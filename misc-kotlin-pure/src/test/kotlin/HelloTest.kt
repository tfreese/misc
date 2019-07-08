package hello.tests

import hello.getHelloString
import org.junit.jupiter.api.Test

import kotlin.test.assertEquals

class HelloTest
{
    @Test
    fun testAssert(): Unit
    {
        assertEquals("Hello, world!", getHelloString())
    }
}
