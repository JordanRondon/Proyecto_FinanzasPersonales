package com.example.finanzaspersonales

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class Aplicacion: Application() {
    private lateinit var database: DatabaseReference
    private var userName: String? = null

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "noti_fcm"
        const val RECORDATORIO_CHANNEL_ID = "recordatorio_channel"
        const val RECORDATORIO_HIGH_PRIORITY_CHANNEL_ID = "recordatorio_channel_high_priority"
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseInAppMessaging.getInstance().isAutomaticDataCollectionEnabled = true
        Log.i("FIAM.Headless", "In-App Messaging runtime initialized")

        database = FirebaseDatabase.getInstance().reference

        val currentUsuario = FirebaseAuth.getInstance().currentUser
        if (currentUsuario != null) {
            userName = currentUsuario.uid
        } else {
            Log.e("Aplicacion", "No hay usuario")
        }

        Firebase.messaging.token.addOnCompleteListener {
            if (!it.isSuccessful) {
                println("El token no fue generado")
                return@addOnCompleteListener
            }
            val token = it.result
            println("El token es $token")
        }

        createNotificationChannels()

        val instanceId = FirebaseApp.getInstance().options.projectId
        Log.i("FIAM.Headless", "Starting InAppMessaging runtime with Installation ID $instanceId")
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val fcmChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notificaciones FCM",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Estas son notificaciones desde FCM"
            }

//            val recordatorioChannel = NotificationChannel(
//                RECORDATORIO_CHANNEL_ID,
//                "Notificaciones de Recordatorios",
//                NotificationManager.IMPORTANCE_DEFAULT
//            ).apply {
//                description = "Estas son notificaciones de recordatorios"
//            }
//
//            val highPriorityRecordatorioChannel = NotificationChannel(
//                RECORDATORIO_HIGH_PRIORITY_CHANNEL_ID,
//                "Notificaciones de Recordatorios de Alta Prioridad",
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = "Estas son notificaciones de recordatorios de alta prioridad"
//            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(fcmChannel)
//            notificationManager.createNotificationChannel(recordatorioChannel)
//            notificationManager.createNotificationChannel(highPriorityRecordatorioChannel)
        }
    }
}
