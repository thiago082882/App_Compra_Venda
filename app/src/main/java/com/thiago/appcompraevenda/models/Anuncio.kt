package com.thiago.appcompraevenda.models

class Anuncio {

    var id : String = ""
    var uid : String = ""
    var marca : String = ""
    var categoria : String = ""
    var condicao : String = ""
    var direcao : String = ""
    var preco : String = ""
    var titulo : String = ""
    var descricao : String = ""
    var status : String = ""
    var tempo : Long = 0
    var latitude = 0.0
    var longitude = 0.0
    var favorito = false
    var contadorVistas = 0

    constructor()
    constructor(
        id: String,
        uid: String,
        marca: String,
        categoria: String,
        condicao: String,
        direcao: String,
        preco: String,
        titulo: String,
        descricao: String,
        status: String,
        tempo: Long,
        latitude: Double,
        longitude: Double,
        favorito: Boolean,
        contadorVistas: Int
    ) {
        this.id = id
        this.uid = uid
        this.marca = marca
        this.categoria = categoria
        this.condicao = condicao
        this.direcao = direcao
        this.preco = preco
        this.titulo = titulo
        this.descricao = descricao
        this.status = status
        this.tempo = tempo
        this.latitude = latitude
        this.longitude = longitude
        this.favorito = favorito
        this.contadorVistas = contadorVistas
    }


}