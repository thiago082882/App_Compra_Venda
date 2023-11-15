package com.thiago.appcompraevenda.opcao_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thiago.appcompraevenda.R
import com.thiago.appcompraevenda.RegistroEmail
import com.thiago.appcompraevenda.databinding.ActivityLoginEmailBinding

class LoginEmail : AppCompatActivity() {

    private lateinit var  binding : ActivityLoginEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  =ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.TxtRegistrar.setOnClickListener {
            startActivity(Intent(this@LoginEmail,RegistroEmail::class.java))
        }


    }
}