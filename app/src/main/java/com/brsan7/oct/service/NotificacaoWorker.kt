package com.brsan7.oct.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.utils.TempoUtils
import com.brsan7.oct.utils.showNotification
import java.util.*

private var evtsDoDiaFeriado: MutableList<EventoVO> = mutableListOf()
private var evtsDoDiaCompromisso: MutableList<EventoVO> = mutableListOf()
private var evtsDoDiaLembrete: MutableList<EventoVO> = mutableListOf()

class NotificacaoDiariaWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        verificarNotificarEventosDoDia("NotificacaoDiariaWorker")
        return Result.success()
    }
}
class NotificacaoProxEventoWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        verificarNotificarEventosDoDia("NotificacaoProxEventoWorker")
        return Result.success()
    }
}

private fun verificarNotificarEventosDoDia(tipo: String){
    Thread {
        try {
            ////////////////ATUALIZAR////////////////
            val listaCompleta = OctApplication.instance.helperDB?.buscarEventos(
                    busca = "",
                    isBuscaPorData = false)
                    ?: mutableListOf()

            val evtsAtualizados = TempoUtils().atualizarEventos(listaCompleta)

            for (index in evtsAtualizados.indices){
                if (evtsAtualizados[index].recorrencia == "Finalizado"){
                    OctApplication.instance.helperDB?.deletarEvento(evtsAtualizados[index].id)
                }
                else{
                    OctApplication.instance.helperDB?.modificarEvento(evtsAtualizados[index])
                }
            }
            ////////////////ATUALIZAR////////////////

            ////////////////BUSCAR////////////////
            val hoje = Calendar.getInstance()
            val data = "${hoje[Calendar.DAY_OF_MONTH]}/${hoje[Calendar.MONTH]+1}/${hoje[Calendar.YEAR]}"
            val evtsDoDia = OctApplication.instance.helperDB?.buscarEventos(
                    busca = data,
                    isBuscaPorData = true)
                    ?: mutableListOf()
            ////////////////BUSCAR////////////////

            when(tipo){
                "NotificacaoDiariaWorker" -> {

                    filtrarEventosPorTipo(evtsDoDia)
                    if (evtsDoDiaCompromisso.isNotEmpty()){ notificarCompromissosDoDia(evtsDoDiaCompromisso) }
                    if (evtsDoDiaLembrete.isNotEmpty()){ notificarLembretesDoDia(evtsDoDiaLembrete) }
                    if (evtsDoDiaFeriado.isNotEmpty()){ notificarFeriadosDoDia(evtsDoDiaFeriado) }
                }
                "NotificacaoProxEventoWorker" -> {
                    if (evtsDoDia.isNotEmpty()){ notificarProxCompromisso(evtsDoDia) }
                }
            }


        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }.start()
}

private fun notificarFeriadosDoDia(feriado: List<EventoVO>){
    if (feriado.size == 1){
        OctApplication.instance.showNotification(
                chanelId = "10",
                title = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoFeriado),
                body = feriado.first().titulo
        )
    }
    else {
        OctApplication.instance.showNotification(
                chanelId = "10",
                title = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoFeriado),
                body = "+${feriado.size} -> ${feriado.first().titulo}"
        )
    }
}

private fun notificarLembretesDoDia(lembrete: List<EventoVO>){
    if (lembrete.size == 1){
        OctApplication.instance.showNotification(
                chanelId = "20",
                title = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoLembrete),
                body = lembrete.first().titulo
        )
    }
    else {
        OctApplication.instance.showNotification(
                chanelId = "20",
                title = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoLembrete),
                body = "+${lembrete.size} -> ${lembrete.first().titulo}"
        )
    }
}

private fun notificarCompromissosDoDia(compromisso: List<EventoVO>){
    if (compromisso.size == 1){
        OctApplication.instance.showNotification(
                chanelId = "30",
                title = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoCompromisso),
                body = "${compromisso.first().hora} -> ${compromisso.first().titulo}"
        )
    }
    else {
        val proxEvt = TempoUtils().proxEvento(compromisso)
        OctApplication.instance.showNotification(
                chanelId = "30",
                title = OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoCompromisso),
                body = "+${compromisso.size} | ${proxEvt.hora} -> ${proxEvt.titulo}"
        )
    }
    ScheduleWorkNotificacao().setupNotificacaoProxEvt(compromisso)
}

private fun notificarProxCompromisso(evtsDoDia: List<EventoVO>){
    val proxEvt = TempoUtils().proxEvento(evtsDoDia)
    if (proxEvt.hora.split(":")[0].toInt() < 24) {
        OctApplication.instance.showNotification(
                chanelId = "40",
                title = OctApplication.instance.getString(R.string.aviso_ProxCompromissoNotification),
                body = "${proxEvt.hora} -> ${proxEvt.titulo}"
        )
    }
    if (evtsDoDia.size > 1){
        val proxAgendamento: MutableList<EventoVO>  = mutableListOf()
        evtsDoDia.forEach {
            if (proxEvt != it){
                proxAgendamento.add(it)
            }
        }
        ScheduleWorkNotificacao().setupNotificacaoProxEvt(proxAgendamento)
    }
}

private fun filtrarEventosPorTipo(evtsDoDia: List<EventoVO>){
    evtsDoDiaFeriado = mutableListOf()
    evtsDoDiaCompromisso = mutableListOf()
    evtsDoDiaLembrete = mutableListOf()
    evtsDoDia.forEach {
        if (it.tipo == OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoFeriado)){
            evtsDoDiaFeriado.add(it)
        }
        if (it.tipo == OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoLembrete)){
            evtsDoDiaLembrete.add(it)
        }
        if (it.tipo == OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoCompromisso)){
            evtsDoDiaCompromisso.add(it)
        }
    }
}