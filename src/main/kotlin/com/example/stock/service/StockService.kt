package com.example.stock.service

import com.example.stock.repository.StockRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(
    private val stockRepository: StockRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun decrease(id: Long, quantity: Long) {
        val stock = stockRepository.findByIdOrNull(id) ?: throw EntityNotFoundException()
        stock.decrease(quantity)
        stockRepository.saveAndFlush(stock)
    }

//    fun decrease(id: Long, quantity: Long) {
//        synchronized(this) { //스레드를 하나만 사용
//            val stock = stockRepository.findByIdOrNull(id) ?: throw EntityNotFoundException()
//            stock.decrease(quantity)
//            stockRepository.saveAndFlush(stock)
//        }
//    }

//    fun decrease(id: Long, quantity: Long) { // NamedLock
//        val stock = stockRepository.findByIdOrNull(id) ?: throw EntityNotFoundException()
//        stock.decrease(quantity)
//        stockRepository.saveAndFlush(stock)
//    }

}
