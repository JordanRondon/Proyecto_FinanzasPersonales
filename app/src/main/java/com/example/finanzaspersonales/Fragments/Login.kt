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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.log

class Login : Fragment() {

    private lateinit var btnInicioSecion: Button
    private lateinit var tvRegistrate: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvContrasenia: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        tvRegistrate = view.findViewById(R.id.tvRegistrate)
        tvCorreo = view.findViewById(R.id.tvCorreo)
        tvContrasenia = view.findViewById(R.id.tvContrasenia)
        btnInicioSecion = view.findViewById(R.id.btnInicioSecion)

        auth = FirebaseAuth.getInstance()

        val user = FirebaseAuth.getInstance().currentUser

        //Permite dejar el inicio de sesion activo
        if(user != null){
            Toast.makeText(requireContext(), "BIENVENIDO", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_login_to_home2)
        }

        tvRegistrate.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        btnInicioSecion.setOnClickListener {
            login()
        }

        return view
    }

    private fun login() {
        val email = tvCorreo.text.toString()
        val password = tvContrasenia.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "BIENVENIDO", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_login_to_home2)
                    val user = auth.currentUser
                } else {
                    Toast.makeText(requireContext(), "DATOS INCORRECTOS", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "CAMPOS FALTANTES", Toast.LENGTH_SHORT).show()
        }
    }

}