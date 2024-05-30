package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finanzaspersonales.GastosFragment
import com.example.finanzaspersonales.GraficosFragment
import com.example.finanzaspersonales.R
import com.google.android.material.button.MaterialButtonToggleGroup

class HistorialFragment : Fragment() {

    private lateinit var toggleGroup: MaterialButtonToggleGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleGroup = view.findViewById(R.id.toggleButton)

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val fragment = when (checkedId) {
                    R.id.btnGastos -> GastosFragment()
                    R.id.BtnGraficos -> GraficosFragment()
                    else -> null
                }
                fragment?.let {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, it)
                        .commit()
                }
            }
        }

        if (savedInstanceState == null) {
            toggleGroup.check(R.id.btnGastos)
        }
    }
}