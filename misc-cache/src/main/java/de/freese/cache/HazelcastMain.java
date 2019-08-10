/**
 * Created: 28.05.2018
 */

package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * https://github.com/hazelcast/hazelcast-code-samples<br>
 * http://docs.hazelcast.org/docs/latest-dev/manual/html-single/index.html
 *
 * @author Thomas Freese
 */
public class HazelcastMain
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        System.setProperty("hazelcast.map.partition.count", "1");

        URL configUrl = ClassLoader.getSystemResource("hazelcast.xml");

        Config config = new XmlConfigBuilder(configUrl).build();
        // config.setProperty("hazelcast.partition.count", "271");

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        // Map ist niemals null.
        IMap<String, String> map = hazelcastInstance.getMap("test");

        ForkJoinPool.commonPool().execute(() -> {
            while (true)
            {
                String value = map.get("key");
                System.out.printf("%s: cache value = %s%n", Thread.currentThread().getName(), value);

                if (value == null)
                {
                    map.put("key", "value");
                }

                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        // main-Thread blockieren.
        System.in.read();

        hazelcastInstance.shutdown();
        Hazelcast.shutdownAll();
    }

    /**
     * Erstellt ein neues {@link HazelcastMain} Object.
     */
    public HazelcastMain()
    {
        super();
    }
}
