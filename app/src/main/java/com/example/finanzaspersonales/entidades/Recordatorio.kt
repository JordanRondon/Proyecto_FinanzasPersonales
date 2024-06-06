package com.example.finanzaspersonales.entidades

import java.util.Date

data class Recordatorio(
    val fecha: Date = Date(),
    val descripcion: String = "",
    val estado: Boolean = true){
    constructor() : this(Date(),"",true)
}
