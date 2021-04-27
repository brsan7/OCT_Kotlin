package com.brsan7.oct

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.dialogs.SelectDataDialog
import com.brsan7.oct.dialogs.SelectDiasSemanaDialog
import com.brsan7.oct.dialogs.SelectHoraDialog
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.viewmodels.RegEvtActivityViewModel

class RegistroEventoActivity : DrawerMenuActivity(),
        SelectDiasSemanaDialog.SelecaoDiasSemana,
        SelectDataDialog.SelecaoData,
        SelectHoraDialog.SelecaoHora {

    private lateinit var regEvtActivityViewModel: RegEvtActivityViewModel
    private var ID_EXTRA = -1
    private var startupActivity = true
    private lateinit var etRegEvtActTitulo: EditText
    private lateinit var spnRegEvtActTipo: Spinner
    private lateinit var spnRegEvtActRecorrencia: Spinner
    private lateinit var spnRegEvtActModo: Spinner
    private lateinit var tvRegEvtActDiasSemana: TextView
    private lateinit var tvRegEvtActData: TextView
    private lateinit var tvRegEvtActHora: TextView
    private lateinit var etRegEvtActModo: EditText
    private lateinit var etRegEvtActDescricao: EditText
    private lateinit var btnRegEvtActRegistrar: Button


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
        spnRegEvtActTipo = findViewById(R.id.spnRegEvtActTipo)
        spnRegEvtActRecorrencia = findViewById(R.id.spnRegEvtActRecorrencia)
        spnRegEvtActModo = findViewById(R.id.spnRegEvtActModo)
        tvRegEvtActDiasSemana = findViewById(R.id.tvRegEvtActDiasSemana)
        tvRegEvtActData = findViewById(R.id.tvRegEvtActData)
        tvRegEvtActHora = findViewById(R.id.tvRegEvtActHora)
        etRegEvtActModo = findViewById(R.id.etRegEvtActModo)
        etRegEvtActDescricao = findViewById(R.id.etRegEvtActDescricao)
        btnRegEvtActRegistrar = findViewById(R.id.btnRegEvtActRegistrar)

        if (ID_EXTRA > -1){btnRegEvtActRegistrar.text = getString(R.string.txt_btnDialogsEditar) }

        spnRegEvtActTipo.adapter = ArrayAdapter(
                this,
                R.layout.item_spinner,
                resources.getStringArray(R.array.tipo_registro))
        spnRegEvtActModo.adapter = ArrayAdapter(
                this,
                R.layout.item_spinner,
                resources.getStringArray(R.array.recorrencia_periodica_modos))
    }

    private fun setupListeners() {

        spnRegEvtActTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (regEvtActivityViewModel.vmComposeReg.value?.tipo?.toIntOrNull() ?: 0 == 0){
                    startupActivity = false
                    regEvtActivityViewModel.reStartActivity = false
                }
                if (!regEvtActivityViewModel.reStartActivity) {
                    atualizarSpnRegEvtActTipo(position)
                }
                if (startupActivity) { startupActivity = false }
                else { regEvtActivityViewModel.reStartActivity = false }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spnRegEvtActRecorrencia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {

                atualizarSpnRegEvtActRecorrencia(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spnRegEvtActModo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {

                atualizarSpnRegEvtActModo(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        tvRegEvtActDiasSemana.setOnClickListener{
            val fragment = SelectDiasSemanaDialog.newInstance()
            fragment.show(supportFragmentManager, "dialog")
        }

        tvRegEvtActData.setOnClickListener{
            if (spnRegEvtActRecorrencia.selectedItem != resources.getStringArray(R.array.recorrencia_compromisso)[3]) {//Semanal Dinâmico
                val fragment = SelectDataDialog.newInstance("${spnRegEvtActRecorrencia.selectedItem}")
                fragment.show(supportFragmentManager, "dialog")
            }
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
        regEvtActivityViewModel.reStartActivity = true
    }

    private fun setComposeRegistro(evtSelecionado: EventoVO){
        etRegEvtActTitulo.setText(evtSelecionado.titulo)
        tvRegEvtActData.text = evtSelecionado.data
        tvRegEvtActHora.text = evtSelecionado.hora
        spnRegEvtActTipo.setSelection(evtSelecionado.tipo.toInt())
        setAdapterSpnRegEvtActRecorrencia()

        if (evtSelecionado.recorrencia.split("_")[0].toIntOrNull() != null) {

            spnRegEvtActRecorrencia.setSelection(evtSelecionado.recorrencia.split("_")[0].toInt())

            if (evtSelecionado.recorrencia.split("_")[0].toInt() == 3) {// Semanal Dinâmico

                tvRegEvtActDiasSemana.text = evtSelecionado.recorrencia.split("_")[1].split("*")[1]
            }

            if (evtSelecionado.recorrencia.split("_")[1].split("*")[0].toInt() > 0) { //Periódico

                etRegEvtActModo.setText(evtSelecionado.recorrencia.split("_")[1].split("*")[1])
                spnRegEvtActModo.setSelection(
                        evtSelecionado.recorrencia.split("_")[1].split("*")[0].toInt())
            }
        }
        etRegEvtActDescricao.setText(evtSelecionado.descricao)
    }

    private fun getComposeRegistro(isRegistro: Boolean):EventoVO{
        var id = ID_EXTRA
        val tipo: String
        var recorrencia: String
        val data: String
        if (isRegistro){
            tipo = "${spnRegEvtActTipo.selectedItem}"
            recorrencia = "${spnRegEvtActRecorrencia.selectedItem}"
            when (spnRegEvtActRecorrencia.selectedItem){
                resources.getStringArray(R.array.recorrencia_lembrete)[3] -> {//Semanal Dinâmico
                    recorrencia += "_${tvRegEvtActDiasSemana.text}"
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[5] -> {//Mensal Dinâmico
                    recorrencia += "_${tvRegEvtActData.text.substring(0..4)}"
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[7] -> {//Anual Dinâmico
                    recorrencia += "_${tvRegEvtActData.text.substring(0..4)}"
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[8] -> {//Periódico
                    recorrencia += "_${etRegEvtActModo.text}*${spnRegEvtActModo.selectedItem}"
                }
            }
            if (spnRegEvtActTipo.selectedItem != resources.getStringArray(R.array.tipo_registro)[2]){//Compromisso
                tvRegEvtActHora.text = ""
            }
            data = if (tvRegEvtActData.text.length > 14){ tvRegEvtActData.text.substring(7,tvRegEvtActData.text.length) }
            else{ tvRegEvtActData.text.substring(5,tvRegEvtActData.text.length) }
        }
        else{
            id = -1
            tipo = "${spnRegEvtActTipo.selectedItemId}"
            val complemento = if (spnRegEvtActModo.selectedItemId == 0L){tvRegEvtActDiasSemana.text}else{etRegEvtActModo.text}
            recorrencia = "${spnRegEvtActRecorrencia.selectedItemId}_${spnRegEvtActModo.selectedItemId}*$complemento"
            data = "${tvRegEvtActData.text}"
        }
        return EventoVO(
                id = id,
                titulo = "${etRegEvtActTitulo.text}",
                data = data,
                hora = "${tvRegEvtActHora.text}",
                tipo = tipo,
                recorrencia = recorrencia,
                descricao = "${etRegEvtActDescricao.text}"
        )
    }

    override fun onDiasSemanaSelecionado(diasSemanaSelecionado: EventoVO) {
        tvRegEvtActData.text = diasSemanaSelecionado.data
        tvRegEvtActDiasSemana.text = diasSemanaSelecionado.recorrencia
        tvRegEvtActData.visibility = View.VISIBLE
        if (spnRegEvtActTipo.selectedItemId == 2L){ //Compromisso
            tvRegEvtActHora.visibility = View.VISIBLE
        }
    }

    override fun onDataSelecionada(dataSelecionada : EventoVO) {
        tvRegEvtActData.text = dataSelecionada.data
        if (spnRegEvtActTipo.selectedItemId == 2L){ //Compromisso
            tvRegEvtActHora.visibility = View.VISIBLE
        }
    }

    override fun onHoraSelecionada(horaSelecionada : EventoVO) {
        tvRegEvtActHora.text = horaSelecionada.hora
    }

    private fun setAdapterSpnRegEvtActRecorrencia(){
        when (spnRegEvtActTipo.selectedItem) {
            resources.getStringArray(R.array.tipo_registro)[1] -> {//Feriado
                spnRegEvtActRecorrencia.adapter = ArrayAdapter(
                        this,
                        R.layout.item_spinner,
                        resources.getStringArray(R.array.recorrencia_feriado))
            }
            resources.getStringArray(R.array.tipo_registro)[2] -> {//Compromisso
                spnRegEvtActRecorrencia.adapter = ArrayAdapter(
                        this,
                        R.layout.item_spinner,
                        resources.getStringArray(R.array.recorrencia_compromisso))
            }
            resources.getStringArray(R.array.tipo_registro)[3] -> {//Lembrete
                spnRegEvtActRecorrencia.adapter = ArrayAdapter(
                        this,
                        R.layout.item_spinner,
                        resources.getStringArray(R.array.recorrencia_lembrete))
            }
        }
    }

    private fun atualizarSpnRegEvtActTipo(position: Int){

        spnRegEvtActRecorrencia.visibility = View.GONE
        spnRegEvtActModo.visibility = View.GONE
        etRegEvtActModo.visibility = View.GONE
        tvRegEvtActDiasSemana.visibility = View.GONE
        tvRegEvtActData.visibility = View.GONE
        tvRegEvtActHora.visibility = View.GONE

        if (position > 0){
            spnRegEvtActRecorrencia.visibility = View.VISIBLE
            setAdapterSpnRegEvtActRecorrencia()
        }

    }
    private fun atualizarSpnRegEvtActRecorrencia(position: Int){

        spnRegEvtActModo.visibility = View.GONE
        etRegEvtActModo.visibility = View.GONE
        tvRegEvtActDiasSemana.visibility = View.GONE
        tvRegEvtActData.visibility = View.GONE
        tvRegEvtActHora.visibility = View.GONE

        if (position > 0 ) {
            when (position) {
                3 -> { //Semanal Dinamico
                    tvRegEvtActDiasSemana.visibility = View.VISIBLE
                    if (tvRegEvtActDiasSemana.text != getString(R.string.txt_tvRegAgActDiasSemana)){
                        tvRegEvtActData.visibility = View.VISIBLE
                        if (spnRegEvtActTipo.selectedItemId == 2L) {
                            tvRegEvtActHora.visibility = View.VISIBLE
                        }
                    }
                }
                8 -> { spnRegEvtActModo.visibility = View.VISIBLE }
                else -> { tvRegEvtActData.visibility = View.VISIBLE }
            }
        }
    }
    private fun atualizarSpnRegEvtActModo(position: Int){

        etRegEvtActModo.visibility = View.GONE
        tvRegEvtActDiasSemana.visibility = View.GONE
        tvRegEvtActData.visibility = View.GONE
        tvRegEvtActHora.visibility = View.GONE

        if (position > 0 ) {
            etRegEvtActModo.visibility = View.VISIBLE
            tvRegEvtActData.visibility = View.VISIBLE
        }
    }
}