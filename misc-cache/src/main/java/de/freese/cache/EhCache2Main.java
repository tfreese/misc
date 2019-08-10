/**
 * Created: 27.05.2018
 */
package de.freese.cache;

import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * @author Thomas Freese
 */
public class EhCache2Main
{

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        URL configUrl = ClassLoader.getSystemResource("ehcache2.xml");
        CacheManager cacheManager = CacheManager.create(configUrl);
        // CacheManager cacheManager = CacheManager.create();

        Cache defaultCache = cacheManager.getCache("defaultCache");

        if (defaultCache == null)
        {
            System.out.println("defaultCache: " + defaultCache);

            cacheManager.addCacheIfAbsent("defaultCache");
        }

        Cache cache = cacheManager.getCache("myCache");

        // @formatter:off
        Cache testCache = new Cache(
                new CacheConfiguration("testCache", 50)
                  .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
                  .eternal(false)
                  .timeToLiveSeconds(60)
                  .timeToIdleSeconds(30)
                  .diskExpiryThreadIntervalSeconds(0));
        System.out.println("testCache: " + testCache);
        cacheManager.addCache(testCache);
        // @formatter:on

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
                System.out.printf("%s: cache value = %s%n", Thread.currentThread().getName(), value);

                if (value == null)
                {
                    cache.put(new Element("key", "value"));
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
     * Erstellt ein neues {@link EhCache2Main} Object.
     */
    public EhCache2Main()
    {
        super();
    }
}
