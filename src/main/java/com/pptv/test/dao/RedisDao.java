package com.pptv.test.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisDao {
    private final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    @Autowired
    private JedisPool jedisPool;
    public void sadd(String key, String members) {
        Jedis jedis = jedisPool.getResource();
        jedis.sadd(key, members);
        jedis.close();
    }
    public void saddWithoutDuplication(String key, String members) {
        Jedis jedis = jedisPool.getResource();
        if (!jedis.sismember(key, members)) {
            jedis.sadd(key, members);
        }
        jedis.close();
    }
    public String spop(String key) {
        Jedis jedis = jedisPool.getResource();
        String ret = jedis.spop(key);
        jedis.close();
        return ret;
    }
    public List<String> blpop(int timeout,String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> ret = jedis.blpop(timeout, key);
        jedis.close();
        return ret;
    }
    public Long lpush(String key, String strings) {
        Jedis jedis = jedisPool.getResource();
        Long ret = jedis.lpush(key, strings);
        jedis.close();
        return ret;
    }
    public void hsetWithExpired(String key, Map<String, String> hash) {
        Jedis jedis = jedisPool.getResource();
        jedis.hmset(key, hash);
        jedis.expire(key, ONE_WEEK_IN_SECONDS);
        jedis.close();
    }
    public void zset(String key, double score, String member) {
        Jedis jedis = jedisPool.getResource();
        jedis.zadd(key, score , member);
        jedis.close();
    }
}