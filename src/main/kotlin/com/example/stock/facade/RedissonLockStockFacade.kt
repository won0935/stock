package com.example.stock.facade

import com.example.stock.service.StockService
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

@Component
class RedissonLockStockFacade(
    private val redissonClient: RedissonClient,
    private val stockService: StockService
) {

    fun decrease(id : Long, quantity: Long)
    {
        val lock = redissonClient.getLock(id.toString())

        try {
            val available = lock.tryLock(5, 1, TimeUnit.SECONDS)

            if(!available){
                println("락 획득 실패")
                return
            }

            stockService.decrease(id, quantity)
        } catch (e : InterruptedException){
            throw RuntimeException()
        } finally {
            lock.unlock()
        }

    }
}
