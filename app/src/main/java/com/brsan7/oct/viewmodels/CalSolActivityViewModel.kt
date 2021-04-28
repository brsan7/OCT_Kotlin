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

    private var idItemSpinners: MutableList<IdItemSpinnersVO> = mutableListOf()
////////////////////////Spinners///////////////////////////////////////
////////////////////////Local Selecionado///////////////////////////////////////
    private var _vmCalSolarActDadosLocal = MutableLiveData<LocalVO>()
    val vmCalSolarActDadosLocal: LiveData<LocalVO>
        get() = _vmCalSolarActDadosLocal
////////////////////////Local Selecionado///////////////////////////////////////
////////////////////////Calendário///////////////////////////////////////
    private var _vmMcvCalSolarAct = MutableLiveData<MutableList<CalendarDay>>()
    val vmMcvCalSolarAct: LiveData<MutableList<CalendarDay>>
        get() = _vmMcvCalSolarAct
////////////////////////Calendário///////////////////////////////////////
    var reStartActivity = false

    private fun getIdRegistro(idSpinner: Int): Int {
        val idRegistro = idItemSpinners.find { idItemSpinnerVO ->
            idItemSpinnerVO.idSpinner == idSpinner
        }
        return idRegistro?.idRegistro ?: -1
    }

    fun getIdSpinner(idRegistro: Int): Int {
        val idSpinner = idItemSpinners.find { idItemSpinnerVO ->
            idItemSpinnerVO.idRegistro == idRegistro
        }
        return idSpinner?.idSpinner ?: -1
    }

    fun getAllEstacoesAno() {
        _vmMcvCalSolarAct.postValue(SolarUtils().datasEstacoesDoAno(ano = Calendar.getInstance()[Calendar.YEAR]))
    }

    fun getAllLocais(localDefault: LocalVO) {

        if (!reStartActivity) {
            _vmCalSolarActDadosLocal.postValue(localDefault)
            var lista: List<LocalVO>
            Thread {
                try {
                    lista = OctApplication.instance.helperDB?.buscarLocais(
                            busca = "",
                            isBuscaPorId = false
                    ) ?: mutableListOf()

                    _vmSpnCalSolarActLocal.postValue(convListArraySpnLocalVO(lista))
                }
                catch (ex: Exception) { ex.printStackTrace() }
            }.start()
        }
    }

    fun getDadosLocalSelecionado(idSpinner: Int) {

        if (!reStartActivity && idSpinner > 0) {
            var lista: List<LocalVO>
            Thread {
                try {
                    lista = OctApplication.instance.helperDB?.buscarLocais(
                            busca = "${getIdRegistro(idSpinner)}",
                            isBuscaPorId = true
                    ) ?: mutableListOf()

                    _vmCalSolarActDadosLocal.postValue(lista.first())
                }
                catch (ex: Exception) { ex.printStackTrace() }
            }.start()
        }
    }

    private fun convListArraySpnLocalVO(lista: List<LocalVO>): Array<String> {
        val itensArray = Array(lista.size+1) { "" }
        itensArray[0] = "Locais"

        for (index in lista.indices) {
            itensArray[index+1] = lista[index].titulo
            idItemSpinners.add(
                    IdItemSpinnersVO(
                            idSpinner = index+1,
                            idRegistro = lista[index].id,
                            tipo = "Local"
                    )
            )
        }
        return itensArray
    }
}