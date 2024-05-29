package com.example.finanzaspersonales.Fragments

import com.example.finanzaspersonales.adaptadores.RecordatorioAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Recordatorio
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Recordatorio : Fragment() {
    private lateinit var calendarView: CalendarView
    private lateinit var RecyclerViewRecordatorio: RecyclerView
    private lateinit var adapter: RecordatorioAdapter
    private lateinit var fbAgregarRecordatorio: FloatingActionButton
    private val listaRecordatorio = mutableListOf<Recordatorio>()
    private var fechaSeleccionada: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recordatorio, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        RecyclerViewRecordatorio = view.findViewById(R.id.RVrecordatorios)
        fbAgregarRecordatorio = view.findViewById(R.id.fabAddRecordatorio)

        RecyclerViewRecordatorio.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecordatorioAdapter(listaRecordatorio)
        RecyclerViewRecordatorio.adapter = adapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val FechaElegida = "$dayOfMonth/${month+1}/$year"
            mostrarRecordatorios(FechaElegida)
        }
        fbAgregarRecordatorio.setOnClickListener {
            if (fechaSeleccionada != null) {
                mostrarDialogoAgregarRecordatorio(fechaSeleccionada!!)
            } else {
                Toast.makeText(requireContext(), "Por favor, seleccione una fecha", Toast.LENGTH_SHORT).show()
            }
        }
        mostrarDatosEjemplo()
    }
     private fun mostrarDatosEjemplo(){
         listaRecordatorio.add(Recordatorio("1/5/2024","Pagar alquiler"))
         listaRecordatorio.add(Recordatorio("15/5/2024","Cancelar subscripcion"))
         listaRecordatorio.add(Recordatorio("25/5/2024","Pagar deuda Kevin"))

         adapter.notifyDataSetChanged()
     }
    private fun mostrarRecordatorios(fecha: String){
        val recordatoriosFiltrados = listaRecordatorio.filter { it.fecha == fecha}
        adapter.actualizarLista(recordatoriosFiltrados)
    }
}