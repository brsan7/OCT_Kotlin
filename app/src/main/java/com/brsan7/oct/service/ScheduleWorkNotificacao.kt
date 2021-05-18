package com.brsan7.oct.service

import androidx.work.*
import com.brsan7.oct.application.OctApplication
import java.util.*
import java.util.concurrent.TimeUnit

private const val VINTE_QUATRO_HORAS_MINUTOS = 24 * 60L // 1 Dia
private const val SETE_HORAS_MINUTOS = 7 * 60L // 7 Horas

open class ScheduleWorkNotificacao {

    fun setupNotificacaoDiaria() {

        val agora = Calendar.getInstance()
        val agoraEmMinutos = (agora[Calendar.HOUR_OF_DAY]*60L)+agora[Calendar.MINUTE]
        val inicioDiaComercial =
                (VINTE_QUATRO_HORAS_MINUTOS - agoraEmMinutos) + SETE_HORAS_MINUTOS

        val notificacaoWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<NotificacaoDiariaWorker>()
                        .setInitialDelay(inicioDiaComercial, TimeUnit.MINUTES)
                        .addTag("setupNotificacaoDiaria")
                        .build()

        WorkManager
                .getInstance(OctApplication.instance)
                .cancelAllWorkByTag("setupNotificacaoDiaria")

        WorkManager
                .getInstance(OctApplication.instance)
                .enqueue(notificacaoWorkRequest)
    }

    fun startForegroudService() {

        val notificacaoWorkRequest =
                OneTimeWorkRequestBuilder<ForegroudServiceWorker>().build()

        WorkManager
                .getInstance(OctApplication.instance)
                .enqueueUniqueWork(
                        "startForegroudService",
                        ExistingWorkPolicy.REPLACE,
                        notificacaoWorkRequest
                )
    }
}