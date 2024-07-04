package com.example.finanzaspersonales.Fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.finanzaspersonales.Clases.isOnline
import com.example.finanzaspersonales.GastosFragment
import com.example.finanzaspersonales.GraficosFragment
import com.example.finanzaspersonales.R
import com.google.android.material.button.MaterialButtonToggleGroup
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal

class HistorialFragment : Fragment() {

    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var main: ConstraintLayout
    private lateinit var connection: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleGroup = view.findViewById(R.id.toggleButton)
        main = view.findViewById(R.id.main2)
        connection = view.findViewById(R.id.connection)

        if (!isOnline(requireContext())) {
            connection.visibility = View.VISIBLE
            main.visibility = View.INVISIBLE
        } else {
            connection.visibility = View.INVISIBLE
            main.visibility = View.VISIBLE

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


}