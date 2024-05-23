package com.example.finanzaspersonales
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
class CategoriaAdapter(context: Context, private val resource: Int, private val items: List<Categoria>) :
    ArrayAdapter<Categoria>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(convertView, parent, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(convertView, parent, position)
    }

    private fun createViewFromResource(convertView: View?, parent: ViewGroup, position: Int): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val categoria = items[position]
        val imageView = view.findViewById<ImageView>(R.id.imageView2)
        val textView = view.findViewById<TextView>(R.id.textView3)

        imageView.setImageResource(categoria.icono)
        textView.text = categoria.nombre

        return view
    }
}