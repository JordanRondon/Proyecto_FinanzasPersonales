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
import com.example.finanzaspersonales.entidades.GastoSemanal
import com.example.finanzaspersonales.entidades.GastoSemanal_resultado
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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

                    val gasto_Data = mapOf(
                        "contador" to mapOf(
                            "ultimo_gasto" to 0
                        )
                    )
                    database.child("Gasto").child(userId).setValue(gasto_Data)
                    database.child("GastoAnual").child(userId).setValue("")
                    database.child("GastoSemanal").child(userId).setValue(inicializarGastoSemanal())
                    val NotificacionPago_Data = mapOf(
                        "contador" to mapOf(
                            "ultimo_NotificacionPago" to 0
                        )
                    )
                    database.child("NotificacionPago").child(userId).setValue(NotificacionPago_Data)
                    database.child("Presupuesto").child(userId).setValue("")
                    database.child("Notificacion").child(userId).setValue("")
                } else {
                    Toast.makeText(requireContext(), "CORREO YA REGISTRADO", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "CAMPOS FALTANTES", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerFechaActual(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    private fun obtenerInicioYFinDeSemana(fecha: Date): Pair<String, String> {
        val calendar = Calendar.getInstance().apply {
            time = fecha
        }

        val inicioSemana = calendar.clone() as Calendar
        inicioSemana.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            inicioSemana.add(Calendar.WEEK_OF_YEAR, -1)
        }

        val finSemana = inicioSemana.clone() as Calendar
        finSemana.add(Calendar.DAY_OF_WEEK, 6)

        // Formatear las fechas como cadenas
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inicioSemanaStr = formatoFecha.format(inicioSemana.time)
        val finSemanaStr = formatoFecha.format(finSemana.time)

        return Pair(inicioSemanaStr, finSemanaStr)
    }

    private fun inicializarGastoSemanal(): GastoSemanal {
        val resultado = GastoSemanal_resultado(
            domingo = 0,
            jueves = 0,
            lunes = 0,
            martes = 0,
            miercoles = 0,
            sabado = 0,
            viernes = 0
        )

        val fecha_actual = obtenerFechaActual()
        val (nuevo_inicio_semana, nuevo_fin_semana) = obtenerInicioYFinDeSemana(fecha_actual)

        val gastoSemanal = GastoSemanal(
            fin_semana = nuevo_fin_semana,
            inicio_semana = nuevo_inicio_semana,
            resultado = resultado
        )

        return  gastoSemanal
    }
}