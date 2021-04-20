package com.brsan7.oct

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
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
    private var startupActivity = true
    private lateinit var etRegEvtActTitulo: EditText
    private lateinit var tvRegEvtActData: TextView
    private lateinit var tvRegEvtActHora: TextView
    private lateinit var spnRegEvtActTipo: Spinner
    private lateinit var spnRegEvtActRecorrencia: Spinner
    private lateinit var spnRegEvtActModo: Spinner
    private lateinit var rbtnRegEvtActDiasSemana: LinearLayout
    private lateinit var etRegEvtActModo: EditText
    private lateinit var etRegEvtActDescricao: EditText
    private lateinit var btnRegEvtActRegistrar: Button
    private lateinit var rbtnRegEvtActDom: CheckBox
    private lateinit var rbtnRegEvtActSeg: CheckBox
    private lateinit var rbtnRegEvtActTer: CheckBox
    private lateinit var rbtnRegEvtActQua: CheckBox
    private lateinit var rbtnRegEvtActQui: CheckBox
    private lateinit var rbtnRegEvtActSex: CheckBox
    private lateinit var rbtnRegEvtActSab: CheckBox

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
        spnRegEvtActModo = findViewById(R.id.spnRegEvtActModo)
        rbtnRegEvtActDiasSemana = findViewById(R.id.rbtnRegEvtActDiasSemana)
        etRegEvtActModo = findViewById(R.id.etRegEvtActModo)
        etRegEvtActDescricao = findViewById(R.id.etRegEvtActDescricao)
        btnRegEvtActRegistrar = findViewById(R.id.btnRegEvtActRegistrar)
        rbtnRegEvtActDom = findViewById(R.id.rbtnRegEvtActDom)
        rbtnRegEvtActSeg = findViewById(R.id.rbtnRegEvtActSeg)
        rbtnRegEvtActTer = findViewById(R.id.rbtnRegEvtActTer)
        rbtnRegEvtActQua = findViewById(R.id.rbtnRegEvtActQua)
        rbtnRegEvtActQui = findViewById(R.id.rbtnRegEvtActQui)
        rbtnRegEvtActSex = findViewById(R.id.rbtnRegEvtActSex)
        rbtnRegEvtActSab = findViewById(R.id.rbtnRegEvtActSab)
        if (ID_EXTRA > -1){btnRegEvtActRegistrar.text = getString(R.string.txt_btnRegAgActEditar)}
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
        regEvtActivityViewModel.reStartActivity = true
    }

    private fun setComposeRegistro(_evento: EventoVO){
        etRegEvtActTitulo.setText(_evento.titulo)
        tvRegEvtActData.text = _evento.data
        tvRegEvtActHora.text = _evento.hora
        spnRegEvtActTipo.setSelection(_evento.tipo.toInt())
        setAdapterSpnRegEvtActRecorrencia()
        spnRegEvtActRecorrencia.setSelection(_evento.recorrencia.split("_")[0].toInt())
        spnRegEvtActModo.setSelection(_evento.recorrencia.split("_")[1].toInt())
        etRegEvtActDescricao.setText(_evento.descricao)
    }

    private fun getComposeRegistro(isRegistro: Boolean):EventoVO{
        val tipo: String
        var recorrencia: String
        if (isRegistro){
            tipo = "${spnRegEvtActTipo.selectedItem}"
            recorrencia = "${spnRegEvtActRecorrencia.selectedItem}"
            when (spnRegEvtActRecorrencia.selectedItem){
                resources.getStringArray(R.array.recorrencia_lembrete)[1] -> {//Único
                    //
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[2] -> {//Semanal Fixo
                    //
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[3] -> {//Semanal Dinâmico
                    recorrencia += composeSelectionDiasSemana()
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[4] -> {//Mensal Fixo
                    //
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[5] -> {//Mensal Dinâmico
                    //
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[6] -> {//Anual Fixo
                    //
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[7] -> {//Anual Dinâmico
                    //
                }
                resources.getStringArray(R.array.recorrencia_lembrete)[8] -> {//Periódico
                    //
                }
            }
        }
        else{
            tipo = "${spnRegEvtActTipo.selectedItemId}"
            recorrencia = "${spnRegEvtActRecorrencia.selectedItemId}_${spnRegEvtActModo.selectedItemId}"
        }
        return EventoVO(
                ID_EXTRA,
                "${etRegEvtActTitulo.text}",
                "${tvRegEvtActData.text}",
                "${tvRegEvtActHora.text}",
                tipo,
                recorrencia,
                "${etRegEvtActDescricao.text}"
        )
    }

    override fun onDataSelecionada(data: DataVO) {
        val dataSelecionada = "${data.dia}/${data.mes+1}/${data.ano}"
        tvRegEvtActData.text = dataSelecionada
    }

    override fun onHoraSelecionada(hora: HoraVO) {
        val horaSelecionada = "${hora.hora}:${hora.minuto}"
        tvRegEvtActHora.text = horaSelecionada
    }

    private fun composeSelectionDiasSemana() : String{
        var diasSemana = "_"
        if (rbtnRegEvtActDom.isChecked){ diasSemana += "Dom," }
        if (rbtnRegEvtActSeg.isChecked){ diasSemana += "Seg," }
        if (rbtnRegEvtActTer.isChecked){ diasSemana += "Ter," }
        if (rbtnRegEvtActQua.isChecked){ diasSemana += "Qua," }
        if (rbtnRegEvtActQui.isChecked){ diasSemana += "Qui," }
        if (rbtnRegEvtActSex.isChecked){ diasSemana += "Sex," }
        if (rbtnRegEvtActSab.isChecked){ diasSemana += "Sab," }

        return diasSemana
    }

    private fun setAdapterSpnRegEvtActRecorrencia(){
        when (spnRegEvtActTipo.selectedItem) {
            "Feriado" -> {
                spnRegEvtActRecorrencia.adapter = ArrayAdapter(
                        this,
                        R.layout.item_spinner,
                        resources.getStringArray(R.array.recorrencia_feriado))
            }
            "Compromisso" -> {
                spnRegEvtActRecorrencia.adapter = ArrayAdapter(
                        this,
                        R.layout.item_spinner,
                        resources.getStringArray(R.array.recorrencia_compromisso))
            }
            "Lembrete" -> {
                spnRegEvtActRecorrencia.adapter = ArrayAdapter(
                        this,
                        R.layout.item_spinner,
                        resources.getStringArray(R.array.recorrencia_lembrete))
            }
        }
    }

    private fun atualizarSpnRegEvtActTipo(position: Int){
        rbtnRegEvtActDiasSemana.visibility = View.GONE
        etRegEvtActModo.visibility = View.GONE
        tvRegEvtActData.visibility = View.GONE
        tvRegEvtActHora.visibility = View.GONE
        if (position == 0){
            spnRegEvtActRecorrencia.visibility = View.GONE
            spnRegEvtActModo.visibility = View.GONE
        }
        else {
            spnRegEvtActRecorrencia.visibility = View.VISIBLE
            spnRegEvtActModo.visibility = View.VISIBLE
            setAdapterSpnRegEvtActRecorrencia()
        }
    }
    private fun atualizarSpnRegEvtActRecorrencia(position: Int){

        etRegEvtActModo.visibility = View.GONE
        if (position == 8){
            spnRegEvtActModo.visibility = View.VISIBLE
        }
        else {
            spnRegEvtActModo.visibility = View.GONE
            if (position != 0) {
                tvRegEvtActData.visibility = View.VISIBLE
                tvRegEvtActHora.visibility = View.VISIBLE
            }
        }
        if (spnRegEvtActRecorrencia.selectedItem == "Semanal Dinâmico"){rbtnRegEvtActDiasSemana.visibility = View.VISIBLE}
        else{rbtnRegEvtActDiasSemana.visibility = View.GONE}
        if (spnRegEvtActTipo.selectedItem != "Compromisso"){tvRegEvtActHora.visibility = View.GONE}
    }
    private fun atualizarSpnRegEvtActModo(position: Int){
        if (position == 0){ etRegEvtActModo.visibility = View.GONE }
        else { etRegEvtActModo.visibility = View.VISIBLE }
    }
}