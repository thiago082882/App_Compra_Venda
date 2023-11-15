package com.thiago.appcompraevenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thiago.appcompraevenda.databinding.ActivityLoginEmailBinding
import com.thiago.appcompraevenda.databinding.ActivityOpcaoLoginBinding
import com.thiago.appcompraevenda.opcao_login.LoginEmail

class OpcaoLogin : AppCompatActivity() {
    private lateinit var  binding : ActivityOpcaoLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityOpcaoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.IngressarEmail.setOnClickListener {
            startActivity(Intent(this@OpcaoLogin,LoginEmail::class.java))
        }
    }
}