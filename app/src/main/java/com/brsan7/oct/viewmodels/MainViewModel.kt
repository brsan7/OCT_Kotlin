package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO
import com.prolificinteractive.materialcalendarview.CalendarDay

class MainViewModel: ViewModel() {

    private var _vmRcvMain = MutableLiveData<List<EventoVO>>()
    val vmRcvMain: LiveData<List<EventoVO>>
        get() = _vmRcvMain

    fun buscarEventosDoDia(isDeleted: Boolean){

        if (_vmRcvMain.value?.toList()?.size == null || isDeleted) {
            var listaFiltrada: List<EventoVO>
            Thread {
                if (isDeleted){Thread.sleep(1000)}
                try {
                    listaFiltrada = OctApplication.instance.helperDB?.buscarEventos(
                            "${CalendarDay.today().day}/${CalendarDay.today().month}/${CalendarDay.today().year}",
                            true)
                            ?: mutableListOf()
                    _vmRcvMain.postValue(listaFiltrada)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }
}