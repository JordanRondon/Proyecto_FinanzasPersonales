package com.example.finanzaspersonales

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GraficosFragment : Fragment() {
    private lateinit var graficoBarra: BarChart
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("GastoSemanal/$username")
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

        obtenerGastoSemanal(database) { datos ->
            graficoBarraSemana(graficoBarra, datos)
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
}