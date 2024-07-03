
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finanzaspersonales.entidades.Recordatorio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecordatorioViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val _recordatorios = MutableLiveData<List<Recordatorio>>()
    val recordatorios: LiveData<List<Recordatorio>> get() = _recordatorios

    private val userName = FirebaseAuth.getInstance().currentUser!!.uid

    init {
        obtenerRecordatorios()
    }

    private fun obtenerRecordatorios() {
        val refUsuario = database.child("NotificacionPago").child(userName)
        refUsuario.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaRecordatorio = mutableListOf<Recordatorio>()
                for (recordatorioSnapshot in snapshot.children) {
                    if (recordatorioSnapshot.key != "contador") {
                        val recordatorio = recordatorioSnapshot.getValue(Recordatorio::class.java)
                        if (recordatorio != null) {
                            listaRecordatorio.add(recordatorio)
                        }
                    }
                }
                _recordatorios.value = listaRecordatorio
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Recordatorio", "Error al leer los recordatorios", error.toException())
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
                    val nuevaLista = _recordatorios.value?.toMutableList() ?: mutableListOf()
                    nuevaLista.add(recordatorio)
                    _recordatorios.value = nuevaLista
                } else {
                    Log.e("Recordatorio", "Error al agregar recordatorio: ${task.exception}")
                }
            }
        }.addOnFailureListener {
            Log.e("Recordatorio", "Error al obtener el contador", it)
        }
    }
}