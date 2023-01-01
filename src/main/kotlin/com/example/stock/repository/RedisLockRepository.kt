package com.example.stock.repository

import io.lettuce.core.RedisException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisLockRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {

    fun lock(key: Long): Boolean {
        return redisTemplate
            .opsForValue()
            .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000))
            ?: throw RedisException("")
    }

    fun unlock(key: Long) {
        redisTemplate.delete(generateKey(key))
    }

    fun generateKey(key: Long): String {
        return key.toString()
    }

}
