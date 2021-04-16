package com.brsan7.oct.utils

import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlin.math.*

const val RAD = 57.295 //conv=1rad=57,3º
const val DECLINACAO_MAX_TERRA = 23.45
const val ORBITA_COMPLETA = 360.0
const val ALINHAMENTO_ANO_SOLAR = 284

open class SolarUtils {

    fun fotoPeriodo(
            latitude: Double,
            longitude: Double,
            fusoHorario: Int,
            diaJuliano: Int,
            ano: Int): Array<String>{

        val fotoperiodo = Array(3) {""}
        val nascente: Double
        val poente: Double
        val horasDeSol: Double
        val declinacaoTerrestre: Double
        val correcaoLongitude: Double

        val fuso = if (fusoHorario<0){fusoHorario*-15} else{fusoHorario*15}
        val isBissexto: Boolean = (( ano % 4 == 0 && ano % 100 != 0 ) || ano % 400 == 0)
        val diasDoAno: Int = if(isBissexto){ 366 } else{ 365 }

        declinacaoTerrestre =
                DECLINACAO_MAX_TERRA*sin(ORBITA_COMPLETA/diasDoAno*(diaJuliano+ALINHAMENTO_ANO_SOLAR)/RAD)
        horasDeSol=(2.0/15.0)*(acos(-tan(latitude/ RAD)*tan(declinacaoTerrestre/RAD))*RAD)
        correcaoLongitude = (((longitude-fuso)*60)/15)/60 //fração de hora na relação (fuso ~ longitude)
        nascente = (12-((horasDeSol/2)))+correcaoLongitude
        poente = (12+((horasDeSol/2)))+correcaoLongitude

        fotoperiodo[0] = "${convHorasHorario(horasDeSol).subSequence(0,2)}h" +
                         "${convHorasHorario(horasDeSol).subSequence(3,5)}m" +
                         "${convHorasHorario(horasDeSol).subSequence(6,8)}s" //FotoPeriodo Total
        fotoperiodo[1] = convHorasHorario(nascente) //Nascer do Sol
        fotoperiodo[2] = convHorasHorario(poente) //Por do Sol
        return fotoperiodo
    }

    fun estacaoDoAnoAtual(diaJuliano: Int, ano: Int,latitude: Double): String{

        val isBissexto: Boolean = (( ano % 4 == 0 && ano % 100 != 0 ) || ano % 400 == 0)
        val diasDoAno: Int = if(isBissexto){ 366 } else{ 365 }

        return if (latitude < 0){
            //Hemisfério Sul
            when (diaJuliano){
                in 1..79+(diasDoAno-365) -> "Verão"

                80+(diasDoAno-365) -> "Equinócio de Outono"
                in 81+(diasDoAno-365)..171+(diasDoAno-365) -> "Outono"

                172+(diasDoAno-365) -> "Solstício de Inverno"
                in 173+(diasDoAno-365)..264+(diasDoAno-365) -> "Inverno"

                265+(diasDoAno-365) -> "Equinócio de Primavera"
                in 266+(diasDoAno-365)..354+(diasDoAno-365) -> "Primavera"

                355+(diasDoAno-365) -> "Solstício de Verão"
                in 356+(diasDoAno-365)..diasDoAno -> "Verão"

                else -> ""
            }
        }
        else{
            //Hemisfério Norte
            when (diaJuliano){
                in 1..79+(diasDoAno-365) -> "Inverno"

                80+(diasDoAno-365) -> "Equinócio de Primavera"
                in 81+(diasDoAno-365)..171+(diasDoAno-365) -> "Primavera"

                172+(diasDoAno-365) -> "Solstício de Verão"
                in 173+(diasDoAno-365)..264+(diasDoAno-365) -> "Verão"

                265+(diasDoAno-365) -> "Equinócio de Outono"
                in 266+(diasDoAno-365)..354+(diasDoAno-365) -> "Outono"

                355+(diasDoAno-365) -> "Solstício de Inverno"
                in 356+(diasDoAno-365)..diasDoAno -> "Inverno"

                else -> ""
            }
        }
    }

    fun datasEstacoesDoAno(ano: Int): MutableList<CalendarDay>{

        val datas: MutableList<CalendarDay> = mutableListOf()
        val isBissexto: Boolean = (( ano % 4 == 0 && ano % 100 != 0 ) || ano % 400 == 0)
        val diasDoAno: Int = if(isBissexto){ 366 } else{ 365 }

        datas.add(CalendarDay.from(ano+0,3,21+(diasDoAno - 365)))
        datas.add(CalendarDay.from(ano+0,6,21+(diasDoAno - 365)))
        datas.add(CalendarDay.from(ano+0,9,22+(diasDoAno - 365)))
        datas.add(CalendarDay.from(ano+0,12,21+(diasDoAno - 365)))
        return datas
    }

    private fun convHorasHorario(horas: Double):String{

        var resto = horas
        var hora = 0
        var minuto = 0
        var segundo = 0

        while (resto > 1){
            hora++
            resto--
        }

        resto *= 60

        while (resto > 1){
            minuto++
            resto--
            if (minuto==60){
                hora++
                minuto=0
            }
        }

        resto *= 60

        while (resto > 1){
            segundo++
            resto--
            if (segundo==60){
                minuto++
                segundo=0
            }
        }

        var horarioComposto = if (hora >= 10){ "$hora:" } else { "0$hora:" }

        horarioComposto += if (minuto >= 10){ "$minuto:" } else { "0$minuto:" }

        horarioComposto += if (segundo >= 10){ "$segundo" } else { "0$segundo" }

        return horarioComposto
    }
}