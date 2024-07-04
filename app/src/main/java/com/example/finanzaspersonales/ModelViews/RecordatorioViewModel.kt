import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.finanzaspersonales.Fragments.RecordatorioNotification
import com.example.finanzaspersonales.entidades.Recordatorio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import java.util.concurrent.TimeUnit

class RecordatorioViewModel(application: Application) : AndroidViewModel(application) {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val _recordatorios = MutableLiveData<List<Pair<String, Recordatorio>>>()
    val recordatorios: LiveData<List<Pair<String, Recordatorio>>> get() = _recordatorios

    private val userName = FirebaseAuth.getInstance().currentUser!!.uid

    init {
        obtenerRecordatorios(null)
    }

    private fun obtenerRecordatorios(callback: ((List<Pair<String, Recordatorio>>) -> Unit)?) {
        val refUsuario = database.child("NotificacionPago").child(userName)
        refUsuario.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaRecordatorio = mutableListOf<Pair<String, Recordatorio>>()
                for (recordatorioSnapshot in snapshot.children) {
                    if (recordatorioSnapshot.key != "contador") {
                        val recordatorio = recordatorioSnapshot.getValue(Recordatorio::class.java)
                        if (recordatorio != null) {
                            listaRecordatorio.add(Pair(recordatorioSnapshot.key!!, recordatorio))
                        }
                    }
                }
                _recordatorios.value = listaRecordatorio.sortedByDescending { it.second.fecha }
                Log.d("RecordatorioViewModel", "Recordatorios obtenidos: ${_recordatorios.value?.size}")
                _recordatorios.value?.forEach {
                    Log.d("RecordatorioViewModel", "Recordatorio: ${it.second.descripcion}, Fecha: ${it.second.fecha}")
                }
                callback?.let { it(listaRecordatorio) }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Recordatorio", "Error al leer los recordatorios", error.toException())
                callback?.let { it(emptyList()) }
            }
        })
    }

    fun agregarRecordatorio(recordatorio: Recordatorio) {
        val refUsuario = database.child("NotificacionPago").child(userName)
        val refContador = refUsuario.child("contador").child("ultimo_NotificacionPago")

        refContador.get().addOnSuccessListener { dataSnapshot ->
            val contadorActual = dataSnapshot.getValue(Int::class.java) ?: 0
            val nuevoContador = contadorActual + 1

            refContador.setValue(nuevoContador)

            val refNuevoRecordatorio = refUsuario.child(nuevoContador.toString())
            refNuevoRecordatorio.setValue(recordatorio).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // No need to update LiveData manually; it will be updated through the ValueEventListener in obtenerRecordatorios().
                } else {
                    Log.e("Recordatorio", "Error al agregar recordatorio: ${task.exception}")
                }
            }
        }.addOnFailureListener {
            Log.e("Recordatorio", "Error al obtener el contador", it)
        }
    }

    fun actualizarRecordatorio(recordatorio: Recordatorio, recordatorioId: String) {
        val refUsuario = database.child("NotificacionPago").child(userName).child(recordatorioId)
        Log.d("RecordatorioViewModel", "Actualizando recordatorio con ID: $recordatorioId")
        Log.d("RecordatorioViewModel", "Nuevos datos del recordatorio: $recordatorio")
        Log.d("RecordatorioViewModel", "Nueva descripción: ${recordatorio.descripcion}")
        refUsuario.setValue(recordatorio).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("RecordatorioViewModel", "Recordatorio actualizado: ${recordatorio.descripcion}")
                // No need to update LiveData manually; it will be updated through the ValueEventListener in obtenerRecordatorios().
            } else {
                Log.d("Recordatorio", "Error al actualizar recordatorio: ${task.exception}")
            }
        }
    }

    fun eliminarRecordatorio(recordatorioId: String) {
        val refUsuario = database.child("NotificacionPago").child(userName).child(recordatorioId)
        refUsuario.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // No need to update LiveData manually; it will be updated through the ValueEventListener in obtenerRecordatorios().
            } else {
                Log.e("Recordatorio", "Error al eliminar recordatorio: ${task.exception}")
            }
        }
    }

    fun obtenerRecordatoriosVencidos(callback: (List<Recordatorio>) -> Unit) {
        obtenerRecordatorios { recordatorios ->
            val fechaActual = Calendar.getInstance().time
            Log.d("RecordatorioViewModel", "Fecha actual: $fechaActual")
            Log.d("RecordatorioViewModel", "Iniciando verificación de recordatorios vencidos")

            val vencidos = recordatorios.filter {
                val recordatorioFecha = it.second.fecha
                Log.d("RecordatorioViewModel", "Verificando recordatorio: ${it.second.descripcion} con fecha ${recordatorioFecha} (timestamp: ${recordatorioFecha.time})")
                recordatorioFecha.time < fechaActual.time
            }.map {
                Log.d("RecordatorioViewModel", "Recordatorio vencido: ${it.second.descripcion} con fecha ${it.second.fecha} (timestamp: ${it.second.fecha.time})")
                it.second
            }

            Log.d("RecordatorioViewModel", "Total de recordatorios vencidos: ${vencidos.size}")
            callback(vencidos)
        }
    }
    fun obtenerRecordatoriosProximosAVencer(callback: (List<Recordatorio>) -> Unit) {
        obtenerRecordatorios { recordatorios ->
            val fechaActual = Calendar.getInstance()
            val proximos = recordatorios.filter {
                val recordatorioFecha = Calendar.getInstance().apply { time = it.second.fecha }
                val diff = recordatorioFecha.timeInMillis - fechaActual.timeInMillis
                val daysDiff = TimeUnit.MILLISECONDS.toDays(diff)
                daysDiff == 1L || daysDiff == 2L
            }.map { it.second }

            callback(proximos)
        }
    }
}