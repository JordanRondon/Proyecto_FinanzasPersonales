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
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class Aplicacion: Application() {
    private lateinit var database: DatabaseReference
    private var userName:String? = null

    companion object{
        const val NOTIFICATION_CHANNEL_ID = "noti_fcm"
    }
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        userName = FirebaseAuth.getInstance().currentUser?.uid
        database=FirebaseDatabase.getInstance().reference

        Firebase.messaging.token.addOnCompleteListener{
            if(!it.isSuccessful){
                println("El token no fue generado")
                return@addOnCompleteListener
            }
            val token = it.result
            userName?.let{
                registrarToken(it,token)
            }?: Log.w("FCM", "User is not logged in, token not registered.")
            Log.d("FCM", "Token: $token")
            println("El token es $token")
        }
        createNotificationChannel()
    }
    private fun registrarToken(user:String, token:String){
        val refToken = database.child("Usuario").child(user).child("Token")
        refToken.setValue(token).addOnCompleteListener{task ->
            if(task.isSuccessful){
                Log.d("FCM", "Token registered successfully")
            }else {
                Log.w("FCM", "Token registration failed", task.exception)
            }
        }
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