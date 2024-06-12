package com.example.finanzaspersonales.Clases


data class Presupuesto_Firebase(
    var nombre: String = "",
    val categoriaID: String = "",
    val estado: Boolean = true,
    val fechaCulminacion: String = "",
    val fechaInicio: String = "",
    val monto_actual: Double =0.0,
    val monto_total: Double =0.0,
    val periodo: String = "",
    )
data class Presupuesto_Firebase_insertar(
    val categoriaID: String = "",
    val estado: Boolean = true,
    val fechaCulminacion: String = "",
    val fechaInicio: String = "",
    val monto_actual: Double =0.0,
    val monto_total: Double =0.0,
    val periodo: String = "",
)