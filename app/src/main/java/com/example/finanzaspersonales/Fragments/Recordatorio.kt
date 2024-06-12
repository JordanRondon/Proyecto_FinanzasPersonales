package com.example.finanzaspersonales.Fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.RecordatorioAdapter
import com.example.finanzaspersonales.entidades.Recordatorio
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Recordatorio : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var calendarView: CalendarView
    private lateinit var RecyclerViewRecordatorio: RecyclerView
    private lateinit var adapter: RecordatorioAdapter
    private lateinit var fbAgregarRecordatorio: FloatingActionButton
    private val listaRecordatorio = mutableListOf<Recordatorio>()
    private val eventosCalendario = mutableListOf<EventDay>()
    //private var fechaSeleccionada: Date? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val userName = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        database = FirebaseDatabase.getInstance().reference
        return inflater.inflate(R.layout.fragment_recordatorio, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        //Inicializar vistas
        calendarView = view.findViewById(R.id.calendarView)
        RecyclerViewRecordatorio = view.findViewById(R.id.RVrecordatorios)
        fbAgregarRecordatorio = view.findViewById(R.id.fabAddRecordatorio)

        //Configurar RecyclerView
        RecyclerViewRecordatorio.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecordatorioAdapter(requireContext(),listaRecordatorio)
        RecyclerViewRecordatorio.adapter = adapter

        // Configurar CalendarView
//        calendarView.setOnCalendarDayLongClickListener{ calendarDay  ->
//            val fechaSeleccionada = calendarDay.get
//            obtenerRecordatorios(fechaSeleccionada)
//        }
        //Configurar boton flotante
        fbAgregarRecordatorio.setOnClickListener {
           mostrarDatePickerDialog()
        }

        //OBTENER RECORDATORIOS DE FIREBASE
        obtenerRecordatorios()
    }

    //PARA REGISTRAR RECORDATORIOS MEDIANTE DATEPICKER
    private fun mostrarDatePickerDialog(){
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            {_, year, month, dayOfMonth ->
                val selectDate = Calendar.getInstance()
                selectDate.set(year,month,dayOfMonth)
                val fechaSeleccionada = selectDate.time
                mostrarDialogoAgregarRecordatorio(fechaSeleccionada)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun mostrarRecordatorios(fecha: Date){
        val recordatoriosFiltrados = listaRecordatorio.filter { dateFormat.format(it.fecha) == dateFormat.format(fecha)}
        adapter.actualizarLista(recordatoriosFiltrados)
    }

    private fun mostrarDialogoAgregarRecordatorio(fecha:Date) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Agregar Recordatorio")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_agregar_recordatorio, null)
        val inputDescripcion = view.findViewById<EditText>(R.id.etDescripcion)

        builder.setView(view)

        builder.setPositiveButton("Agregar") { dialog, _ ->
            val descripcion = inputDescripcion.text.toString()
            if (descripcion.isNotEmpty()) {
                val recordatorio = Recordatorio(fecha, descripcion, true)
                listaRecordatorio.add(recordatorio)
                mostrarRecordatorios(fecha)
                AgregarRecordatorioFirebase(recordatorio, userName)
                dialog.dismiss()
                agregarEvento(fecha)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Por favor, ingrese una descripción",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
    private fun agregarEvento(fecha: Date){
        val calendar = Calendar.getInstance().apply { time=fecha }
//        val calendarDay = CalendarDay(calendar)
//        calendarDay.labelColor
//        calendarDay.imageResource
//        eventosCalendario.add(calendarDay)
//        calendarView.setCalendarDays(eventosCalendario)

        val eventDay = EventDay(calendar, R.drawable.noti_icon)

        eventosCalendario.add(eventDay)
        calendarView.setEvents(eventosCalendario)
    }
    //Agregar Recordatorio -> Firebase
    private fun AgregarRecordatorioFirebase(recordatorio: Recordatorio, nombreUsuario: String){
        val refUsuario = database.child("NotificacionPago").child(nombreUsuario)
        val refcontador = refUsuario.child("Contador").child("ultimo_NotificacionPago")

        refcontador.get().addOnSuccessListener { dataSnapshot ->
            val contadorActual = dataSnapshot.getValue(Int::class.java) ?: 0
            val nuevoContador = contadorActual + 1

            refcontador.setValue(nuevoContador)

            //Referencia nuevo recordatorio
            val refNuevoRecordatorio = refUsuario.child(nuevoContador.toString())

            //Registrando en la base de datos, nuevo recordatorio
            refNuevoRecordatorio.setValue(recordatorio).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(requireContext(),"Registro completado exitosamente", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(requireContext(),"Error al registrar",Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener{
            Toast.makeText(requireContext(), "Error al obtener el contador", Toast.LENGTH_SHORT).show()
        }
    }
    //Obtener recordatorios -> Firebase

    private fun obtenerRecordatorios(){
        val refUsuario = database.child("NotificacionPago").child(userName)
        refUsuario.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaRecordatorio.clear()
                eventosCalendario.clear()
                for (recordatorioSnapshot in snapshot.children){
                    if(recordatorioSnapshot.key != "Contador"){
                        val recordatorio = recordatorioSnapshot.getValue(Recordatorio::class.java)
                        if(recordatorio != null){
                            listaRecordatorio.add(recordatorio)
                            agregarEvento(recordatorio.fecha)
                        }
                    }
                }
                val fechaActual = Calendar.getInstance().time
                mostrarRecordatorios(fechaActual)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Recordatorio", "Error al leer los recordatorios", error.toException())
            }
        })

    }

}