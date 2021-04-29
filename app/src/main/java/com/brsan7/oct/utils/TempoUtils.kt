package com.brsan7.oct.utils

import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.EventoVO
import java.util.*


open class TempoUtils {

    private fun isBissexto(ano: Int): Boolean = (( ano % 4 == 0 && ano % 100 != 0 ) || ano % 400 == 0)

    private fun isObsoleto(evento: EventoVO): Boolean{

        val dia = evento.data.split("/")[0].toInt()
        val mes = evento.data.split("/")[1].toInt()
        val ano = evento.data.split("/")[2].toInt()
        var hora = 24
        var minuto = 60
        if (evento.hora.isNotEmpty()) {
            hora = evento.hora.split(":")[0].toInt()
            minuto = evento.hora.split(":")[1].toInt()
        }
        val hoje = Calendar.getInstance()

        return when{
            ano<hoje[Calendar.YEAR] -> true
            ano>hoje[Calendar.YEAR] -> false
            else -> {
                when{
                    mes<hoje[Calendar.MONTH]+1 -> true
                    mes>hoje[Calendar.MONTH]+1 -> false
                    else -> {
                        when{
                            dia<hoje[Calendar.DAY_OF_MONTH] -> true
                            dia>hoje[Calendar.DAY_OF_MONTH] -> false
                            else -> {
                                when{
                                    hora<hoje[Calendar.HOUR_OF_DAY] -> true
                                    hora>hoje[Calendar.HOUR_OF_DAY] -> false
                                    else -> {
                                        when{
                                            minuto<hoje[Calendar.MINUTE] -> true
                                            minuto>hoje[Calendar.MINUTE] -> false
                                            else -> false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun diaSemanaToInt(diaSemana: String): Int{
        return when(diaSemana){
            OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemDom).substring(0..2) -> {1}
            OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemSeg).substring(0..2) -> {2}
            OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemTer).substring(0..2) -> {3}
            OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemQua).substring(0..2) -> {4}
            OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemQui).substring(0..2) -> {5}
            OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemSex).substring(0..2) -> {6}
            OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemSab).substring(0..2) -> {7}
            else -> {0}
        }
    }

    fun getStringDiaSemana(data: String) : String{
        val calendardata = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, data.split("/")[0].toInt())
            set(Calendar.MONTH, data.split("/")[1].toInt()-1)
            set(Calendar.YEAR, data.split("/")[2].toInt())
        }
        return when(calendardata[Calendar.DAY_OF_WEEK]){
            1 -> { OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemDom).substring(0..2) }
            2 -> { OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemSeg).substring(0..2) }
            3 -> { OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemTer).substring(0..2) }
            4 -> { OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemQua).substring(0..2) }
            5 -> { OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemQui).substring(0..2) }
            6 -> { OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemSex).substring(0..2) }
            7 -> { OctApplication.instance.getString(R.string.label_rbtnDiagSelDiaSemSab).substring(0..2) }
            else -> { "" }
        }
    }

    fun atualizarEventos(evtsSelecionado: List<EventoVO>): List<EventoVO>{

        val eventosAtualizado: MutableList<EventoVO>  = mutableListOf()

        for(index in evtsSelecionado.indices){
            if(isObsoleto(evtsSelecionado[index])){
                eventosAtualizado.add(
                        projecaoEventos(evtsSelecionado[index])
                )
            }
        }
        return eventosAtualizado
    }

    private fun projecaoEventos(evtObsoleto: EventoVO): EventoVO {

        return when(evtObsoleto.recorrencia.split("_")[0]){
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaUnico) -> {

                EventoVO(id=evtObsoleto.id,recorrencia = "Finalizado")
            }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaSmnFixo) -> { projecaoSemanalFixo(evtObsoleto) }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaSmnDin) -> { projecaoSemanalDinamico(evtObsoleto) }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaMnsFixo) -> { projecaoMensalFixo(evtObsoleto) }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaMnsDin) -> { projecaoMensalDinamico(evtObsoleto) }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaAnualFixo) -> { projecaoAnualFixo(evtObsoleto) }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaAnualDin) -> { projecaoAnualDinamico(evtObsoleto) }
            OctApplication.instance.getString(R.string.txt_spnRegEvtActRecorrenciaPeriodico) -> { //Periódico

                return when (evtObsoleto.recorrencia.split("_")[1].split("*")[1]){
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoAnos) -> { projecaoAnosPeriodico(evtObsoleto) }
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoMeses) -> { projecaoMesesPeriodico(evtObsoleto) }
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoSemanas) -> { projecaoSemanasPeriodico(evtObsoleto) }
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoDiasCorridos) -> { projecaoDiasCorridoPeriodico(evtObsoleto) }
                    OctApplication.instance.getString(R.string.txt_spnRegEvtActModoDiasUteis) -> { projecaoDiasUteisPeriodico(evtObsoleto) }
                    else -> { EventoVO() }
                }
            }
            else -> EventoVO()
        }
    }

    private fun projecaoSemanalFixo(evtObsoleto: EventoVO): EventoVO{
        var evtAtualizado = EventoVO()

        val dataObsoleta = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,evtObsoleto.data.split("/")[0].toInt())
            set(Calendar.MONTH,evtObsoleto.data.split("/")[1].toInt()-1)
            set(Calendar.YEAR,evtObsoleto.data.split("/")[2].toInt())
        }
        var diaJuliano = dataObsoleta[Calendar.DAY_OF_YEAR]
        var ano = dataObsoleta[Calendar.YEAR]
        while (evtAtualizado == EventoVO()) {
            diaJuliano += 7
            if (diaJuliano > 365 && !isBissexto(ano) || diaJuliano > 366 && isBissexto(ano)) {
                diaJuliano -= if (isBissexto(ano)) { 366 } else { 365 }
                ano++
            }
            val dataProjetada = Calendar.getInstance().apply {
                set(Calendar.YEAR,ano)
                set(Calendar.DAY_OF_YEAR,diaJuliano)
            }
            if (!isObsoleto(EventoVO(
                            data="${dataProjetada[Calendar.DAY_OF_MONTH]}/${dataProjetada[Calendar.MONTH]+1}/${dataProjetada[Calendar.YEAR]}",
                            hora = evtObsoleto.hora))) {
                evtAtualizado = EventoVO(
                        id = evtObsoleto.id,
                        titulo = evtObsoleto.titulo,
                        data = "${dataProjetada[Calendar.DAY_OF_MONTH]}/${dataProjetada[Calendar.MONTH] + 1}/${dataProjetada[Calendar.YEAR]}",
                        hora = evtObsoleto.hora,
                        tipo = evtObsoleto.tipo,
                        recorrencia = evtObsoleto.recorrencia,
                        descricao = evtObsoleto.descricao,
                )
            }
        }
        return evtAtualizado
    }

    fun projecaoSemanalDinamico(evtObsoleto: EventoVO): EventoVO{
        var evtAtualizado = EventoVO()
        var diaJuliano = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
        var ano = Calendar.getInstance()[Calendar.YEAR]
        val diasSemanaValidos = evtObsoleto.recorrencia.split("_")[1]

        while (evtAtualizado == EventoVO()){
            val dataProjetada = Calendar.getInstance().apply {
                set(Calendar.YEAR,ano)
                set(Calendar.DAY_OF_YEAR,diaJuliano)
            }
            for (index in diasSemanaValidos.split(",").indices){
                if (diaSemanaToInt(diasSemanaValidos.split(",")[index]) == dataProjetada[Calendar.DAY_OF_WEEK] &&
                        evtAtualizado == EventoVO() &&
                        !isObsoleto(EventoVO(
                                data="${dataProjetada[Calendar.DAY_OF_MONTH]}/${dataProjetada[Calendar.MONTH]+1}/${dataProjetada[Calendar.YEAR]}",
                                hora = evtObsoleto.hora))) {
                    evtAtualizado = EventoVO(
                            id = evtObsoleto.id,
                            titulo = evtObsoleto.titulo,
                            data = "${dataProjetada[Calendar.DAY_OF_MONTH]}/${dataProjetada[Calendar.MONTH]+1}/${dataProjetada[Calendar.YEAR]}",
                            hora = evtObsoleto.hora,
                            tipo = evtObsoleto.tipo,
                            recorrencia = evtObsoleto.recorrencia,
                            descricao = evtObsoleto.descricao,
                    )
                }
            }
            diaJuliano++
            if (diaJuliano == 366 && !isBissexto(ano) || diaJuliano == 367 && isBissexto(ano)){
                diaJuliano = 1
                ano++
            }
        }
        return evtAtualizado
    }

    private fun projecaoMensalFixo(evtObsoleto: EventoVO): EventoVO{
        val dia = evtObsoleto.data.split("/")[0].toInt()
        var mes = Calendar.getInstance()[Calendar.MONTH]
        var ano = Calendar.getInstance()[Calendar.YEAR]
        var correcaoFev = if (evtObsoleto.data.split("/")[0].toInt() == 29
                && evtObsoleto.data.split("/")[1].toInt() == 2
                && !isBissexto(ano)) { -1 } else { 0 }

        while (isObsoleto(EventoVO(data="${dia+correcaoFev}/$mes/$ano",hora = evtObsoleto.hora))) {

            mes++
            if (mes > 12){
                mes = 1
                ano++
            }
            correcaoFev = if (evtObsoleto.data.split("/")[0].toInt() == 29
                    && mes == 2
                    && !isBissexto(ano)) { -1 } else { 0 }
        }
        return EventoVO(
                id = evtObsoleto.id,
                titulo = evtObsoleto.titulo,
                data = "${dia+correcaoFev}/${mes}/${ano}",
                hora = evtObsoleto.hora,
                tipo = evtObsoleto.tipo,
                recorrencia = evtObsoleto.recorrencia,
                descricao = evtObsoleto.descricao,
        )
    }

    private fun projecaoMensalDinamico(evtObsoleto: EventoVO): EventoVO{
        var evtAtualizado = EventoVO()
        var diaJuliano = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
        var ano = Calendar.getInstance()[Calendar.YEAR]
        val semanasAlvo = evtObsoleto.recorrencia.split("_")[1].split("*")[0].toInt()
        val diaSemanaAlvo = evtObsoleto.recorrencia.split("_")[1].split("*")[1]

        while (evtAtualizado == EventoVO()) {

            val dataProjetada = Calendar.getInstance().apply {
                set(Calendar.YEAR,ano)
                set(Calendar.DAY_OF_YEAR,diaJuliano)
            }
            if (diaSemanaToInt(diaSemanaAlvo) == dataProjetada[Calendar.DAY_OF_WEEK] &&
                    semanasAlvo == dataProjetada[Calendar.DAY_OF_WEEK_IN_MONTH] &&
                    evtAtualizado == EventoVO() &&
                    !isObsoleto(EventoVO(
                            data="${dataProjetada[Calendar.DAY_OF_MONTH]}/${dataProjetada[Calendar.MONTH]+1}/${dataProjetada[Calendar.YEAR]}",
                            hora = evtObsoleto.hora)))
                            {
                evtAtualizado = EventoVO(
                        id = evtObsoleto.id,
                        titulo = evtObsoleto.titulo,
                        data = "${dataProjetada[Calendar.DAY_OF_MONTH]}/${dataProjetada[Calendar.MONTH]+1}/${dataProjetada[Calendar.YEAR]}",
                        hora = evtObsoleto.hora,
                        tipo = evtObsoleto.tipo,
                        recorrencia = evtObsoleto.recorrencia,
                        descricao = evtObsoleto.descricao,
                )
            }
            diaJuliano++
            if (diaJuliano == 366 && !isBissexto(ano) || diaJuliano == 367 && isBissexto(ano)){
                diaJuliano = 1
                ano++
            }
        }
        return evtAtualizado
    }

    private fun projecaoAnualFixo(evtObsoleto: EventoVO): EventoVO{
        val dia = evtObsoleto.data.split("/")[0].toInt()
        val mes = evtObsoleto.data.split("/")[1].toInt()
        var ano = Calendar.getInstance()[Calendar.YEAR]
        var correcaoFev = if (evtObsoleto.data.split("/")[0].toInt() == 29
                && evtObsoleto.data.split("/")[1].toInt() == 2
                && !isBissexto(ano)) { -1 } else { 0 }

        if (isObsoleto(EventoVO(data="${dia+correcaoFev}/$mes/$ano"))) {

            ano++
            correcaoFev = if (evtObsoleto.data.split("/")[0].toInt() == 29
                    && mes == 2
                    && !isBissexto(ano)) { -1 } else { 0 }
        }
        return EventoVO(
                id = evtObsoleto.id,
                titulo = evtObsoleto.titulo,
                data = "${dia+correcaoFev}/${mes}/${ano}",
                hora = evtObsoleto.hora,
                tipo = evtObsoleto.tipo,
                recorrencia = evtObsoleto.recorrencia,
                descricao = evtObsoleto.descricao,
        )
    }
    private fun projecaoAnualDinamico(evtObsoleto: EventoVO): EventoVO{
        var evtAtualizado = EventoVO()
        val mes = evtObsoleto.data.split("/")[1].toInt()
        var ano = Calendar.getInstance()[Calendar.YEAR]
        var dataProjetada = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,1)
            set(Calendar.DAY_OF_YEAR,mes-1)
            set(Calendar.YEAR,ano)
        }
        var diaJuliano = dataProjetada[Calendar.DAY_OF_YEAR]
        val semanasAlvo = evtObsoleto.recorrencia.split("_")[1].split("*")[0].toInt()
        val diaSemanaAlvo = evtObsoleto.recorrencia.split("_")[1].split("*")[1]

        while (evtAtualizado == EventoVO()) {

            if (diaSemanaToInt(diaSemanaAlvo) == dataProjetada[Calendar.DAY_OF_WEEK] &&
                    semanasAlvo == dataProjetada[Calendar.DAY_OF_WEEK_IN_MONTH] &&
                    evtAtualizado == EventoVO() &&
                    !isObsoleto(EventoVO(
                            data="${dataProjetada[Calendar.DAY_OF_MONTH]}/${dataProjetada[Calendar.MONTH]+1}/${dataProjetada[Calendar.YEAR]}")))
            {
                evtAtualizado = EventoVO(
                        id = evtObsoleto.id,
                        titulo = evtObsoleto.titulo,
                        data = "${dataProjetada[Calendar.DAY_OF_MONTH]}/${dataProjetada[Calendar.MONTH]+1}/${dataProjetada[Calendar.YEAR]}",
                        hora = evtObsoleto.hora,
                        tipo = evtObsoleto.tipo,
                        recorrencia = evtObsoleto.recorrencia,
                        descricao = evtObsoleto.descricao,
                )
            }
            diaJuliano++
            dataProjetada = Calendar.getInstance().apply {
                set(Calendar.YEAR,ano)
                set(Calendar.DAY_OF_YEAR,diaJuliano)
            }
            if (dataProjetada[Calendar.MONTH] != mes-1){
                ano++
                dataProjetada = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH,1)
                    set(Calendar.MONTH,mes-1)
                    set(Calendar.YEAR,ano)
                }
                diaJuliano = dataProjetada[Calendar.DAY_OF_YEAR]
            }
        }
        return evtAtualizado
    }

    private fun projecaoAnosPeriodico(evtObsoleto: EventoVO): EventoVO{
        val dia = evtObsoleto.data.split("/")[0].toInt()
        val mes = evtObsoleto.data.split("/")[1].toInt()
        var ano = evtObsoleto.data.split("/")[2].toInt()
        var correcaoFev = 0

        while (isObsoleto(EventoVO(data = "${dia+correcaoFev}/$mes/$ano"))){
            ano += evtObsoleto.recorrencia.split("_")[1].split("*")[0].toInt()
            correcaoFev = if (dia == 29 && mes == 2 && !isBissexto(ano)) { -1 } else { 0 }
        }
        return EventoVO(
                id = evtObsoleto.id,
                titulo = evtObsoleto.titulo,
                data = "${dia+correcaoFev}/${mes}/${ano}",
                hora = evtObsoleto.hora,
                tipo = evtObsoleto.tipo,
                recorrencia = evtObsoleto.recorrencia,
                descricao = evtObsoleto.descricao,
        )
    }

    private fun projecaoMesesPeriodico(evtObsoleto: EventoVO): EventoVO{
        val dia = evtObsoleto.data.split("/")[0].toInt()
        var mes = evtObsoleto.data.split("/")[1].toInt()
        var ano = evtObsoleto.data.split("/")[2].toInt()
        var correcaoFev = 0

        while (isObsoleto(EventoVO(data = "${dia+correcaoFev}/$mes/$ano"))){
            mes += evtObsoleto.recorrencia.split("_")[1].split("*")[0].toInt()
            if (mes > 12){
                mes -= 12
                ano++
            }
            correcaoFev = if (dia == 29 && mes == 2 && !isBissexto(ano)) { -1 } else { 0 }
        }
        return EventoVO(
                id = evtObsoleto.id,
                titulo = evtObsoleto.titulo,
                data = "${dia+correcaoFev}/${mes}/${ano}",
                hora = evtObsoleto.hora,
                tipo = evtObsoleto.tipo,
                recorrencia = evtObsoleto.recorrencia,
                descricao = evtObsoleto.descricao,
        )
    }

    private fun projecaoSemanasPeriodico(evtObsoleto: EventoVO): EventoVO{
        var dia = evtObsoleto.data.split("/")[0].toInt()
        var mes = evtObsoleto.data.split("/")[1].toInt()
        var ano = evtObsoleto.data.split("/")[2].toInt()
        var correcaoFev = 0
        var dataProjetada = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,dia)
            set(Calendar.MONTH,mes-1)
            set(Calendar.YEAR,ano)
        }
        var diaJuliano = dataProjetada[Calendar.DAY_OF_YEAR]

        while (isObsoleto(EventoVO(data = "${dia+correcaoFev}/$mes/$ano"))){

            diaJuliano += 7*evtObsoleto.recorrencia.split("_")[1].split("*")[0].toInt()

            if (diaJuliano > 365 && !isBissexto(ano) || diaJuliano > 366 && isBissexto(ano)){

                diaJuliano -= if (isBissexto(ano)){ 366 } else { 365 }
                ano++
            }
            dataProjetada = Calendar.getInstance().apply {
                set(Calendar.YEAR,ano)
                set(Calendar.DAY_OF_YEAR,diaJuliano)
            }
            dia = dataProjetada[Calendar.DAY_OF_MONTH]
            mes = dataProjetada[Calendar.MONTH]+1
            ano = dataProjetada[Calendar.YEAR]
            correcaoFev = if (dia == 29 && mes == 2 && !isBissexto(ano)) { -1 } else { 0 }
        }
        return EventoVO(
                id = evtObsoleto.id,
                titulo = evtObsoleto.titulo,
                data = "${dia+correcaoFev}/${mes}/${ano}",
                hora = evtObsoleto.hora,
                tipo = evtObsoleto.tipo,
                recorrencia = evtObsoleto.recorrencia,
                descricao = evtObsoleto.descricao,
        )
    }

    private fun projecaoDiasCorridoPeriodico(evtObsoleto: EventoVO): EventoVO{
        var dia = evtObsoleto.data.split("/")[0].toInt()
        var mes = evtObsoleto.data.split("/")[1].toInt()
        var ano = evtObsoleto.data.split("/")[2].toInt()
        var correcaoFev = 0
        var dataProjetada = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,dia)
            set(Calendar.MONTH,mes-1)
            set(Calendar.YEAR,ano)
        }
        var diaJuliano = dataProjetada[Calendar.DAY_OF_YEAR]

        while (isObsoleto(EventoVO(data = "${dia+correcaoFev}/$mes/$ano"))){

            diaJuliano += evtObsoleto.recorrencia.split("_")[1].split("*")[0].toInt()

            if (diaJuliano > 365 && !isBissexto(ano) || diaJuliano > 366 && isBissexto(ano)){

                diaJuliano -= if (isBissexto(ano)){ 366 } else { 365 }
                ano++
            }
            dataProjetada = Calendar.getInstance().apply {
                set(Calendar.YEAR,ano)
                set(Calendar.DAY_OF_YEAR,diaJuliano)
            }
            dia = dataProjetada[Calendar.DAY_OF_MONTH]
            mes = dataProjetada[Calendar.MONTH]+1
            ano = dataProjetada[Calendar.YEAR]
            correcaoFev = if (dia == 29 && mes == 2 && !isBissexto(ano)) { -1 } else { 0 }
        }
        return EventoVO(
                id = evtObsoleto.id,
                titulo = evtObsoleto.titulo,
                data = "${dia+correcaoFev}/${mes}/${ano}",
                hora = evtObsoleto.hora,
                tipo = evtObsoleto.tipo,
                recorrencia = evtObsoleto.recorrencia,
                descricao = evtObsoleto.descricao,
        )
    }

    private fun projecaoDiasUteisPeriodico(evtObsoleto: EventoVO): EventoVO{
        var dia = evtObsoleto.data.split("/")[0].toInt()
        var mes = evtObsoleto.data.split("/")[1].toInt()
        var ano = evtObsoleto.data.split("/")[2].toInt()
        var correcaoFev = 0
        var dataProjetada = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,dia)
            set(Calendar.MONTH,mes-1)
            set(Calendar.YEAR,ano)
        }
        var diaJuliano = dataProjetada[Calendar.DAY_OF_YEAR]

        while (isObsoleto(EventoVO(data = "${dia+correcaoFev}/$mes/$ano"))){

            var count = 1

            while (count <= evtObsoleto.recorrencia.split("_")[1].split("*")[0].toInt()){

                diaJuliano++
                if (diaJuliano == 366 && !isBissexto(ano) || diaJuliano == 367 && isBissexto(ano)){
                    diaJuliano = 1
                    ano++
                }
                dataProjetada = Calendar.getInstance().apply {
                    set(Calendar.YEAR,ano)
                    set(Calendar.DAY_OF_YEAR,diaJuliano)
                }
                if (dataProjetada[Calendar.DAY_OF_WEEK] in 2..6) {
                    count++
                }
            }
            dia = dataProjetada[Calendar.DAY_OF_MONTH]
            mes = dataProjetada[Calendar.MONTH]+1
            ano = dataProjetada[Calendar.YEAR]
            correcaoFev = if (dia == 29 && mes == 2 && !isBissexto(ano)) { -1 } else { 0 }
        }
        return EventoVO(
                id = evtObsoleto.id,
                titulo = evtObsoleto.titulo,
                data = "${dia+correcaoFev}/${mes}/${ano}",
                hora = evtObsoleto.hora,
                tipo = evtObsoleto.tipo,
                recorrencia = evtObsoleto.recorrencia,
                descricao = evtObsoleto.descricao,
        )
    }
}