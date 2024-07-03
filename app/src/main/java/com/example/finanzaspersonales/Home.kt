package com.example.finanzaspersonales

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.finanzaspersonales.Clases.isOnline
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolBar: MaterialToolbar
    private lateinit var custom_title: TextView

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var database: DatabaseReference
    private lateinit var databaseGastoSemanal: DatabaseReference

    private val zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Lima"))
    private val formatter =
        DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    private val formattedDate =
        zonedDateTime.format(formatter).lowercase().replaceFirstChar { it.uppercase() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.main)
        toolBar = findViewById(R.id.toolBar)
        custom_title = findViewById(R.id.custom_title)

        database = FirebaseDatabase.getInstance().reference
        val username = FirebaseAuth.getInstance().currentUser!!.uid
        databaseGastoSemanal = FirebaseDatabase.getInstance().getReference("GastoSemanal/$username")

        toolBar.setTitle("")
        custom_title.text = formattedDate

        reiniciarGastoSemanal(databaseGastoSemanal)
        verificarPresupuestosVencidos()
        setSupportActionBar(toolBar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setupWithNavController(navController)

        //findViewById<NavigationView>(R.id.navigation_view).setupWithNavController(navController)
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setupWithNavController(navController)

        val header = navigationView.getHeaderView(0)
        val txtCorreo = header.findViewById<TextView>(R.id.txtCorreo)
        val txtLetra = header.findViewById<TextView>(R.id.txtInicial)

        txtCorreo.text = user?.email
        txtLetra.text = user?.email?.substring(0, 1)?.uppercase()
        val menu = navigationView.menu[4]

        menu.setOnMenuItemClickListener {
            Toast.makeText(this, "HASTA LUEGO", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            finish()

            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.navigateUp()) {
                    navController.navigateUp()
                } else {
                    finishAffinity()
                }
            }
        })

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

        gastoSemanlRef.get().addOnSuccessListener { datos ->
            val fecha_actual = obtenerFechaActual()
            val fin_semana = datos.child("fin_semana").value as String
            val fin_semana_convertido = convertirFecha(fin_semana)

            if (fecha_actual.after(fin_semana_convertido)) {
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
                        Log.e(
                            "FirebaseError",
                            "Error al actualizar los datos: ${tarea2.exception?.message}"
                        )
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseError", "Error al obtener los datos: ${exception.message}")
        }
    }

    private fun obtenerFechaActual(): Date {
        val timeZone = TimeZone.getTimeZone("America/Lima")
        val calendar = Calendar.getInstance(timeZone).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    private fun convertirFecha(fechaString: String): Date {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatoFecha.timeZone = TimeZone.getTimeZone("America/Lima")
        return formatoFecha.parse(fechaString)!!
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

    //Funcion para verificar presupuesto
    fun verificarPresupuestosVencidos() {
        database = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val presupuestosReference = database.child("Presupuesto").child("$userId")

        presupuestosReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (presupuestoSnapshot in snapshot.children) {
                    val presupuestoId = presupuestoSnapshot.key ?: continue
                    val fechaVencimiento =
                        presupuestoSnapshot.child("fechaCulminacion").getValue(String::class.java)
                    val estado =
                        presupuestoSnapshot.child("estado").getValue(Boolean::class.java) ?: true

                    if (fechaVencimiento != null && estado) {
                        val calendar = Calendar.getInstance()
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val currentDate = simpleDateFormat.format(calendar.time)

                        val fechaVencimientoDate = simpleDateFormat.parse(fechaVencimiento)

                        if (fechaVencimientoDate != null && simpleDateFormat.parse(currentDate)
                                .after(fechaVencimientoDate)
                        ) {
                            presupuestosReference.child(presupuestoId).child("estado")
                                .setValue(false)
                                .addOnSuccessListener {
                                    println("Presupuesto $presupuestoId actualizado a vencido.")
                                }
                                .addOnFailureListener {
                                    println("Error al actualizar el presupuesto $presupuestoId: ${it.message}")
                                }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener los presupuestos: ${error.message}")
            }
        })
    }
    //

}


