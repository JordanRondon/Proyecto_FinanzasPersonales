package com.example.finanzaspersonales.Fragments


import AgregarRecordatorioBottomSheet
import RecordatorioViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.finanzaspersonales.Clases.isOnline
import com.example.finanzaspersonales.Listeners.CustomDayClickListener
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.RecordatorioAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Recordatorio : Fragment() {
    private val viewModel: RecordatorioViewModel by viewModels()
    private lateinit var calendarView: CalendarView
    private lateinit var RecyclerViewRecordatorio: RecyclerView
    private lateinit var adapter: RecordatorioAdapter
    private lateinit var fbAgregarRecordatorio: FloatingActionButton
    private val eventosCalendario = mutableListOf<EventDay>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recordatorio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Inicializar vistas
        calendarView = view.findViewById(R.id.calendarView)
        RecyclerViewRecordatorio = view.findViewById(R.id.RVrecordatorios)
        fbAgregarRecordatorio = view.findViewById(R.id.fabAddRecordatorio)

        //Configurar RecyclerView
        RecyclerViewRecordatorio.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecordatorioAdapter(requireContext(), emptyList())
        RecyclerViewRecordatorio.adapter = adapter

        //Configurar CalendarView
        calendarView.setOnDayClickListener(CustomDayClickListener(calendarView) { fechaSeleccionada ->
            mostrarRecordatorios(fechaSeleccionada)
        })

        //Configurar boton flotante
        fbAgregarRecordatorio.setOnClickListener {
            val bottomSheet = AgregarRecordatorioBottomSheet { recordatorio ->
                viewModel.agregarRecordatorio(recordatorio)
                agregarEvento(recordatorio.fecha)
            }
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        // Observa los cambios en la lista de recordatorios
        viewModel.recordatorios.observe(viewLifecycleOwner, Observer { listaRecordatorio ->
            adapter.actualizarLista(listaRecordatorio)
            eventosCalendario.clear()
            listaRecordatorio.forEach { agregarEvento(it.fecha) }
        })
    }

    private fun mostrarRecordatorios(fecha: Date) {
        val recordatoriosFiltrados = viewModel.recordatorios.value?.filter {
            dateFormat.format(it.fecha) == dateFormat.format(fecha)
        } ?: emptyList()
        adapter.actualizarLista(recordatoriosFiltrados)
    }

    private fun agregarEvento(fecha: Date) {
        val calendar = Calendar.getInstance().apply { time = fecha }
        val eventDay = EventDay(calendar, R.drawable.noti_icon)
        eventosCalendario.add(eventDay)
        calendarView.setEvents(eventosCalendario)
    }
}
