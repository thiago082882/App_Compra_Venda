package com.thiago.appcompraevenda.models

import android.net.Uri

class ImagemSelecionada {


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