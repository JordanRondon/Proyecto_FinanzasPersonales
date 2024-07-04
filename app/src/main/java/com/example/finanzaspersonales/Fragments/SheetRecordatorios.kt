import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Recordatorio
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AgregarRecordatorioBottomSheet(private val onRecordatorioAgregado: (Recordatorio) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var etDescripcion: EditText
    private lateinit var tvFechaSeleccionada: TextView
    private lateinit var ivCalendario: ImageView
    private lateinit var btnGuardar: Button

    private var fechaSeleccionada: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sheet_recordatorios, container, false)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        tvFechaSeleccionada = view.findViewById(R.id.tvFechaSeleccionada)
        ivCalendario = view.findViewById(R.id.ivCalendario)
        btnGuardar = view.findViewById(R.id.btnGuardarRecordatorio)

        ivCalendario.setOnClickListener {
            mostrarDatePickerDialog()
        }

        btnGuardar.setOnClickListener {
            guardarRecordatorio()
        }

        return view
    }

    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val fecha = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            fechaSeleccionada = fecha
            tvFechaSeleccionada.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun guardarRecordatorio() {
        val descripcion = etDescripcion.text.toString()
        if (descripcion.isNotEmpty() && fechaSeleccionada != null) {
            val recordatorio = Recordatorio(fechaSeleccionada!!, descripcion, true)
            onRecordatorioAgregado(recordatorio)
            dismiss()
        } else {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}
