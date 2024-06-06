package com.example.finanzaspersonales.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.Home
import com.example.finanzaspersonales.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.log

class Login : Fragment() {

    private lateinit var btnInicioSecion: Button
    private lateinit var tvRegistrate: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvContrasenia: TextView
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        tvRegistrate = view.findViewById(R.id.tvRegistrate)
        tvCorreo = view.findViewById(R.id.tvCorreo)
        tvContrasenia = view.findViewById(R.id.tvContrasenia)
        btnInicioSecion = view.findViewById(R.id.btnInicioSecion)


        tvRegistrate.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        btnInicioSecion.setOnClickListener {
            login()
        }

        return view
    }

    private fun login() {
        database = FirebaseDatabase.getInstance().reference

        val correo = tvCorreo.text.toString()
        val contra = tvContrasenia.text.toString()

        database.child("Usuario").child("Admin").get().addOnSuccessListener { it ->
            if (it.exists()) {
                val getCorreo = it.child("correo").value.toString()
                val getContrasenia = it.child("contraseña").value.toString()

                if (getCorreo == correo && getContrasenia == contra) {
                    Toast.makeText(requireContext(), "BIENVENIDO", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_login_to_home2)
                } else {
                    Toast.makeText(requireContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Algo salió mal", Toast.LENGTH_SHORT).show()
        }
    }

}