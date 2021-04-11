package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.model.EventoVO

class RegEvtActivityViewModel: ViewModel() {

    private var _vmComposeReg = MutableLiveData<EventoVO>()
    val vmComposeReg: LiveData<EventoVO>
        get() = _vmComposeReg

    fun getSelecaoData(evento: EventoVO){
        if (_vmComposeReg.value?.data == null){
            _vmComposeReg.postValue(evento)
        }
    }
}