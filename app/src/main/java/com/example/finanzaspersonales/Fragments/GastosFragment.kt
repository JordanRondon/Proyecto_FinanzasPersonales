package com.example.finanzaspersonales

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Fragments.Gastos
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class GastosFragment : Fragment() {
    private lateinit var RecyclerViewHistorial: RecyclerView
    private val historialGasto = mutableListOf<EntidadGasto>()
    private lateinit var adaptadorPersonalizado: GastoAdapter
    private lateinit var databaseGasto: DatabaseReference
    private lateinit var databaseCategoria: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = FirebaseAuth.getInstance().currentUser!!.uid
        databaseGasto = FirebaseDatabase.getInstance().getReference("Gasto/$username")
        databaseCategoria = FirebaseDatabase.getInstance().getReference("Categoria/$username")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial_gastos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RecyclerViewHistorial = view.findViewById(R.id.rvListaGastos)
        adaptadorPersonalizado = GastoAdapter(requireContext(), historialGasto, databaseCategoria)
        RecyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())
        RecyclerViewHistorial.adapter = adaptadorPersonalizado

        obtenerDatosDeFirebase(databaseGasto)
    }

    private fun obtenerDatosDeFirebase(gastosRef: DatabaseReference) {

        gastosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historialGasto.clear()
                for (gastoSnapshot in snapshot.children) {
                    if (gastoSnapshot.key != "contador") {
                        val categoriaID = gastoSnapshot.child("categoriaID").value.toString()
                        val presupuestoID = gastoSnapshot.child("presupuestoID").value.toString()
                        val monto = gastoSnapshot.child("monto").getValue(Float::class.java) ?: 0.0f
                        val fechaRegistro = gastoSnapshot.child("fechaRegistro").value.toString()

                        val entidad = EntidadGasto(
                            categoriaId = categoriaID,
                            presupuestoId = presupuestoID,
                            valorGasto = monto,
                            fechaGasto = fechaRegistro
                        )
                        historialGasto.add(entidad)
                    }
                }
                adaptadorPersonalizado.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error al obtener los datos: ${error.message}")
            }
        })
    }
}