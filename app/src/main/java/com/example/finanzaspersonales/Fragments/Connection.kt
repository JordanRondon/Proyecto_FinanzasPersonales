package com.example.finanzaspersonales.Fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.Clases.isOnline
import com.example.finanzaspersonales.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Connection : Fragment() {

//    private lateinit var btn_Reload: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_connection, container, false)

//        btn_Reload = view.findViewById(R.id.btn_Reload)
//
//        btn_Reload.setOnClickListener {
//            if (isOnline(requireContext())) {
//                val ft = parentFragmentManager.beginTransaction()
//                ft.detach(this).attach(this).commit()
//            } else {
//                Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
//            }
//        }

        return view
    }
}