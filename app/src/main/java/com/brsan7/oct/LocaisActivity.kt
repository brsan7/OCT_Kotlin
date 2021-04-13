package com.brsan7.oct

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brsan7.oct.adapter.LocaisAdapter
import com.brsan7.oct.dialogs.LocalEditDialog
import com.brsan7.oct.model.LocalVO
import com.brsan7.oct.viewmodels.LocaisViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocaisActivity : DrawerMenuActivity(), LocalEditDialog.Atualizar {

    private lateinit var locaisViewModel: LocaisViewModel
    private lateinit var adapter : LocaisAdapter
    private lateinit var pbLocais: ProgressBar
    private lateinit var tvLocActLocal: TextView
    private lateinit var tvLocActNascente: TextView
    private lateinit var tvLocActPoente: TextView
    private lateinit var rcvLocAct: RecyclerView
    private lateinit var fabLocAct: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locais)

        setupDrawerMenu("Meus Locais")
        setupComponentes()
        setupRecyclerView()
        setupLocaisViewModel()
        atualizarLocalDefault(getShareLocalDefault())
    }

    private fun setupComponentes() {

        pbLocais = findViewById(R.id.pbLocais)
        tvLocActLocal = findViewById(R.id.tvLocActLocal)
        tvLocActNascente = findViewById(R.id.tvLocActNascente)
        tvLocActPoente = findViewById(R.id.tvLocActPoente)
        rcvLocAct = findViewById(R.id.rcvLocAct)
        fabLocAct = findViewById(R.id.fabLocAct)
        fabLocAct.setOnClickListener {
            val intent = Intent(this, RegistroLocalActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView(){
        rcvLocAct.layoutManager = LinearLayoutManager(this)
    }

    private fun setupLocaisViewModel() {
        locaisViewModel = ViewModelProvider(this).get(LocaisViewModel::class.java)
        locaisViewModel.vmDefLocal.observe(this, { lista->
            setShareLocalDefault(lista)
        })
        locaisViewModel.vmRcvLocais.observe(this, { lista->
            atualizarLocais(lista)
        })
        carregamentoDados(true)
        locaisViewModel.buscarLocais("",false,false)
    }

    private fun onClickItemRecyclerView(index: Int){
        val fragment = LocalEditDialog.newInstance(index)
        fragment.show(supportFragmentManager, "dialog")
    }

    private fun onClickItemButtonRecyclerView(index: Int){
        carregamentoDados(true)
        locaisViewModel.buscarLocais("$index",true,false)
    }

    private fun atualizarLocalDefault(defLocal: LocalVO){
        tvLocActLocal.text = defLocal.titulo
        tvLocActNascente.text = defLocal.latitude
        tvLocActPoente.text = defLocal.longitude
        carregamentoDados(false)
    }

    private fun atualizarLocais(lista: List<LocalVO>){
        adapter = LocaisAdapter(this,lista ,object : LocaisClickedListener{
            override fun localClickedItem(index: Int) {onClickItemRecyclerView(index)}
            override fun localClickedItemButton(index: Int) {onClickItemButtonRecyclerView(index)}
        })
        rcvLocAct.adapter = adapter
        carregamentoDados(false)
    }

    override fun onModifyLocal() {
        carregamentoDados(true)
        locaisViewModel.buscarLocais("",false,true)
    }
    
    private fun carregamentoDados(isLoading: Boolean){
        pbLocais.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getInstanceSharedPreferences() : SharedPreferences {
        return getSharedPreferences("com.brsan7.oct.LOCAL_DEFAULT", Context.MODE_PRIVATE)
    }

    private fun setShareLocalDefault(meuLocal: LocalVO){
        getInstanceSharedPreferences().edit{
            putString("localDef", Gson().toJson(meuLocal))
            commit()
        }
        atualizarLocalDefault(meuLocal)
    }

    private fun getShareLocalDefault() : LocalVO{
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