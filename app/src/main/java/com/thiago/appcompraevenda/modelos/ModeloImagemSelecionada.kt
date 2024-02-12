package com.thiago.appcompraevenda.modelos

import android.net.Uri

class ModeloImagemSelecionada {


    var id = ""
    var imageUri : Uri? = null
    var imageUrl : String? = null
    var deInternet = false

    constructor()
    constructor(id: String, imageUri: Uri?, imageUrl: String?, deInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imageUrl = imageUrl
        this.deInternet = deInternet
    }


}