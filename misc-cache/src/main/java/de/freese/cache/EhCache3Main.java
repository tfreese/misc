/// **
// * Created: 27.05.2018
// */
// package de.freese.cache;
//
// import java.net.URL;
// import java.util.concurrent.ForkJoinPool;
// import org.ehcache.Cache;
// import org.ehcache.CacheManager;
// import org.ehcache.config.Configuration;
// import org.ehcache.config.builders.CacheManagerBuilder;
// import org.ehcache.xml.XmlConfiguration;
//
/// **
// * @author Thomas Freese
// */
// public class EhCache3Main
// {
//
// /**
// * @param args String[]
// * @throws Exception Falls was schief geht.
// */
// public static void main(final String[] args) throws Exception
// {
// URL configUrl = ClassLoader.getSystemResource("ehcache3.xml");
// Configuration xmlConfig = new XmlConfiguration(configUrl);
// CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
// cacheManager.init();
//
// Cache<String, Object> cache = cacheManager.getCache("defaultCache", String.class, Object.class);
//
// if (cache == null)
// {
// System.err.println("Cache not exist");
// return;
// }
//
// ForkJoinPool.commonPool().execute(() -> {
// while (true)
// {
// Object value = cache.get("key");
// System.out.println("value = " + value);
//
// if (value == null)
// {
// cache.put("key", "value");
// }
//
// try
// {
// Thread.sleep(1000);
// }
// catch (Exception ex)
// {
// ex.printStackTrace();
// }
// }
// });
//
// // main-Thread blockieren.
// System.in.read();
//
// cacheManager.close();
// }
//
// /**
// * Erstellt ein neues {@link EhCache3Main} Object.
// */
// public EhCache3Main()
// {
// super();
// }
// }
