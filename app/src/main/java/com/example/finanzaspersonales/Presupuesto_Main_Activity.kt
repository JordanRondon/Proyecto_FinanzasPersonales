package com.example.finanzaspersonales

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.Clases.Presupuesto

class Presupuesto_Main_Activity : AppCompatActivity() {

    private val presupuestos: MutableList<Presupuesto> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_presupuesto_main)

        val categorias = listOf(
            Categoria("AGUA", R.drawable.agua_icono),
            Categoria("COMIDA", R.drawable.comida_icono),
            Categoria("LUZ", R.drawable.luz_icono),

        )
        val pre1 = Presupuesto("Presupuesto luz casa 1", "Detalles", R.drawable.luz_icono)
        val pre2 = Presupuesto("Presupuesto comida semanal", "Detalles", R.drawable.comida_icono)
        val pre3 = Presupuesto("Presupuesto agua casa 2", "Detalles", R.drawable.agua_icono)
        val pre4 = Presupuesto("Presupuesto comida familia 2", "Detalles", R.drawable.comida_icono)
        val pre5 = Presupuesto("Presupuesto luz casa 2", "Detalles", R.drawable.luz_icono)
        val pre6 = Presupuesto("Presupuesto comida familia 2", "Detalles", R.drawable.comida_icono)
        val pre7 = Presupuesto("Presupuesto luz casa 1", "Detalles", R.drawable.luz_icono)
        presupuestos.add(pre1)
        presupuestos.add(pre2)
        presupuestos.add(pre3)
        presupuestos.add(pre4)
        presupuestos.add(pre5)
        presupuestos.add(pre6)
        presupuestos.add(pre7)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PresupuestoAdapter(presupuestos)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = CategoriaAdapter(this, R.layout.presupuesto_items_spinner, categorias)
        spinner.adapter = adapter
        val buttonAgregar = findViewById<Button>(R.id.button2)
        buttonAgregar.setOnClickListener {
            val nombrePresupuesto = findViewById<EditText>(R.id.editTextText).text.toString()
            val categoriaSeleccionada = spinner.selectedItem as Categoria

            val nuevoPresupuesto = Presupuesto(nombrePresupuesto, "Detalles", categoriaSeleccionada.icono)
            presupuestos.add(nuevoPresupuesto)
            recyclerView.adapter?.notifyItemInserted(presupuestos.size - 1)
            findViewById<EditText>(R.id.editTextText).setText("")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}