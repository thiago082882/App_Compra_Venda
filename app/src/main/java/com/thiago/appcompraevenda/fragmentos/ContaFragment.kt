package com.thiago.appcompraevenda.fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thiago.appcompraevenda.Constants
import com.thiago.appcompraevenda.EditarPerfil
import com.thiago.appcompraevenda.OpcaoLogin
import com.thiago.appcompraevenda.R

import com.thiago.appcompraevenda.databinding.FragmentContaBinding


class ContaFragment : Fragment() {

    private lateinit var binding: FragmentContaBinding
    private  lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContaBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        lerInfo()

        binding.BtnEditarPerfil.setOnClickListener {
            startActivity(Intent(mContext,
                EditarPerfil::class.java))
        }
        binding.BtnEncerrarSessao.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext,OpcaoLogin::class.java))
            activity?.finishAffinity()


        }
    }

    private fun lerInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                  val nomes = "${snapshot.child("nome").value}"
                    val email = "${snapshot.child("email").value}"
                    val imagem = "${snapshot.child("urlImagemPerfil").value}"
                    val f_nasc = "${snapshot.child("fecha_nac").value}"
                    var tempo = "${snapshot.child("tempo").value}"
                    val telefone = "${snapshot.child("email").value}"
                    val cod_telefone = "${snapshot.child("codigoTelefone").value}"
                    val provedor = "${snapshot.child("provedor").value}"


                    val codTel = cod_telefone+telefone
                    if(tempo == "null"){
                        tempo = "0"
                    }
                    val for_tempo = Constants.obterData(tempo = tempo.toLong())

                    binding.TvEmail.text = email
                    binding.TvNomes.text = nomes
                    binding.TvDtaNascimento.text = f_nasc
                    binding.TvTelefone.text = codTel
                    binding.TvMembro.text = for_tempo

                    try {
                        Glide.with(mContext)
                            .load(imagem)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.IvPerfil)

                    }catch (e:Exception){
                       Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    if(provedor == "Email"){

                        val esVerificado  = firebaseAuth.currentUser!!.isEmailVerified
                         if(esVerificado){
                             binding.TvStatusConta.text = "Verificado"
                         }else {
                             binding.TvStatusConta.text = "Não Verificado"
                         }

                    }else{
                        binding.TvStatusConta.text = "Verificado"
                    }

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

}