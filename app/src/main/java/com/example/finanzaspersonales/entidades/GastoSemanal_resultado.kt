package com.example.finanzaspersonales.entidades

data class GastoSemanal_resultado(
    val domingo: Int = 0,
    val jueves: Int = 0,
    val lunes: Int = 0,
    val martes: Int = 0,
    val miercoles: Int = 0,
    val sabado: Int = 0,
    val viernes: Int = 0
)