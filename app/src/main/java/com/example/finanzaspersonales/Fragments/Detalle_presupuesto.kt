package com.example.finanzaspersonales.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.Clases.Categoria_insertar
import com.example.finanzaspersonales.Clases.Presupuesto_Firebase_insertar
import com.example.finanzaspersonales.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat


class Detalle_presupuesto : Fragment() {


    private lateinit var database: DatabaseReference
    private lateinit var database_categoria: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_detalle_presupuesto, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val presupuesto_id = arguments?.getString("Presupuesto_id")

        val txtPresupuestoDetalle: TextView? = view.findViewById(R.id.txt_presupuesto_detalle)

        val txtmontototal: TextView? = view.findViewById(R.id.txt_monto_total_detalle)
        val txtmontoactual: TextView? = view.findViewById(R.id.txt_monto_actual_detalle)
        val txtcategoria: TextView? = view.findViewById(R.id.txt_categoria_detalle)
        val txtperiodo: TextView? = view.findViewById(R.id.txt_periodo_detalle)
        val txtfecharegistro: TextView? = view.findViewById(R.id.txt_fecha_registro_detalle)
        val txtfechavencimiento: TextView? = view.findViewById(R.id.txt_fecha_vencimiento_detalle)
        val txtestado: TextView? = view.findViewById(R.id.estado_activo_detalle)
        val txt_numero_barra: TextView? = view.findViewById(R.id.txt_numero_progress_bar)
        val progressBar: ProgressBar? = view.findViewById(R.id.progress_bar_presupuesto)
        var img_presupuesto: ImageView? = view.findViewById(R.id.img_presupuesto_detalle)
        val username = FirebaseAuth.getInstance().currentUser!!.uid
        val imgDescartarDetallePresupuesto: ImageView = view.findViewById(R.id.img_descartar_detalle_presupuesto)

        if (presupuesto_id != null) {
            txtPresupuestoDetalle?.text = presupuesto_id
            database = FirebaseDatabase.getInstance().getReference("Presupuesto/$username/$presupuesto_id")

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    val presupuesto = snapshot.getValue(Presupuesto_Firebase_insertar::class.java)

                    if (presupuesto != null) {
                        database_categoria= FirebaseDatabase.getInstance().getReference("Categoria/$username/${presupuesto.categoriaID}")

                        database_categoria.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot2: DataSnapshot) {

                                val categoria = snapshot2.getValue(Categoria_insertar::class.java)

                                if (categoria != null) {

                                    if (img_presupuesto != null) {
                                        img_presupuesto.setImageResource(getIconResource(categoria.URLicono))
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Maneja el error
                                Log.e("Firebase", "Error al obtener datos", error.toException())
                            }
                        })




                        val porcentaje_barra=((presupuesto.monto_actual/presupuesto.monto_total)*100).toInt()
                        txt_numero_barra?.text =porcentaje_barra.toString()+"%"
                        progressBar?.progress = porcentaje_barra
                        if(porcentaje_barra<=40){

                            progressBar?.progressDrawable= ContextCompat.getDrawable(requireContext(), R.drawable.estilo_barra_progreso)
                        }
                        else if(porcentaje_barra>50 && porcentaje_barra<=75){
                            progressBar?.progressDrawable= ContextCompat.getDrawable(requireContext(), R.drawable.estilo_barra_progreso2)
                        }
                        else{
                            progressBar?.progressDrawable= ContextCompat.getDrawable(requireContext(), R.drawable.estilo_barra_progreso3)
                        }
                        txtmontototal?.text = "S/ "+presupuesto.monto_total.toString()
                        txtmontoactual?.text = "S/ %.2f".format(presupuesto.monto_actual)

                        txtcategoria?.text = presupuesto.categoriaID
                        txtperiodo?.text = presupuesto.periodo
                        txtfecharegistro?.text = presupuesto.fechaInicio
                        txtfechavencimiento?.text = presupuesto.fechaCulminacion
                        if(presupuesto.estado== true){
                            txtestado?.text = "El periodo del presupuesto esta activo"
                        }else{
                            txtestado?.text = "El periodo del presupuesto esta vencido"
                        }
                        //txtestado?.text = presupuesto.estado.toString()
                        imgDescartarDetallePresupuesto.setOnClickListener {
                            try {
                                MostrarAlertDialog(username, presupuesto_id)
                            } catch (e: NumberFormatException) {
                                Toast.makeText(context, "ERROR: No se pudo eliminar el presupuesto", Toast.LENGTH_SHORT).show()
                            }


                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error al obtener datos", error.toException())
                }
            })
        }




    }
    private fun MostrarAlertDialog(username: String, presupuesto_id: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminación Presupuesto")
        builder.setMessage("¿Esta seguro que desea eliminar un presupuesto?.")

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            eliminarPresupuesto(username, presupuesto_id,
                onSuccess = {
                    Toast.makeText(context, "Presupuesto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_detalle_presupuesto_to_presupuestos)
                },
                onFailure = { exception ->
                    Toast.makeText(context, "Error al eliminar el presupuesto: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->

            dialog.dismiss()
        }
        builder.create().show()
    }
    private fun eliminarPresupuesto(username: String, presupuestoId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Presupuesto/$username/$presupuestoId")

        databaseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                task.exception?.let { onFailure(it) }
            }
        }
    }
    private fun getIconResource(iconName: String): Int {
        val dictIconos: Map<String, Int> = mapOf(
            "icono_agua" to R.drawable.icono_agua, "icono_comida" to R.drawable.icono_comida,
            "icono_antena" to R.drawable.icono_antena, "icono_barco" to R.drawable.icono_barco,
            "icono_bebe" to R.drawable.icono_bebe, "icono_bicicleta" to R.drawable.icono_bicicleta,
            "icono_boleta" to R.drawable.icono_boleta, "icono_reloj" to R.drawable.icono_reloj,
            "icono_bus" to R.drawable.icono_bus, "icono_carro2" to R.drawable.icono_carro2,
            "icono_casa" to R.drawable.icono_casa, "icono_casco_moto" to R.drawable.icono_casco_moto,
            "icono_comer_fuera" to R.drawable.icono_comer_fuera, "icono_comida_chatarra" to R.drawable.icono_comida_chatarra,
            "icono_computadora" to R.drawable.icono_computadora, "icono_corazon" to R.drawable.icono_corazon,
            "icono_diente" to R.drawable.icono_diente, "icono_ejercicio" to R.drawable.icono_ejercicio,
            "icono_entregas" to R.drawable.icono_entregas, "icono_fijo" to R.drawable.icono_fijo,
            "icono_fijo2" to R.drawable.icono_fijo2, "icono_fuego" to R.drawable.icono_fuego,
            "icono_gadget" to R.drawable.icono_gadget, "icono_gasolina" to R.drawable.icono_gasolina,
            "icono_gel" to R.drawable.icono_gel, "icono_graduacion" to R.drawable.icono_graduacion,
            "icono_grifo" to R.drawable.icono_grifo, "icono_hamburguesa" to R.drawable.icono_hamburguesa,
            "icono_libros" to R.drawable.icono_libros, "icono_limpieza" to R.drawable.icono_limpieza,
            "icono_luz" to R.drawable.icono_luz, "icono_maletin_primeros_aux" to R.drawable.icono_maletin_primeros_aux,
            "icono_mapa" to R.drawable.icono_mapa, "icono_mascota" to R.drawable.icono_mascota,
            "icono_medicina" to R.drawable.icono_medicina, "icono_mochila" to R.drawable.icono_mochila,
            "icono_perro" to R.drawable.icono_perro, "icono_play" to R.drawable.icono_play,
            "icono_raqueta" to R.drawable.icono_raqueta, "icono_red" to R.drawable.icono_red,
            "icono_salud" to R.drawable.icono_salud, "icono_timon" to R.drawable.icono_timon,
            "icono_tren" to R.drawable.icono_tren, "icono_viaje" to R.drawable.icono_viaje,
            "icono_videojuegos" to R.drawable.icono_videojuegos, "icono_vuelo" to R.drawable.icono_vuelo,
            "icono_vuelo2" to R.drawable.icono_vuelo2, "icono_wifi" to R.drawable.icono_wifi,
            "icono_xbox" to R.drawable.icono_xbox, "agua_icono" to R.drawable.agua_icono,
            "comida_icono" to R.drawable.comida_icono, "luz_icono" to R.drawable.luz_icono
        )
        return dictIconos[iconName] ?: R.drawable.moneda
    }

}