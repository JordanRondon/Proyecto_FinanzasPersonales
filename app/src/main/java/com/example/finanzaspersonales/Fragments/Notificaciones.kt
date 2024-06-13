package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
import java.util.Date

class Notificaciones : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var contador: DatabaseReference
    private lateinit var binding: FragmentNotificacionesBinding
    private var notificationList = mutableListOf<Notificacion>()
    private lateinit var adapter: NotificationAdapter
    private lateinit var testButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Get identifier of logged user
        val user = Firebase.auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("Notificacion").child(user)
        contador = FirebaseDatabase.getInstance().getReference("Notificacion").child(user).child("contador").child("ultima_notificacion")

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificacionesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testButton = view.findViewById<Button>(R.id.testButton)
        testButton.setOnClickListener{createNotification()}
        getNotifications()
        initRecyclerView(view)
    }

    private fun getNotifications() {
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                showNotification(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun showNotification(snapshot: DataSnapshot){
        for(i in snapshot.children){
            if(i.key != "contador"){
                val notificacion = Notificacion(
                    i.child("urlImagen").getValue().toString(),
                    i.child("asunto").getValue().toString(),
                    i.child("descripcion").getValue().toString(),
                    i.child("fecha").getValue().toString(),
                    i.child("visto").getValue().toString().toBoolean())
                notificationList.add(notificacion)
            }
        }
    }

    private fun initRecyclerView(view : View) {
        adapter = NotificationAdapter(notificationList){ notification -> onNotificationSelected(notification)}
        binding.rwNotificaciones.layoutManager = LinearLayoutManager(this.context)
        binding.rwNotificaciones.adapter = adapter
    }

    private fun createNotification() {
        val sdf= SimpleDateFormat("dd/M/yyyy hh:mm:ss")
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
        adapter.notifyItemInserted(notificationList.size-1)
    }
    private fun onNotificationSelected(notificacion: Notificacion){
        Toast.makeText(this.context, notificacion.asunto,Toast.LENGTH_SHORT).show()
    }
}