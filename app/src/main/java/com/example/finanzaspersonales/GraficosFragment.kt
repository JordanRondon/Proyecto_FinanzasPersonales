package com.example.finanzaspersonales

import android.graphics.Color
import android.os.Bundle
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

class GraficosFragment : Fragment() {
    private lateinit var graficoBarra: BarChart
    private lateinit var graficoLinea: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
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
        graficoLinea = view.findViewById(R.id.grafiAnio)

        val gastoDiarioLista: MutableList<Float> =
            mutableListOf(
                50f,    // Lunes
                65.50f, // Martes
                24.60f, // Miercoles
                30f,    // Jueves
                55.40f, // Viernes
                120.50f,// Sabado
                200.30f // Domingo
            )

        val gastoMensualLista: MutableList<Float> =
            mutableListOf(
                800.90f,  // Enero
                500.50f,  // Febrero
                450.60f,  // Marzo
                600f,     // Abril
                400.20f,  // Mayo
                790.99f,  // Junio
                899.10f, // Julio
                520.15f, // Agosto
                400.30f,  // Septiembre
                500.60f,  // Octubre
                400.20f,  // Novienbre
                1010.70f, // Diciembre
            )

        graficoBarraSemana(graficoBarra, gastoDiarioLista)
        graficoLineaAnio(graficoLinea, gastoMensualLista)
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

    private fun graficoLineaAnio(lineChart: LineChart, gastoMensual: List<Float>) {
        val datos = ArrayList<Entry>()

        for (i in gastoMensual.indices) { //ingreso de datos: BarEntry(coordenada x, coordenada y)
            datos.add(BarEntry(i.toFloat(), gastoMensual[i]))
        }

        val obtenerDato = LineDataSet(datos, "Gasto Anual")
        obtenerDato.color = Color.RED
        obtenerDato.setCircleColor(Color.BLUE)
        obtenerDato.lineWidth = 2f
        obtenerDato.circleRadius = 4f
        obtenerDato.setDrawCircleHole(false)

        val LineDato = LineData(obtenerDato)
        lineChart.data = LineDato
        lineChart.invalidate()

        // Configurar el eje X
        val ejeX = lineChart.xAxis
        ejeX.position = XAxis.XAxisPosition.BOTTOM
        ejeX.granularity = 1f
        ejeX.isGranularityEnabled = true
        ejeX.valueFormatter = mesDelAnio()

        // Configurar el eje Y izquierdo
        val ejeYIzquierdo = lineChart.axisLeft
        ejeYIzquierdo.axisMinimum = 0f

        // Configurar el eje Y derecho
        val ejeYDerecho = lineChart.axisRight
        ejeYDerecho.isEnabled = false

        // Configurar descripción
        lineChart.description.isEnabled = false

        // Configurar animaciones
        lineChart.animateY(1000)
        lineChart.animateX(1000)
    }

    inner class mesDelAnio : com.github.mikephil.charting.formatter.ValueFormatter() {
        private val diaSemana = arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nob", "Dis")
        override fun getFormattedValue(value: Float): String {
            return diaSemana.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraficosFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}