package com.brsan7.oct.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.brsan7.oct.application.OctApplication
import java.util.*

private const val UM_QUARTO_DE_HORA_MSEG = 15 * 60 * 1000L // 15 Minutos

fun setupGeralNotification() {
    val target: Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 7)
        set(Calendar.MINUTE, 0)
    }
    val intent = Intent("STARTUP_EVENTOS_DO_DIA")
    val pIntent = PendingIntent.getBroadcast(OctApplication.instance, 0, intent, 0)
    val alarme = OctApplication.instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarme.setRepeating(
            AlarmManager.RTC_WAKEUP,
            target.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pIntent
    )
}

fun setupProxEvtNotification(evtTimeInMillis: Long) {
    val intent = Intent("NOTIFICATION_PROX_EVENTO")
    val pIntent = PendingIntent.getBroadcast(OctApplication.instance, 0, intent, 0)
    val alarme = OctApplication.instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarme.set(
            AlarmManager.RTC_WAKEUP,
            evtTimeInMillis - UM_QUARTO_DE_HORA_MSEG,
            pIntent
    )
}