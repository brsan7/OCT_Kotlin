package com.brsan7.oct.service

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.brsan7.oct.application.OctApplication

private const val INTERVALO_VERIFICACAO_MSEG = 1 * 60 * 60 * 1000L // 1 hora
private const val JOB_ID = 2211
//private var sInitialize = false

@Synchronized
fun eventosJobScheduler() {
    //if (sInitialize) return

    val jobInfo = JobInfo.Builder(JOB_ID, ComponentName(OctApplication.instance, EventosJobService::class.java))
    val job = jobInfo
            //.setBackoffCriteria(INTERVALO_VERIFICACAO_MSEG,JobInfo.BACKOFF_POLICY_LINEAR)
            .setMinimumLatency(INTERVALO_VERIFICACAO_MSEG)
            .setOverrideDeadline(INTERVALO_VERIFICACAO_MSEG)
            //.setPeriodic(INTERVALO_VERIFICACAO_MSEG)
            .build()

    val jobScheduler = OctApplication.instance.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    jobScheduler.schedule(job)

    //sInitialize = true
}