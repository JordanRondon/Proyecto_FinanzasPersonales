package com.example.finanzaspersonales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Fragments.Gastos
import com.example.finanzaspersonales.entidades.EntidadGasto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class GastosFragment : Fragment() {
    private lateinit var RecyclerViewHistorial: RecyclerView
    private val historialGasto = mutableListOf<EntidadGasto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial_gastos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RecyclerViewHistorial = view.findViewById(R.id.rvListaGastos)

        val categorias = listOf("Comida", "Transporte", "Entretenimiento", "Compras", "Salud", "asd", "qwe")
        val valores = listOf(50.0f, 30.5f, 20.0f, 40.0f, 15.0f, 56.0f, 67.7f)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Generar datos ficticios para cada categoría
        for (i in 0 until categorias.size) {
            val entidad = EntidadGasto()
            entidad.nombreCategoria = categorias[i]

            val currentDate = Date()
            val fechaFormateada = sdf.format(currentDate)
            entidad.fechaGasto = fechaFormateada

            entidad.valorGasto = valores[i]
            historialGasto.add(entidad)
        }

        val adaptadorPersonalizado = GastoAdapter(requireContext(), historialGasto)

        RecyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())
        RecyclerViewHistorial.adapter = adaptadorPersonalizado
    }
}