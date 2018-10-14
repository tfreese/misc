// Created: 24.04.2017
package de.freese.sonstiges.methodHandle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * @author Thomas Freese
 */
public class MethodHandleDemo
{
    /**
     * @author Thomas Freese
     */
    static class MyPoint
    {
        /**
         *
         */
        int x = 0;

        /**
         *
         */
        int y = 0;

        /**
        *
        */
        int z = 0;
    }

    /**
     * @throws Throwable Falls was schief geht.
     */
    private static void accessFields() throws Throwable
    {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MyPoint point = new MyPoint();

        // Set the x and y fields.
        MethodHandle mh = lookup.findSetter(MyPoint.class, "x", int.class);
        mh.invoke(point, 15);

        mh = lookup.findSetter(MyPoint.class, "y", int.class);
        mh.invoke(point, 30);

        // Get the field values.
        mh = lookup.findGetter(MyPoint.class, "x", int.class);
        int x = (int) mh.invoke(point);
        System.out.printf("x = %d%n", x);

        mh = lookup.findGetter(MyPoint.class, "y", int.class);
        int y = (int) mh.invoke(point);
        System.out.printf("y = %d%n", y);
    }

    /**
     * @throws Throwable Falls was schief geht.
     */
    private static void accessPrivateFields() throws Throwable
    {
        Field field = MyPoint.class.getDeclaredField("z");
        field.setAccessible(true);

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mhSetter = lookup.unreflectSetter(field);

        MyPoint point = new MyPoint();

        // field.set(point, 5);
        // field.get(point);
        mhSetter.invoke(point, 5);

        MethodHandle mhGetter = lookup.unreflectGetter(field);
        int z = (int) mhGetter.invoke(point);

        System.out.printf("z = %d%n", z);
    }

    /**
     * @throws Throwable Falls was schief geht.
     */
    private static void insertArguments() throws Throwable
    {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        MethodHandle mh = lookup.findStatic(Math.class, "pow", MethodType.methodType(double.class, double.class, double.class));
        System.out.printf("2^10 = %f%n", mh.invoke(2.0, 10.0));

        // Vordefinition des 2. Parameters.
        mh = MethodHandles.insertArguments(mh, 1, 10);
        System.out.printf("2^10 = %f%n", mh.invoke(2.0));
    }

    /**
     * @param args String[]
     * @throws Throwable Falls was schief geht.
     */
    public static void main(final String[] args) throws Throwable
    {
        accessFields();
        accessPrivateFields();
        insertArguments();

        // MethodHandle mh = MethodHandles.throwException(Void.class, SQLException.class);
        // mh.invoke(new SQLException("täschd"));
    }

    /**
     * Erzeugt eine neue Instanz von {@link MethodHandleDemo}.
     */
    private MethodHandleDemo()
    {
        super();
    }
}
