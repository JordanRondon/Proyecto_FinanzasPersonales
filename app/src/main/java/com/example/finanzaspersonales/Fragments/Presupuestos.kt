package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.CategoriaAdapter
import com.example.finanzaspersonales.Clases.Presupuesto
import com.example.finanzaspersonales.PresupuestoAdapter
import com.example.finanzaspersonales.R


class Presupuestos : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var btnAgregar: Button
    private lateinit var etNombre: EditText
    private lateinit var etMonto: EditText
    private lateinit var adapterPresupuesto: PresupuestoAdapter
    private val presupuestos: ArrayList<Presupuesto> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_presupuestos, container, false)

        recycler = view.findViewById(R.id.recyclerView)
        spinner = view.findViewById(R.id.spinner)
        btnAgregar = view.findViewById(R.id.btnAgregar)
        etNombre = view.findViewById(R.id.etNombre)
        etMonto = view.findViewById(R.id.etMonto)

        recycler.layoutManager = LinearLayoutManager(context)

        adapterPresupuesto = PresupuestoAdapter(presupuestos)

        recycler.adapter = adapterPresupuesto


        btnAgregar.setOnClickListener {
            val nombrePresupuesto = etNombre.text.toString()
            val categoriaSeleccionada = spinner.selectedItem as Categoria

            val nuevoPresupuesto =
                Presupuesto(nombrePresupuesto, "Detalles", categoriaSeleccionada.icono)

            presupuestos.add(nuevoPresupuesto)
            adapterPresupuesto.notifyDataSetChanged()
            etNombre.text.clear()
            etMonto.text.clear()
            spinner.setSelection(0)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        spinner.adapter =
            CategoriaAdapter(requireContext(), R.layout.presupuesto_items_spinner, categorias)


    }

}