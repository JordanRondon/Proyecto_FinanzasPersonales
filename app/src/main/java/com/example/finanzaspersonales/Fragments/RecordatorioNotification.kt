package com.example.finanzaspersonales.Fragments
import RecordatorioViewModel
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.finanzaspersonales.Contenedor_Fragment
import com.example.finanzaspersonales.Home
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.RecordatorioViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.concurrent.TimeUnit

class RecordatorioNotification : BroadcastReceiver() {
    companion object {
//        const val CHANNEL_ID = "recordatorio_channel"
//        const val CHANNEL_HIGH_PRIORITY_ID = "recordatorio_channel_high_priority"
        const val NOTI_ID_VENCIDOS = 9
        const val NOTI_ID_PROXIMOS = 10
        const val CANAL_ID = "CanalRecordatorio"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val tipoNotificacion = intent.getStringExtra("tipoNotificacion")
        val id = if (intent.action == "CHECK_VENCIDOS") {
            NOTI_ID_VENCIDOS
        } else {
            NOTI_ID_PROXIMOS
        }

        when (intent.action) {
            "CHECK_VENCIDOS" -> {
                obtenerRecordatoriosVencidos(context) { recordatorios ->
                    val mensajes = recordatorios.joinToString(separator = "\n") { it.descripcion }
                    createNotification(context, tipoNotificacion, mensajes, id)
                }

            }
            "CHECK_PROXIMOS" -> {
                obtenerRecordatoriosProximos(context) { recordatorios ->
                    val mensajes = recordatorios.joinToString(separator = "\n") { it.descripcion }
                    createNotification(context, tipoNotificacion, mensajes, id)

                }
            }
        }
    }
    private fun obtenerRecordatoriosVencidos(context: Context, callback: (List<com.example.finanzaspersonales.entidades.Recordatorio>) -> Unit) {
        val userName = FirebaseAuth.getInstance().currentUser!!.uid
        val database = FirebaseDatabase.getInstance().reference.child("NotificacionPago").child(userName)
        val fechaActual = Calendar.getInstance().time

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recordatoriosVencidos = mutableListOf<com.example.finanzaspersonales.entidades.Recordatorio>()
                for (recordatorioSnapshot in snapshot.children) {
                    if (recordatorioSnapshot.key != "contador") {
                        val recordatorio = recordatorioSnapshot.getValue(com.example.finanzaspersonales.entidades.Recordatorio::class.java)
                        if (recordatorio != null && recordatorio.fecha.before(fechaActual)) {
                            recordatoriosVencidos.add(recordatorio)
                        }
                    }
                }
                Log.d("RecordatorioNotification", "Recordatorios vencidos: ${recordatoriosVencidos.size}")
                callback(recordatoriosVencidos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RecordatorioNotification", "Error al obtener recordatorios vencidos", error.toException())
            }
        })
    }
    private fun obtenerRecordatoriosProximos(context: Context, callback: (List<com.example.finanzaspersonales.entidades.Recordatorio>) -> Unit) {
        val userName = FirebaseAuth.getInstance().currentUser!!.uid
        val database = FirebaseDatabase.getInstance().reference.child("NotificacionPago").child(userName)
        val fechaActual = Calendar.getInstance()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recordatoriosProximos = mutableListOf<com.example.finanzaspersonales.entidades.Recordatorio>()
                for (recordatorioSnapshot in snapshot.children) {
                    if (recordatorioSnapshot.key != "contador") {
                        val recordatorio = recordatorioSnapshot.getValue(com.example.finanzaspersonales.entidades.Recordatorio::class.java)
                        if (recordatorio != null) {
                            val recordatorioFecha = Calendar.getInstance().apply { time = recordatorio.fecha }
                            val diff = recordatorioFecha.timeInMillis - fechaActual.timeInMillis
                            val daysDiff = TimeUnit.MILLISECONDS.toDays(diff)
                            if (daysDiff in 1..2) {
                                recordatoriosProximos.add(recordatorio)
                            }
                        }
                    }
                }
                Log.d("RecordatorioNotification", "Recordatorios próximos a vencer: ${recordatoriosProximos.size}")
                callback(recordatoriosProximos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RecordatorioNotification", "Error al obtener recordatorios próximos a vencer", error.toException())
            }
        })
    }



    private fun createNotification(context: Context, tipoNotificacion: String?, mensajes: String?, id: Int) {
        val intent = Intent(context, Home::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("openRecordatorio", true)
        }
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val notification = NotificationCompat.Builder(context, Recordatorio.CANAL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Recordatorio")
            .setContentText(tipoNotificacion)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensajes))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }
}