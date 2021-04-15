package com.brsan7.oct.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.IdItemSpinnersVO
import com.brsan7.oct.model.LocalVO
import com.brsan7.oct.utils.SolarUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.*

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
    private var _vmCalSolarActDadosLocal = MutableLiveData<LocalVO>()
    val vmCalSolarActDadosLocal: LiveData<LocalVO>
        get() = _vmCalSolarActDadosLocal
////////////////////////TextView///////////////////////////////////////
////////////////////////Calendário///////////////////////////////////////
    private var _vmMcvCalSolarAct = MutableLiveData<MutableList<CalendarDay>>()
    val vmMcvCalSolarAct: LiveData<MutableList<CalendarDay>>
        get() = _vmMcvCalSolarAct
////////////////////////Calendário///////////////////////////////////////

    fun getAllEstacoesAno() {
        if (_vmMcvCalSolarAct.value?.size == null) {

            _vmMcvCalSolarAct.postValue(SolarUtils().datasEstacoesDoAno(Calendar.getInstance()[1]+0))
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
            var lista: List<LocalVO>
            Thread {
                try {
                    lista = OctApplication.instance.helperDB?.buscarLocais("",false) ?: mutableListOf()

                    _vmSpnCalSolarActLocal.postValue(convListArraySpnLocalVO(lista))
                }
                catch (ex: Exception) { ex.printStackTrace() }
            }.start()
        }
    }

    fun getDadosLocalSelecionado(idSpinner: Int) {

        if (!reStartActivity) {
            var lista: List<LocalVO>
            Thread {
                try {
                    lista = OctApplication.instance.helperDB?.buscarLocais(
                            "${idItemSpinners[idSpinner].idRegistro}",
                            true) ?: mutableListOf()

                    _vmCalSolarActDadosLocal.postValue(lista.first())
                }
                catch (ex: Exception) { ex.printStackTrace() }
            }.start()
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

}