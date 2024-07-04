package com.example.finanzaspersonales.Fragments

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import AgregarRecordatorioBottomSheet
import RecordatorioDialogFragment
import android.content.Context
import RecordatorioViewModel
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.finanzaspersonales.Fragments.RecordatorioNotification.Companion.NOTI_ID_PROXIMOS
import com.example.finanzaspersonales.Fragments.RecordatorioNotification.Companion.NOTI_ID_VENCIDOS
import com.example.finanzaspersonales.Listeners.CustomDayClickListener
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.RecordatorioViewModelFactory
import com.example.finanzaspersonales.adaptadores.RecordatorioAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.Locale


class Recordatorio : Fragment() {
    private val viewModel: RecordatorioViewModel by viewModels {
        RecordatorioViewModelFactory(requireActivity().application)
    }
    private lateinit var calendarView: CalendarView
    private lateinit var RecyclerViewRecordatorio: RecyclerView
    private lateinit var adapter: RecordatorioAdapter
    private lateinit var fbAgregarRecordatorio: FloatingActionButton
    private val eventosCalendario = mutableListOf<EventDay>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    companion object {
        const val CANAL_ID = "CanalRecordatorio"
        const val REQUEST_CODE_PERMISSIONS = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recordatorio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicializar vistas
        calendarView = view.findViewById(R.id.calendarView)
        RecyclerViewRecordatorio = view.findViewById(R.id.RVrecordatorios)
        fbAgregarRecordatorio = view.findViewById(R.id.fabAddRecordatorio)
        crearCanalDeNotificacion()
        // Configurar RecyclerView
        RecyclerViewRecordatorio.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecordatorioAdapter(requireContext(), emptyList(), { recordatorio, recordatorioId ->
            val editDialog = RecordatorioDialogFragment(recordatorio, recordatorioId) { recordatorioActualizado, id ->
                viewModel.actualizarRecordatorio(recordatorioActualizado, id)
            }
            editDialog.show(childFragmentManager, editDialog.tag)
        }, { recordatorioId ->
            mostrarDialogoEliminar(recordatorioId)
        })
        RecyclerViewRecordatorio.adapter = adapter

        // Configurar CalendarView
        calendarView.setOnDayClickListener(CustomDayClickListener(calendarView) { fechaSeleccionada ->
            mostrarRecordatorios(fechaSeleccionada)
        })

        // Configurar botón flotante
        fbAgregarRecordatorio.setOnClickListener {
            val bottomSheet = AgregarRecordatorioBottomSheet { recordatorio ->
                viewModel.agregarRecordatorio(recordatorio)
            }
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        // Observa los cambios en la lista de recordatorios
        viewModel.recordatorios.observe(viewLifecycleOwner, Observer { listaRecordatorio ->
            adapter.actualizarLista(listaRecordatorio)
            eventosCalendario.clear()
            listaRecordatorio.forEach { agregarEvento(it.second.fecha) }
        })

        // Solicitar permisos de notificación si no se han concedido
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    SheetGastos.REQUEST_CODE_PERMISSIONS
                )
            }else {
                // Programar alarma diaria si los permisos ya están concedidos
                programarAlarmas()
            }
        }else {
            // Programar alarma diaria para versiones anteriores a Android TIRAMISU
            programarAlarmas()
        }
    }

    private fun mostrarRecordatorios(fecha: Date) {
        val recordatoriosFiltrados = viewModel.recordatorios.value?.filter {
            dateFormat.format(it.second.fecha) == dateFormat.format(fecha)
        } ?: emptyList()
        adapter.actualizarLista(recordatoriosFiltrados)
    }

    private fun agregarEvento(fecha: Date) {
        val calendar = Calendar.getInstance().apply { time = fecha }
        val eventDay = EventDay(calendar, R.drawable.noti_icon)
        eventosCalendario.add(eventDay)
        calendarView.setEvents(eventosCalendario)
    }

    private fun mostrarDialogoEliminar(recordatorioId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Desea eliminar este recordatorio?")
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("SI") { dialog, _ ->
                viewModel.eliminarRecordatorio(recordatorioId)
                dialog.dismiss()
            }
            .show()
    }

    private fun setAlarm(context: Context, hour: Int, minute: Int, id: Int, descripcion: String) {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val intent = Intent(context, RecordatorioNotification::class.java).apply {
            putExtra("descripcion", descripcion)
            putExtra("id", id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis, 20000,  // Repite cada 20 segundos para propósitos de prueba
            pendingIntent
        )
        Log.d("Alarma", "Programada notificación de recordatorio")
    }
    private fun programarAlarmas() {
        programarAlarmaDiariaVencidos()
        programarAlarmaDiariaProximos()
    }
    private fun programarAlarmaDiariaVencidos() {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 1)  // Ajusta la hora según tu necesidad
            set(Calendar.MINUTE, 39)
        }

        val intent = Intent(requireContext(), RecordatorioNotification::class.java).apply {
            action = "CHECK_VENCIDOS"
            putExtra("tipoNotificacion", "Recordatorio vencido: ")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            NOTI_ID_VENCIDOS,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            20000,  // Repite diariamente
            pendingIntent
        )
        Log.d("Alarma", "Programada alarma diaria para vencidos a las ${calendar.time}")
    }
    private fun programarAlarmaDiariaProximos() {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 1)  // Ajusta la hora según tu necesidad
            set(Calendar.MINUTE, 39)
        }

        val intent = Intent(requireContext(), RecordatorioNotification::class.java).apply {
            action = "CHECK_PROXIMOS"
            putExtra("tipoNotificacion", "Recordatorio próximo a vencer: ")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            NOTI_ID_PROXIMOS,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            20000,  // Repite diariamente
            pendingIntent
        )
        Log.d("Alarma", "Programada alarma diaria para próximos a vencer a las ${calendar.time}")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(requireContext(), "Permisos de notificación concedidos", Toast.LENGTH_SHORT).show()
                programarAlarmas()
            } else {
                Toast.makeText(requireContext(), "Permisos de notificación denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Canal Recordatorio"
            val descripcion = "Descripción canal"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CANAL_ID, nombre, importancia).apply {
                description = descripcion
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

}
