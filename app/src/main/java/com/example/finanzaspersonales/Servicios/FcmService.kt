package com.example.finanzaspersonales.Servicios

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.example.finanzaspersonales.Aplicacion
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService: FirebaseMessagingService(){
    //metodo para comunicacion
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotificacion(message)
    }
    private fun showNotificacion(message: RemoteMessage){
        val notificationManager = getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(this, Aplicacion.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setSmallIcon(android.R.drawable.ic_menu_add)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1,notification)
    }
}