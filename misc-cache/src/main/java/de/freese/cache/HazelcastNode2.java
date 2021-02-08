/**
 * Created: 27.05.2018
 */
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

/**
 * @author Thomas Freese
 */
public final class HazelcastNode2
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        URL configUrl = ClassLoader.getSystemResource("hazelcast-node2.xml");
        Config config = new XmlConfigBuilder(configUrl).build();
        // config.setProperty("hazelcast.partition.count", "271");

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        // Map ist niemals null.
        IMap<String, String> map = hazelcastInstance.getMap("test");
        // ReplicatedMap<String, String> map = hazelcastInstance.getReplicatedMap("test1");

        ForkJoinPool.commonPool().execute(() -> {
            while (true)
            {
                String value = map.get("key");
                System.out.printf("HazelcastNode2: %s: cache value = %s%n", Thread.currentThread().getName(), value);

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
     * Erstellt ein neues {@link HazelcastNode2} Object.
     */
    private HazelcastNode2()
    {
        super();
    }
}
