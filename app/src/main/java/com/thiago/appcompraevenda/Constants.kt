package com.thiago.appcompraevenda

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

object Constants {
    fun obterTempoDispositivo() : Long{
        return  System.currentTimeMillis()

    }

    fun obterData(tempo:Long):String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tempo

        return  DateFormat.format("dd/MM/yyyy",calendario).toString()

    }
}