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
        const val NOTI_ID3 = 9
        const val NOTI_ID4 = 10
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RecordatorioNotification", "onReceive called with action: ${intent.action}")
        if (intent.action == "CHECK_VENCIDOS") {
            Log.d("RecordatorioNotification", "Processing CHECK_VENCIDOS action")

            val application = context.applicationContext as Application
            val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(RecordatorioViewModel::class.java)

            viewModel.obtenerRecordatoriosVencidos { recordatoriosVencidos ->
                Log.d("RecordatorioNotification", "Recordatorios vencidos: ${recordatoriosVencidos.size}")
                if (recordatoriosVencidos.isNotEmpty()) {
                    createNotification(context, recordatoriosVencidos)
                } else {
                    Log.d("RecordatorioNotification", "No hay recordatorios vencidos")
                }
            }
        } else {
            Log.d("RecordatorioNotification", "Action did not match CHECK_VENCIDOS")
        }
    }

    private fun createNotification(context: Context, recordatoriosVencidos: List<com.example.finanzaspersonales.entidades.Recordatorio>) {
        Log.d("RecordatorioNotification", "Creating notification")
        val notificationTitle = "Recordatorios vencidos"
        val notificationText = recordatoriosVencidos.joinToString(separator = "\n") { it.descripcion }

        val intent = Intent(context, Home::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val notification = NotificationCompat.Builder(context, Recordatorio.CANAL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTI_ID3, notification)
        Log.d("RecordatorioNotification", "Notificación creada con éxito")
    }
}