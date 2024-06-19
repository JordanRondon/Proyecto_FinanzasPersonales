package com.example.finanzaspersonales.adaptadores

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.databinding.ItemNotificacionesBinding
import com.example.finanzaspersonales.entidades.Notificacion


class NotificationAdapter(private val notificationList: List<Notificacion>, private val onClickListener: (Notificacion) -> Unit):
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_notificaciones,parent,false))
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        val item = notificationList[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = notificationList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemNotificacionesBinding.bind(view)//binding the received view

        fun render(notificationModel: Notificacion, onClickListener: (Notificacion)->Unit){
            binding.tvAsunto.text = notificationModel.asunto
            val spannableString = BoldBudgetText(notificationModel.descripcion)
            binding.tvDescripcion.text = spannableString
            binding.tvFecha.text = notificationModel.fecha
            binding.ivIcono.setImageResource(R.drawable.moneda)
            itemView.setOnClickListener{ onClickListener(notificationModel)}
        }
    }

    fun BoldBudgetText(text : String):SpannableString{
        val input = text
        val words = input.split("Se notifica que "," ha excedido")[1].length
        val spannableString = SpannableString(text)
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldSpan, 16, words+16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

}