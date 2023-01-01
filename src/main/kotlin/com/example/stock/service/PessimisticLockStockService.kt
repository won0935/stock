package com.example.stock.service

import com.example.stock.repository.StockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PessimisticLockStockService(
    private val stockRepository: StockRepository

) {

    @Transactional
    fun decrease(id: Long, quantity: Long) {
        val stock = stockRepository.findByIdWithPessimisticLock(id)
        stock.decrease(quantity)
        stockRepository.saveAndFlush(stock)
    }
}
