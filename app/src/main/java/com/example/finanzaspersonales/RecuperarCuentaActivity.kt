package com.example.finanzaspersonales

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RecuperarCuentaActivity : AppCompatActivity() {
    private lateinit var tvCorreoRCuenta: TextInputEditText
    private lateinit var btnRecuperarCuenta: Button
    private lateinit var correo: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_cuenta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvCorreoRCuenta.findViewById<TextInputEditText>(R.id.tvCorreoRCuenta)
        btnRecuperarCuenta.findViewById<Button>(R.id.btnRecuperarCuenta)
        correo = tvCorreoRCuenta.text.toString()
        mAuth = FirebaseAuth.getInstance()
        mDialog = ProgressDialog(this@RecuperarCuentaActivity)

        btnRecuperarCuenta.setOnClickListener {
            restablecerContrasenia()
        }
    }

    fun inicioSecion(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun restablecerContrasenia() {
        if (correo.isNotEmpty()){
            mDialog.setMessage("Espere un momento...")
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.show()

            mAuth.sendPasswordResetEmail(correo).addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    // Correo electrónico de restablecimiento de contraseña enviado
                    Toast.makeText(this@RecuperarCuentaActivity, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el envío del correo falla, muestra un mensaje al usuario.
                    Toast.makeText(this@RecuperarCuentaActivity, "Failed to send password reset email.", Toast.LENGTH_SHORT).show()
                }
                mDialog.dismiss()
            })
        }
    }
}