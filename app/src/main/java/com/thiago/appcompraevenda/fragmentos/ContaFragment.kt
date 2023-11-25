package com.thiago.appcompraevenda.fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.thiago.appcompraevenda.OpcaoLogin

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
        binding.BtnEncerrarSessao.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext,OpcaoLogin::class.java))
            activity?.finishAffinity()


        }
    }

}