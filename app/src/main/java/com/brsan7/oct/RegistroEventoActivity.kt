package com.brsan7.oct

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

        setupDrawerMenu("Novo Registro")
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
            //val fragment = RegistroDetailDialog.newInstance("selectHora")
            //fragment.show(supportFragmentManager, "dialog")
        }
        //val spinner = findViewById<Spinner>(R.id.spnRegAgActTipo)
        //selectData.text = spinner.selectedItem.toString()
        //ArrayAdapter.createFromResource(this, R.array.tipos_registro)
    }

    private fun setupRegistroActivityViewModel(){
        regEvtActivityViewModel = ViewModelProvider(this).get(RegEvtActivityViewModel::class.java)
        val teste = EventoVO(
                0,
                etRegEvtActTitulo.text.toString(),
                tvRegEvtActData.text.toString(),
                tvRegEvtActHora.text.toString(),
                spnRegEvtActTipo.id.toString(),
                spnRegEvtActRecorrencia.id.toString(),
                etRegEvtActDescricao.text.toString()
        )
        regEvtActivityViewModel.getSelecaoData(teste)
        regEvtActivityViewModel.vmComposeReg.value?.id = 0
        regEvtActivityViewModel.vmComposeReg.observe(this, { _registro->
            setComposeRegistro(_registro)
        })
    }

    override fun onPause() {
        super.onPause()
        regEvtActivityViewModel.vmComposeReg.value?.titulo = etRegEvtActTitulo.text.toString()
        regEvtActivityViewModel.vmComposeReg.value?.data = tvRegEvtActData.text.toString()
        regEvtActivityViewModel.vmComposeReg.value?.hora = tvRegEvtActHora.text.toString()
        regEvtActivityViewModel.vmComposeReg.value?.tipo = spnRegEvtActTipo.id.toString()
        regEvtActivityViewModel.vmComposeReg.value?.recorrencia = spnRegEvtActRecorrencia.id.toString()
        regEvtActivityViewModel.vmComposeReg.value?.descricao = etRegEvtActDescricao.text.toString()
    }

    fun setComposeRegistro(_evento: EventoVO){
        etRegEvtActTitulo.setText(_evento.titulo)
        tvRegEvtActData.text = _evento.data
        tvRegEvtActHora.text = _evento.hora
        spnRegEvtActTipo.id = _evento.tipo.toInt()
        spnRegEvtActRecorrencia.id = _evento.recorrencia.toInt()
        etRegEvtActDescricao.setText(_evento.descricao)
    }

    override fun onDataSelecionada(data: DataVO) {
        val dataSelecionada = "${data.dia}/${data.mes}/${data.ano}"
        tvRegEvtActData.text = dataSelecionada
    }

    override fun onHoraSelecionada(hora: HoraVO) {
        val horaSelecionada = "${hora.hora}:${hora.minuto}"
        tvRegEvtActHora.text = horaSelecionada
    }
}