package com.brsan7.oct.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO

class RegEvtActivityViewModel: ViewModel() {

    private var _vmComposeReg = MutableLiveData<EventoVO>()
    val vmComposeReg: LiveData<EventoVO>
        get() = _vmComposeReg
    var reStartActivity = false

    private var ID = -1

    fun getComposicaoEventoAtual(evento: EventoVO){
        //if (_vmComposeReg.value?.data == null){
            _vmComposeReg.postValue(evento)
        //}
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
                    _vmComposeReg.postValue(listaFiltrada.first())
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }
}