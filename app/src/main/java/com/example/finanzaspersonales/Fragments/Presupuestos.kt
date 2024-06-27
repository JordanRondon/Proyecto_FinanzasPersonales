package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.CategoriaAdapter
import com.example.finanzaspersonales.Clases.Presupuesto
import com.example.finanzaspersonales.Clases.Presupuesto_Firebase
import com.example.finanzaspersonales.PresupuestoAdapter
import com.example.finanzaspersonales.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.Clases.Presupuesto_Firebase_insertar
import com.example.finanzaspersonales.Clases.isOnline
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern


class Presupuestos : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var btnAgregar: Button
    private lateinit var etNombre: EditText
    private lateinit var etMonto: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var adapterPresupuesto: PresupuestoAdapter
    private val presupuestos_firebase = mutableListOf<Presupuesto_Firebase>()
    private lateinit var database_categoria: DatabaseReference
    private lateinit var database_presupuesto: DatabaseReference
    private val categoriasList = mutableListOf<Categoria>()
    private lateinit var textoSeleccionado: String

    private lateinit var main: ConstraintLayout
    private lateinit var connection: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_presupuestos, container, false)

        val navController = findNavController()

        recycler = view.findViewById(R.id.recyclerView)
        spinner = view.findViewById(R.id.spinner)
        btnAgregar = view.findViewById(R.id.btnAgregar)
        etNombre = view.findViewById(R.id.etNombre)
        etMonto = view.findViewById(R.id.etMonto)
        etMonto.filters = arrayOf(DecimalDigitsInputFilter(5, 2))
        main = view.findViewById(R.id.main)
        connection = view.findViewById(R.id.connection)

        if (!isOnline(requireContext())) {
            connection.visibility = View.VISIBLE
            main.visibility = View.INVISIBLE
        } else {
            connection.visibility = View.INVISIBLE
            main.visibility = View.VISIBLE

            val username = FirebaseAuth.getInstance().currentUser!!.uid

            database_categoria = FirebaseDatabase.getInstance().getReference("Categoria/$username")
            database_presupuesto =
                FirebaseDatabase.getInstance().getReference("Presupuesto/$username")


            recycler.layoutManager = LinearLayoutManager(context)

            adapterPresupuesto = PresupuestoAdapter(presupuestos_firebase, navController)

            recycler.adapter = adapterPresupuesto


            btnAgregar.setOnClickListener {

                val nombrePresupuesto = etNombre.text.toString()
                val categoriaSeleccionada = spinner.selectedItem as? Categoria
                val montoTotalText = etMonto.text.toString()
                var montototal: Double
                if (categoriaSeleccionada == null) {
                    Toast.makeText(context, "ERROR: No hay ninguna categoria", Toast.LENGTH_SHORT)
                        .show()
                    if (montoTotalText.isEmpty()) {
                        Toast.makeText(
                            context,
                            "ERROR: Monto total no puede estar vacío",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
                try {
                    montototal = montoTotalText.toDouble()
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        context,
                        "ERROR: Monto total debe ser un número válido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                var fecha_siguiente = "05/12/2025"
                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                var currentDate = "05/11/2025"
                currentDate = simpleDateFormat.format(calendar.time)

                Toast.makeText(context, currentDate, Toast.LENGTH_SHORT).show()

                radioGroup = view.findViewById(R.id.radiogrouppresupuesto)
                val radioButtonId = radioGroup.checkedRadioButtonId

                if (radioButtonId != -1) {

                    val radioButton: RadioButton = view.findViewById(radioButtonId)
                    textoSeleccionado = radioButton.text.toString()
                    if (textoSeleccionado == "Mensual") {
                        calendar.add(Calendar.MONTH, 1)
                        fecha_siguiente = simpleDateFormat.format(calendar.time)
                    } else {
                        calendar.add(Calendar.WEEK_OF_YEAR, 1)
                        fecha_siguiente = simpleDateFormat.format(calendar.time)
                    }
                }

                try {
                    montototal = montoTotalText.toDouble()
                } catch (e: NumberFormatException) {
                    montototal = Double.NaN
                    Toast.makeText(
                        context,
                        "ERROR: Monto total debe ser un número válido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (nombrePresupuesto.isNotEmpty() && !montototal.isNaN() && categoriaSeleccionada != null) {
                    val Presupuesto_registro = Presupuesto_Firebase_insertar(
                        categoriaSeleccionada.nombre,
                        true,
                        fecha_siguiente,
                        currentDate,
                        0.0,
                        montototal,
                        textoSeleccionado
                    )
                    database_presupuesto.child("Presupuesto ${nombrePresupuesto}")
                        .setValue(Presupuesto_registro)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loadPresupuesto()
                                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT)
                                    .show()

                            } else {
                                Toast.makeText(
                                    context,
                                    "Ha ocurrido un error al registrar.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Ha ocurrio un error", Toast.LENGTH_SHORT).show()
                }


                adapterPresupuesto.notifyDataSetChanged()
                etNombre.text.clear()
                etMonto.text.clear()
                spinner.setSelection(0)
            }
            loadCategories()
            loadPresupuesto()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emptyCategoriasList = mutableListOf<Categoria>()
        spinner.adapter = CategoriaAdapter(
            requireContext(),
            R.layout.presupuesto_items_spinner,
            emptyCategoriasList
        )


    }

    private fun loadCategories() {
        database_categoria.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasList.clear()
                for (presupuestoSnapshot in snapshot.children) {
                    val nombreCategoria = presupuestoSnapshot.key
                    val categoria = presupuestoSnapshot.getValue(Categoria::class.java)
                    if (categoria != null && nombreCategoria != null) {
                        categoria.nombre = nombreCategoria
                        categoriasList.add(categoria)
                    }
                }
                (spinner.adapter as CategoriaAdapter).apply {
                    clear()
                    addAll(categoriasList)
                    notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Presupuestos", "Error al obtener las categorías", error.toException())
            }
        })
    }
    class DecimalDigitsInputFilter(private val maxDigitsBeforeDecimal: Int, private val maxDigitsAfterDecimal: Int) :
        InputFilter {
        private val pattern = Pattern.compile("[0-9]{0,$maxDigitsBeforeDecimal}(\\.[0-9]{0,$maxDigitsAfterDecimal})?")

        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val newString = dest?.substring(0, dstart) + source?.substring(start, end) + dest?.substring(dend)
            return if (pattern.matcher(newString).matches()) {
                null
            } else {
                ""
            }
        }
    }

    private fun loadPresupuesto() {
        database_presupuesto.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                presupuestos_firebase.clear()
                for (presupuestoSnapshot in snapshot.children) {
                    val nombrePresupuesto = presupuestoSnapshot.key
                    val presupuesto = presupuestoSnapshot.getValue(Presupuesto_Firebase::class.java)
                    if (presupuesto != null && nombrePresupuesto != null) {
                        presupuesto.nombre = nombrePresupuesto
                        presupuestos_firebase.add(presupuesto)
                    }
                }
                adapterPresupuesto.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CategoriaFragment", "Error al obtener las categorías", error.toException())
            }
        })
    }


}