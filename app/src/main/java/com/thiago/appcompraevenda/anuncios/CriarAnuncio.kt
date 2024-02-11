package com.thiago.appcompraevenda.anuncios

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thiago.appcompraevenda.R
import com.thiago.appcompraevenda.databinding.ActivityCriarAnuncioBinding
import com.thiago.appcompraevenda.databinding.ActivityEditarPerfilBinding

class CriarAnuncio : AppCompatActivity() {
    private lateinit var binding : ActivityCriarAnuncioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}