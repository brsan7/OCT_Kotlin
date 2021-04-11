package com.brsan7.oct.model

data class EventoVO(
        var id: Int = -1,
        var titulo: String = "",
        var data: String = "",
        var hora: String = "",
        var tipo: String = "",
        var recorrencia: String = "",
        var descricao: String = ""
)
