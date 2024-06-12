package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.util.Log
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
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class SheetGastos : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSheetGastosBinding
    private lateinit var taskViewModel: TaskViewModel
    private val arrayListCategoria: ArrayList<CategoriaGastos> = ArrayList()
    private val arrayListPresupuestos: ArrayList<CategoriaGastos> = ArrayList()

    private val zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Lima"))
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val contadorReference = FirebaseDatabase.getInstance().getReference("Gasto/$userId/contador/ultimo_gasto")
    private val gastoReference = FirebaseDatabase.getInstance().getReference("Gasto/$userId")
    private val presupuestoReference = FirebaseDatabase.getInstance().getReference("Presupuesto/$userId")

    private lateinit var database: DatabaseReference

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

        contadorReference.get().addOnSuccessListener { data ->
            val contador = data.getValue(Int::class.java) ?: 0
            val contadorUpdate = contador + 1

            if (categoriaNombre != null && categoriaMonto != null) {
                val gasto = EntidadGasto(categoriaNombre, "vacio", categoriaMonto, date)

                gastoReference.child(contadorUpdate.toString()).setValue(gasto)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Gasto guardado exitosamente", Toast.LENGTH_SHORT).show()

                        setGastoSemanal_dia(categoriaMonto)
                        binding.etMonto.text.clear()
                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error al guardar el gasto", Toast.LENGTH_SHORT).show()

                    }
            } else {
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }

            contadorReference.setValue(contadorUpdate)
        }
    }

    private fun getCategorias() {
        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.child("Categoria").child(user).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                arrayListCategoria.clear()
                if (dataSnapshot.exists()) {
                    for (ds: DataSnapshot in dataSnapshot.children) {
                        val categoriaNombre = ds.key
                        val urlIcon = ds.child("urlicono").getValue(String::class.java)

                        arrayListCategoria.add(CategoriaGastos(categoriaNombre, urlIcon))
                    }
                    categoriaGastosAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun getPresupuestos(){
        presupuestoReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(data: DataSnapshot) {
                if(data.exists()){
                    for(ds:DataSnapshot in data.children){
                        val presupuestoID = ds.key


                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    private fun setGastoSemanal_dia(NuevoGastoMonto: Float) {
        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        val diaSemana = obtenerDiaSemana()
        val gastoSemanal_dia_Ref =
            FirebaseDatabase.getInstance().getReference("GastoSemanal/$user/resultado/$diaSemana")

        //obtiene el valor actual del dia indicado
        gastoSemanal_dia_Ref.get().addOnSuccessListener { data ->
            val gastoActual = data.getValue(Float::class.java) ?: 0f
            val gastoActualizado = gastoActual + NuevoGastoMonto

            //actualiza monto del dia
            gastoSemanal_dia_Ref.setValue(gastoActualizado).addOnCompleteListener { tarea ->
                if (!tarea.isSuccessful) {
                    println("Error al actualizar el valor: ${tarea.exception?.message}")
                }
            }
        }.addOnFailureListener { exception ->
            println("Error al obtener el valor actual: ${exception.message}")
        }
    }

    private fun obtenerDiaSemana(): String {
        // Obtener la instancia del calendario actual
        val calendar = Calendar.getInstance()

        // Obtener el día de la semana (1=domingo, 2=lunes, ..., 7=sábado)
        val diaSemana = calendar.get(Calendar.DAY_OF_WEEK)
        val diasSemana =
            arrayOf("domingo", "lunes", "martes", "miercoles", "jueves", "viernes", "sabado")

        return diasSemana[diaSemana - 1]
    }


}