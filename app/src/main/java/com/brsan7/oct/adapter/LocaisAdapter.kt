package com.brsan7.oct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.brsan7.oct.R
import com.brsan7.oct.LocaisClickedListener
import com.brsan7.oct.model.LocalVO
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LocaisAdapter(
        private val context: Context,
        private val lista: List<LocalVO>,
        private val listener: LocaisClickedListener
) : RecyclerView.Adapter<LocaisViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocaisViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_locais,parent,false)
        return LocaisViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: LocaisViewHolder, position: Int) {
        val itemLocal = lista[position]
        with(holder){
            tvItemLocaisTitulo.text = itemLocal.titulo
            tvItemLocaisLatitude.text = itemLocal.latitude
            tvItemLocaisLongitude.text = itemLocal.longitude
            cvItemLocais.setOnClickListener { listener.localClickedItem(itemLocal.id) }
            fabItemLocaisSetDef.setOnClickListener { listener.localClickedItemButton(itemLocal.id) }
        }
    }
}

class LocaisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    var tvItemLocaisTitulo: TextView = itemView.findViewById(R.id.tvItemLocaisTitulo)
    var tvItemLocaisLatitude: TextView = itemView.findViewById(R.id.tvItemLocaisLatitude)
    var tvItemLocaisLongitude: TextView = itemView.findViewById(R.id.tvItemLocaisLongitude)
    var cvItemLocais: CardView = itemView.findViewById(R.id.cvItemLocais)
    var fabItemLocaisSetDef: FloatingActionButton = itemView.findViewById(R.id.fabItemLocaisSetDef)
}