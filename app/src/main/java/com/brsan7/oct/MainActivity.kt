package com.brsan7.oct

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brsan7.oct.adapter.EventosAdapter
import com.brsan7.oct.dialogs.RegistroAgendaDetailDialog
import com.brsan7.oct.viewmodels.CalEvtActivityViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prolificinteractive.materialcalendarview.CalendarDay


class MainActivity : DrawerMenuActivity() {

    private lateinit var tvMainLocal: TextView
    private lateinit var tvMainHoje: TextView
    private lateinit var tvMainNascente: TextView
    private lateinit var tvMainPoente: TextView
    private lateinit var rcvMain: RecyclerView
    private lateinit var fabMain: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDrawerMenu("Eventos do Dia")
        setupComponentes()
        setupRecyclerView()
    }
    private fun setupComponentes() {

        tvMainLocal = findViewById(R.id.tvMainLocal)
        tvMainHoje = findViewById(R.id.tvMainHoje)
        tvMainNascente = findViewById(R.id.tvMainNascente)
        tvMainPoente = findViewById(R.id.tvMainPoente)
        rcvMain = findViewById(R.id.rcvMain)
        fabMain = findViewById(R.id.fabMain)
        fabMain.setOnClickListener {
            val intent = Intent(this, RegistroEventoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView(){
        rcvMain.layoutManager = LinearLayoutManager(this)

        val lista = CalEvtActivityViewModel().auxTesteSpinner(CalendarDay.today(), false)
        val adapter = EventosAdapter(this,lista) {onClickItemRecyclerView(it)}
        rcvMain.adapter = adapter
    }

    private fun onClickItemRecyclerView(id: Int){
        val fragment = RegistroAgendaDetailDialog.newInstance(id)
        fragment.show(supportFragmentManager, "dialog")
    }
}