package com.thiago.appcompraevenda.filtro

import android.widget.Filter
import com.thiago.appcompraevenda.adapters.AnuncioAdapter
import com.thiago.appcompraevenda.models.Anuncio

import java.util.Locale

class FiltrarAnuncio (
    private val anuncioAdapter : AnuncioAdapter,
    private val filtroLista : ArrayList<Anuncio>
) : Filter(){
    override fun performFiltering(filtro : CharSequence?): FilterResults {
        var filtro = filtro
        val resultados = FilterResults()

        if (!filtro.isNullOrEmpty()){
            filtro = filtro.toString().uppercase(Locale.getDefault())
            val filtroModelo = ArrayList<Anuncio>()
            for (i in filtroLista.indices){
                if (filtroLista[i].marca.uppercase(Locale.getDefault()).contains(filtro) ||
                    filtroLista[i].categoria.uppercase(Locale.getDefault()).contains(filtro)||
                    filtroLista[i].condicao.uppercase(Locale.getDefault()).contains(filtro) ||
                    filtroLista[i].titulo.uppercase(Locale.getDefault()).contains(filtro)){
                    filtroModelo.add(filtroLista[i])
                }
            }
            resultados.count = filtroModelo.size
            resultados.values = filtroModelo
        }else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }

    override fun publishResults(filtro: CharSequence?, resultados: FilterResults) {
        anuncioAdapter.anuncioArrayList = resultados.values as ArrayList<Anuncio>
        anuncioAdapter.notifyDataSetChanged()
    }


}