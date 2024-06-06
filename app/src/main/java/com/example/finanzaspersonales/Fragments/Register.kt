package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.R


class Register : Fragment() {

    private lateinit var btnRegistro: Button
    private lateinit var tvRegistroInicio: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        btnRegistro = view.findViewById(R.id.btnRegistro)
        tvRegistroInicio = view.findViewById(R.id.tvRegistroInicio)

        val clickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        btnRegistro.setOnClickListener(clickListener)
        tvRegistroInicio.setOnClickListener(clickListener)

        return view
    }

}