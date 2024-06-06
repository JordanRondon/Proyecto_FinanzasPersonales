package com.example.finanzaspersonales.Fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Register : Fragment() {

    private lateinit var btnRegistro: Button
    private lateinit var tvRegistroInicio: TextView
    private lateinit var tvCorreoRegistro: TextInputEditText
    private lateinit var tvContraseniaRegistro: TextInputEditText
    private lateinit var cbRegistroTerminos: CheckBox

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        btnRegistro = view.findViewById(R.id.btnRegistro)
        tvRegistroInicio = view.findViewById(R.id.tvRegistroInicio)
        tvCorreoRegistro = view.findViewById(R.id.tvCorreoRegistro)
        tvContraseniaRegistro = view.findViewById(R.id.tvContraseniaRegistro)
        cbRegistroTerminos = view.findViewById(R.id.cbRegistroTerminos)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference


        btnRegistro.setOnClickListener {
            register()
        }
        tvRegistroInicio.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        return view
    }

    private fun register() {
        val email = tvCorreoRegistro.text.toString()
        val password = tvContraseniaRegistro.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && cbRegistroTerminos.isChecked) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "REGISTRO EXITOSO", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_register_to_login)
                    val userId = auth.currentUser!!.uid

                    val user = mapOf(
                        "correo" to email,
                        "contraseña" to password
                    )

                    database.child("Usuario").child(userId).setValue(user)
                        .addOnCompleteListener { task ->
                            Log.d("USUARIO", "USUARIO REGISTRADO: $email")
                        }

                    // Registrar userId en todas las tablas sin ningún valor adicional
                    database.child("Categoria").child(userId).setValue("")
                    database.child("Gasto").child(userId).setValue("")
                    database.child("GastoAnual").child(userId).setValue("")
                    database.child("GastoSemanal").child(userId).setValue("")
                    database.child("NotificacionPago").child(userId).setValue("")
                    database.child("Presupuesto").child(userId).setValue("")

                } else {
                    Toast.makeText(requireContext(), "CORREO YA REGISTRADO", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "CAMPOS FALTANTES", Toast.LENGTH_SHORT).show()
        }
    }


}