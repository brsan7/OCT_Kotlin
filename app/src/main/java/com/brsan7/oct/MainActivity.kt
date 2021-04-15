package com.brsan7.oct

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.brsan7.oct.model.LocalVO
import com.brsan7.oct.utils.SolarUtils
import com.brsan7.oct.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

        setupDrawerMenu("Eventos do Dia")
        setupComponentes()
        setupLocalDefault(getShareLocalDefault())
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
            intent.putExtra("titulo","Novo Registro")
            startActivity(intent)
        }
    }

    private fun setupLocalDefault(defLocal: LocalVO){
        tvMainLocal.text = defLocal.titulo
        if (defLocal.latitude.toDoubleOrNull() != null) {
            val fotoPeriodo = SolarUtils().fotoPeriodo(
                    defLocal.latitude.toDouble()+0,
                    defLocal.longitude.toDouble()+0,
                    defLocal.fusoHorario.toInt()+0,
                    Calendar.getInstance()[Calendar.DAY_OF_YEAR]+0,
                    Calendar.getInstance()[Calendar.YEAR]+0)
            tvMainNascente.text = fotoPeriodo[1]
            tvMainPoente.text = fotoPeriodo[2]
        }
        carregamentoDados(false)
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
        mainViewModel.buscarEventosDoDia(false)
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
        mainViewModel.buscarEventosDoDia(true)
    }

    private fun carregamentoDados(isLoading: Boolean){
        pbMain.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getInstanceSharedPreferences() : SharedPreferences {
        return getSharedPreferences("com.brsan7.oct.LOCAL_DEFAULT", Context.MODE_PRIVATE)
    }

    private fun getShareLocalDefault() : LocalVO {
        val defLocal = LocalVO(
            -1,
            "Selecione sua Localização",
            "",
            "",
            ""
        )
        val ultimoItemRegGson = getInstanceSharedPreferences().getString("localDef", Gson().toJson(defLocal))
        val convTipo = object : TypeToken<LocalVO>(){}.type
        return Gson().fromJson(ultimoItemRegGson,convTipo)
    }
}