package com.example.finanzaspersonales.Clases

data class Categoria(
    var nombre: String = "",
    var descripcion: String = "",
    var URLicono: String = ""
)
data class Categoria_insertar(

    var descripcion: String = "",
    var URLicono: String = ""
)
data class Presupuesto(val nombre: String, val detalles: String, val URLicono: String)

