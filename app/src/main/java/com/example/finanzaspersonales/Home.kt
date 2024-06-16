package com.example.finanzaspersonales

import android.os.Bundle
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

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolBar: MaterialToolbar

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.main)
        toolBar = findViewById(R.id.toolBar)
        database = FirebaseDatabase.getInstance().reference

        toolBar.setTitle("")

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
}


