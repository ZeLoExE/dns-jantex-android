package com.zeloexe.dnsjantex.model

data class AppSettings(
    val autoPingOnSelect: Boolean = true,
    val vibrationFeedback: Boolean = true,
    val keepAlive: Boolean = true
)
