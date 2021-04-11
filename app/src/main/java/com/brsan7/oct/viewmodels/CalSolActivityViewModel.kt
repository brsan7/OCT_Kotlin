package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.model.IdItemSpinnersVO
import com.brsan7.oct.model.LocalVO
import com.prolificinteractive.materialcalendarview.CalendarDay

class CalSolActivityViewModel: ViewModel() {


////////////////////////Spinners///////////////////////////////////////
    private var _vmSpnCalSolarActLocal = MutableLiveData<Array<String>>()
    val vmSpnCalSolarActLocal: LiveData<Array<String>>
        get() = _vmSpnCalSolarActLocal


    var idItemSpinners: MutableList<IdItemSpinnersVO> = mutableListOf()
    var diaSelecionado: CalendarDay = CalendarDay.today()
    var reStartActivity = false
////////////////////////Spinners///////////////////////////////////////
////////////////////////TextView///////////////////////////////////////
    private var _vmCalSolarActNascPoen = MutableLiveData<Array<String>>()
    val vmCalSolarActNascPoen: LiveData<Array<String>>
        get() = _vmCalSolarActNascPoen
////////////////////////TextView///////////////////////////////////////
////////////////////////Calendário///////////////////////////////////////
    private var _vmMcvCalSolarAct = MutableLiveData<Array<String>>()
    val vmMcvCalSolarAct: LiveData<Array<String>>
        get() = _vmMcvCalSolarAct
////////////////////////Calendário///////////////////////////////////////

    fun getAllEstacoesAno() {
        if (_vmMcvCalSolarAct.value?.size == null) {

            _vmMcvCalSolarAct.postValue(auxTesteDecorateEstacoes())
        }
    }

    fun getIdRegistro(tipo: String, index: Int): Int {
        val id = idItemSpinners.find { idItemSpinnerVO ->
            idItemSpinnerVO.tipo == tipo
                    && idItemSpinnerVO.idSpinner == index
        }
        return id?.idRegistro ?: -1
    }

    fun getAllLocais() {

        if (!reStartActivity) {
            val arrayLocais = convListArraySpnLocalVO(auxTesteRecyclerView())

            _vmSpnCalSolarActLocal.postValue(arrayLocais)
        }


    }


    fun convListArraySpnLocalVO(lista: List<LocalVO>): Array<String> {
        val itensArray = Array(lista.size) { "" }
        for (index in lista.indices) {
            itensArray[index] = lista[index].titulo
            idItemSpinners.add(
                    IdItemSpinnersVO(
                            idSpinner = index,
                            idRegistro = lista[index].id,
                            tipo = "Local"
                    )
            )
        }
        return itensArray
    }

    /*private fun convArrayColletionDecorateEstacoes(arrayEstacoes: Array<String>): MutableCollection<CalendarDay> {
        val collectionEstacoes: MutableCollection<CalendarDay> = mutableListOf()

        for (index in arrayEstacoes.indices){
            collectionEstacoes.add(CalendarDay.from(
                    arrayEstacoes[index].split("/")[2].toInt(),//ano
                    arrayEstacoes[index].split("/")[1].toInt(),//mes
                    arrayEstacoes[index].split("/")[0].toInt()//dia
            ))
        }
        return collectionEstacoes
    }*/

    private fun auxTesteRecyclerView(): List<LocalVO>{
        val lista: MutableList<LocalVO> = mutableListOf()
        for (index in 0..7) {
            val itemLocal = LocalVO(
                    id = index,
                    titulo = "Local $index",
                    latitude = "-23.123",
                    longitude = "-45.123",
                    descricao = "Teste\ntestando\n123\nTestando"
            )
            lista.add(itemLocal)
        }
        return lista
    }
    private fun auxTesteDecorateEstacoes(): Array<String>{
        val estacoes = Array(4) { "" }
        var count = 1
        for (index in estacoes.indices){
            estacoes[index] = count.toString()//utilizar o mês na implementação
            count+=3
        }
        return estacoes
    }
}