package com.example.finanzaspersonales

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.finanzaspersonales.Fragments.Categoria
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolBar: MaterialToolbar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        initializeViews()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupWithNavController(bottomNavigation, navController)


        setupActionBar()
        setupNavigationDrawer()
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.main)
        toolBar = findViewById(R.id.toolBar)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        navigationView = findViewById(R.id.navigation_view)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolBar)
    }

    private fun setupNavigationDrawer() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolBar, R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.inicio_item -> {
                    navController.navigate(R.id.action_inicio)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.historial_item -> {
                    navController.navigate(R.id.action_historialGastos)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.categoria_item -> {
                    navController.navigate(R.id.action_nueva_categoria)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.cerrar_sesion_item ->{
                    Toast.makeText(this, "HASTA LUEGO", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val header = navigationView.getHeaderView(0)
        val btnNotificacion = header.findViewById<ImageView>(R.id.btnNotificacion)

        btnNotificacion.setOnClickListener {
            navController.navigate(R.id.action_notificaciones)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
           .replace(R.id.fragment_container, fragment)
            .commit()
   }
}

//        setupBottomNavigation()
//        replaceFragment(Gastos())

//    private fun setupBottomNavigation() {
//        bottomNavigation.setOnItemSelectedListener { item ->
//            val fragment: Fragment = when (item.itemId) {
//                R.id.item_gastos -> Gastos()
//                R.id.item_presupuestos -> Presupuestos()
//                R.id.item_ahorro -> Ahorro()
//                R.id.item_recordatorios -> Recordatorio()
//                else -> return@setOnItemSelectedListener false
//            }
//            replaceFragment(fragment)
//            true
//        }
//    }
//
//

