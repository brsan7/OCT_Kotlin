package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.LocalVO

class LocEditDialogViewModel: ViewModel() {

    private var _vmLocSelecionado = MutableLiveData<List<LocalVO>>()
    val vmLocSelecionado: LiveData<List<LocalVO>>
        get() = _vmLocSelecionado

    fun buscarLocalSelecionado(id: Int){

        if (_vmLocSelecionado.value?.toList()?.size == null) {
            var listaFiltrada: List<LocalVO>
            Thread {
                try {
                    listaFiltrada = OctApplication.instance.helperDB?.buscarLocais("$id",true) ?: mutableListOf()
                    _vmLocSelecionado.postValue(listaFiltrada)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }

    fun editarLocalSelecionado(localComposto: LocalVO){

        Thread {
            OctApplication.instance.helperDB?.modificarLocal(localComposto)
        }.start()
    }

    fun deletarLocalSelecionado(id: Int){

        Thread {
            OctApplication.instance.helperDB?.deletarLocal(id)
        }.start()
    }
}