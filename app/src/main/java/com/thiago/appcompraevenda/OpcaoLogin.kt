package com.thiago.appcompraevenda

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.thiago.appcompraevenda.databinding.ActivityLoginEmailBinding
import com.thiago.appcompraevenda.databinding.ActivityOpcaoLoginBinding
import com.thiago.appcompraevenda.opcao_login.LoginEmail

class OpcaoLogin : AppCompatActivity() {
    private lateinit var  binding : ActivityOpcaoLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityOpcaoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        comprovarSessao()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)


        binding.IngressarGoogle.setOnClickListener {
           googleLogin()
        }
        binding.IngressarEmail.setOnClickListener {
            startActivity(Intent(this@OpcaoLogin,LoginEmail::class.java))
        }
    }

    private fun googleLogin() {
       val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignInIntent)
    }
    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){resultado->
        if(resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val conta = task.getResult(ApiException::class.java)
                autenticacaoGoogle(conta.idToken)

            }catch (e:Exception){
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun autenticacaoGoogle(idToken: String?) {
         val credencial = GoogleAuthProvider.getCredential(idToken,null)
        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener { resultadoAuth->
                if(resultadoAuth.additionalUserInfo!!.isNewUser){
                    chamarInfoBD()
                }else{
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }

            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun chamarInfoBD() {
        progressDialog.setMessage("Guardando informação")

        val tempo = Constants.obterTempoDispositivo()
        val emailUsuario = firebaseAuth.currentUser!!.email
        val uidUsuario = firebaseAuth.uid
        val nomeUsuario = firebaseAuth.currentUser?.displayName
        val hasMap = HashMap<String, Any>()
        hasMap["nome"] = "${nomeUsuario}"
        hasMap["codigoTelefone"] = ""
        hasMap["telefone"] = ""
        hasMap["urlImagemPerfil"] = ""
        hasMap["provedor"] = "Google"
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

    private fun comprovarSessao(){
        if(firebaseAuth.currentUser != null){
            startActivity(Intent(this,MainActivity::class.java))
            finishAffinity()

        }
    }
}