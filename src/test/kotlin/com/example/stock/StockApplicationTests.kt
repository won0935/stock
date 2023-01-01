package com.example.stock

import com.example.stock.domain.Stock
import com.example.stock.facade.LettuceLockStockFacade
import com.example.stock.facade.NamedLockStockFacade
import com.example.stock.facade.OptimisticLockStockFacade
import com.example.stock.facade.RedissonLockStockFacade
import com.example.stock.repository.StockRepository
import com.example.stock.service.PessimisticLockStockService
import com.example.stock.service.StockService
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class StockApplicationTests(
    @Autowired
    private val stockService: StockService,
    @Autowired
    private val stockRepository: StockRepository,
    @Autowired
    private val pessimisticLockStockService: PessimisticLockStockService,
    @Autowired
    private val optimisticLockStockFacade: OptimisticLockStockFacade,
    @Autowired
    private val namedLockStockFacade: NamedLockStockFacade,
    @Autowired
    private val lettuceLockStockFacade: LettuceLockStockFacade,
    @Autowired
    private val redissonLockStockFacade: RedissonLockStockFacade,
) {


    @BeforeEach
    fun before() {
        val stock = Stock(productId = 1, quantity = 100)
        stockRepository.saveAndFlush(stock)
    }

    @AfterEach
    fun after() {
        stockRepository.deleteAll()
    }

    @Test
    fun 재고_한개_감소() {
        stockService.decrease(1, 1)

        val stock = stockRepository.findByIdOrNull(1) ?: throw EntityNotFoundException()
        Assertions.assertEquals(99, stock.quantity)
    }

    @Test
    fun 동시에_100개_요청() {
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executor.submit {
                try {
                    stockService.decrease(1, 1)  //FIXME 레이스 컨디션이 일어남
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()

        val stock = stockRepository.findByIdOrNull(1) ?: throw EntityNotFoundException()
        Assertions.assertEquals(0, stock.quantity)
    }


    @Test
    fun 동시에_100개_요청_비관락() {
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executor.submit {
                try {
                    pessimisticLockStockService.decrease(1, 1)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()

        val stock = stockRepository.findByIdOrNull(1) ?: throw EntityNotFoundException()
        Assertions.assertEquals(0, stock.quantity)
    }

    @Test
    fun 동시에_100개_요청_긍정락() {
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executor.submit {
                try {
                    optimisticLockStockFacade.decrease(1, 1)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()

        val stock = stockRepository.findByIdOrNull(1) ?: throw EntityNotFoundException()
        Assertions.assertEquals(0, stock.quantity)
    }


    @Test
    fun 동시에_100개_요청_네임드락() {
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executor.submit {
                try {
                    namedLockStockFacade.decrease(1, 1)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()

        val stock = stockRepository.findByIdOrNull(1) ?: throw EntityNotFoundException()
        Assertions.assertEquals(0, stock.quantity)
    }


    @Test
    fun 동시에_100개_요청_LettuceLock() {
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executor.submit {
                try {
                    lettuceLockStockFacade.decrease(1, 1)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()

        val stock = stockRepository.findByIdOrNull(1) ?: throw EntityNotFoundException()
        Assertions.assertEquals(0, stock.quantity)
    }

    @Test
    fun 동시에_100개_요청_RedissonLock() {
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        for (i in 0..threadCount) {
            executor.submit {
                try {
                    redissonLockStockFacade.decrease(1, 1)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()

        val stock = stockRepository.findByIdOrNull(1) ?: throw EntityNotFoundException()
        Assertions.assertEquals(0, stock.quantity)
    }

}
