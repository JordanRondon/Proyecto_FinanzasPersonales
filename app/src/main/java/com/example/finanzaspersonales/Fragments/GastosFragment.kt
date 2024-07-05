package com.example.finanzaspersonales

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Typeface
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
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
    private var filtroMonto: Int? = null

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
            val sliderView = layoutInflater.inflate(R.layout.dialog_filtro_monto, null)
            val slider = sliderView.findViewById<Slider>(R.id.slider)
            val textView_valor = sliderView.findViewById<TextView>(R.id.slider_valor)
            var valorSilder = 1
            val imageButton_restarSlider = sliderView.findViewById<ImageButton>(R.id.imageButton_restarSlider)
            val imageButton_sumarSlider = sliderView.findViewById<ImageButton>(R.id.imageButton_sumarSlider)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Monto mayores a")
                .setView(sliderView)
                .setNegativeButton("Cancelar") { dialog, which ->
                    filtroMonto = null
                    aplicarFiltros()
                    dialog.dismiss()
                }
                .setPositiveButton("Aceptar") { dialog, which ->
                    filtroMonto = valorSilder
                    aplicarFiltros()
                    dialog.dismiss()
                }
                .show()

            imageButton_restarSlider.setOnClickListener {
                incrementarValor_Slider(slider, textView_valor, -1)
            }

            imageButton_sumarSlider.setOnClickListener {
                incrementarValor_Slider(slider, textView_valor, 1)
            }

            slider.addOnChangeListener { _, value, _ ->
                valorSilder = value.toInt()
                textView_valor.text = "Valor: ${value.toInt()}"
            }
        }

        imageButton_filtroFecha.setOnClickListener {
            //dialog data para seleccionar una fecha
            val calendario: Calendar = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val recolectarFecha = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
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

        tutorial()

    }

    private fun incrementarValor_Slider(slider: Slider, textView_valor:TextView, cantidad: Int) {
        val nuevoValor = slider.value + cantidad
        if (nuevoValor in slider.valueFrom..slider.valueTo) {
            slider.value = nuevoValor
            textView_valor.text = "Valor: ${nuevoValor.toInt()}"
        }
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
            val coincideMonto = filtroMonto == null || gasto.monto.toInt() >= filtroMonto!!

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

    private fun filtarPorMonto(montoFiltrado: Int) {
        filtroMonto= montoFiltrado
        aplicarFiltros()
    }

    private fun actualizarAdaptador() {
        adaptadorPersonalizado =
            GastoAdapter(requireContext(), historialGastoFiltrado, databaseCategoria)
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


    fun Fragment.isAttachedToActivity(): Boolean {
        return this.isAdded && this.activity != null
    }

    private fun tutorial() {
        val sharedPreferences = requireActivity().getSharedPreferences("tutorial_prefs_gastos", Context.MODE_PRIVATE)
        val tutorialShown = sharedPreferences.getBoolean("tutorial_historial", false)

        if (!tutorialShown) {
            showFirstPrompt()
        }
    }

    private fun showFirstPrompt() {
        if (isAttachedToActivity()) {
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(R.id.btnGastos)
                .setSecondaryText("Busque sus gastos")
                .setSecondaryTextTypeface(Typeface.SANS_SERIF)
                .setSecondaryTextColour(resources.getColor(R.color.white))
                .setPromptBackground(RectanglePromptBackground())
                .setPromptFocal(RectanglePromptFocal())
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHED) {
                        showSecondPrompt()
                    }
                }
                .show()
        }
    }

    private fun showSecondPrompt() {
        if (isAttachedToActivity()) {
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(R.id.BtnGraficos)
                .setSecondaryText("Visualice sus gastos mediante graficos")
                .setSecondaryTextTypeface(Typeface.SANS_SERIF)
                .setSecondaryTextColour(resources.getColor(R.color.white))
                .setPromptBackground(RectanglePromptBackground())
                .setPromptFocal(RectanglePromptFocal())
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHED) {
                        showThreePrompt()
                    }
                }
                .show()
        }
    }

    private fun showThreePrompt() {
        if (isAttachedToActivity()) {
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(R.id.tvBuscarGasto)
                .setSecondaryText("Busque sus gastos")
                .setSecondaryTextTypeface(Typeface.SANS_SERIF)
                .setSecondaryTextColour(resources.getColor(R.color.white))
                .setPromptBackground(RectanglePromptBackground())
                .setPromptFocal(RectanglePromptFocal())
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHED) {
                        showFourPrompt()
                    }
                }
                .show()
        }
    }

    private fun showFourPrompt() {
        if (isAttachedToActivity()) {
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(R.id.imageButton_filtroMontoGasto)
                .setSecondaryText("Filtre sus gastos por montos")
                .setSecondaryTextTypeface(Typeface.SANS_SERIF)
                .setSecondaryTextColour(resources.getColor(R.color.white))
                .setPromptBackground(RectanglePromptBackground())
                .setPromptFocal(RectanglePromptFocal())
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHED) {
                        showFivePrompt()
                    }
                }
                .show()
        }
    }

    private fun showFivePrompt() {
        if (isAttachedToActivity()) {
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(R.id.imageButton_filtroFecha)
                .setSecondaryText("Filtre sus gastos por fecha")
                .setSecondaryTextTypeface(Typeface.SANS_SERIF)
                .setSecondaryTextColour(resources.getColor(R.color.white))
                .setPromptBackground(RectanglePromptBackground())
                .setPromptFocal(RectanglePromptFocal())
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHED) {
                        val sharedPreferences = requireActivity().getSharedPreferences("tutorial_prefs_gastos", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putBoolean("tutorial_historial", true)
                            apply()
                        }
                    }
                }
                .show()
        }
    }


}