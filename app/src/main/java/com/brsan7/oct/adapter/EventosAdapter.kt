package com.brsan7.oct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.brsan7.oct.R
import com.brsan7.oct.model.EventoVO


class EventosAdapter(
        private val context: Context,
        private val lista: List<EventoVO>,
        private val onClickItemRecyclerView: ((Int) -> Unit)
) : RecyclerView.Adapter<EventosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventosViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_eventos,parent,false)
        return EventosViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: EventosViewHolder, position: Int) {
        val evento = lista[position]
        with(holder){
            tvItemEventosData.text = evento.data
            tvItemEventosHora.text = evento.hora
            tvItemEventosTitulo.text = evento.titulo
            cvItemEventos.setOnClickListener { onClickItemRecyclerView(evento.id) }
        }
    }
}

class EventosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    var tvItemEventosData: TextView = itemView.findViewById(R.id.tvItemEventosData)
    var tvItemEventosHora: TextView = itemView.findViewById(R.id.tvItemEventosHora)
    var tvItemEventosTitulo: TextView = itemView.findViewById(R.id.tvItemEventosTitulo)
    var cvItemEventos: CardView = itemView.findViewById(R.id.cvItemEventos)
}