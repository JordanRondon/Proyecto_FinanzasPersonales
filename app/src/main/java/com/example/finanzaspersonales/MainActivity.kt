package com.example.finanzaspersonales

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var tvCorreo: TextInputEditText
    private lateinit var tvContrasenia: TextInputEditText
    private lateinit var btnInicioSecion: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvCorreo = findViewById(R.id.tvCorreo)
        tvContrasenia = findViewById(R.id.tvContrasenia)
        btnInicioSecion = findViewById(R.id.btnInicioSecion)

        btnInicioSecion.setOnClickListener {
            login()
        }

    }

    fun recuperarCuenta(view: View) {
        val intent = Intent(this, RecuperarCuentaActivity::class.java)
        startActivity(intent)
    }

    fun registrate(view: View) {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    private fun login() {
        database = FirebaseDatabase.getInstance().reference

        val correo = tvCorreo.text.toString()
        val contra = tvContrasenia.text.toString()

        database.child("Usuario").get().addOnSuccessListener {
            if (it.exists()) {
                //for (ds: DataSnapshot in it.children) {
                    val getCorreo = it.child("correo").value.toString()
                    val getContrasenia = it.child("password").value.toString()

                    if (getCorreo == correo && getContrasenia == contra) {
                        Toast.makeText(this, "BIENVENIDO", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Home::class.java))

                    }
               //

            } else {
                Toast.makeText(this, "Usurio no encontrado", Toast.LENGTH_SHORT).show()
            }


        }.addOnFailureListener {
            Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show()
        }
    }
}
