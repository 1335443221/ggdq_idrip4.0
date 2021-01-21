package com.sl.common.utils;

import com.sl.common.exception.WebMyException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Set;

@Component
public class RedisPoolUtil {
    Logger logger = Logger.getLogger(RedisPool.class);

    public String set(String key, String value) {
        Jedis jedis = null;
        //jedis返回的结果
        String result = null;
        try {
            jedis = RedisPool.getInstance();
            //设置key-value
            result = jedis.set(key, value);
        } catch (Exception e) {
        } finally {
            returnJedis(jedis);
        }
        return result;
    }

    public String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getInstance();
            //根据key获取value值
            result = jedis.get(key);
        } catch (Exception e) {
        } finally {
            returnJedis(jedis);
        }
        return result;
    }

    public String get(String key, int db) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getInstance();
            jedis.select(db);
            //根据key获取value值
            result = jedis.get(key);
        } catch (Exception e) {
            throw new WebMyException(500, "redis连接失败",e.toString());
        } finally {
            returnJedis(jedis);
        }
        return result;
    }

    public byte[][] mget(Set<byte[]> keySet) {
        Jedis jedis = null;
        byte[][] result = null;
        try {
            jedis = RedisPool.getInstance();
            //根据key获取value值
            byte[][] keys = keySet.toArray(new byte[keySet.size()][]);
            result = jedis.mget(keys).toArray(new byte[keySet.size()][]);
        } catch (Exception e) {
        } finally {
            returnJedis(jedis);
        }
        return result;
    }


    // 关闭 jedis
    public void returnJedis(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error("===================returnBrokenResource===============" + e);
            closeBrokenResource(jedis);
        }
    }

    /**
     * Return jedis connection to the pool, call diFfferent return methods depends on whether the connection is broken
     */
    public void closeBrokenResource(Jedis jedis) {
        try {
            logger.info("===================returnBrokenResource===============");
            RedisPool.returnBrokenResource(jedis);
        } catch (Exception e) {
            logger.error("===================returnBrokenResource===============" + e);
            destroyJedis(jedis);
        }
    }

    /**
     * 在 Jedis Pool 以外强行销毁 Jedis
     */
    public void destroyJedis(Jedis jedis) {
        if (jedis != null) {
            try {
                logger.info("===================destroyJedis===============");
                jedis.quit();
            } catch (Exception e) {
            }

            try {
                jedis.disconnect();
            } catch (Exception e) {
            }
        }
    }
}