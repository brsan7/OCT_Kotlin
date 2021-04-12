package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO

class RegEvtActivityViewModel: ViewModel() {

    private var _vmComposeReg = MutableLiveData<EventoVO>()
    val vmComposeReg: LiveData<EventoVO>
        get() = _vmComposeReg

    private var ID = -1

    fun getComposicaoEventoAtual(evento: EventoVO){
        if (_vmComposeReg.value?.data == null){
            _vmComposeReg.postValue(evento)
        }
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

    fun buscarEventoSelecionado(id: Int){

        if (_vmComposeReg.value?.data == null) {
            var listaFiltrada: List<EventoVO>
            Thread {
                try {
                    listaFiltrada = OctApplication.instance.helperDB?.buscarEventos(
                            "$id",
                            false)
                            ?: mutableListOf()
                    //_vmEvtSelecionado.postValue(listaFiltrada)
                    val tipo = when(listaFiltrada[0].tipo){
                        "Feriado" -> {"1"}
                        "Compromisso" -> {"2"}
                        "Lembrete" -> {"3"}
                        else -> {"0"}
                    }
                    val recorrencia = when(listaFiltrada[0].recorrencia){
                        "Anual" -> {"1"}
                        "Mensal" -> {"2"}
                        "Semanal" -> {"3"}
                        else -> {"0"}
                    }
                    val evtSelecionado = EventoVO(
                            listaFiltrada[0].id,
                            listaFiltrada[0].titulo,
                            listaFiltrada[0].data,
                            listaFiltrada[0].hora,
                            tipo,
                            recorrencia,
                            listaFiltrada[0].descricao
                    )
                    _vmComposeReg.postValue(evtSelecionado)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }
}