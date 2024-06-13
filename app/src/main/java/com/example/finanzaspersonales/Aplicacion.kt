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
        //userName = FirebaseAuth.getInstance().currentUser?.uid
        database=FirebaseDatabase.getInstance().reference

        val currentUsuario = FirebaseAuth.getInstance().currentUser
        if(currentUsuario != null){
            userName = currentUsuario.uid
            //reiniciarGastoSemanal()
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
        //reiniciarGastoSemanal()
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

    private fun reiniciarGastoSemanal() {
        val user = userName
        if(user != null){
            val gastoSemanlRef = FirebaseDatabase.getInstance().getReference("GastoSemanal/$user")

            gastoSemanlRef.get().addOnSuccessListener { datos ->
                val fecha_actual = obtenerFechaActual()
                val fin_semana = datos.child("fin_semana").value as String

                if (fecha_actual.after(convertirFecha(fin_semana))) {
                    val (nuevo_inicio_semana, nuevo_fin_semana) = obtenerInicioYFinDeSemana(fecha_actual)
                    // Actualizar los valores en Firebase
                    val resultadoActualizado = mapOf(
                        "domingo" to 0,
                        "lunes" to 0,
                        "martes" to 0,
                        "miercoles" to 0,
                        "jueves" to 0,
                        "viernes" to 0,
                        "sabado" to 0
                    )
                    val datosActualizados = mapOf(
                        "fin_semana" to nuevo_fin_semana,
                        "inicio_semana" to nuevo_inicio_semana,
                        "resultado" to resultadoActualizado
                    )

                    gastoSemanlRef.updateChildren(datosActualizados).addOnCompleteListener { tarea2 ->
                        if (!tarea2.isSuccessful) {
                            Log.e("FirebaseError", "Error al actualizar los datos: ${tarea2.exception?.message}")
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error al obtener los datos: ${exception.message}")
            }
        }else{
            Log.e("Aplicacion","El usuario no esta autenticado")
        }
    }

    private fun obtenerFechaActual(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    private fun convertirFecha(fechaString: String): Date {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatoFecha.parse(fechaString)!!
    }

    private  fun obtenerInicioYFinDeSemana(fecha: Date): Pair<String, String> {
        val calendar = Calendar.getInstance().apply {
            time = fecha
        }

        val inicioSemana = calendar.clone() as Calendar
        inicioSemana.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            inicioSemana.add(Calendar.WEEK_OF_YEAR, -1)
        }

        val finSemana = inicioSemana.clone() as Calendar
        finSemana.add(Calendar.DAY_OF_WEEK, 6)

        // Formatear las fechas como cadenas
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inicioSemanaStr = formatoFecha.format(inicioSemana.time)
        val finSemanaStr = formatoFecha.format(finSemana.time)

        return Pair(inicioSemanaStr, finSemanaStr)
    }
}