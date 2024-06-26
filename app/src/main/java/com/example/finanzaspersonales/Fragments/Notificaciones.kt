package com.example.finanzaspersonales.Fragments

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finanzaspersonales.Clases.isOnline
//import com.example.finanzaspersonales.Fragments.AlarmNotification.Companion.NOTIFICATION_ID
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.NotificationAdapter
import com.example.finanzaspersonales.databinding.FragmentNotificacionesBinding
import com.example.finanzaspersonales.entidades.Notificacion
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class Notificaciones : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var contador: DatabaseReference
    private lateinit var binding: FragmentNotificacionesBinding
    private var notificationList = mutableListOf<Notificacion>()
    private lateinit var adapter: NotificationAdapter
    private lateinit var testButton: Button

    private lateinit var main: ConstraintLayout
    private lateinit var connection: ConstraintLayout

    /*companion object{
        const val MY_CHANNEL_ID="myChannel"
    }*/
    /*private fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply{
                description = "Descripcion del canal"
            }

            val notificationManager: NotificationManager = requireActivity().getSystemService (Context.NOTIFICATION_SERVICE)as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Get identifier of logged user
        val user = Firebase.auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("Notificacion").child(user)
        contador = FirebaseDatabase.getInstance().getReference("Notificacion").child(user)
            .child("contador").child("ultima_notificacion")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificacionesBinding.inflate(layoutInflater)

        main = binding.root.findViewById(R.id.main)
        connection = binding.root.findViewById(R.id.connection)

        if (!isOnline(requireContext())) {
            connection.visibility = View.VISIBLE
            main.visibility = View.INVISIBLE
        } else {
            connection.visibility = View.INVISIBLE
            main.visibility = View.VISIBLE
            initRecyclerView()
            getNotifications()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*testButton = view.findViewById<Button>(R.id.testButton)
        //createChannel()
        testButton.setOnClickListener{
            //createNotification()
        }*/
    }

    private fun getNotifications() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                showNotification(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showNotification(snapshot: DataSnapshot) {
        for (i in snapshot.children) {
            if (i.key != "contador") {
                val notificacion = Notificacion(
                    i.child("urlImagen").getValue().toString(),
                    i.child("asunto").getValue().toString(),
                    i.child("descripcion").getValue().toString(),
                    i.child("fecha").getValue().toString(),
                    i.child("visto").getValue().toString().toBoolean()
                )
                notificationList.add(0, notificacion)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        adapter = NotificationAdapter(notificationList) { notification ->
            onNotificationSelected(notification)
        }
        binding.rwNotificaciones.layoutManager = LinearLayoutManager(this.context)
        binding.rwNotificaciones.adapter = adapter
    }

    /*private fun createNotification() {
        val sdf= SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val date = sdf.format(Date()).toString()
        val notification = Notificacion("agua_icono", "Gastos","Registra tus gastos no lo olvides!",date,false)
        database.get().addOnSuccessListener {dataSnapshot->
            var  nextNumNotification = 1// default
            for(i in dataSnapshot.children)
                if(i.key != "contador") nextNumNotification += 1
            Toast.makeText(this.context,"Numero de ultima notificacion: "+nextNumNotification,Toast.LENGTH_SHORT).show()

            contador.setValue(nextNumNotification)
            database.child(nextNumNotification.toString()).setValue(notification).addOnSuccessListener {
                Toast.makeText(this.context,"Notificacion creada",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this.context,"Error al crear la notificacion",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this.context,"Error al obtener el numero de notificaciones",Toast.LENGTH_SHORT).show()
        }
        notificationList.add(notification)
        //createNotification(notification)

        scheduleNotification(notification)

        adapter.notifyItemInserted(notificationList.size-1)
    }*/

    /*private fun scheduleNotification(notificacion: Notificacion) {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 17)
        }
        val intent = Intent(this.context, AlarmNotification::class.java)
            .putExtra("asunto",notificacion.asunto)
            .putExtra("descripcion",notificacion.descripcion)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = this.requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,AlarmManager.INTERVAL_HALF_HOUR,pendingIntent)

    }*/

    private fun onNotificationSelected(notificacion: Notificacion) {
        Toast.makeText(this.context, notificacion.asunto, Toast.LENGTH_SHORT).show()
    }
}