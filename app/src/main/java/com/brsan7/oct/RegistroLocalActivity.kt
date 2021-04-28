package com.brsan7.oct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.LocalVO

class RegistroLocalActivity : DrawerMenuActivity() {

    private lateinit var etRegLocActLocal: EditText
    private lateinit var etRegLocActLatitude: EditText
    private lateinit var etRegLocActLongitude: EditText
    private lateinit var etRegLocActFusoHorario: EditText
    private lateinit var etRegLocActDescricao: EditText
    private lateinit var btnRegLocActMapa: Button
    private lateinit var btnRegLocActRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_local)

        setupDrawerMenu(getString(R.string.titulo_RegLocal))
        setupComponentes()
        setupListeners()
    }

    private fun setupComponentes() {
        etRegLocActLocal = findViewById(R.id.etRegLocActLocal)
        etRegLocActLatitude = findViewById(R.id.etRegLocActLatitude)
        etRegLocActLongitude = findViewById(R.id.etRegLocActLongitude)
        etRegLocActFusoHorario = findViewById(R.id.etRegLocActFusoHorario)
        etRegLocActDescricao = findViewById(R.id.etRegLocActDescricao)
        btnRegLocActMapa = findViewById(R.id.btnRegLocActMapa)
        btnRegLocActRegistrar = findViewById(R.id.btnRegLocActRegistrar)
    }
    private fun setupListeners() {
        btnRegLocActMapa.setOnClickListener {
            Toast.makeText(this,"Buscar no Mapa:\nNão Implementado",Toast.LENGTH_SHORT).show()
        }
        btnRegLocActRegistrar.setOnClickListener {
            Thread{
                OctApplication.instance.helperDB?.registrarLocal(getComposeRegistro())
            }.start()
            val intent = Intent(this, LocaisActivity::class.java)
            startActivity(intent)
        }
    }
    private fun getComposeRegistro(): LocalVO {
        return LocalVO(
                id = +1,
                titulo = "${etRegLocActLocal.text}",
                latitude = "${etRegLocActLatitude.text}",
                longitude = "${etRegLocActLongitude.text}",
                fusoHorario = "${etRegLocActFusoHorario.text}",
                descricao = "${etRegLocActDescricao.text}"
        )
    }
}