package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class Login : Fragment() {

    private lateinit var btnInicioSecion: Button
    private lateinit var tvRegistrate: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvContrasenia: TextView

    private lateinit var auth: FirebaseAuth
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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference // Inicializar aquí


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

                    val user = auth.currentUser
                    user?.let {
                        // Obtener y registrar el token FCM
                        Firebase.messaging.token.addOnCompleteListener { tokenTask ->
                            if (!tokenTask.isSuccessful) {
                                Log.w("FCM", "Fetching FCM registration token failed", tokenTask.exception)
                                return@addOnCompleteListener
                            }

                            val token = tokenTask.result
                            registrarToken(it.uid, token)
                        }
                    }
                    Toast.makeText(requireContext(), "BIENVENIDO", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_login_to_home2)
                } else {
                    Toast.makeText(requireContext(), "DATOS INCORRECTOS", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "CAMPOS FALTANTES", Toast.LENGTH_SHORT).show()
        }
    }
    private fun registrarToken(userId: String, token: String) {
        val refToken = database.child("Usuario").child(userId).child("Token")
        refToken.setValue(token).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Token registered successfully")
            } else {
                Log.w("FCM", "Token registration failed", task.exception)
            }
        }
    }

}