package com.example.finanzaspersonales

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class GastosFragment : Fragment() {
    private lateinit var imageView_sinDatos: ImageView
    private lateinit var textView_SinDatos: TextView
    private lateinit var tvBuscarGasto: TextInputEditText
    private lateinit var imageButton_filtroMontoGasto: ImageButton
    private lateinit var imageButton_filtroFecha: ImageButton
    private lateinit var RecyclerViewHistorial: RecyclerView
    private val historialGasto = mutableListOf<EntidadGasto>()
    private val historialGastoFiltrado = mutableListOf<EntidadGasto>()
    private lateinit var adaptadorPersonalizado: GastoAdapter
    private lateinit var databaseGasto: DatabaseReference
    private lateinit var databaseCategoria: DatabaseReference
    private var fecha_filtro: String? = null
    private val gastoListaMonto = mutableListOf<String>()

    private var filtroCategoria: String = ""
    private var filtroFecha: String? = null
    private var filtroMontosSeleccionados: MutableList<String> = mutableListOf()
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

        imageView_sinDatos = view.findViewById(R.id.imageView_sinDatos)
        textView_SinDatos = view.findViewById(R.id.textView_SinDatos)
        ocultar_mensajeSinDatos()

        tvBuscarGasto = view.findViewById(R.id.tvBuscarGasto)
        imageButton_filtroMontoGasto = view.findViewById(R.id.imageButton_filtroMontoGasto)
        imageButton_filtroFecha = view.findViewById(R.id.imageButton_filtroFecha)

        RecyclerViewHistorial = view.findViewById(R.id.rvListaGastos)

        adaptadorPersonalizado = GastoAdapter(requireContext(), historialGasto, databaseCategoria)
        RecyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())
        RecyclerViewHistorial.adapter = adaptadorPersonalizado

        tvBuscarGasto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //Cada vez que se detecte que se agregó una letra, llama al método filtrar.
                filtrarPorCategoria(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        imageButton_filtroMontoGasto.setOnClickListener {

            // Convertir la lista de String a un array de Strings
            val montoArray = gastoListaMonto.toTypedArray()
            // Array para almacenar los ítems seleccionados
            val checkedItems = BooleanArray(montoArray.size)
            // Crea un Dialog para mostrar la lista de montos
            val ListarMontoDialog: AlertDialog.Builder = AlertDialog.Builder(context)
            ListarMontoDialog
                .setTitle("Filtra tus gastos por Monto")
                .setPositiveButton("Aceptar") { dialog, which ->
                    val montosSeleccionados = mutableListOf<String>()
                    for (i in montoArray.indices) {
                        if (checkedItems[i]) {
                            montosSeleccionados.add(montoArray[i])
                        }
                    }
                    filtarPorMonto(montosSeleccionados)
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    filtarPorMonto(mutableListOf())
                }
                .setMultiChoiceItems(montoArray, checkedItems) { dialog, which, isChecked ->
                    // Actualizar el estado del ítem seleccionado
                    checkedItems[which] = isChecked
                }

            val dialog: AlertDialog = ListarMontoDialog.create()
            dialog.show()
        }

        imageButton_filtroFecha.setOnClickListener {
            //dialog data para seleccionar una fecha
            val calendario: Calendar = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val recolectarFecha = DatePickerDialog(requireContext(), {_, year, month, dayOfMonth ->
                val mesActual = month + 1
                val diaFormateado = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                val mesFormateado = if (mesActual < 10) "0$mesActual" else mesActual.toString()
                fecha_filtro = "$diaFormateado/$mesFormateado/$year"
                filtrarPorFecha(fecha_filtro)
            }, anio, mes, dia)
            recolectarFecha.setOnCancelListener {
                fecha_filtro = null
                filtrarPorFecha(fecha_filtro)
            }
            recolectarFecha.setTitle("Filtra tus gastos por fecha")
            recolectarFecha.show()
        }

        obtenerDatosGastos(databaseGasto)
        obtenerDatosMontoGastos(databaseGasto)
    }

    private fun obtenerDatosGastos(gastosRef: DatabaseReference) {

        gastosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historialGasto.clear()
                val gastoList = mutableListOf<EntidadGasto>()
                for (gastoSnapshot in snapshot.children) {
                    if (gastoSnapshot.key != "contador") {
                        val categoriaID = gastoSnapshot.child("categoriaID").value.toString()
                        val presupuestoID = gastoSnapshot.child("presupuestoID").value.toString()
                        val monto = gastoSnapshot.child("monto").getValue(Float::class.java) ?: 0.0f
                        val fechaRegistro = gastoSnapshot.child("fechaRegistro").value.toString()
                        val horaRegistro = gastoSnapshot.child("horaRegistro").value.toString()

                        val entidad = EntidadGasto(
                            categoriaID = categoriaID,
                            presupuestoID = presupuestoID,
                            monto = monto,
                            fechaRegistro = fechaRegistro,
                            horaRegistro = horaRegistro
                        )
                        //historialGasto.add(entidad)
                        gastoList.add(entidad)
                    }
                }
                historialGasto.addAll(gastoList.asReversed())
                gastoList.clear()
                adaptadorPersonalizado.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error al obtener los datos: ${error.message}")
            }
        })
    }

    private fun obtenerDatosMontoGastos(gastosRef: DatabaseReference) {

        gastosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gastoListaMonto.clear()
                for (gastoSnapshot in snapshot.children) {
                    if (gastoSnapshot.key != "contador") {
                        val monto = gastoSnapshot.child("monto").getValue(Float::class.java) ?: 0.0f
                        if (!gastoListaMonto.contains(monto.toString())) {
                            // Si no existe el monto en la lista, se agrega
                            gastoListaMonto.add(monto.toString())
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error al obtener los datos: ${error.message}")
            }
        })
    }

    private fun aplicarFiltros() {
        historialGastoFiltrado.clear()

        historialGastoFiltrado.addAll(historialGasto.filter { gasto ->
            val coincideCategoria = filtroCategoria.isEmpty() || gasto.categoriaID.contains(filtroCategoria, ignoreCase = true)
            val coincideFecha = filtroFecha == null || gasto.fechaRegistro == filtroFecha
            val coincideMonto = filtroMontosSeleccionados.isEmpty() || filtroMontosSeleccionados.contains(gasto.monto.toString())

            coincideCategoria && coincideFecha && coincideMonto
        })

        if (historialGastoFiltrado.isEmpty()) {
            mostrar_mensajeSinDatos()
        } else {
            ocultar_mensajeSinDatos()
        }

        actualizarAdaptador()
    }

    private fun filtrarPorCategoria(texto: String) {
        filtroCategoria = texto
        aplicarFiltros()
    }

    private fun filtrarPorFecha(fecha: String?) {
        filtroFecha = fecha
        aplicarFiltros()
    }

    private fun filtarPorMonto(montosSeleccionados: MutableList<String>) {
        filtroMontosSeleccionados = montosSeleccionados
        aplicarFiltros()
    }

    private fun actualizarAdaptador() {
        adaptadorPersonalizado = GastoAdapter(requireContext(), historialGastoFiltrado, databaseCategoria)
        RecyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())
        RecyclerViewHistorial.adapter = adaptadorPersonalizado
    }

    private fun mostrar_mensajeSinDatos() {
        imageView_sinDatos.visibility = View.VISIBLE
        textView_SinDatos.visibility = View.VISIBLE
    }

    private fun ocultar_mensajeSinDatos() {
        imageView_sinDatos.visibility = View.GONE
        textView_SinDatos.visibility = View.GONE
    }
}