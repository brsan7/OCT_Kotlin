package com.brsan7.oct.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.utils.TempoUtils
import com.brsan7.oct.utils.showNotification
import java.util.*

class NotificacaoBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            "android.intent.action.BOOT_COMPLETED" -> { setupGeralNotification() }
            "STARTUP_EVENTOS_DO_DIA" -> { notificacaoGeralEventosDoDia() }
            "NOTIFICATION_PROX_EVENTO" -> { notificarProxEvento() }
        }
    }

    fun agendarProxEvento(proxAgendamento: List<EventoVO>){
        val proxEvt = TempoUtils().proxEvento(proxAgendamento)
        val target: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, proxEvt.hora.split(":")[0].toInt())
            set(Calendar.MINUTE, proxEvt.hora.split(":")[1].toInt())
        }
        setupProxEvtNotification(target.timeInMillis)
    }

    private fun notificarProxEvento(){
        Thread {
            try {
                val hoje = Calendar.getInstance()
                val data = "${hoje[Calendar.DAY_OF_MONTH]}/${hoje[Calendar.MONTH]+1}/${hoje[Calendar.YEAR]}"
                val evtsDoDia = OctApplication.instance.helperDB?.buscarEventos(
                        busca = data,
                        isBuscaPorData = true)
                        ?: mutableListOf()

                val proxEvt = TempoUtils().proxEvento(evtsDoDia)
                OctApplication.instance.showNotification(
                        chanelId = "40",
                        title = proxEvt.titulo,
                        body = "${proxEvt.data}  ->  ${proxEvt.hora}"
                )
                if (evtsDoDia.size > 1){
                    val proxAgendamento: MutableList<EventoVO>  = mutableListOf()
                    evtsDoDia.forEach {
                        if (proxEvt != it){
                            proxAgendamento.add(it)
                        }
                    }
                    agendarProxEvento(proxAgendamento)
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }

    private fun notificacaoGeralEventosDoDia(){
        Thread {
            try {
                val hoje = Calendar.getInstance()
                val data = "${hoje[Calendar.DAY_OF_MONTH]}/${hoje[Calendar.MONTH]+1}/${hoje[Calendar.YEAR]}"
                val listaFiltrada = OctApplication.instance.helperDB?.buscarEventos(
                        busca = data,
                        isBuscaPorData = true)
                        ?: mutableListOf()

                val feriado: MutableList<EventoVO>  = mutableListOf()
                val lembrete: MutableList<EventoVO>  = mutableListOf()
                val compromisso: MutableList<EventoVO>  = mutableListOf()
                listaFiltrada.forEach {
                    if (it.tipo == OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoFeriado)){
                        feriado.add(it)
                    }
                    if (it.tipo == OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoLembrete)){
                        lembrete.add(it)
                    }
                    if (it.tipo == OctApplication.instance.getString(R.string.txt_spnRegEvtActTipoCompromisso)){
                        compromisso.add(it)
                    }
                }

                notificarCompromisso(compromisso)
                notificarLembrete(lembrete)
                notificarFeriado(feriado)

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }

    private fun notificarFeriado(feriado: List<EventoVO>){
        if (feriado.isNotEmpty()){

            if (feriado.size == 1){
                OctApplication.instance.showNotification(
                        chanelId = "10",
                        title = "Feriado do Dia",
                        body = feriado.first().titulo
                )
            }
            else {
                OctApplication.instance.showNotification(
                        chanelId = "10",
                        title = "Feriados do Dia",
                        body = "${feriado.size} - Feriados"
                )
            }
        }
    }
    private fun notificarLembrete(lembrete: List<EventoVO>){
        if (lembrete.isNotEmpty()){

            if (lembrete.size == 1){
                OctApplication.instance.showNotification(
                        chanelId = "20",
                        title = "Lembrete do Dia",
                        body = lembrete.first().titulo
                )
            }
            else {
                OctApplication.instance.showNotification(
                        chanelId = "20",
                        title = "Lembretes do Dia",
                        body = "${lembrete.size} - Lembretes"
                )
            }
        }
    }
    private fun notificarCompromisso(compromisso: List<EventoVO>){
        if (compromisso.isNotEmpty()){

            if (compromisso.size == 1){
                OctApplication.instance.showNotification(
                        chanelId = "30",
                        title = "Compromisso do Dia",
                        body = compromisso.first().titulo
                )
            }
            else {
                OctApplication.instance.showNotification(
                        chanelId = "30",
                        title = "Compromissos do Dia",
                        body = "${compromisso.size} - Compromissos"
                )
            }
            agendarProxEvento(compromisso)
        }
    }
}