package com.thiago.appcompraevenda

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

object Constants {

    const val  anuncio_disponivel = "Disponivel"
    const val  anuncio_vendido = "Vendido"

    val categorias = arrayOf(
        "Celulares",
        "Computadores/Laptops",
        "Eletrônicos e Eletrodomésticos",
        "Veiculos",
        "Consoles e videogames",
        "Casa e Móveis",
        "Beleza e Cuidado Pessoal",
        "Livros",
        "Esportes"

    )

    val condicao = arrayOf(
        "Novo",
        "Usado",
        "Renovado"
    )
    fun obterTempoDispositivo() : Long{
        return  System.currentTimeMillis()

    }

    fun obterData(tempo:Long):String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tempo

        return  DateFormat.format("dd/MM/yyyy",calendario).toString()

    }
}