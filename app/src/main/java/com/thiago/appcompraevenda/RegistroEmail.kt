package com.thiago.appcompraevenda

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.thiago.appcompraevenda.databinding.ActivityMainBinding
import com.thiago.appcompraevenda.databinding.ActivityRegistroEmailBinding

class RegistroEmail : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnRegistrar.setOnClickListener {
            validarInfo()
        }
    }

    private var email = ""
    private var password = ""
    private var r_password = ""

    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()
        r_password = binding.EtRPassword.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EtEmail.error = "Email inválido"
            binding.EtEmail.requestFocus()

        } else if (email.isEmpty()) {
            binding.EtEmail.error = "Coloque um E-mail"
            binding.EtEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.EtPassword.error = "Coloque uma senha"
            binding.EtPassword.requestFocus()
        } else if (r_password.isEmpty()) {
            binding.EtRPassword.error = "Repita a senha"
            binding.EtRPassword.requestFocus()
        } else if (password != r_password) {
            binding.EtPassword.error = "senhas não coincidem"
            binding.EtPassword.requestFocus()

        } else {
            registrarUsuario()
        }

    }

    private fun registrarUsuario() {
        progressDialog.setMessage("Criando Conta")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                chamarInfoBD()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Não foi registrado nenhum usuario devido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }

    private fun chamarInfoBD() {
        progressDialog.setMessage("Guardando informação")

        val tempo = Constants.obterTempoDispositivo()
        val emailUsuario = firebaseAuth.currentUser!!.email
        val uidUsuario = firebaseAuth.uid
        val hasMap = HashMap<String, Any>()
        hasMap["nome"] = ""
        hasMap["codigoTelefone"] = ""
        hasMap["telefone"] = ""
        hasMap["urlImagemPerfil"] = ""
        hasMap["provedor"] = "Email"
        hasMap["escrevendo"] = ""
        hasMap["tempo"] = tempo
        hasMap["online"] = true
        hasMap["email"] = "${emailUsuario}"
        hasMap["uid"] = "${uidUsuario}"
        hasMap["fecha_nac"] = ""
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidUsuario!!)
            .setValue(hasMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Não foi registrado  devido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
}