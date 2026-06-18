package com.example.activaescom.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activaescom.R
import com.example.activaescom.model.HistorialUniversalItem
import java.io.File

class HistorialAdapter(
    private val lista: MutableList<HistorialUniversalItem>,
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTipoActividad: TextView = itemView.findViewById(R.id.tvTipoActividad)
        val tvNombreEntreno: TextView = itemView.findViewById(R.id.tvNombreEntreno)
        val tvResumen: TextView = itemView.findViewById(R.id.tvResumen)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvDetalles: TextView = itemView.findViewById(R.id.tvDetalles)
        val layoutExpandible: LinearLayout = itemView.findViewById(R.id.layoutExpandible)
        val layoutHeader: LinearLayout = itemView.findViewById(R.id.layoutHeader)
        val imgExpandir: ImageView = itemView.findViewById(R.id.imgExpandir)
        val imgActividad: ImageView = itemView.findViewById(R.id.imgActividad)
        val imgMapaCaptura: ImageView = itemView.findViewById(R.id.imgMapaCaptura)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_historial_expandible,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.tvTipoActividad.text = item.tipoActividad
        holder.tvNombreEntreno.text = item.nombreEntrenamiento
        holder.tvResumen.text = item.resumen
        holder.tvFecha.text = item.fecha
        holder.tvDetalles.text = item.detalles

        // Lógica de inflado del mapa guardado en disco
        if (!item.rutaMapa.isNullOrEmpty()) {
            val imgFile = File(item.rutaMapa)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.imgMapaCaptura.setImageBitmap(bitmap)
                holder.imgMapaCaptura.visibility = View.VISIBLE
            } else {
                holder.imgMapaCaptura.visibility = View.GONE
            }
        } else {
            holder.imgMapaCaptura.visibility = View.GONE
        }

        holder.layoutHeader.setOnClickListener {
            if (holder.layoutExpandible.visibility == View.GONE) {
                holder.layoutExpandible.visibility = View.VISIBLE
                holder.imgExpandir.rotation = 180f
            } else {
                holder.layoutExpandible.visibility = View.GONE
                holder.imgExpandir.rotation = 0f
            }
        }

        val icono = when (item.tipoActividad.lowercase()) {
            "carrera" -> R.drawable.ic_carrera
            "caminata" -> R.drawable.ic_caminata
            "bicicleta" -> R.drawable.ic_bicicleta
            "fuerza" -> R.drawable.ic_fuerza
            "natación" -> R.drawable.ic_natacion
            "yoga" -> R.drawable.ic_yoga
            else -> R.drawable.workout
        }
        holder.imgActividad.setImageResource(icono)
    }

    override fun getItemCount(): Int = lista.size
}