package com.brsan7.oct

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brsan7.oct.adapter.LocaisAdapter
import com.brsan7.oct.dialogs.LocalEditDialog
import com.brsan7.oct.model.LocalVO
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LocalizacaoActivity : DrawerMenuActivity() {

    lateinit var adapter : LocaisAdapter
    private lateinit var tvLocActLocal: TextView
    private lateinit var tvLocActHoje: TextView
    private lateinit var tvLocActNascente: TextView
    private lateinit var tvLocActPoente: TextView
    private lateinit var rcvLocAct: RecyclerView
    private lateinit var fabLocAct: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localizacao)

        setupDrawerMenu("Meus Locais")
        setupComponentes()
        setupRecyclerView()
    }

    private fun setupComponentes() {

        tvLocActLocal = findViewById(R.id.tvLocActLocal)
        tvLocActHoje = findViewById(R.id.tvLocActHoje)
        tvLocActNascente = findViewById(R.id.tvLocActNascente)
        tvLocActPoente = findViewById(R.id.tvLocActPoente)
        rcvLocAct = findViewById(R.id.rcvLocAct)
        fabLocAct = findViewById(R.id.fabLocAct)
        fabLocAct.setOnClickListener {
            val intent = Intent(this, RegistroLocalizacaoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView(){
        rcvLocAct.layoutManager = LinearLayoutManager(this)
        val lista = auxTesteRecyclerView()
        adapter = LocaisAdapter(this,lista ,object : LocaisClickedListener{
            override fun localClickedItem(index: Int) {onClickItemRecyclerView(index)}
            override fun localClickedItemButton(index: Int) {onClickItemButtonRecyclerView(index)}
        })
        rcvLocAct.adapter = adapter
    }
    private fun onClickItemRecyclerView(index: Int){
        val fragment = LocalEditDialog.newInstance(index)
        fragment.show(supportFragmentManager, "dialog")
    }
    private fun onClickItemButtonRecyclerView(index: Int){
        val lista = auxTesteRecyclerView()
        val evento = lista[index]
        tvLocActLocal.text = evento.titulo
        tvLocActHoje.text = evento.titulo
        tvLocActNascente.text = evento.latitude
        tvLocActPoente.text = evento.longitude
    }
    private fun auxTesteRecyclerView(): List<LocalVO>{
        val lista: MutableList<LocalVO> = mutableListOf()
        for (index in 0..7) {
            val itemLocal = LocalVO(
                id = index,
                titulo = "Local $index",
                latitude = "-23.123",
                longitude = "-45.123",
                descricao = "Teste\ntestando\n123\nTestando"
            )
            lista.add(itemLocal)
        }
        return lista
    }

}