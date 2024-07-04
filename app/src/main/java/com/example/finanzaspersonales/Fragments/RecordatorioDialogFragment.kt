
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Recordatorio
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RecordatorioDialogFragment(
    private val recordatorio: Recordatorio,
    private val recordatorioId: String,
    private val onRecordatorioActualizado: (Recordatorio, String) -> Unit
) : DialogFragment() {

    private lateinit var etDescripcion: EditText
    private lateinit var tvFechaSeleccionadaNew: TextView
    private lateinit var ivCalendario: ImageView
    private lateinit var tvCancelar: TextView
    private lateinit var tvGuardar: TextView

    private var fechaSeleccionada: Date = recordatorio.fecha

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_recordatorio, container, false)
        etDescripcion = view.findViewById(R.id.etDescripcionNew)
        tvFechaSeleccionadaNew = view.findViewById(R.id.tvFechaSeleccionadaNew)
        ivCalendario = view.findViewById(R.id.ivCalendario)
        tvCancelar = view.findViewById(R.id.tvCancelar)
        tvGuardar = view.findViewById(R.id.tvGuardar)

        etDescripcion.setText(recordatorio.descripcion)
        tvFechaSeleccionadaNew.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(recordatorio.fecha)

        ivCalendario.setOnClickListener {
            mostrarDatePickerDialog()
        }

        tvCancelar.setOnClickListener {
            dismiss()
        }

        tvGuardar.setOnClickListener {
            val descripcionActualizada = etDescripcion.text.toString()
            Log.d("RecordatorioDialogFragment", "Texto del EditText al guardar: $descripcionActualizada")
            guardarRecordatorio(descripcionActualizada)
        }

        return view
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = activity?.layoutInflater?.inflate(R.layout.dialog_recordatorio, null)

        if (dialogView != null) {
            etDescripcion = dialogView.findViewById(R.id.etDescripcionNew)
            tvFechaSeleccionadaNew = dialogView.findViewById(R.id.tvFechaSeleccionadaNew)
            ivCalendario = dialogView.findViewById(R.id.ivCalendario)
            tvCancelar = dialogView.findViewById(R.id.tvCancelar)
            tvGuardar = dialogView.findViewById(R.id.tvGuardar)

            etDescripcion.setText(recordatorio.descripcion)
            tvFechaSeleccionadaNew.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(recordatorio.fecha)

            ivCalendario.setOnClickListener {
                mostrarDatePickerDialog()
            }

            tvCancelar.setOnClickListener {
                dismiss()
            }

            tvGuardar.setOnClickListener {
                val descripcionActualizada = etDescripcion.text.toString().trim()
                guardarRecordatorio(descripcionActualizada)
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()
    }

    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance().apply { time = fechaSeleccionada }
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val fecha = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            fechaSeleccionada = fecha
            tvFechaSeleccionadaNew.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun guardarRecordatorio(descripcion:String) {

        Log.d("RecordatorioDialogFragment", "Texto del EditText: $descripcion")
        if (descripcion.isNotEmpty()) {
            val recordatorioActualizado = Recordatorio(fechaSeleccionada, descripcion, recordatorio.estado)
            Log.d("RecordatorioDialogFragment", "Guardando recordatorio con nueva descripción: $descripcion")
            onRecordatorioActualizado(recordatorioActualizado, recordatorioId)
            dismiss()
        } else {
            Toast.makeText(requireContext(), "Por favor, ingrese una descripción", Toast.LENGTH_SHORT).show()
        }
    }
}