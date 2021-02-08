/**
 * Created: 27.05.2018
 */
package de.freese.cache;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

/**
 * https://reflectoring.io/spring-boot-hazelcast
 *
 * @author Thomas Freese
 */
public final class HazelcastJavaConfigNode2
{
    /**
     * @return {@link HazelcastInstance}
     */
    private static HazelcastInstance getHazelcastInstance()
    {
        MapConfig mapConfig = new MapConfig("test");
        mapConfig.setTimeToLiveSeconds(3);
        mapConfig.setMaxIdleSeconds(3);
        mapConfig.setAsyncBackupCount(0);
        mapConfig.setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.LRU).setMaxSizePolicy(MaxSizePolicy.PER_NODE).setSize(271));
        mapConfig.setStatisticsEnabled(false);
        mapConfig.setInMemoryFormat(InMemoryFormat.BINARY);
        mapConfig.setCacheDeserializedValues(CacheDeserializedValues.INDEX_ONLY);

        Config config = new Config();
        config.addMapConfig(mapConfig);

        // @formatter:off
        config.setNetworkConfig(new NetworkConfig()
                .setPort(5802)
                .setPortAutoIncrement(false)
                .setReuseAddress(true)
                .setInterfaces(new InterfacesConfig()
                        .setEnabled(true)
                        .addInterface("192.168.155.100")
                        )
                .setJoin(new JoinConfig()
                        .setMulticastConfig(new MulticastConfig()
                                .setEnabled(true)
                                .setMulticastGroup("224.2.2.3")
                                .setMulticastPort(54327)
                                .setMulticastTimeToLive(32)
                                .setMulticastTimeoutSeconds(3)
                                .setTrustedInterfaces(Set.of("192.168.155.100"))
                                )
                        .setTcpIpConfig(new TcpIpConfig()
                                .setEnabled(false) // Entweder Multicast oder TCP
                                )
                        )
                );
        // @formatter:on

        // Enable back pressure.
        // Verhindert Überlastung des Clusters
        config.setProperty("hazelcast.backpressure.enabled", Boolean.TRUE.toString());

        // Defines cache invalidation event batch sending frequency in seconds
        config.setProperty("hazelcast.cache.invalidation.batchfrequency.seconds", "15");

        // Number of threads that the client engine has available for processing requests that are blocking, e.g., transactions.
        // When not set, it is set as the value of core size * 20.
        config.setProperty("hazelcast.clientengine.blocking.thread.count", "2");

        // Number of threads to process query requests coming from the clients.
        // Default count is the number of cores multiplied by 1.
        config.setProperty("hazelcast.clientengine.query.thread.count", "2");

        // Maximum number of threads to process non-partition-aware client requests, like map.size(), executor tasks, etc.
        // Default count is the number of cores multiplied by 20.
        config.setProperty("hazelcast.clientengine.thread.count", "2");

        // Number of event handler threads.
        config.setProperty("hazelcast.event.thread.count", "2");

        // Maximum wait in seconds during graceful shutdown.
        config.setProperty("hazelcast.graceful.shutdown.max.wait", "10");

        // Number of socket input threads.
        // config.setProperty("hazelcast.io.input.thread.count", "2");

        // Number of socket output threads.
        // config.setProperty("hazelcast.io.output.thread.count", "2");

        // Number of threads performing socket input and socket output.
        // If, for example, the default value (3) is used, it means there
        // are 3 threads performing input and 3 threads performing output (6 threads in total).
        config.setProperty("hazelcast.io.thread.count", "2");

        // Name of logging framework type to send logging events.
        config.setProperty("hazelcast.logging.type", "slf4j");

        // Total partition count.
        // Default 271
        // config.setProperty("hazelcast.partition.count", "2");

        // Enable or disable the sending of phone home data to Hazelcast’s phone home server.
        config.setProperty("hazelcast.phone.home.enabled", Boolean.FALSE.toString());

        // Enable Hazelcast shutdownhook thread.
        // When this is enabled, this thread terminates the Hazelcast instance without waiting to shutdown gracefully.
        config.setProperty("hazelcast.shutdownhook.enabled", Boolean.FALSE.toString());

        // TERMINATE / GRACEFUL
        config.setProperty("hazelcast.shutdownhook.policy", "GRACEFUL");

        // 2nd Level Cache, nur für HazelcastClients
        // NearCacheConfig nearCacheConfig = new NearCacheConfig();
        // nearCacheConfig.setName("test");
        // nearCacheConfig.setTimeToLiveSeconds(360);
        // nearCacheConfig.setMaxIdleSeconds(60);

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        return hazelcastInstance;
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        HazelcastInstance hazelcastInstance = getHazelcastInstance();

        // Map ist niemals null.
        IMap<String, String> map = hazelcastInstance.getMap("test");
        // ReplicatedMap<String, String> map = hazelcastInstance.getReplicatedMap("test1");

        ForkJoinPool.commonPool().execute(() -> {
            while (true)
            {
                String value = map.get("key");
                System.out.printf("HazelcastJavaConfigNode2: %s: cache value = %s%n", Thread.currentThread().getName(), value);

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
     * Erstellt ein neues {@link HazelcastJavaConfigNode2} Object.
     */
    private HazelcastJavaConfigNode2()
    {
        super();
    }
}
