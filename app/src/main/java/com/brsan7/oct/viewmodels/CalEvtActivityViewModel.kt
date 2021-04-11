package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.model.IdItemSpinnersVO
import com.brsan7.oct.model.EventoVO
import com.prolificinteractive.materialcalendarview.CalendarDay

class CalEvtActivityViewModel: ViewModel() {

////////////////////////Calendário///////////////////////////////////////
    private var _vmMcvCalActFeriado = MutableLiveData<MutableCollection<CalendarDay>>()
    val vmMcvCalActFeriado: LiveData<MutableCollection<CalendarDay>>
        get() = _vmMcvCalActFeriado

    private var _vmMcvCalActEvento = MutableLiveData<MutableCollection<CalendarDay>>()
    val vmMcvCalActEvento: LiveData<MutableCollection<CalendarDay>>
        get() = _vmMcvCalActEvento

    private var _vmMcvCalActLembrete = MutableLiveData<MutableCollection<CalendarDay>>()
    val vmMcvCalActLembrete: LiveData<MutableCollection<CalendarDay>>
        get() = _vmMcvCalActLembrete
////////////////////////Calendário///////////////////////////////////////

////////////////////////Spinners///////////////////////////////////////
    private var _vmSpnCalActFeriado = MutableLiveData<Array<String>>()
    val vmSpnCalActFeriado: LiveData<Array<String>>
        get() = _vmSpnCalActFeriado

    private var _vmSpnCalActEvento = MutableLiveData<Array<String>>()
    val vmSpnCalActEvento: LiveData<Array<String>>
        get() = _vmSpnCalActEvento

    private var _vmSpnCalActLembrete = MutableLiveData<Array<String>>()
    val vmSpnCalActLembrete: LiveData<Array<String>>
        get() = _vmSpnCalActLembrete

    var idItemSpinners: MutableList<IdItemSpinnersVO> = mutableListOf()
    var diaSelecionado: CalendarDay = CalendarDay.today()
    var reStartActivity = false
////////////////////////Spinners///////////////////////////////////////

    fun getAllDecorateDates() {
        if (_vmMcvCalActFeriado.value?.toMutableList()?.size == null) {

            _vmMcvCalActFeriado.postValue(auxTesteDecorator("Feriado"))

            _vmMcvCalActEvento.postValue(auxTesteDecorator("Compromisso"))

            _vmMcvCalActLembrete.postValue(auxTesteDecorator("Lembrete"))
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
            val lista = auxTesteSpinner(data, true)

            _vmSpnCalActFeriado.postValue(convListArraySpinner("Feriado", lista))

            _vmSpnCalActEvento.postValue(convListArraySpinner("Compromisso", lista))

            _vmSpnCalActLembrete.postValue(convListArraySpinner("Lembrete", lista))
        }


    }

    private fun auxTesteDecorator(tipo: String): MutableCollection<CalendarDay> {
        val allTipoEvento: MutableCollection<CalendarDay> = mutableListOf()
        val lista = auxTesteSpinner(CalendarDay.today(), false)
        lista.forEach {
            if (it.tipo == tipo) {
                allTipoEvento.add(
                        CalendarDay.from(
                                it.data.substring(4, 8).toInt(),
                                it.data.substring(2, 3).toInt(),
                                it.data.substring(0, 1).toInt()
                        )
                )
            }

        }
        return allTipoEvento
    }

    fun auxTesteSpinner(data: CalendarDay?, filtro: Boolean): List<EventoVO> {
        var lista: MutableList<EventoVO> = mutableListOf()
        var tipo = ""
        var count: Int
        for (index in 1..7) {
            if (index == 1 || index == 4) { tipo = "Feriado" }
            if (index == 2 || index == 5) { tipo = "Compromisso" }
            if (index == 3 || index == 6) { tipo = "Lembrete" }
            count = if (index == 7) { 3 } else { 1 }
            while (count > 0) {
                val itemEvento = EventoVO(
                        id = index + count,
                        titulo = "Teste $index - $tipo",
                        data = "$index/4/2021",
                        hora = "20:30",
                        tipo = tipo,
                        recorrencia = "Anual",
                        descricao = "Teste\ntestando\n123\nTestando"
                )
                if (count == 3) { tipo = "Compromisso" }
                if (count == 2) { tipo = "Feriado" }
                count--
                lista.add(itemEvento)
            }
        }
        val listaFiltrada: MutableList<EventoVO> = mutableListOf()

        lista.forEach {
            if (it.data == "${data?.day}/${data?.month}/${data?.year}") {
                listaFiltrada.add(it)
            }
        }
        if (filtro) { lista = listaFiltrada }
        return lista
    }
}