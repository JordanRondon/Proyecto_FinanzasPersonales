package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finanzaspersonales.Clases.isOnline
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

class Notificaciones : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var contador: DatabaseReference
    private lateinit var binding: FragmentNotificacionesBinding
    private var notificationList = mutableListOf<Notificacion>()
    private lateinit var adapter: NotificationAdapter

    private lateinit var main: ConstraintLayout
    private lateinit var connection: ConstraintLayout

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
    private fun onNotificationSelected(notificacion: Notificacion) {
        Toast.makeText(this.context, notificacion.asunto, Toast.LENGTH_SHORT).show()
    }
}