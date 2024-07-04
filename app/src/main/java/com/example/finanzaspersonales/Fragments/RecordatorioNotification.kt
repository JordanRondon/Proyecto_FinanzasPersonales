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
import com.example.finanzaspersonales.Home
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.RecordatorioViewModelFactory

class RecordatorioNotification : BroadcastReceiver() {
    companion object {
//        const val CHANNEL_ID = "recordatorio_channel"
//        const val CHANNEL_HIGH_PRIORITY_ID = "recordatorio_channel_high_priority"
        const val NOTI_ID_VENCIDOS = 9
        const val NOTI_ID_PROXIMOS = 10
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
                    recordatorios.forEach {
                        createNotification(context, tipoNotificacion, it.descripcion, id)
                    }
                }
            }
            "CHECK_PROXIMOS" -> {
                obtenerRecordatoriosProximos(context) { recordatorios ->
                    recordatorios.forEach {
                        createNotification(context, tipoNotificacion, it.descripcion, id)
                    }
                }
            }
        }
    }

    private fun obtenerRecordatoriosVencidos(context: Context, callback: (List<com.example.finanzaspersonales.entidades.Recordatorio>) -> Unit) {
        val viewModel = RecordatorioViewModel(context.applicationContext as Application)
        viewModel.obtenerRecordatoriosVencidos(callback)
    }

    private fun obtenerRecordatoriosProximos(context: Context, callback: (List<com.example.finanzaspersonales.entidades.Recordatorio>) -> Unit) {
        val viewModel = RecordatorioViewModel(context.applicationContext as Application)
        viewModel.obtenerRecordatoriosProximosAVencer(callback)
    }

    private fun createNotification(context: Context, tipoNotificacion: String?, descripcion: String?, id: Int) {
        val intent = Intent(context, Home::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val notification = NotificationCompat.Builder(context, Recordatorio.CANAL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Recordatorio")
            .setContentText("$tipoNotificacion $descripcion")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }
}