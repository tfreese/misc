/**
 * Created: 27.05.2018
 */
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * @author Thomas Freese
 */
public final class EhCache2Node2
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        URL configUrl = ClassLoader.getSystemResource("ehcache2-node2.xml");
        CacheManager cacheManager = CacheManager.create(configUrl);
        Cache cache = cacheManager.getCache("myCache");

        if (cache == null)
        {
            System.err.println("Cache not exist");
            return;
        }

        ForkJoinPool.commonPool().execute(() -> {
            while (true)
            {
                Element element = cache.get("key");

                Object value = element != null ? element.getObjectValue() : null;
                System.out.printf("EhCache2Node2: %s: cache value = %s%n", Thread.currentThread().getName(), value);

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
     * Erstellt ein neues {@link EhCache2Node2} Object.
     */
    private EhCache2Node2()
    {
        super();
    }
}
