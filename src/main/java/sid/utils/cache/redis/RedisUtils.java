package sid.utils.cache.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {

    private static JedisPool pool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(5);
        config.setMaxTotal(10);
        pool = new JedisPool(config,"192.168.96.128",6379);
    }

    public static String get(String key){
        return pool.getResource().get(key);
    }

    public static boolean set(String key,String value){
        return "OK".equalsIgnoreCase(pool.getResource().set(key, value));
    }

    public static boolean set(String key,String value,int validSecond){
        return "OK".equalsIgnoreCase(pool.getResource().set(key, value, "NX", "EX", validSecond));
    }
}
