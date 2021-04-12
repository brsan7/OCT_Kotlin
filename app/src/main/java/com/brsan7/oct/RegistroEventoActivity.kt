package com.brsan7.oct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.dialogs.SelectDataDialog
import com.brsan7.oct.dialogs.SelectHoraDialog
import com.brsan7.oct.model.DataVO
import com.brsan7.oct.model.HoraVO
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.viewmodels.RegEvtActivityViewModel

class RegistroEventoActivity : DrawerMenuActivity(),
                            SelectDataDialog.SelecaoData,
                                SelectHoraDialog.SelecaoHora {

    private lateinit var regEvtActivityViewModel: RegEvtActivityViewModel
    private var ID_EXTRA = -1
    lateinit var etRegEvtActTitulo: EditText
    lateinit var tvRegEvtActData: TextView
    lateinit var tvRegEvtActHora: TextView
    lateinit var spnRegEvtActTipo: Spinner
    lateinit var spnRegEvtActRecorrencia: Spinner
    lateinit var etRegEvtActDescricao: EditText
    lateinit var btnRegEvtActRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_evento)

        ID_EXTRA = intent.getIntExtra("id",-1)
        setupDrawerMenu(intent.getStringExtra("titulo")?:"")
        setupComponentes()
        setupListeners()
        setupRegistroActivityViewModel()
    }

    private fun setupComponentes(){
        etRegEvtActTitulo = findViewById(R.id.etRegEvtActTitulo)
        tvRegEvtActData = findViewById(R.id.tvRegEvtActData)
        tvRegEvtActHora = findViewById(R.id.tvRegEvtActHora)
        spnRegEvtActTipo = findViewById(R.id.spnRegEvtActTipo)
        spnRegEvtActRecorrencia = findViewById(R.id.spnRegEvtActRecorrencia)
        etRegEvtActDescricao = findViewById(R.id.etRegEvtActDescricao)
        btnRegEvtActRegistrar = findViewById(R.id.btnRegEvtActRegistrar)
        if (ID_EXTRA > -1){btnRegEvtActRegistrar.text = getString(R.string.txt_btnRegAgActEditar)}
    }

    private fun setupListeners() {

        tvRegEvtActData.setOnClickListener{
            val fragment = SelectDataDialog.newInstance()
            fragment.show(supportFragmentManager, "dialog")
        }

        tvRegEvtActHora.setOnClickListener{
            val fragment = SelectHoraDialog.newInstance()
            fragment.show(supportFragmentManager, "dialog")
        }
        btnRegEvtActRegistrar.setOnClickListener{

            regEvtActivityViewModel.registrarEvento(getComposeRegistro(true))
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRegistroActivityViewModel(){
        regEvtActivityViewModel = ViewModelProvider(this).get(RegEvtActivityViewModel::class.java)

        regEvtActivityViewModel.vmComposeReg.observe(this, { _registro->
            setComposeRegistro(_registro)
        })
        regEvtActivityViewModel.verificarEdicao(ID_EXTRA)
    }

    override fun onPause() {
        super.onPause()
        regEvtActivityViewModel.getComposicaoEventoAtual(getComposeRegistro(false))
    }

    fun setComposeRegistro(_evento: EventoVO){
        etRegEvtActTitulo.setText(_evento.titulo)
        tvRegEvtActData.text = _evento.data
        tvRegEvtActHora.text = _evento.hora
        spnRegEvtActTipo.setSelection(_evento.tipo.toInt())
        spnRegEvtActRecorrencia.setSelection(_evento.recorrencia.toInt())
        etRegEvtActDescricao.setText(_evento.descricao)
    }

    fun getComposeRegistro(isRegistro: Boolean):EventoVO{
        val tipo: String
        val recorrencia: String
        if (isRegistro){
            tipo = "${spnRegEvtActTipo.selectedItem}"
            recorrencia = "${spnRegEvtActRecorrencia.selectedItem}"
        }
        else{
            tipo = "${spnRegEvtActTipo.selectedItemId}"
            recorrencia = "${spnRegEvtActRecorrencia.selectedItemId}"
        }
        val composicaoRegistro = EventoVO(
                ID_EXTRA,
                "${etRegEvtActTitulo.text}",
                "${tvRegEvtActData.text}",
                "${tvRegEvtActHora.text}",
                tipo,
                recorrencia,
                "${etRegEvtActDescricao.text}"
        )
        return composicaoRegistro
    }

    override fun onDataSelecionada(data: DataVO) {
        val dataSelecionada = "${data.dia}/${data.mes+1}/${data.ano}"
        tvRegEvtActData.text = dataSelecionada
    }

    override fun onHoraSelecionada(hora: HoraVO) {
        val horaSelecionada = "${hora.hora}:${hora.minuto}"
        tvRegEvtActHora.text = horaSelecionada
    }
}