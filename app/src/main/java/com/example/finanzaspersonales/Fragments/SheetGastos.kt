package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.TaskViewModel
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.CategoriaGastosAdapter
import com.example.finanzaspersonales.databinding.FragmentSheetGastosBinding
import com.example.finanzaspersonales.entidades.Categoria
import com.example.finanzaspersonales.entidades.CategoriaGastos
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SheetGastos : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSheetGastosBinding
    private lateinit var taskViewModel: TaskViewModel
    private val arrayListCategoria: ArrayList<CategoriaGastos> = ArrayList()

    private val zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Lima"))
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    //private val database = FirebaseDatabase.getInstance().getReference("Categoria/$userId")

    private lateinit var database: DatabaseReference
    private var idCategoria = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoriaGastosAdapter: CategoriaGastosAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewCategoriaGastos)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        categoriaGastosAdapter = CategoriaGastosAdapter(arrayListCategoria, requireContext())
        recyclerView.adapter = categoriaGastosAdapter


        getCategorias()

        binding.btnGuardarCategoria.setOnClickListener {
            saveGastos()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSheetGastosBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun saveGastos() {
        val categoriaNombre = categoriaGastosAdapter.getCategoriaSelected()?.nombre
        val categoriaMonto = binding.etMonto.text.toString().toFloatOrNull()
        val date = zonedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

        if (userId != null && categoriaNombre != null && categoriaMonto != null) {
            val categoria = Categoria(categoriaNombre, categoriaMonto, date)
            database.child("Gasto").child(userId).child((idCategoria + 1).toString())
                .setValue(categoria)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Gasto guardado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.etMonto.text.clear()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al guardar el gasto", Toast.LENGTH_SHORT)
                        .show()

                }
        } else {
            Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getCategorias() {
        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser!!.uid

        database.child("Categoria").child(user).get().addOnSuccessListener { dataSnapshot ->
            arrayListCategoria.clear()
            if (dataSnapshot.exists()) {
                for (ds: DataSnapshot in dataSnapshot.children) {
                    val categoriaNombre = ds.key
                    val urlIcon = ds.child("urlicono").getValue(String::class.java)

                    arrayListCategoria.add(CategoriaGastos(categoriaNombre, urlIcon))
                }
                categoriaGastosAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Algo salio mal", Toast.LENGTH_SHORT).show()
        }
    }

}