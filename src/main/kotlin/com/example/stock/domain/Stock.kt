package com.example.stock.domain

import jakarta.persistence.*

@Entity
class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val productId: Long = 0L,
    var quantity: Long = 0L,

    @Version //긍정락
    val version: Long = 0L,

    ) {
    fun decrease(quantity: Long) {

        if (this.quantity - quantity < 0)
            throw RuntimeException("$quantity 0개 미만")

        this.quantity -= quantity
    }

}
