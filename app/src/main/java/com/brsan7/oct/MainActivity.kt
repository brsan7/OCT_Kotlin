package com.brsan7.oct

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brsan7.oct.adapter.EventosAdapter
import com.brsan7.oct.dialogs.EventoDetailDialog
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.service.ScheduleWorkNotificacao
import com.brsan7.oct.utils.SharedPreferencesUtils
import com.brsan7.oct.utils.SolarUtils
import com.brsan7.oct.utils.TempoUtils
import com.brsan7.oct.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : DrawerMenuActivity(), EventoDetailDialog.Atualizar {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter : EventosAdapter
    private lateinit var pbMain: ProgressBar
    private lateinit var tvMainLocal: TextView
    private lateinit var tvMainNascente: TextView
    private lateinit var tvMainPoente: TextView
    private lateinit var rcvMain: RecyclerView
    private lateinit var fabMain: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDrawerMenu(getString(R.string.menu1_1_Main))
        setupComponentes()
        setupLocalDefault()
        setupNotification()
        setupRecyclerView()
        setupMainViewModel()
    }

    private fun setupComponentes() {

        pbMain = findViewById(R.id.pbMain)
        tvMainLocal = findViewById(R.id.tvMainLocal)
        tvMainNascente = findViewById(R.id.tvMainNascente)
        tvMainPoente = findViewById(R.id.tvMainPoente)
        rcvMain = findViewById(R.id.rcvMain)
        fabMain = findViewById(R.id.fabMain)
        fabMain.setOnClickListener {
            val intent = Intent(this, RegistroEventoActivity::class.java)
            intent.putExtra("titulo",getString(R.string.menu1_2_RegEvt))
            startActivity(intent)
        }
    }

    private fun setupLocalDefault(){
        val defLocal = SharedPreferencesUtils().getSharedLocalDefault()
        tvMainLocal.text = defLocal.titulo
        if (defLocal.latitude.toDoubleOrNull() != null) {
            val fotoPeriodo = SolarUtils().fotoPeriodo(
                    latitude = defLocal.latitude.toDouble(),
                    longitude = defLocal.longitude.toDouble(),
                    fusoHorario = defLocal.fusoHorario.toInt(),
                    diaJuliano = Calendar.getInstance()[Calendar.DAY_OF_YEAR],
                    ano = Calendar.getInstance()[Calendar.YEAR])
            tvMainNascente.text = fotoPeriodo[1]
            tvMainPoente.text = fotoPeriodo[2]
        }
        carregamentoDados(false)
    }

    private fun setupNotification(){
        ScheduleWorkNotificacao().setupNotificacaoDiaria()
    }

    private fun setupRecyclerView(){
        rcvMain.layoutManager = LinearLayoutManager(this)
    }

    private fun setupMainViewModel() {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.vmRcvMain.observe(this, { lista->
            atualizarEventosDoDia(lista)
        })
        carregamentoDados(true)
        mainViewModel.verificarAtualizarTodosEventos()
    }

    private fun onClickItemRecyclerView(id: Int){
        val fragment = EventoDetailDialog.newInstance(id)
        fragment.show(supportFragmentManager, "dialog")
    }

    private fun atualizarEventosDoDia(listaFiltrada: List<EventoVO>){
        if (listaFiltrada.isEmpty()){
            Toast.makeText(this,getString(R.string.aviso_SemEventosHoje), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CalendarioEventoActivity::class.java)
            startActivity(intent)
        }
        else {
            if (TempoUtils().proxEvento(listaFiltrada).tipo == getString(R.string.txt_spnRegEvtActTipoCompromisso)) {
                ScheduleWorkNotificacao().startForegroudService()
            }
            adapter = EventosAdapter(this, listaFiltrada) { onClickItemRecyclerView(it) }
            rcvMain.adapter = adapter
            carregamentoDados(false)
        }
    }

    override fun onDeleteEvento() {
        carregamentoDados(true)
        mainViewModel.buscarEventosDoDia(true)
    }

    private fun carregamentoDados(isLoading: Boolean){
        pbMain.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}