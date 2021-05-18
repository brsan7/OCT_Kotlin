package com.brsan7.oct.service

import android.content.Context
import androidx.work.*
import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.utils.*
import com.google.common.util.concurrent.ListenableFuture
import java.util.*

private var evtsDoDiaFeriado: MutableList<EventoVO> = mutableListOf()
private var evtsDoDiaCompromisso: MutableList<EventoVO> = mutableListOf()
private var evtsDoDiaLembrete: MutableList<EventoVO> = mutableListOf()

class NotificacaoDiariaWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        atualizarEventos()
        val evtsDoDia = buscarEventosDoDia()
        filtrarEventosPorTipo(evtsDoDia)
        if (evtsDoDia.isNotEmpty()){ notificarEventosDoDia("0",evtsDoDia) }
        if (evtsDoDiaCompromisso.isNotEmpty()){ notificarEventosDoDia("2212",evtsDoDiaCompromisso) }
        if (evtsDoDiaLembrete.isNotEmpty()){ notificarEventosDoDia("2211",evtsDoDiaLembrete) }
        if (evtsDoDiaFeriado.isNotEmpty()){ notificarEventosDoDia("2210",evtsDoDiaFeriado) }
        ScheduleWorkNotificacao().setupNotificacaoDiaria()
        if (evtsDoDiaCompromisso.isNotEmpty()){ ScheduleWorkNotificacao().startForegroudService() }
        return Result.success()
    }
}

class ForegroudServiceWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val workStatus = WorkManager.getInstance(OctApplication.instance).getWorkInfoById(id)
        var evtsDoDia = buscarEventosDoDia()
        val proxEvt = TempoUtils().proxEvento(evtsDoDia)
        setForeground(
                OctApplication.instance.createForegroundNotification(
                        workId = id,
                        title = OctApplication.instance.getString(R.string.aviso_ProxCompromissoNotification),
                        body = "${proxEvt.hora} -> ${proxEvt.titulo}",
                        bigText = "${proxEvt.hora} -> ${proxEvt.titulo}\n\n${OctApplication.instance.getString(R.string.hint_etRegAgActDescricao)}:\n${proxEvt.descricao}"
                )
        )
        return try {
            verificarNotificarAproxCompromisso(tempoRestanteProxEvt(), workStatus)
            if(workStatus.isCancelled){ Result.failure() }
            else {
                notificarInicioCompromisso(proxEvt)
                atualizarEventos()
                evtsDoDia = buscarEventosDoDia()
                filtrarEventosPorTipo(evtsDoDia)
                if (evtsDoDiaCompromisso.isNotEmpty()) {
                    ScheduleWorkNotificacao().startForegroudService()
                }
                Result.success()
            }
        }
        catch (throwable: Throwable) {
            Result.failure()
        }
    }
}

private fun verificarNotificarAproxCompromisso(tempoRestanteMseg: Long,
                                               workStatus: ListenableFuture<WorkInfo>){

    val quinzeMinMseg = 15 * 60 * 1000L
    val meiaHoraAntes = tempoRestanteMseg - (quinzeMinMseg*2)
    val quinzeMinAntes = tempoRestanteMseg - quinzeMinMseg

    if (meiaHoraAntes > 0){
        Thread.sleep(meiaHoraAntes)
        if(workStatus.isCancelled){ return }
        else{ notificarAproxCompromisso(30) }
        Thread.sleep(quinzeMinMseg)
        if(workStatus.isCancelled){ return }
        else{ notificarAproxCompromisso(15) }
        Thread.sleep(quinzeMinMseg)
    }
    else {
        if (quinzeMinAntes > 0) {
            Thread.sleep(quinzeMinAntes)
            if(workStatus.isCancelled){ return }
            else{ notificarAproxCompromisso(15) }
            Thread.sleep(quinzeMinMseg)
        }
        else{Thread.sleep(tempoRestanteMseg)}
    }
}

private fun notificarEventosDoDia(chanelId: String, evtsTipo: List<EventoVO>){

    val titulo =
            if (evtsTipo.first().hora.isEmpty()){ evtsTipo.first().titulo }
            else { "${evtsTipo.first().hora} -> ${evtsTipo.first().titulo}" }

    if (evtsTipo.size == 1){
        OctApplication.instance.showNotification(
                chanelId = chanelId,
                title = evtsTipo.first().tipo,
                body = titulo,
                bigText = ""
        )
    }
    else {
        OctApplication.instance.showNotification(
                chanelId = chanelId,
                title = evtsTipo.first().tipo,
                body = "+${evtsTipo.size-1}_$titulo",
                bigText = ""
        )
    }
}

private fun notificarAproxCompromisso(tempoRestante: Int){
//private fun notificarAproxCompromisso(proxEvt: EventoVO,tempoRestante: Int){
    OctApplication.instance.showNotification(
        chanelId = "2202",
        title = OctApplication.instance.getString(R.string.aviso_ProxCompromissoNotification),
        body = OctApplication.instance.getString(R.string.txt_AproxCompromisso1)+" $tempoRestante "+OctApplication.instance.getString(R.string.txt_AproxCompromisso2),
        //bigText = "${proxEvt.hora} -> ${proxEvt.titulo}\n\n${OctApplication.instance.getString(R.string.hint_etRegAgActDescricao)}:\n${proxEvt.descricao}"
        bigText = ""
    )
}

private fun notificarInicioCompromisso(proxEvt: EventoVO){
    OctApplication.instance.showNotification(
            chanelId = "2200",
            title = proxEvt.titulo,
            body = "${proxEvt.hora} -> ${proxEvt.descricao}",
            bigText = "${proxEvt.hora} -> ${proxEvt.titulo}\n\n${OctApplication.instance.getString(R.string.hint_etRegAgActDescricao)}:\n${proxEvt.descricao}"
    )
}

private fun atualizarEventos(){
    try {
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
    }
    catch (ex: Exception) {
        ex.printStackTrace()
    }
}

private fun buscarEventosDoDia():List<EventoVO>{
    return try {
        val hoje = Calendar.getInstance()
        val data = "${hoje[Calendar.DAY_OF_MONTH]}/${hoje[Calendar.MONTH] + 1}/${hoje[Calendar.YEAR]}"
        OctApplication.instance.helperDB?.buscarEventos(
                busca = data,
                isBuscaPorData = true)
                ?: mutableListOf()
    }
    catch (ex: Exception) {
        ex.printStackTrace()
        mutableListOf()
    }
}

private fun tempoRestanteProxEvt(): Long{
    val proxEvt = TempoUtils().proxEvento(buscarEventosDoDia())
    val agora = Calendar.getInstance()
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY,proxEvt.hora.split(":")[0].toInt())
        set(Calendar.MINUTE,proxEvt.hora.split(":")[1].toInt())
        set(Calendar.SECOND,0)
    }.timeInMillis - agora.timeInMillis
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