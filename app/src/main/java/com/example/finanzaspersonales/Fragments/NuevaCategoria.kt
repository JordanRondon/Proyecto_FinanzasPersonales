package com.example.finanzaspersonales.Fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.finanzaspersonales.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class NuevaCategoria : Fragment() {
    private lateinit var iconoAgua: ImageView ;private lateinit var iconoAntena: ImageView ;private lateinit var iconoBarco: ImageView
    private lateinit var iconobebe: ImageView ;private lateinit var iconobicicleta: ImageView ;private lateinit var iconoboleta: ImageView
    private lateinit var iconoreloj: ImageView ;private lateinit var iconobus: ImageView ;private lateinit var iconocarro2: ImageView
    private lateinit var iconocasa: ImageView ;private lateinit var iconocascomoto: ImageView ;private lateinit var iconocomerfuera: ImageView
    private lateinit var iconocomida: ImageView ;private lateinit var iconocomidachatarra: ImageView ;private lateinit var iconocomputadora: ImageView
    private lateinit var iconocorazon: ImageView ;private lateinit var iconodiente: ImageView ;private lateinit var iconoejercicio: ImageView
    private lateinit var iconofijo: ImageView ;private lateinit var iconofijo2: ImageView ;private lateinit var iconofuego: ImageView
    private lateinit var iconogadget: ImageView ;private lateinit var iconogasolina: ImageView ;private lateinit var iconogel: ImageView
    private lateinit var iconograduacion: ImageView ;private lateinit var iconogrifo: ImageView ;private lateinit var iconohamburguesa: ImageView
    private lateinit var iconolibros: ImageView ;private lateinit var iconolimpieza: ImageView ;private lateinit var iconoluz: ImageView
    private lateinit var iconomaletinprimeros_aux: ImageView ;private lateinit var iconomapa: ImageView ;private lateinit var iconomascota: ImageView
    private lateinit var iconomedicina: ImageView ;private lateinit var iconomochila: ImageView ;private lateinit var iconoperro: ImageView
    private lateinit var iconoplay: ImageView ;private lateinit var iconoraqueta: ImageView ;private lateinit var iconored: ImageView ;private lateinit var iconosalud: ImageView
    private lateinit var iconotimon: ImageView ;private lateinit var iconotren: ImageView
    private lateinit var iconoviaje: ImageView ;private lateinit var iconovideojuegos: ImageView ;private lateinit var iconovuelo2: ImageView
    private lateinit var iconowifi: ImageView ;private lateinit var iconoxbox: ImageView
    private var idiconoseleccionado: String=""
    private lateinit var database: DatabaseReference
    private lateinit var btnguardarcategoria: Button
    private lateinit var txtnombre: EditText
    private lateinit var txtdescripcion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = FirebaseAuth.getInstance().currentUser!!.uid

        database  = FirebaseDatabase.getInstance().getReference("Categoria/"+username)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nueva_categoria, container, false)
        txtnombre = view.findViewById(R.id.txt_nombre_nueva_categoria)
        txtdescripcion = view.findViewById(R.id.txt_descripcion)
        btnguardarcategoria = view.findViewById(R.id.btn_guardar_categoria)
        iconoAgua = view.findViewById(R.id.icono_agua);iconoAntena = view.findViewById(R.id.icono_antena)
        iconoBarco = view.findViewById(R.id.icono_barco);iconobebe= view.findViewById(R.id.icono_bebe)
        iconobicicleta= view.findViewById(R.id.icono_bicicleta);iconoboleta= view.findViewById(R.id.icono_boleta)
        iconoreloj= view.findViewById(R.id.icono_reloj);iconobus= view.findViewById(R.id.icono_bus)
        iconocarro2= view.findViewById(R.id.icono_carro2);iconocasa= view.findViewById(R.id.icono_casa)
        iconocascomoto= view.findViewById(R.id.icono_casco_moto);iconocomerfuera= view.findViewById(R.id.icono_comer_fuera)
        iconocomida= view.findViewById(R.id.icono_comida);iconocomidachatarra= view.findViewById(R.id.icono_comida_chatarra)
        iconocomputadora= view.findViewById(R.id.icono_computadora);iconocorazon= view.findViewById(R.id.icono_corazon)
        iconodiente= view.findViewById(R.id.icono_diente);iconoejercicio= view.findViewById(R.id.icono_ejercicio)
        iconofijo= view.findViewById(R.id.icono_fijo);iconofijo2= view.findViewById(R.id.icono_fijo2)
        iconofuego= view.findViewById(R.id.icono_fuego);iconogadget= view.findViewById(R.id.icono_gadget)
        iconogasolina= view.findViewById(R.id.icono_gasolina);iconogel= view.findViewById(R.id.icono_gel)
        iconograduacion= view.findViewById(R.id.icono_graduacion);iconogrifo= view.findViewById(R.id.icono_grifo)
        iconohamburguesa= view.findViewById(R.id.icono_hamburguesa);iconolibros= view.findViewById(R.id.icono_libros)
        iconolimpieza= view.findViewById(R.id.icono_limpieza);iconoluz= view.findViewById(R.id.icono_luz)
        iconomaletinprimeros_aux= view.findViewById(R.id.icono_maletin_primeros_aux)
        iconomapa= view.findViewById(R.id.icono_mapa);iconomascota= view.findViewById(R.id.icono_mascota)
        iconomedicina= view.findViewById(R.id.icono_medicina);iconomochila= view.findViewById(R.id.icono_mochila)
        iconoperro= view.findViewById(R.id.icono_perro);iconoplay= view.findViewById(R.id.icono_play)
        iconoraqueta= view.findViewById(R.id.icono_raqueta);iconored= view.findViewById(R.id.icono_red)
        iconosalud= view.findViewById(R.id.icono_salud);iconotimon= view.findViewById(R.id.icono_timon)
        iconotren= view.findViewById(R.id.icono_tren);iconoviaje= view.findViewById(R.id.icono_viaje)
        iconovideojuegos= view.findViewById(R.id.icono_videojuegos);iconovuelo2= view.findViewById(R.id.icono_vuelo2)
        iconowifi= view.findViewById(R.id.icono_wifi);iconoxbox= view.findViewById(R.id.icono_xbox)

        val iconos = listOf(iconoAgua, iconoAntena, iconoBarco ,iconobebe,
            iconobicicleta,iconoreloj,iconocarro2,iconocascomoto,iconocomida,iconocomputadora,iconodiente,iconofijo,iconofuego,
            iconogasolina,iconograduacion,iconohamburguesa,iconolimpieza,iconomaletinprimeros_aux,iconomapa,iconomedicina,iconoperro,
            iconoraqueta,iconosalud,iconotren,iconovideojuegos,iconoboleta,iconobus,iconocasa,iconocomerfuera,iconocomidachatarra,
            iconocorazon,iconoejercicio,iconofijo2,iconogadget,iconogel,iconogrifo,iconolibros,iconoluz,iconomascota,iconomochila,
            iconoplay,iconored,iconotimon,iconoviaje,iconovuelo2,iconowifi,iconoxbox
            )

        for (icono in iconos) {
            icono.setOnClickListener {
                selectIcon(it as ImageView, iconos)
                Toast.makeText(context, idiconoseleccionado, Toast.LENGTH_SHORT).show()
            }
        }
        btnguardarcategoria.setOnClickListener {
            RegistrarCategoria()
        }
        return view

    }
    private fun selectIcon(selected: ImageView, iconos: List<ImageView>) {
        for (icono in iconos) {
            if (icono == selected) {
                icono.setBackgroundColor(Color.CYAN)
                icono.setBackgroundResource(R.drawable.borde_cyan_categoria)
                idiconoseleccionado = resources.getResourceEntryName(icono.id)

            } else {
                icono.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
    private fun RegistrarCategoria() {
        val textonombre = txtnombre.text.toString()
        val textdescripcion=txtdescripcion.text.toString()
        if (textonombre.isNotEmpty() && textdescripcion.isNotEmpty() && idiconoseleccionado != "") {
            val nuevaCategoria = com.example.finanzaspersonales.Clases.Categoria_insertar(

                descripcion = textdescripcion,
                URLicono = idiconoseleccionado!!
            )

            database.child("Categoria $textonombre").setValue(nuevaCategoria)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_nuevaCategoria_to_categoria_item)
                    } else {
                        Toast.makeText(context, "Ha ocurrido un error al registrar.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Ha ocurrio un error", Toast.LENGTH_SHORT).show()
        }

    }
}