package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.utils.TempoUtils
import java.util.*

class MainViewModel: ViewModel() {

    private var _vmRcvMain = MutableLiveData<List<EventoVO>>()
    val vmRcvMain: LiveData<List<EventoVO>>
        get() = _vmRcvMain

    fun verificarAtualizarTodosEventos(){
        var listaCompleta: List<EventoVO>
        Thread {
            try {
                listaCompleta = OctApplication.instance.helperDB?.buscarEventos(
                        busca = "",
                        isBuscaPorData = false)
                        ?: mutableListOf()

                val evtsAtualizados = TempoUtils().atualizarEventos(listaCompleta)
                for (index in evtsAtualizados.indices){
                    if (evtsAtualizados[index].recorrencia == "Finalizado"){
                        OctApplication.instance.helperDB?.deletarEvento(evtsAtualizados[index].id)
                    }
                    else{
                        OctApplication.instance.helperDB?.modificarEvento(evtsAtualizados[index])
                    }
                }
                buscarEventosDoDia(isDeleted = false)

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }

    fun buscarEventosDoDia(isDeleted: Boolean){

        if (_vmRcvMain.value?.toList()?.size == null || isDeleted) {
            var listaFiltrada: List<EventoVO>
            Thread {
                if (isDeleted){Thread.sleep(1000)}
                try {
                    val hoje = Calendar.getInstance()
                    listaFiltrada = OctApplication.instance.helperDB?.buscarEventos(
                            busca = "${hoje[Calendar.DAY_OF_MONTH]}/${hoje[Calendar.MONTH]}/${hoje[Calendar.YEAR]}",
                            isBuscaPorData = true
                    ) ?: mutableListOf()

                    _vmRcvMain.postValue(listaFiltrada)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }
}