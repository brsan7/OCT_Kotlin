package com.brsan7.oct

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brsan7.oct.adapter.EventosAdapter
import com.brsan7.oct.dialogs.EventoDetailDialog
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : DrawerMenuActivity(), EventoDetailDialog.Atualizar {

    private lateinit var mainActivityViewModel: MainViewModel
    private lateinit var adapter : EventosAdapter
    private lateinit var pbMain: ProgressBar
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
        setupMainViewModel()
    }

    private fun setupComponentes() {

        pbMain = findViewById(R.id.pbMain)
        tvMainLocal = findViewById(R.id.tvMainLocal)
        tvMainHoje = findViewById(R.id.tvMainHoje)
        tvMainNascente = findViewById(R.id.tvMainNascente)
        tvMainPoente = findViewById(R.id.tvMainPoente)
        rcvMain = findViewById(R.id.rcvMain)
        fabMain = findViewById(R.id.fabMain)
        fabMain.setOnClickListener {
            val intent = Intent(this, RegistroEventoActivity::class.java)
            intent.putExtra("titulo","Novo Registro")
            startActivity(intent)
        }
    }

    private fun setupRecyclerView(){
        rcvMain.layoutManager = LinearLayoutManager(this)
    }

    private fun setupMainViewModel() {
        mainActivityViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainActivityViewModel.vmRcvMain.observe(this, { lista->
            atualizarEventosDoDia(lista)
        })
        carregamentoDados(true)
        mainActivityViewModel.buscarEventosDoDia(false)
    }

    private fun onClickItemRecyclerView(id: Int){
        val fragment = EventoDetailDialog.newInstance(id)
        fragment.show(supportFragmentManager, "dialog")
    }

    private fun atualizarEventosDoDia(lista: List<EventoVO>){
        adapter = EventosAdapter(this,lista) {onClickItemRecyclerView(it)}
        rcvMain.adapter = adapter
        carregamentoDados(false)
    }

    override fun onDeleteEvento() {
        carregamentoDados(true)
        mainActivityViewModel.buscarEventosDoDia(true)
    }

    private fun carregamentoDados(isLoading: Boolean){
        pbMain.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}