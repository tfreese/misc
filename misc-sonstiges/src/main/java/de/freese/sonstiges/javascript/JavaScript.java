/**
 * Created: 10.10.2012
 */
package de.freese.sonstiges.javascript;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Demo für JavaScriptEngine.
 *
 * @author Thomas Freese
 */
public class JavaScript
{
    /**
     * @param args String[]
     * @throws ScriptException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public static void main(final String[] args) throws ScriptException, IOException
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("js");

        JavaScript javaScript = new JavaScript();
        javaScript.simpleScript(engine);
        javaScript.bindings(engine);
        javaScript.scriptFile(engine);
        javaScript.simpleFunction(engine);
        javaScript.withInterface(engine);
    }

    /**
     * Erstellt ein neues {@link JavaScript} Object.
     */
    public JavaScript()
    {
        super();
    }

    /**
     * @param engine {@link ScriptEngine}
     * @throws ScriptException Falls was schief geht.
     */
    private void bindings(final ScriptEngine engine) throws ScriptException
    {
        StringBuilder script = new StringBuilder();
        script.append("a + b");

        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("a", Integer.valueOf(6));
        bindings.put("b", Integer.valueOf(7));

        Object result = engine.eval(script.toString(), bindings);
        Double summe = (Double) result;

        System.out.printf("%d + %d = %f%n", bindings.get("a"), bindings.get("a"), summe);
        System.out.println();
    }

    /**
     * @param engine {@link ScriptEngine}
     * @throws ScriptException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    private void scriptFile(final ScriptEngine engine) throws ScriptException, IOException
    {
        Conf conf = new Conf();

        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("conf", conf);

        try (Reader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("configuration.js"), StandardCharsets.UTF_8))
        {
            engine.eval(reader, bindings);
        }

        System.out.printf("Threads = %d%n", Integer.valueOf(conf.getThreads()));
        System.out.printf("BlockSize = %d%n", Integer.valueOf(conf.getBlocksize()));
        System.out.println();
    }

    /**
     * @param engine {@link ScriptEngine}
     * @throws ScriptException Falls was schief geht.
     */
    private void simpleFunction(final ScriptEngine engine) throws ScriptException
    {
        try
        {
            StringBuilder script = new StringBuilder();
            script.append("function addiere(a,b)");
            script.append("{");
            script.append(" return a + b;");
            script.append("}");

            engine.eval(script.toString());

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("addiere", Integer.valueOf(5), Integer.valueOf(6));
            Double summe = (Double) result;

            System.out.printf("Summe = %f%n", summe);
        }
        catch (Exception ex)
        {
            System.err.println("Funktion addiere ist nicht vorhanden");
        }

        System.out.println();
    }

    /**
     * @param engine {@link ScriptEngine}
     * @throws ScriptException Falls was schief geht.
     */
    private void simpleScript(final ScriptEngine engine) throws ScriptException
    {
        StringBuilder script = new StringBuilder();
        script.append("var sum = 0;");
        script.append("for(var i = 0; i < 1000; i++)");
        script.append("{");
        script.append(" sum+=i;");
        script.append("}");

        Object result = engine.eval(script.toString());
        Double summe = (Double) result;

        System.out.printf("Summe = %f%n", summe);
        System.out.println();
    }

    /**
     * @param engine {@link ScriptEngine}
     * @throws ScriptException Falls was schief geht.
     */
    private void withInterface(final ScriptEngine engine) throws ScriptException
    {
        engine.eval("function plus(a,b) { return a + b; }");
        engine.eval("function minus(a,b) { return a - b; }");

        Invocable invocable = (Invocable) engine;
        IRechner rechner = invocable.getInterface(IRechner.class);

        System.out.printf("Plus = %f%n", Double.valueOf(rechner.plus(5, 4)));
        System.out.printf("Minus = %f%n", Double.valueOf(rechner.minus(5, 4)));
        System.out.println();
    }
}
