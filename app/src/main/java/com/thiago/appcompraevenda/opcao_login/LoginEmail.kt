package com.thiago.appcompraevenda.opcao_login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.thiago.appcompraevenda.MainActivity
import com.thiago.appcompraevenda.R
import com.thiago.appcompraevenda.RegistroEmail
import com.thiago.appcompraevenda.databinding.ActivityLoginEmailBinding

class LoginEmail : AppCompatActivity() {

    private lateinit var  binding : ActivityLoginEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  =ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnIngressar.setOnClickListener {
           validarInfo()
        }
        binding.TxtRegistrar.setOnClickListener {
            startActivity(Intent(this@LoginEmail,RegistroEmail::class.java))
        }


    }
    private var email = ""
    private var password = ""
    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EtEmail.error = "Email inválido"
            binding.EtEmail.requestFocus()

        } else if (email.isEmpty()) {
            binding.EtEmail.error = "Coloque um E-mail"
            binding.EtEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.EtPassword.error = "Coloque uma senha"
            binding.EtPassword.requestFocus()
        }else{
            loginUsuario()
        }
    }

    private fun loginUsuario() {
        progressDialog.setMessage("Ingressando")
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
progressDialog.dismiss()
                startActivity(Intent(this,MainActivity::class.java))
           finishAffinity()
                Toast.makeText(
                    this,
                    "bem-vindo(a)",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Não foi possivel  iniciar a sessão devido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }
}