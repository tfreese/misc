/**
 * Created: 27.05.2018
 */
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * @author Thomas Freese
 */
public final class EhCache2Node1
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        URL configUrl = ClassLoader.getSystemResource("ehcache2-node1.xml");
        CacheManager cacheManager = CacheManager.create(configUrl);
        Cache cache = cacheManager.getCache("myCache");

        if (cache == null)
        {
            System.err.println("Cache not exist");
            return;
        }

        AtomicInteger atomicInteger = new AtomicInteger(0);

        ForkJoinPool.commonPool().execute(() -> {
            while (true)
            {
                Element element = cache.get("key");

                Object value = element != null ? element.getObjectValue() : null;
                System.out.printf("EhCache2Node1: %s: cache value = %s%n", Thread.currentThread().getName(), value);

                if (value == null)
                {
                    cache.put(new Element("key", "value" + atomicInteger.getAndIncrement()));
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

        cacheManager.shutdown();
    }

    /**
     * Erstellt ein neues {@link EhCache2Node1} Object.
     */
    private EhCache2Node1()
    {
        super();
    }
}
