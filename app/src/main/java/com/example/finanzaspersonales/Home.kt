package com.example.finanzaspersonales

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.finanzaspersonales.Clases.TaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolBar: MaterialToolbar

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var database: DatabaseReference
    private lateinit var databaseGastoSemanal: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.main)
        toolBar = findViewById(R.id.toolBar)
        database = FirebaseDatabase.getInstance().reference
        val username = FirebaseAuth.getInstance().currentUser!!.uid
        databaseGastoSemanal = FirebaseDatabase.getInstance().getReference("GastoSemanal/$username")

        toolBar.setTitle("")
        reiniciarGastoSemanal(databaseGastoSemanal)

        setSupportActionBar(toolBar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        findViewById<NavigationView>(R.id.navigation_view).setupWithNavController(navController)
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setupWithNavController(
            navController
        )

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val header = navigationView.getHeaderView(0)
        val txtCorreo = header.findViewById<TextView>(R.id.txtCorreo)

        txtCorreo.text = user?.email
        val menu = navigationView.menu[4]

        menu.setOnMenuItemClickListener {
            Toast.makeText(this, "HASTA LUEGO", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            finish()

            true
        }

//        deleteUser()
    }

//USAR ESTA FUNCION PARA ELIMINAR EL USUARIO ACTUAL DE TODAS LAS TABLAS
    private fun deleteUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database.child("Gasto").child(userId).removeValue()
            database.child("Usuario").child(userId).removeValue()
            database.child("Categoria").child(userId).removeValue()
            database.child("GastoAnual").child(userId).removeValue()
            database.child("GastoSemanal").child(userId).removeValue()
            database.child("NotificacionPago").child(userId).removeValue()
            database.child("Presupuesto").child(userId).removeValue()
        }
    }

    private fun reiniciarGastoSemanal(gastoSemanlRef: DatabaseReference) {
        //if(user != null){
            gastoSemanlRef.get().addOnSuccessListener { datos ->
                val fecha_actual = obtenerFechaActual()
                val fin_semana = datos.child("fin_semana").value as String

                if (fecha_actual.after(convertirFecha(fin_semana))) {
                    val (nuevo_inicio_semana, nuevo_fin_semana) = obtenerInicioYFinDeSemana(fecha_actual)
                    // Actualizar los valores en Firebase
                    val resultadoActualizado = mapOf(
                        "domingo" to 0,
                        "lunes" to 0,
                        "martes" to 0,
                        "miercoles" to 0,
                        "jueves" to 0,
                        "viernes" to 0,
                        "sabado" to 0
                    )
                    val datosActualizados = mapOf(
                        "fin_semana" to nuevo_fin_semana,
                        "inicio_semana" to nuevo_inicio_semana,
                        "resultado" to resultadoActualizado
                    )

                    gastoSemanlRef.updateChildren(datosActualizados).addOnCompleteListener { tarea2 ->
                        if (!tarea2.isSuccessful) {
                            Log.e("FirebaseError", "Error al actualizar los datos: ${tarea2.exception?.message}")
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error al obtener los datos: ${exception.message}")
            }
        //}else{
        //    Log.e("Aplicacion","El usuario no esta autenticado")
        //}
    }

    private fun obtenerFechaActual(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    private fun convertirFecha(fechaString: String): Date {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatoFecha.parse(fechaString)!!
    }

    private  fun obtenerInicioYFinDeSemana(fecha: Date): Pair<String, String> {
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
}


