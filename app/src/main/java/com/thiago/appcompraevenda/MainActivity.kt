package com.thiago.appcompraevenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.thiago.appcompraevenda.anuncios.CriarAnuncio
import com.thiago.appcompraevenda.databinding.ActivityMainBinding
import com.thiago.appcompraevenda.databinding.FragmentInicioBinding
import com.thiago.appcompraevenda.fragmentos.ChatsFragment
import com.thiago.appcompraevenda.fragmentos.ContaFragment
import com.thiago.appcompraevenda.fragmentos.InicioFragment
import com.thiago.appcompraevenda.fragmentos.MeusAnunciosFragment

class MainActivity : AppCompatActivity() {

    private lateinit var  binding :ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprovarSessao()
        verFragmentInicio()

        binding.BottonNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Item_Inicio -> {
                    verFragmentInicio()
                    return@setOnItemSelectedListener true
                }
                R.id.Item_Chats -> {
                    verFragmentChats()
                    return@setOnItemSelectedListener true
                }
                R.id.Item_Meus_Anuncios -> {
                    verFragmentMeusAnuncios()
                    return@setOnItemSelectedListener true
                }
                R.id.Item_Conta -> {
                    verFragmentConta()
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }

binding.FAB.setOnClickListener {
    startActivity(Intent(this,CriarAnuncio::class.java))
}
    }

    private fun comprovarSessao(){
        if(firebaseAuth.currentUser == null){
            startActivity(Intent(this,OpcaoLogin::class.java))
            finishAffinity()

        }
    }
    private fun verFragmentInicio(){
  binding.TituloRl.text = "Inicio"
        val fragment = InicioFragment()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id,fragment,"InicioFragment")
        fragmentTransition.commit()
    }
    private fun verFragmentChats(){
        binding.TituloRl.text = "Chats"
        val fragment = ChatsFragment()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id,fragment,"ChatsFragment")
        fragmentTransition.commit()
    }
    private fun verFragmentMeusAnuncios(){
        binding.TituloRl.text = "Meus Anuncios"
        val fragment = MeusAnunciosFragment()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id,fragment,"MeusAnunciosFragment")
        fragmentTransition.commit()
    }
    private fun verFragmentConta(){
        binding.TituloRl.text = "Conta"
        val fragment = ContaFragment()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id,fragment,"ContaFragment")
        fragmentTransition.commit()
    }
}



