package com.example.finanzaspersonales

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

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
                R.id.agregar_item,
                R.id.editar_item,
                R.id.eliminar_item -> Log.d("NavigationDrawer", "Item selected: ${it.title}")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
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
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commit()
//    }
