package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.finanzaspersonales.Clases.TaskViewModel
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.databinding.FragmentSheetGastosBinding
import com.example.finanzaspersonales.entidades.Categoria
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SheetGastos : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSheetGastosBinding
    private lateinit var taskViewModel: TaskViewModel
    private val arrayListCategoria: ArrayList<String> = ArrayList()


    private lateinit var database: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)

        binding.btnGuardarCategoria.setOnClickListener {
            saveCategoria()
        }

        getCategorias()
    }

//    private fun saveCategoria() {
//        if (binding.etMonto.text.isNotEmpty()) {
//            taskViewModel.categoria.value = binding.spCategoria.selectedItem.toString()
//            taskViewModel.monto.value = binding.etMonto.text.toString().toFloat()
//
//
//            binding.spCategoria.setSelection(0)
//            binding.etMonto.text.clear()
//            dismiss()
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSheetGastosBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun saveCategoria() {
        val categoriaNombre = binding.spCategoria.selectedItem.toString()
        val categoriaMonto = binding.etMonto.text.toString().toFloatOrNull()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null && categoriaNombre.isNotEmpty() && categoriaMonto != null) {
            val categoriaId = database.child("Gastos").child(userId).push().key
            if (categoriaId != null) {
                val categoria = Categoria(categoriaNombre, categoriaMonto)
                database.child("Gastos").child(userId).child(categoriaId).setValue(categoria)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Categoría guardada exitosamente", Toast.LENGTH_SHORT).show()
                        binding.spCategoria.setSelection(0)
                        binding.etMonto.text.clear()
                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error al guardar la categoría", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCategorias() {
        database = FirebaseDatabase.getInstance().reference

        database.child("Categoria").child("Admin").get().addOnSuccessListener { dataSnapshot ->
            arrayListCategoria.clear()
            if (dataSnapshot.exists()) {
                for (ds: DataSnapshot in dataSnapshot.children) {
                    val categoriaNombre = ds.key

                    categoriaNombre?.let {
                        arrayListCategoria.add(it)
                    }
                }

                // Actualizar el Spinner con las categorías obtenidas
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    arrayListCategoria
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spCategoria.adapter = adapter
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Algo salio mal", Toast.LENGTH_SHORT).show()
        }
    }

}