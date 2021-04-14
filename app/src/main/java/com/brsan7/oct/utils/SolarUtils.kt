package com.brsan7.oct.utils

import kotlin.math.*

const val RAD = 57.295 //conv=1rad=57,3º
const val DECLINACAO_MAX_TERRA = 23.45
const val ORBITA_COMPLETA = 360.0

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

        declinacaoTerrestre = DECLINACAO_MAX_TERRA * sin(ORBITA_COMPLETA / diasDoAno * (diaJuliano + 284) / RAD)
        horasDeSol=(2.0/15.0)*(acos(-tan(latitude/ RAD)*tan(declinacaoTerrestre/RAD))*RAD)
        correcaoLongitude = (((longitude-fuso)*60)/15)/60 //fração de hora na relação (fuso ~ longitude)
        nascente = (12-((horasDeSol/2)))+correcaoLongitude
        poente = (12+((horasDeSol/2)))+correcaoLongitude

        fotoperiodo[0] = convHorasHorario(horasDeSol) //FotoPeriodo Total
        fotoperiodo[1] = convHorasHorario(nascente) //Nascer do Sol
        fotoperiodo[2] = convHorasHorario(poente) //Por do Sol
        return fotoperiodo
    }

    fun estacoesDoAno(diaJuliano: Int, ano: Int): String{
        var estacao = ""
        //val declinacaoTerrestre: Double
        val isBissexto: Boolean = (( ano % 4 == 0 && ano % 100 != 0 ) || ano % 400 == 0)
        val diasDoAno: Int = if(isBissexto){ 366 } else{ 365 }

        //declinacaoTerrestre = DECLINACAO_MAX_TERRA * sin(ORBITA_COMPLETA / diasDoAno * (diaJuliano + 284) / RAD)
        when (diaJuliano) {
            in 1..79+(diasDoAno-365) -> {estacao="Verão"}
            in 80+(diasDoAno-365)..171+(diasDoAno-365) -> {estacao="Outono"}
            in 172+(diasDoAno-365)..264+(diasDoAno-365) -> {estacao="Inverno"}
            in 265+(diasDoAno-365)..354+(diasDoAno-365) -> {estacao="Primavera"}
            in 355+(diasDoAno-365)..diasDoAno -> {estacao="Verão"}
        }


        return estacao
    }

    fun convHorasHorario(horas: Double):String{
        var resto = horas
        var hora = 0
        var minuto = 0
        var segundo = 0
        var horarioComposto: String

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
        if (hora >= 10){horarioComposto = "$hora:"}
        else{horarioComposto = "0$hora:"}

        if (minuto >= 10){horarioComposto += "$minuto:"}
        else{horarioComposto += "0$minuto:"}

        if (segundo >= 10){horarioComposto += "$segundo"}
        else{horarioComposto += "0$segundo"}

        return horarioComposto
    }
}