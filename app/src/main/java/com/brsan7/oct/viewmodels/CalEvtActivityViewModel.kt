package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.IdItemSpinnersVO
import com.brsan7.oct.model.EventoVO
import com.prolificinteractive.materialcalendarview.CalendarDay

class CalEvtActivityViewModel: ViewModel() {

////////////////////////Calendário///////////////////////////////////////
    private var _vmMcvCalActFeriado = MutableLiveData<MutableCollection<CalendarDay>>()
    val vmMcvCalActFeriado: LiveData<MutableCollection<CalendarDay>>
        get() = _vmMcvCalActFeriado

    private var _vmMcvCalActCompromisso = MutableLiveData<MutableCollection<CalendarDay>>()
    val vmMcvCalActCompromisso: LiveData<MutableCollection<CalendarDay>>
        get() = _vmMcvCalActCompromisso

    private var _vmMcvCalActLembrete = MutableLiveData<MutableCollection<CalendarDay>>()
    val vmMcvCalActLembrete: LiveData<MutableCollection<CalendarDay>>
        get() = _vmMcvCalActLembrete
////////////////////////Calendário///////////////////////////////////////

////////////////////////Spinners///////////////////////////////////////
    private var _vmSpnCalActFeriado = MutableLiveData<Array<String>>()
    val vmSpnCalActFeriado: LiveData<Array<String>>
        get() = _vmSpnCalActFeriado

    private var _vmSpnCalActCompromisso = MutableLiveData<Array<String>>()
    val vmSpnCalActCompromisso: LiveData<Array<String>>
        get() = _vmSpnCalActCompromisso

    private var _vmSpnCalActLembrete = MutableLiveData<Array<String>>()
    val vmSpnCalActLembrete: LiveData<Array<String>>
        get() = _vmSpnCalActLembrete

    var idItemSpinners: MutableList<IdItemSpinnersVO> = mutableListOf()
    var diaSelecionado: CalendarDay = CalendarDay.today()
    var reStartActivity = false
////////////////////////Spinners///////////////////////////////////////

    fun getAllDecorateDates() {
        if (_vmMcvCalActFeriado.value?.toMutableList()?.size == null) {

            var listaCompleta: List<EventoVO>
            Thread {
                try {
                    listaCompleta = OctApplication.instance.helperDB?.buscarEventos(
                            busca = "",
                            isBuscaPorData = false)
                            ?: mutableListOf()

                    _vmMcvCalActFeriado.postValue(convEvtToCalDayList(OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoFeriado),listaCompleta))

                    _vmMcvCalActCompromisso.postValue(convEvtToCalDayList(OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoCompromisso),listaCompleta))

                    _vmMcvCalActLembrete.postValue(convEvtToCalDayList(OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoLembrete),listaCompleta))

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }

    private fun convListArraySpinner(tipo: String, lista: List<EventoVO>): Array<String> {
        var arraySize = 1
        for (index in lista.indices) {
            if (lista[index].tipo == tipo) {
                arraySize++
            }
        }
        val itensArray = Array(arraySize) { "" }
        var countIndexArray = 1
        itensArray[0] = tipo
        for (index in lista.indices) {
            if (lista[index].tipo == tipo) {
                itensArray[countIndexArray] = "${lista[index].data} > ${lista[index].titulo}"
                idItemSpinners.add(
                        IdItemSpinnersVO(
                                idSpinner = countIndexArray,
                                idRegistro = lista[index].id,
                                tipo = tipo
                        )
                )
                countIndexArray++
            }
        }
        return itensArray
    }

    fun getIdRegistro(tipo: String, index: Int): Int {
        val id = idItemSpinners.find { idItemSpinnerVO ->
            idItemSpinnerVO.tipo == tipo
                    && idItemSpinnerVO.idSpinner == index
        }
        return id?.idRegistro ?: -1
    }

    fun getRegistrosDiaSelecionado(data: CalendarDay?) {

        if (!reStartActivity) {
            var listaFiltrada: List<EventoVO>
            Thread {
                try {
                    listaFiltrada = OctApplication.instance.helperDB?.buscarEventos(
                            busca = "${data?.day}/${data?.month}/${data?.year}",
                            isBuscaPorData = true)
                            ?: mutableListOf()

                    _vmSpnCalActFeriado.postValue(convListArraySpinner(tipo = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoFeriado), lista = listaFiltrada))

                    _vmSpnCalActCompromisso.postValue(convListArraySpinner(tipo = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoCompromisso), lista = listaFiltrada))

                    _vmSpnCalActLembrete.postValue(convListArraySpinner(tipo = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoLembrete), lista = listaFiltrada))

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }
    }

    private fun convEvtToCalDayList(tipo: String, listaFiltrada: List<EventoVO>): MutableCollection<CalendarDay> {
        val allTipoEvento: MutableCollection<CalendarDay> = mutableListOf()
        listaFiltrada.forEach {
            if (it.tipo == tipo) {
                allTipoEvento.add(
                        CalendarDay.from(
                                it.data.split("/")[2].toInt(),
                                it.data.split("/")[1].toInt(),
                                it.data.split("/")[0].toInt()
                        )
                )
            }
        }
        return allTipoEvento
    }
}