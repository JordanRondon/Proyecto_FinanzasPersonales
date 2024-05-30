package com.example.finanzaspersonales

import android.graphics.Insets.add
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GastosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GastosFragment : Fragment() {

    private lateinit var RecyclerViewHistorial: RecyclerView
    private val historialGasto = mutableListOf<EntidadGastos>()

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
            val entidad = EntidadGastos()
            entidad.nombreCategoria = categorias[i]

            val currentDate = Date()
            val fechaFormateada = sdf.format(currentDate)
            entidad.fechaGasto = fechaFormateada

            entidad.valorGasto = valores[i]
            historialGasto.add(entidad)
        }

        val adaptadorPersonalizado = adaptadorGastos(requireContext(), historialGasto)

        RecyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())
        RecyclerViewHistorial.adapter = adaptadorPersonalizado
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            GastosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}