package com.example.finanzaspersonales.Fragments

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.finanzaspersonales.Home
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Notificacion

class AlarmNotification(): BroadcastReceiver() {
    companion object{
        const val NOTI_ID=6
    }
    override fun onReceive(context: Context, intent: Intent?) {
        val asunto = intent!!.getStringExtra("asunto")
        val descripcion = intent.getStringExtra("descripcion")
        Log.d("Schedule","entra 2:  "+asunto+ "  "+descripcion)
        createNotification(context,asunto,descripcion)
    }
    private fun createNotification(context: Context, asunto : String?, descripcion : String?){
        val intent = Intent(context, Home::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        Log.d("Schedule","entra3 flag: "+flag)
        val pendingIntent : PendingIntent = PendingIntent.getActivity(context,0, intent,flag)
        val notification= NotificationCompat.Builder(context, SheetGastos.MI_CANAL_ID)
            .setSmallIcon(R.drawable.moneda)
            .setContentTitle(asunto)
            .setContentText(descripcion)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE ) as NotificationManager
        manager.notify(NOTI_ID, notification)
    }

}