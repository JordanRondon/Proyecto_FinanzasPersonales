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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Aplicacion: Application() {
    private lateinit var database: DatabaseReference
    private var userName:String? = null

    companion object{
        const val NOTIFICATION_CHANNEL_ID = "noti_fcm"
    }
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseInAppMessaging.getInstance().isAutomaticDataCollectionEnabled = true
        Log.i("FIAM.Headless", "In-App Messaging runtime initialized")
        //userName = FirebaseAuth.getInstance().currentUser?.uid
        database=FirebaseDatabase.getInstance().reference

        val currentUsuario = FirebaseAuth.getInstance().currentUser
        if(currentUsuario != null){
            userName = currentUsuario.uid
        }else{
            Log.e("Aplicacion","No hay usuario")
        }

        Firebase.messaging.token.addOnCompleteListener{
            if(!it.isSuccessful){
                println("El token no fue generado")
                return@addOnCompleteListener
            }
            val token = it.result
            println("El token es $token")
        }
        createNotificationChannel()
        val instanceId = FirebaseApp.getInstance().getOptions().getProjectId()
        Log.i("FIAM.Headless", "Starting InAppMessaging runtime with Installation ID $instanceId")
    }
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val canal = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notificaciones FCM",
                NotificationManager.IMPORTANCE_HIGH
            )
            canal.description="Estas son notificaciones desde FCM"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(canal)
        }
    }

}