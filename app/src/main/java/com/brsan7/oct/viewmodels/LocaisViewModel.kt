package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.LocalVO

class LocaisViewModel: ViewModel() {

    private var _vmRcvLocais = MutableLiveData<List<LocalVO>>()
    val vmRcvLocais: LiveData<List<LocalVO>>
        get() = _vmRcvLocais
    private var _vmDefLocal = MutableLiveData<LocalVO>()
    val vmDefLocal: LiveData<LocalVO>
        get() = _vmDefLocal

    fun buscarLocais(busca: String,isBuscaPorId: Boolean,isDeleted: Boolean){

        if (_vmRcvLocais.value?.toList()?.size == null || isBuscaPorId || isDeleted) {
            var lista: List<LocalVO>
            Thread {
                if (isDeleted){Thread.sleep(1000)}
                try {
                    lista = OctApplication.instance.helperDB?.buscarLocais(busca,isBuscaPorId) ?: mutableListOf()

                    if (isBuscaPorId){ _vmDefLocal.postValue(lista.first()) }
                    else{ _vmRcvLocais.postValue(lista) }

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }
}