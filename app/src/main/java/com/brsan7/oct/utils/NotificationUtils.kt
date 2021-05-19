package com.brsan7.oct.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat.*
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.brsan7.oct.MainActivity
import com.brsan7.oct.NotificationActivity
import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import java.util.*

lateinit var notificationChannel: NotificationChannel
lateinit var notificationManager: NotificationManager
@SuppressLint("StaticFieldLeak")
lateinit var notificationGeral: Builder

fun Context.showNotification(chanelId: String, title: String, body: String, bigText: String){
    val tipoToque: Uri
    val volumeTipo: Int
    val intent: Intent
    if (chanelId == "2200"){
        tipoToque = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        volumeTipo = AudioManager.STREAM_ALARM
        intent = Intent(this, NotificationActivity::class.java)
        intent.putExtra("body","$body $title")
        intent.putExtra("bigText",bigText)
    }
    else{
        tipoToque = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        volumeTipo = AudioManager.STREAM_NOTIFICATION
        intent = Intent(this, MainActivity::class.java)
    }

    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        notificationChannel = NotificationChannel(chanelId, body, NotificationManager.IMPORTANCE_HIGH).apply {
            enableVibration(true)
            enableLights(true)
            lightColor = Color.BLUE
            setSound(
                tipoToque,
                AudioAttributes.Builder()
                    .setLegacyStreamType(volumeTipo)
                    .build()
            )
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }
    notificationGeral = Builder(this, chanelId).apply {
        setSmallIcon(R.drawable.logo_oct_small)
        setContentTitle(title)
        setContentText(body)
        setSound(tipoToque,volumeTipo)
        setVibrate(longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500))
        priority = PRIORITY_MAX
        when(chanelId.toInt()){
            0 -> { //Sumário Notificação Todos Eventos do Dia
                setGroup("com.brsan7.oct.NOTIFICACAO_DIARIA")
                setGroupSummary(true)
                setStyle(InboxStyle().setSummaryText(OctApplication.instance.getString(R.string.menu1_1_Main)))
            }
            2200 -> { //notificarInicioCompromisso()
                if (bigText != ""){
                    setStyle(BigTextStyle().bigText(bigText))
                }
                setFullScreenIntent(pendingIntent, true)
                setVisibility(VISIBILITY_PUBLIC)
                setAutoCancel(false)
            }
            2202 -> { //notificarAproxCompromisso()
                setTimeoutAfter(60*1000L)// 1 Minuto
                setAutoCancel(true)
            }
            in 2210..2212 -> { //Notificação Todos Eventos do Dia
                if (bigText != ""){
                    setStyle(BigTextStyle().bigText(bigText))
                }
                setGroup("com.brsan7.oct.NOTIFICACAO_DIARIA")
                setContentIntent(pendingIntent)
                setAutoCancel(true)
            }
        }
    }
    notificationManager.notify(chanelId.toInt(), notificationGeral.build())
}

fun Context.createForegroundNotification(workId: UUID, title: String, body: String, bigText: String): ForegroundInfo {

    val intent = WorkManager.getInstance(OctApplication.instance)
            .createCancelPendingIntent(workId)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationChannel = NotificationChannel("2201", OctApplication.instance.getString(R.string.channel_proxCompromissoAtivo), NotificationManager.IMPORTANCE_LOW).apply {
            enableVibration(true)
            enableLights(true)
            lightColor = Color.BLUE
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    val foregroundNotification = Builder(OctApplication.instance, "2201").apply {
        setContentTitle(title)
        setContentText(body)
        if (bigText != ""){
            setStyle(BigTextStyle().bigText(bigText))
        }
        setSmallIcon(R.drawable.logo_oct_small)
        setOngoing(true)
        setVibrate(longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500))
        addAction(android.R.drawable.ic_notification_clear_all, "Cancelar", intent)
        priority = PRIORITY_LOW
    }

    return ForegroundInfo(2201,foregroundNotification.build())
}