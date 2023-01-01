package com.example.stock.facade

import com.example.stock.service.OptimisticLockStockService
import org.springframework.stereotype.Component


@Component
class OptimisticLockStockFacade(
    private val optimisticLockStockService: OptimisticLockStockService
) {
    fun decrease(id: Long, quantity: Long) {
        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity)
                break

            } catch (e: Exception) {
                Thread.sleep(50) //FIXME 긍정락 사용 시 별도의 처리를 해주어야 함
            }
        }
    }
}
