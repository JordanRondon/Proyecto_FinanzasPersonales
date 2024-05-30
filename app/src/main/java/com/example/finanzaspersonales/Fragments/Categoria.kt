package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.adaptadores.CrudCategoriaAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class Categoria : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CrudCategoriaAdapter
    private lateinit var categorias: List<Categoria>
    private lateinit var btn_agregar: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        categorias = listOf(
            Categoria("Agua",R.drawable.icono_agua ),
            Categoria("Comida",R.drawable.icono_comida ),
            Categoria("Entretenimiento",R.drawable.icono_comida )
            // Agrega más categorías según sea necesario
        )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_categoria, container, false)
        recyclerView = view.findViewById(R.id.presupuesto_recycle)
        btn_agregar=view.findViewById(R.id.btn_agregar_categoria)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CrudCategoriaAdapter(categorias)
        recyclerView.adapter = adapter

        btn_agregar.setOnClickListener {

            findNavController().navigate(R.id.action_categoria_to_nuevaCategoria)
        }
        return view
    }



}