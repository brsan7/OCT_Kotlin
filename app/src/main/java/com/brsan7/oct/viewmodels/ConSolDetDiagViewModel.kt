package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.LocalVO

class ConSolDetDiagViewModel: ViewModel() {

    private var _vmLocalSelecionado = MutableLiveData<LocalVO>()
    val vmLocalSelecionado: LiveData<LocalVO>
        get() = _vmLocalSelecionado

    fun buscarLocalSelecionado(id: Int){

        if (_vmLocalSelecionado.value?.id == null) {
            var listaFiltrada: List<LocalVO>
            Thread {
                try {
                    listaFiltrada = OctApplication.instance.helperDB?.buscarLocais(
                            busca = "$id",
                            isBuscaPorId = true
                    ) ?: mutableListOf()

                    _vmLocalSelecionado.postValue(listaFiltrada.first())

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }
}