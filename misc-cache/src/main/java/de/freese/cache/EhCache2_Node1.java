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
public class EhCache2_Node1
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
                System.out.println("value = " + value);

                if (value == null && atomicInteger.get() < 8)
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
     * Erstellt ein neues {@link EhCache2_Node1} Object.
     */
    public EhCache2_Node1()
    {
        super();
    }
}
