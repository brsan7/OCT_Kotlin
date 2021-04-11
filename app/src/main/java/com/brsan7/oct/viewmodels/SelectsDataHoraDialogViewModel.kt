package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.model.DataVO
import com.brsan7.oct.model.HoraVO

class SelectsDataHoraDialogViewModel: ViewModel() {

    private var _vmRegDlgData = MutableLiveData<DataVO>()
    val vmRegDlgData: LiveData<DataVO>
        get() = _vmRegDlgData

    private var _vmRegDlgHora = MutableLiveData<HoraVO>()
    val vmRegDlgHora: LiveData<HoraVO>
        get() = _vmRegDlgHora

    fun getSelecaoData(data: DataVO){
        if (_vmRegDlgData.value?.dia == null){
            _vmRegDlgData.postValue(data)
        }
    }

    fun getSelecaoHora(hora: HoraVO){
        if (_vmRegDlgHora.value?.hora == null){
            _vmRegDlgHora.postValue(hora)
        }
    }
}