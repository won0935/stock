package com.example.stock.facade

import com.example.stock.repository.RedisLockRepository
import com.example.stock.service.StockService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class LettuceLockStockFacade(
    private val redisLockRepository: RedisLockRepository,
    private val stockService: StockService
) {

    @Transactional
    fun decrease(id: Long, quantity: Long) {

        while (!redisLockRepository.lock(id)){
            Thread.sleep(100)  //락잡음
        }

        try {
            stockService.decrease(id, quantity)
        } finally {
            redisLockRepository.unlock(id) //락품
        }
    }
}
