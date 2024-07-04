package com.example.finanzaspersonales.Fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.Clases.isOnline
import com.example.finanzaspersonales.adaptadores.CrudCategoriaAdapter
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.finanzaspersonales.R
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal


class Categoria : Fragment(), CrudCategoriaAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CrudCategoriaAdapter
    private val categoriasList = mutableListOf<Categoria>()
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var database: DatabaseReference

    private lateinit var main: ConstraintLayout
    private lateinit var connection: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("Categoria/$username")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categoria, container, false)
        recyclerView = view.findViewById(R.id.presupuesto_recycle)
        btnAgregar = view.findViewById(R.id.btn_agregar_categoria)

        main = view.findViewById(R.id.main)
        connection = view.findViewById(R.id.connection)

        if (!isOnline(requireContext())) {
            connection.visibility = View.VISIBLE
            main.visibility = View.INVISIBLE
        } else {
            connection.visibility = View.INVISIBLE
            main.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = CrudCategoriaAdapter(categoriasList, this)
            recyclerView.adapter = adapter

            btnAgregar.setOnClickListener {
                findNavController().navigate(R.id.action_categoria_to_nuevaCategoria)
            }

            loadCategories()

            tutorial()
        }
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

    private fun tutorial() {
        val sharedPreferences = requireActivity().getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
        val tutorialShown = sharedPreferences.getBoolean("tutorial_shown", false)

        if (!tutorialShown) {
            showFirstPrompt()
        }
    }

    private fun showFirstPrompt() {
        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(recyclerView)
            .setSecondaryText("En este apartado podrá visualizar sus categorías")
            .setPromptBackground(RectanglePromptBackground())
            .setPromptFocal(RectanglePromptFocal())
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHED) {
                    showSecondPrompt()
                }
            }
            .show()
    }

    private fun showSecondPrompt() {
        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(btnAgregar)
            .setSecondaryText("Aquí podrá agregar más categorías")
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHED) {
                    val sharedPreferences = requireActivity().getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean("tutorial_shown", true)
                        apply()
                    }
                }
            }
            .show()
    }


}
