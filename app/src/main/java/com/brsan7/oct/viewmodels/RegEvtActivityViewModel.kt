package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.utils.TempoUtils

class RegEvtActivityViewModel: ViewModel() {

    private var _vmComposeReg = MutableLiveData<EventoVO>()
    val vmComposeReg: LiveData<EventoVO>
        get() = _vmComposeReg
    var reStartActivity = false

    private var id = -1

    fun getComposicaoEventoAtual(evento: EventoVO){
        _vmComposeReg.postValue(evento)
    }

    fun verificarEdicao(ID_EXTRA: Int){
        id = ID_EXTRA
        if (id >= 0){buscarEventoSelecionado(id)}
    }

    fun registrarEvento(eventoComposto: EventoVO){
        if (id == -1) {
            Thread{
                OctApplication.instance.helperDB?.registrarEvento(eventoComposto)
            }.start()
        }
        else{
            Thread{
                OctApplication.instance.helperDB?.modificarEvento(eventoComposto)
            }.start()
        }
    }

    private fun buscarEventoSelecionado(id: Int){

        if (_vmComposeReg.value?.data == null) {
            var listaFiltrada: List<EventoVO>
            Thread {
                try {
                    listaFiltrada = OctApplication.instance.helperDB?.buscarEventos(
                            busca = "$id",
                            isBuscaPorData = false)
                            ?: mutableListOf()

                    reStartActivity = true
                    _vmComposeReg.postValue(convEvtToFields(listaFiltrada.first()))

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }

    private fun convEvtToFields(evtSelecionado: EventoVO): EventoVO{
        var data = ""
        val tipo = when (evtSelecionado.tipo) {
            OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoFeriado) -> { "1" }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoCompromisso) -> { "2" }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoLembrete) -> { "3" }
            else -> { "0" }
        }
        val recorrencia = when (evtSelecionado.recorrencia.split("_")[0]) {
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaUnico) -> { "1_0" }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaSmnFixo) -> { "2_0" }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaSmnDin) -> { //"3_0" }
                "3_0*${evtSelecionado.recorrencia.split("_")[1]}"
            }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaMnsFixo) -> { "4_0" }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaMnsDin) -> { "5_0" }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaAnualFixo) -> {

                if (tipo == "1") { "1_0" } //Feriado
                else { "6_0" } //Lembrete
            }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaAnualDin) -> {

                if (tipo == "1") { "2_0" } //Feriado
                else { "7_0" } //Lembrete
            }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaPeriodico) -> {

                when (evtSelecionado.recorrencia.split("_")[1].split("*")[1]){ //Modos

                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoAnos) -> {
                        "8_1*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                    }
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoMeses) -> {
                        "8_2*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                    }
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoSemanas) -> {
                        "8_3*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                    }
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoDiasCorridos) -> {
                        "8_4*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                    }
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoDiasUteis) -> {
                        "8_5*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                    }
                    else -> {"8_0"}
                }
            }
            else -> { "0_0" }
        }

        if ( tipo == "1" && recorrencia == "2_0" || recorrencia == "5_0" || recorrencia == "7_0" ){ //Evento Dinâmico

            data = "${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}*" //Regra de recorrência
        }
        data += "${TempoUtils().getStringDiaSemana(evtSelecionado.data)}, ${evtSelecionado.data}" //Dia da Semana
        val evtConvertido = EventoVO(
                id = -1,
                titulo = evtSelecionado.titulo,
                data = data,
                hora = evtSelecionado.hora,
                tipo = tipo,
                recorrencia = recorrencia,
                descricao = evtSelecionado.descricao
        )
        return evtConvertido
    }
}