package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.adaptadores.CrudCategoriaAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import android.util.Log
class Categoria : Fragment(), CrudCategoriaAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CrudCategoriaAdapter
    private val categoriasList = mutableListOf<Categoria>()
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = "Admin"
        database = FirebaseDatabase.getInstance().getReference("Categoria/$username")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categoria, container, false)
        recyclerView = view.findViewById(R.id.presupuesto_recycle)
        btnAgregar = view.findViewById(R.id.btn_agregar_categoria)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CrudCategoriaAdapter(categoriasList, this)
        recyclerView.adapter = adapter

        btnAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_categoria_to_nuevaCategoria)
        }

        loadCategories()

        return view
    }

    override fun onOptionsButtonClick(nombreCategoria: String) {
        Toast.makeText(context, "Opción seleccionada: $nombreCategoria", Toast.LENGTH_SHORT).show()
    }

    private fun loadCategories() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasList.clear()
                for (categoriaSnapshot in snapshot.children) {
                    val nombreCategoria = categoriaSnapshot.key
                    val categoria = categoriaSnapshot.getValue(Categoria::class.java)
                    if (categoria != null && nombreCategoria != null) {
                        categoria.nombre = nombreCategoria
                        categoriasList.add(categoria)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CategoriaFragment", "Error al obtener las categorías", error.toException())
            }
        })
    }
}
