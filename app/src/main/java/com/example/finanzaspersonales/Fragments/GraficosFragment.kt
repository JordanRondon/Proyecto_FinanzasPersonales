package com.example.finanzaspersonales

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GraficosFragment : Fragment() {
    private lateinit var graficoBarra: BarChart
    private lateinit var graficoCategoria: PieChart
    private lateinit var database: DatabaseReference
    private lateinit var databaseGasto: DatabaseReference
    private val listaColores = listOf(
        Color.parseColor("#FF5733"), Color.parseColor("#3380FF"),
        Color.parseColor("#8633FF"), Color.parseColor("#FFBE33"),
        Color.parseColor("#A4E812"), Color.parseColor("#A22B5A"),
        Color.parseColor("#620050"), Color.parseColor("#22AD19"),
        Color.parseColor("#D68910"), Color.parseColor("#616A6B"),
        Color.parseColor("#2980B9"), Color.parseColor("#138D75"),
        Color.parseColor("#34495E"), Color.parseColor("#E9967A"),
        Color.parseColor("#D18C14"), Color.parseColor("#61C289"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("GastoSemanal/$username")
        databaseGasto = FirebaseDatabase.getInstance().getReference("Gasto/$username")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graficos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graficoBarra = view.findViewById(R.id.graficoSemana)
        graficoCategoria = view.findViewById(R.id.graficoCategoria)

        obtenerGastoSemanal(database) { datos ->
            graficoBarraSemana(graficoBarra, datos)
        }

        obtenerCategoriasYMontos(databaseGasto) { datos ->
            graficoMontoPorCategoria(graficoCategoria, datos)
        }
    }

    private fun graficoBarraSemana(barChart: BarChart, gastoDiario: List<Float>) {

        val datos = ArrayList<BarEntry>() // lista de tipo "BarEntry" para pasarlo al grafico

        for (i in gastoDiario.indices) { //ingreso de datos: BarEntry(coordenada x, coordenada y)
            datos.add(BarEntry(i.toFloat(), gastoDiario[i]))
        }

        val obtenerDato = BarDataSet(datos, "Gasto Semanal")
        obtenerDato.color = Color.BLUE //color de barra

        val barraDato = BarData(obtenerDato)
        barraDato.barWidth = 0.5f

        barChart.data = barraDato
        barChart.setFitBars(true)
        barChart.invalidate()

        // Configurar el eje X
        val ejeX = barChart.xAxis
        ejeX.position = XAxis.XAxisPosition.BOTTOM
        ejeX.granularity = 1f
        ejeX.isGranularityEnabled = true
        ejeX.valueFormatter = diasDeLaSemana()

        // Configurar el eje Y izquierdo
        val ejeYIzquierdo = barChart.axisLeft
        ejeYIzquierdo.axisMinimum = 0f

        // Configurar el eje Y derecho
        val ejeYDerecho = barChart.axisRight
        ejeYDerecho.isEnabled = false

        // Configurar descripción
        barChart.description.isEnabled = false

        // Configurar animaciones
        barChart.animateY(1000)
        barChart.animateX(1000)
    }

    inner class diasDeLaSemana : com.github.mikephil.charting.formatter.ValueFormatter() {
        private val diaSemana = arrayOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        override fun getFormattedValue(value: Float): String {
            return diaSemana.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    private fun obtenerGastoSemanal(gastoSemanalRef: DatabaseReference, callback: (List<Float>) -> Unit) {

        gastoSemanalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val resultadoNode = dataSnapshot.child("resultado") //referencia de resultado
                val resultado = mutableMapOf<String, Float>()
                resultadoNode.children.forEach {
                    val key = it.key ?: ""
                    val valor= it.getValue(Float::class.java) ?: 0F
                    resultado[key] = valor
                }

                val valoresOrdenados = mutableListOf<Float>()
                val diasSemana = listOf("lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo")
                for (dia in diasSemana) {
                    valoresOrdenados.add(resultado[dia] ?: 0F)
                }

                callback(valoresOrdenados)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error al obtener los datos: ${error.message}")
            }
        })

    }

    private fun obtenerCategoriasYMontos(gastoRef: DatabaseReference, callback: (MutableMap<String, Float>) -> Unit) {
        val listaGastos = mutableListOf<EntidadGasto>()
        // Una vez que las categorías se han cargado, carga los gastos
        gastoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (gastoSnapshot in dataSnapshot.children) {
                    if (gastoSnapshot.key == "contador") continue // Saltar el nodo contador
                    val categoriaID = gastoSnapshot.child("categoriaID").getValue(String::class.java) ?: ""
                    val nombreCategoria = categoriaID.substring(10)
                    val monto = gastoSnapshot.child("monto").getValue(Float::class.java) ?: 0.0f
                    listaGastos.add(EntidadGasto(nombreCategoria, "", monto, ""))
                }

                // Calcular el monto total por categoría
                val montosPorCategoria = mutableMapOf<String, Float>()
                for (gasto in listaGastos) {
                    montosPorCategoria[gasto.categoriaID] = (montosPorCategoria[gasto.categoriaID] ?: 0.0f) + gasto.monto
                }

                callback(montosPorCategoria)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error al obtener los datos Gastos: ${databaseError.message}")
            }
        })
    }

    private fun graficoMontoPorCategoria(pieChart:PieChart ,montoCategoria: MutableMap<String, Float>) {
        val pieEntries = ArrayList<PieEntry>()

        for ((categoriaID, montoTotal) in montoCategoria) {
            pieEntries.add(PieEntry(montoTotal, categoriaID))
        }

        val pieDataSet = PieDataSet(pieEntries, "Gasto Por Categoria")
        pieDataSet.colors = listaColores//listOf(Color.RED, Color.GREEN, Color.BLUE, Color.parseColor("#FF5733"))
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueTextSize = 16f

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Gasto (S./)"
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.animateY(1000)
    }
}