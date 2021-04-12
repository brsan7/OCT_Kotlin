package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO

class EvtDetDiagViewModel: ViewModel() {

    private var _vmEvtSelecionado = MutableLiveData<List<EventoVO>>()
    val vmEvtSelecionado: LiveData<List<EventoVO>>
        get() = _vmEvtSelecionado

    fun buscarEventoSelecionado(id: Int){

        if (_vmEvtSelecionado.value?.toList()?.size == null) {
            var listaFiltrada: List<EventoVO>
            Thread {
                try {
                    listaFiltrada = OctApplication.instance.helperDB?.buscarEventos(
                            "$id",
                            false)
                            ?: mutableListOf()
                    _vmEvtSelecionado.postValue(listaFiltrada)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }
    fun deletarEventoSelecionado(id: Int){

        Thread {
            OctApplication.instance.helperDB?.deletarEvento(id)
        }.start()

    }
}