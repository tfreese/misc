package de.freese.misc.junit5;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import java.awt.Point;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The type Test misc.
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Test Junit5")
public class TestDemo
{
    /**
     * The type My object.
     */
    static class MyObject extends Point
    {
        /**
         *
         */
        private static final long serialVersionUID = -2330553112363031008L;

        /**
         * Instantiates a new My object.
         *
         * @param x int
         * @param y int
         */
        public MyObject(final int x, final int y)
        {
            super(x, y);

            System.out.println(this);
        }

        /**
         * @see java.awt.Point#toString()
         */
        @Override
        public String toString()
        {
            return "MyObject{" + "x=" + this.x + ", y=" + this.y + '}';
        }
    }

    /**
     * The type My parameter resolver.
     */
    static class MyParameterResolver implements ParameterResolver
    {
        /**
         *
         */
        static final Random RANDOM = new Random();

        /**
         * @see org.junit.jupiter.api.extension.ParameterResolver#resolveParameter(org.junit.jupiter.api.extension.ParameterContext,
         *      org.junit.jupiter.api.extension.ExtensionContext)
         */
        @Override
        public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException
        {
            if (supportsParameter(parameterContext, extensionContext))
            {
                return MY_OBJECTS[RANDOM.nextInt(MY_OBJECTS.length)];
            }

            return null;
        }

        /**
         * @see org.junit.jupiter.api.extension.ParameterResolver#supportsParameter(org.junit.jupiter.api.extension.ParameterContext,
         *      org.junit.jupiter.api.extension.ExtensionContext)
         */
        @Override
        public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException
        {
            return parameterContext.getParameter().getType() == MyObject.class;
        }
    }

    /**
    *
    */
    static final MyObject[] MY_OBJECTS =
    {
            new MyObject(0, 0), new MyObject(0, 1), new MyObject(1, 0), new MyObject(1, 1)
    };

    /**
     * Create objects stream.
     *
     * @return {@link Stream}
     */
    static Stream<MyObject> createObjects()
    {
        return Stream.of(MY_OBJECTS);
    }

    /**
     * Instantiates a new Test misc.
     */
    public TestDemo()
    {
        super();
    }

    /**
     * @return {@link Stream}
     */
    @TestFactory
    public Stream<DynamicTest> testDynamic()
    {
        // @formatter:off
        return Stream.of(MY_OBJECTS)
                .map(obj -> dynamicTest("Test for: " + obj, () -> assertNotNull(obj))
                )
                ;
        // @formatter:on
    }

    /**
     * @return {@link Stream}
     */
    @TestFactory
    public Stream<DynamicTest> testDynamic2()
    {
        // @formatter:off
        return Stream.of(MY_OBJECTS)
                .flatMap(obj -> Stream.of(
                        dynamicTest("NotNull-Test for: " + obj, () -> assertNotNull(obj)),
                        dynamicTest("X-Test", () -> assertTrue(obj.getX() < 2)),
                        dynamicTest("Y-Test", () -> assertTrue(obj.getY() < 2))
                    )
                )
                ;
        // @formatter:on
    }

    /**
     * @return {@link Stream}
     */
    @TestFactory
    public Stream<DynamicNode> testDynamic3()
    {
        // @formatter:off
        return Stream.of(MY_OBJECTS)
                .map(obj -> dynamicContainer(obj.toString(),
                        Stream.of(dynamicTest("NotNull-Test", () -> assertNotNull(obj)),
                                dynamicContainer("Coords.",
                                        Stream.of(dynamicTest("X-Test", () -> assertTrue(obj.getX() < 2)),
                                                dynamicTest("Y-Test", () -> assertTrue(obj.getY() < 2))
                                        )
                                )
                        )
                    )
                )
                ;
        // @formatter:on
    }

    /**
     * Test method source.
     *
     * @param obj {@link MyObject}
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createObjects")
    @DisplayName("Test @MethodSource")
    @Tag("myTest")
    @EnabledOnOs(
    {
            OS.WINDOWS, OS.LINUX
    })
    @EnabledOnJre(JRE.JAVA_11)
    @DisabledOnJre(JRE.JAVA_8)
    // @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
    // @EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server")
    public void testMethodSource(final MyObject obj)
    {
        assertNotNull(obj);
        assertTrue(obj.getX() < 2);
        assertTrue(obj.getY() < 2);
    }

    /**
     * Test parameter resolver.
     *
     * @param obj {@link MyObject}
     */
    @RepeatedTest(value = 4, name = "{displayName}: {currentRepetition}/{totalRepetitions}")
    @ExtendWith(TestDemo.MyParameterResolver.class)
    @DisplayName("Test @ExtendWith")
    public void testParameterResolver(final MyObject obj)
    {
        assertNotNull(obj);
        assertTrue(obj.getX() < 2);
        assertTrue(obj.getY() < 2);
    }
}
