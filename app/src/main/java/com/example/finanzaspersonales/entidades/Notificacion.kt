package com.example.finanzaspersonales.entidades

data class Notificacion (
    var urlImagen: String,
    var asunto : String,
    var descripcion : String,
    var fecha: String,
    var visto: Boolean = false
)

