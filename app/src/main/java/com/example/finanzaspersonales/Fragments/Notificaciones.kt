package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.NotificacionesAdapter
import com.example.finanzaspersonales.entidades.Notificacion
import java.text.SimpleDateFormat
import java.util.Locale

class Notificaciones : Fragment() {


    var notificaciones = mutableListOf<Notificacion>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notificaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val ventana = listOf("Comida", "Transporte", "Entretenimiento", "Compras", "Salud")
        val texto = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin commodo eu mauris nec ornare. Suspendisse."
        val fecha = "29/05/2024"
        for(element in ventana){
            var notificacion = Notificacion(element,texto,fecha)
            notificaciones.add(notificacion)
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.rw_notificaciones)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = NotificacionesAdapter(notificaciones)
    }
}