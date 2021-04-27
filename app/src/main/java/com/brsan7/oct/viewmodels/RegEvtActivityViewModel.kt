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

    private var ID = -1

    fun getComposicaoEventoAtual(evento: EventoVO){
        _vmComposeReg.postValue(evento)
    }

    fun verificarEdicao(ID_EXTRA: Int){
        ID = ID_EXTRA
        if (ID >= 0){buscarEventoSelecionado(ID)}
    }

    fun registrarEvento(eventoComposto: EventoVO){
        if (ID == -1) {
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
                            "$id",
                            false)
                            ?: mutableListOf()
                    reStartActivity = true

                    val evtSelecionado = listaFiltrada.first()
                    var data = ""
                    val tipo = when (evtSelecionado.tipo) {
                        OctApplication.instance.resources.getStringArray(R.array.tipo_registro)[1] -> { "1" }//Feriado
                        OctApplication.instance.resources.getStringArray(R.array.tipo_registro)[2] -> { "2" }//Compromisso
                        OctApplication.instance.resources.getStringArray(R.array.tipo_registro)[3] -> { "3" }//Lembrete
                        else -> { "0" }
                    }
                    val recorrencia = when (evtSelecionado.recorrencia.split("_")[0]) {
                        OctApplication.instance.resources.getStringArray(R.array.recorrencia_lembrete)[1] -> { "1_0" }//Único
                        OctApplication.instance.resources.getStringArray(R.array.recorrencia_lembrete)[2] -> { "2_0" }//Semanal Fixo
                        OctApplication.instance.resources.getStringArray(R.array.recorrencia_lembrete)[3] -> { //"3_0" }//Semanal Dinâmico
                            "3_0*${evtSelecionado.recorrencia.split("_")[1]}"
                        }
                        OctApplication.instance.resources.getStringArray(R.array.recorrencia_lembrete)[4] -> { "4_0" }//Mensal Fixo
                        OctApplication.instance.resources.getStringArray(R.array.recorrencia_lembrete)[5] -> { "5_0" }//Mensal Dinâmico
                        OctApplication.instance.resources.getStringArray(R.array.recorrencia_lembrete)[6] -> { //Anual Fixo

                            if (tipo == "1") { "1_0" } //Feriado
                            else { "6_0" } //Lembrete
                        }
                        OctApplication.instance.resources.getStringArray(R.array.recorrencia_lembrete)[7] -> { //Anual Dinâmico

                            if (tipo == "1") { "2_0" } //Feriado
                            else { "7_0" } //Lembrete
                        }
                        OctApplication.instance.resources.getStringArray(R.array.recorrencia_lembrete)[8] -> { //"8" }//Periódico

                            when (evtSelecionado.recorrencia.split("_")[1].split("*")[1]){ //Modos

                                OctApplication.instance.resources.getStringArray(R.array.recorrencia_periodica_modos)[1] -> { //Anos
                                    "8_1*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                                }
                                OctApplication.instance.resources.getStringArray(R.array.recorrencia_periodica_modos)[2] -> { //Meses
                                    "8_2*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                                }
                                OctApplication.instance.resources.getStringArray(R.array.recorrencia_periodica_modos)[3] -> { //Semanas
                                    "8_3*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                                }
                                OctApplication.instance.resources.getStringArray(R.array.recorrencia_periodica_modos)[4] -> { //Dias Corridos
                                    "8_4*${evtSelecionado.recorrencia.split("_")[1].split("*")[0]}"
                                }
                                OctApplication.instance.resources.getStringArray(R.array.recorrencia_periodica_modos)[5] -> { //Dias Úteis
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
                    _vmComposeReg.postValue(evtConvertido)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }
}