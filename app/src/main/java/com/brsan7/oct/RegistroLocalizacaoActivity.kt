package com.brsan7.oct

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegistroLocalizacaoActivity : DrawerMenuActivity() {

    private lateinit var etRegLocActLocal: EditText
    private lateinit var etRegLocActLatitude: EditText
    private lateinit var etRegLocActLongitude: EditText
    private lateinit var etRegLocActDescricao: EditText
    private lateinit var btnRegLocActMapa: Button
    private lateinit var btnRegLocActRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_localizacao)

        setupDrawerMenu("Novo Local")
        setupComponentes()
        setupListeners()
    }

    private fun setupComponentes() {
        etRegLocActLocal = findViewById(R.id.etRegLocActLocal)
        etRegLocActLatitude = findViewById(R.id.etRegLocActLatitude)
        etRegLocActLongitude = findViewById(R.id.etRegLocActLongitude)
        etRegLocActDescricao = findViewById(R.id.etRegLocActDescricao)
        btnRegLocActMapa = findViewById(R.id.btnRegLocActMapa)
        btnRegLocActRegistrar = findViewById(R.id.btnRegLocActRegistrar)
    }
    private fun setupListeners() {
        btnRegLocActMapa.setOnClickListener {
            Toast.makeText(this,"Buscar no Mapa:\nNão Implementado",Toast.LENGTH_SHORT).show()
        }
        btnRegLocActRegistrar.setOnClickListener {
            Toast.makeText(this,"Registrar:\nNão Implementado",Toast.LENGTH_SHORT).show()
        }
    }
}