package com.example.finanzaspersonales.entidades

data class GastoSemanal(
    val fin_semana: String = "",
    val inicio_semana: String = "",
    val resultado: GastoSemanal_resultado = GastoSemanal_resultado()
)