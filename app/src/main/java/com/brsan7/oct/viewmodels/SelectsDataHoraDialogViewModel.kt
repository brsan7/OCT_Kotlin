package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.model.EventoVO

class SelectsDataHoraDialogViewModel: ViewModel() {

    private var _vmRegDlgData = MutableLiveData<EventoVO>()
    val vmRegDlgData: LiveData<EventoVO>
        get() = _vmRegDlgData

    private var _vmRegDlgHora = MutableLiveData<EventoVO>()
    val vmRegDlgHora: LiveData<EventoVO>
        get() = _vmRegDlgHora

    fun getSelecaoData(data: EventoVO){
        if (_vmRegDlgData.value?.data == null){
            _vmRegDlgData.postValue(data)
        }
    }

    fun getSelecaoHora(hora: EventoVO){
        if (_vmRegDlgHora.value?.hora == null){
            _vmRegDlgHora.postValue(hora)
        }
    }
}