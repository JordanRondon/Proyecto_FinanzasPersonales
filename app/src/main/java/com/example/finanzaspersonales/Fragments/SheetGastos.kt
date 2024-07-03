package com.example.finanzaspersonales.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.TaskViewModel
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.CategoriaGastosAdapter
import com.example.finanzaspersonales.databinding.FragmentSheetGastosBinding
import com.example.finanzaspersonales.entidades.CategoriaGastos
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finanzaspersonales.Fragments.AlarmNotification.Companion.NOTI_ID
import com.example.finanzaspersonales.adaptadores.PresupuestoGastosAdapter
import com.example.finanzaspersonales.Fragments.AlarmNotification.Companion.NOTI_ID2
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.example.finanzaspersonales.entidades.Notificacion
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class SheetGastos : BottomSheetDialogFragment(), CategoriaGastosAdapter.CategoriaClickListener,
    PresupuestoGastosAdapter.PresupuestoClickListener {

    private lateinit var binding: FragmentSheetGastosBinding
    private lateinit var taskViewModel: TaskViewModel
    private val arrayListCategoria: ArrayList<CategoriaGastos> = ArrayList()
    private val arrayListPresupuestos: ArrayList<CategoriaGastos> = ArrayList()

    private val zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Lima"))
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val user = Firebase.auth.currentUser!!.uid
    private val contadorReference =
        FirebaseDatabase.getInstance().getReference("Gasto/$userId/contador/ultimo_gasto")
    private val gastoReference = FirebaseDatabase.getInstance().getReference("Gasto/$userId")
    private val categoriaReference =
        FirebaseDatabase.getInstance().getReference("Categoria/$userId")
    private val presupuestoReference =
        FirebaseDatabase.getInstance().getReference("Presupuesto/$userId")

    ///notificaciones
    private val notificationReference =
        FirebaseDatabase.getInstance().getReference("Notificacion").child(user)
    private val notiticationCounterReference =
        FirebaseDatabase.getInstance().getReference("Notificacion").child(user).child("contador")
            .child("ultima_notificacion")


    private lateinit var database: DatabaseReference

    private lateinit var recyclerViewCategoria: RecyclerView
    private lateinit var recyclerViewPresupuestos: RecyclerView
    private lateinit var categoriaGastosAdapter: CategoriaGastosAdapter
    private lateinit var presupuestoGastosAdapter: PresupuestoGastosAdapter

    private lateinit var navController: NavController

    private val DEFAULT_CATEGORIA = CategoriaGastos("Agregar", "ic_add_circle")
    private val DEFAULT_PRESUPUESTO = CategoriaGastos("Agregar", "ic_add_circle")
    private val SIN_PRESUPUESTO = CategoriaGastos("Sin presupuesto", "ic_not")

    companion object {
        const val MI_CANAL_ID = "CanalPresupuesto"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE_PERMISSIONS = 1001
    }

    fun createSimpleNotification(presupuestoId: String, context: Context) {
        val builder = NotificationCompat.Builder(context, MI_CANAL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Notificación de LooKash")
            .setContentText("Se notifica que $presupuestoId ha excedido el monto límite")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } else {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Mi Canal"
            val descripcion = "Descripción de mi canal"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(MI_CANAL_ID, nombre, importancia).apply {
                description = descripcion
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = false

        crearCanalDeNotificacion()
        val activity = requireActivity()
        navController = findNavController()

        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)

        recyclerViewCategoria = view.findViewById(R.id.recyclerViewCategoriaGastos)
        recyclerViewPresupuestos = view.findViewById(R.id.recyclerViewPresupuestos)

        recyclerViewCategoria.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerViewPresupuestos.layoutManager = GridLayoutManager(requireContext(), 3)


        categoriaGastosAdapter =
            CategoriaGastosAdapter(arrayListCategoria, requireContext(), navController)
        presupuestoGastosAdapter =
            PresupuestoGastosAdapter(arrayListPresupuestos, requireContext(), navController)
        recyclerViewCategoria.adapter = categoriaGastosAdapter
        recyclerViewPresupuestos.adapter = presupuestoGastosAdapter


        binding.btnGuardarCategoria.setOnClickListener {
            saveGastos()
        }
        setAlarm(requireContext(),14,0, NOTI_ID," ☀ Date un momento y registra tus gastos del día para mantener tus finanzas al día! (•‿•)")//alarma a las 2 de la tarde
        setAlarm(requireContext(),23,0, NOTI_ID2,"☾⋆⁺₊✧ Antes de dormir, tus gastos has de escribir ¡regístralos! ⋆⁺₊⋆ ☾ ")//alarmar a las 11 de la noche
        loadCategoriasYPresupuestos()

        categoriaGastosAdapter.setCategoriaClickListener(this)
        presupuestoGastosAdapter.setPresupuestoClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSheetGastosBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun saveGastos() {
        val categoriaID = categoriaGastosAdapter.getCategoriaSelected()?.nombre
        val presupuestoID = presupuestoGastosAdapter.getCategoriaSelected()?.nombre
        val categoriaMonto = binding.etMonto.text.toString().toFloatOrNull()
        val date = zonedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val time = zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        var descripcion = binding.txtDescripcion.text.toString()

        contadorReference.get().addOnSuccessListener { data ->
            val contador = data.getValue(Int::class.java) ?: 0
            val contadorUpdate = contador + 1

            if (categoriaID != null && presupuestoID != null && categoriaMonto != null) {
                if (descripcion.isEmpty()) {
                    descripcion = if (presupuestoID == "Sin presupuesto") {
                        categoriaID.substringAfter(' ')
                    } else {
                        presupuestoID.substringAfter(' ')
                    }
                }

                val gasto = EntidadGasto(
                    categoriaID,
                    presupuestoID,
                    categoriaMonto,
                    date,
                    time,
                    descripcion
                )

                gastoReference.child(contadorUpdate.toString()).setValue(gasto)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Gasto guardado exitosamente", Toast.LENGTH_SHORT).show()

                        if (presupuestoID != "Sin presupuesto") {
                            setGastoSemanal_dia(categoriaMonto)
                            setGastoPresupuesto(categoriaMonto, presupuestoID, requireContext(), categoriaID)
                            binding.etMonto.text.clear()
                        }

                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error al guardar el gasto", Toast.LENGTH_SHORT).show()
                    }
                contadorReference.setValue(contadorUpdate)


            } else {
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }


        }
    }

    private fun loadCategoriasYPresupuestos() {
        categoriaReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(categoriaSnapshot: DataSnapshot) {

                val categoriasMap = mutableMapOf<String, String>()

                arrayListCategoria.clear()

                arrayListCategoria.add(DEFAULT_CATEGORIA)

                for (catSnap: DataSnapshot in categoriaSnapshot.children) {
                    val categoriaID = catSnap.key
                    val urlIcon = catSnap.child("urlicono").getValue(String::class.java) ?: ""
                    if (categoriaID != null) {
                        categoriasMap[categoriaID] = urlIcon

                        arrayListCategoria.add(CategoriaGastos(categoriaID, urlIcon))
                    }
                }

                categoriaGastosAdapter.notifyDataSetChanged()

                presupuestoReference.addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(data: DataSnapshot) {

                        arrayListPresupuestos.clear()

                        arrayListPresupuestos.add(DEFAULT_PRESUPUESTO)
                        arrayListPresupuestos.add(SIN_PRESUPUESTO)


                        for (ds: DataSnapshot in data.children) {
                            val presupuestoID = ds.key
                            val categoriaID =
                                ds.child("categoriaID").getValue(String::class.java)
                            val urlIcon = categoriasMap[categoriaID]
                            val estado = ds.child("estado").getValue(Boolean::class.java)
                            if (estado == true) {
                                arrayListPresupuestos.add(
                                    CategoriaGastos(
                                        presupuestoID ?: "", urlIcon
                                    )
                                )
                            }

                        }
                        presupuestoGastosAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar el error
                    }
                })

            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })


    }

    private fun setGastoSemanal_dia(NuevoGastoMonto: Float) {
        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        val diaSemana = obtenerDiaSemana()
        val gastoSemanal_dia_Ref =
            FirebaseDatabase.getInstance().getReference("GastoSemanal/$user/resultado/$diaSemana")

        //obtiene el valor actual del dia indicado
        gastoSemanal_dia_Ref.get().addOnSuccessListener { data ->
            val gastoActual = data.getValue(Float::class.java) ?: 0f
            val gastoActualizado = gastoActual + NuevoGastoMonto

            //actualiza monto del dia
            gastoSemanal_dia_Ref.setValue(gastoActualizado).addOnCompleteListener { tarea ->
                if (!tarea.isSuccessful) {
                    println("Error al actualizar el valor: ${tarea.exception?.message}")
                }
            }
        }.addOnFailureListener { exception ->
            println("Error al obtener el valor actual: ${exception.message}")
        }
    }
    private fun obtenerIconoCategoria(categoriaID : String,callback: (String) -> Unit){
        //referenciar a la categoria del gasto
        val categoriaRef = FirebaseDatabase.getInstance()
            .getReference("Categoria/$user/$categoriaID/urlicono")
        //obtener icono categoria excedida
        categoriaRef.get().addOnSuccessListener { data->
            val ico_categoria_excedida =data.getValue(String::class.java) ?:"No se encontro"
            callback(ico_categoria_excedida)
        }.addOnFailureListener { exception ->
            println("Error al obtener el icono: ${exception.message}")
            callback("Error al obtener el icono")
        }
    }
    private fun saveNotification(presupuestoId: String, icono : String, context: Context) {
        val sdf= SimpleDateFormat("EEE, dd MMM yyyy, h:mma")
        sdf.setTimeZone(TimeZone.getTimeZone("America/Lima"))
        val date = sdf.format(Date()).toString()
        val notification = Notificacion(icono, "Advertencia","Se notifica que $presupuestoId ha excedido el monto límite",date,false)
        notificationReference.get().addOnSuccessListener {dataSnapshot->
            var  nextNumNotification = 1// default
            if(dataSnapshot.exists()){
                for(i in dataSnapshot.children)
                    if(i.key != "contador") nextNumNotification += 1
            }

            notiticationCounterReference.setValue(nextNumNotification)
            notificationReference.child(nextNumNotification.toString()).setValue(notification).addOnSuccessListener {
            }.addOnFailureListener{exception ->
                println("Error al actualizar registro: ${exception.message}")
            }
        }.addOnFailureListener{exception ->
            println("Error al obtener el valor actual: ${exception.message}")
        }
    }

    private fun setAlarm(context: Context, hour: Int, minute: Int, id: Int, descripcion : String) {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima")).apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        //verificar que la alarma se lance a la hora especificada si no se pasa al dia siguiente en vez de lanzarse inmediatamente
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val intent = Intent(context, AlarmNotification::class.java)
            .putExtra("asunto","LooKash")
            .putExtra("descripcion",descripcion)
            .putExtra("id",id)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,AlarmManager.INTERVAL_DAY,pendingIntent)

    }
    private fun setGastoPresupuesto(nuevoGastoMonto: Float, presupuestoId: String, context: Context, categoriaID: String) {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        if (user == null) {
            println("Usuario no autenticado")
            return
        }

        val gastoPresupuestoRef = FirebaseDatabase.getInstance()
            .getReference("Presupuesto/$user/$presupuestoId/monto_actual")

        gastoPresupuestoRef.get().addOnSuccessListener { data ->
            val montoActual = data.getValue(Float::class.java) ?: 0f
            val montoPresupuestoActualizado = montoActual + nuevoGastoMonto

            obtenerMontoTotalPresupuesto(presupuestoId) { montoTotal ->
                if (montoTotal != null) {
                    if (montoTotal <= montoPresupuestoActualizado) {
                        //obtener icono de la categoria del presupuesto excedido
                        obtenerIconoCategoria(categoriaID) { icoCategoria ->
                            saveNotification(presupuestoId, icoCategoria, context)
                        }
                        createSimpleNotification(presupuestoId, context)

                    }
                } else {
                    println("No se pudo obtener el monto total del presupuesto")
                }
                gastoPresupuestoRef.setValue(montoPresupuestoActualizado)
                    .addOnCompleteListener { tarea ->
                        if (!tarea.isSuccessful) {
                            println("Error al actualizar el valor: ${tarea.exception?.message}")
                        }
                    }
            }
        }.addOnFailureListener { exception ->
            println("Error al obtener el valor actual: ${exception.message}")
        }
    }

    private fun obtenerMontoTotalPresupuesto(presupuestoId: String, callback: (Float?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        if (user == null) {
            println("Usuario no autenticado")
            callback(null)
            return
        }
        val gastoPresupuestoRef = FirebaseDatabase.getInstance()
            .getReference("Presupuesto/$user/$presupuestoId/monto_total")
        gastoPresupuestoRef.get().addOnSuccessListener { data ->
            val montoTotal = data.getValue(Float::class.java) ?: 0f
            callback(montoTotal)
        }.addOnFailureListener { exception ->
            println("Error al obtener el valor actual: ${exception.message}")
            callback(null)
        }
    }


    private fun obtenerDiaSemana(): String {
        // Obtener la instancia del calendario actual
        val timeZone = TimeZone.getTimeZone("America/Lima")
        val calendar = Calendar.getInstance(timeZone).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Obtener el día de la semana (1=domingo, 2=lunes, ..., 7=sábado)
        val diaSemana = calendar.get(Calendar.DAY_OF_WEEK)
        val diasSemana =
            arrayOf("domingo", "lunes", "martes", "miercoles", "jueves", "viernes", "sabado")

        return diasSemana[diaSemana - 1]
    }

    override fun onCategoriaClick(position: Int) {
        dismiss()
    }

    override fun onPresupuestoClick(position: Int) {
        dismiss()
    }

}