package com.example.finanzaspersonales.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RecuperarCuentaFragment : Fragment() {
    private lateinit var tvCorreoRCuenta: TextInputEditText
    private lateinit var btnRecuperarCuenta: Button
    private lateinit var tvRegistroInicio: TextView
    private lateinit var correo: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recuperar_cuenta, container, false)

        tvCorreoRCuenta = view.findViewById(R.id.tvCorreoRCuenta)
        btnRecuperarCuenta = view.findViewById(R.id.btnRecuperarCuenta)
        tvRegistroInicio = view.findViewById(R.id.tvRegistroInicio)
        mAuth = FirebaseAuth.getInstance()
        mDialog = ProgressDialog(context)

        btnRecuperarCuenta.setOnClickListener {
            correo = tvCorreoRCuenta.text.toString()
            restablecerContrasenia()
            findNavController().navigate(R.id.action_recuperarCuenta_to_login)
        }

        tvRegistroInicio.setOnClickListener {
            findNavController().navigate(R.id.action_recuperarCuenta_to_login)
        }

        return view
    }

    private fun restablecerContrasenia() {
        if (correo.isNotEmpty()){
            mDialog.setMessage("Espere un momento...")
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.show()

            mAuth.sendPasswordResetEmail(correo).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Correo electrónico de restablecimiento de contraseña enviado
                    Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el envío del correo falla, muestra un mensaje al usuario.
                    Toast.makeText(context, "Failed to send password reset email.", Toast.LENGTH_SHORT).show()
                }
                mDialog.dismiss()
            }
        }
    }
}