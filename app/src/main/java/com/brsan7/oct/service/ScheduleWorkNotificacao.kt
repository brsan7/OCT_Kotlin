package com.brsan7.oct.service

import androidx.work.*
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.utils.TempoUtils
import java.util.*
import java.util.concurrent.TimeUnit

private const val QUINZE_MINUTOS_MSEG = 15 * 60 * 1000L // 15 Minutos
private const val VINTE_QUATRO_HORAS_MSEG = 24 * 60 * 60 * 1000L // 1 Dia
private const val SETE_HORAS_MSEG = 7 * 60 * 60 * 1000L // 7 Horas

open class ScheduleWorkNotificacao() {

    fun setupNotificacaoDiaria() {

        val inicioDiaUtil = if (Calendar.getInstance().timeInMillis < SETE_HORAS_MSEG){
            SETE_HORAS_MSEG - Calendar.getInstance().timeInMillis
        }else{ (VINTE_QUATRO_HORAS_MSEG - Calendar.getInstance().timeInMillis) + SETE_HORAS_MSEG }

        val notificacaoWorkRequest: WorkRequest =
                PeriodicWorkRequestBuilder<NotificacaoDiariaWorker>(VINTE_QUATRO_HORAS_MSEG, TimeUnit.MILLISECONDS)
                        .setInitialDelay(inicioDiaUtil, TimeUnit.MILLISECONDS)
                        .addTag("setupNotificacaoDiaria")
                        .build()

        WorkManager
                .getInstance(OctApplication.instance)
                .enqueue(notificacaoWorkRequest)
    }

    fun setupNotificacaoProxEvt(proxAgendamento: List<EventoVO>) {

        val proxEvt = TempoUtils().proxEvento(proxAgendamento)

        if (proxEvt.hora.isNotEmpty()) {
            if (proxEvt.hora.split(":")[0].toInt() < 24) {

                val target: Calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, proxEvt.hora.split(":")[0].toInt())
                    set(Calendar.MINUTE, proxEvt.hora.split(":")[1].toInt())
                }

                val tempoProxEvento = (target.timeInMillis - Calendar.getInstance().timeInMillis) - QUINZE_MINUTOS_MSEG

                val notificacaoWorkRequest: OneTimeWorkRequest =
                        OneTimeWorkRequestBuilder<NotificacaoProxEventoWorker>()
                                .setInitialDelay(tempoProxEvento, TimeUnit.MILLISECONDS)
                                .addTag("setupProxEvtNotification")
                                .build()

                WorkManager
                        .getInstance(OctApplication.instance)
                        .enqueueUniqueWork(
                                "setupProxEvt",
                                ExistingWorkPolicy.REPLACE,
                                notificacaoWorkRequest
                        )
            }
        }
    }
}