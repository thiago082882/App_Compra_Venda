package com.thiago.appcompraevenda

import com.thiago.appcompraevenda.models.Categoria

interface ListenerCategoria {

    fun onCategoriaClick(categoria: Categoria)
}