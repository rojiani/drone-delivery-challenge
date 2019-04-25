package com.nrojiani.drone.model

import java.time.LocalDateTime

data class Order(
    val orderId: String,
    val destination: GridCoordinate,
    val dateTime: LocalDateTime
)