package com.example.finanzaspersonales

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GraficosFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graficos, container, false)

        val barChart = view.findViewById<BarChart>(R.id.barChart)
        setupBarChart(barChart)

        return view
    }

    private fun setupBarChart(barChart: BarChart) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 10f))
        entries.add(BarEntry(2f, 20f))
        entries.add(BarEntry(3f, 30f))
        entries.add(BarEntry(4f, 40f))
        entries.add(BarEntry(5f, 50f))

        val dataSet = BarDataSet(entries, "Label")
        dataSet.color = Color.BLUE

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        barChart.data = barData
        barChart.setFitBars(true)
        barChart.invalidate()

        // Configurar el eje X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        // Configurar el eje Y izquierdo
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f

        // Configurar el eje Y derecho
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        // Configurar descripción
        barChart.description.isEnabled = false

        // Configurar animaciones
        barChart.animateY(1000)
        barChart.animateX(1000)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraficosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}