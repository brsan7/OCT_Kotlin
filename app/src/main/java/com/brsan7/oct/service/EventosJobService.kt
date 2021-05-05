package com.brsan7.oct.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.widget.Toast
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.utils.TempoUtils
import com.brsan7.oct.utils.showNotification
import java.util.*

class EventosJobService: JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        Toast.makeText(this,"Teste Start JobService", Toast.LENGTH_SHORT).show()
        val horaDoDia = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        if (horaDoDia in 10..22){
            notificarProxEvento()
        }
        jobFinished(params,false)
        eventosJobScheduler()
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Toast.makeText(this,"Teste Stop JobService", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun notificarProxEvento(){
        Thread {
            try {
                val listaCompleta = OctApplication.instance.helperDB?.buscarEventos(
                        busca = "",
                        isBuscaPorData = false)
                        ?: mutableListOf()

                val proxEvt = TempoUtils().proxEvento(listaCompleta)
                OctApplication.instance.showNotification(
                        chanelId = "7",
                        title = proxEvt.titulo,
                        body = "${proxEvt.data}  ->  ${proxEvt.hora}"
                )

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }
}