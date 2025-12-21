package com.thiago.appcompraevenda.fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thiago.appcompraevenda.adapters.AnuncioAdapter
import com.thiago.appcompraevenda.databinding.FragmentMeusAnunciosPublicadosBinding
import com.thiago.appcompraevenda.models.Anuncio

class Meus_Anuncios_Publicados_Fragment : Fragment() {

    private lateinit var binding: FragmentMeusAnunciosPublicadosBinding
    private lateinit var mContext: Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var anunciosArrayList: ArrayList<Anuncio>
    private lateinit var anunciosAdaptador: AnuncioAdapter

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeusAnunciosPublicadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        carregarMeusAnuncios()

        binding.EtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(texto: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val consulta = texto.toString()
                    anunciosAdaptador.filter.filter(consulta)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.IbLimpar.setOnClickListener {
            val consulta = binding.EtBuscar.text.toString().trim()
            if (consulta.isNotEmpty()) {
                binding.EtBuscar.setText("")
                Toast.makeText(
                    context,
                    "O campo de busca foi limpo",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Nenhuma consulta foi digitada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun carregarMeusAnuncios() {
        anunciosArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.orderByChild("uid").equalTo(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    anunciosArrayList.clear()

                    for (ds in snapshot.children) {
                        try {
                            val modeloAnuncio = ds.getValue(Anuncio::class.java)
                            anunciosArrayList.add(modeloAnuncio!!)
                        } catch (e: Exception) {
                        }
                    }

                    anunciosAdaptador = AnuncioAdapter(mContext, anunciosArrayList)
                    binding.meusAnunciosRv.adapter = anunciosAdaptador
                }

                override fun onCancelled(error: DatabaseError) {
                    // Operação cancelada
                }
            })
    }
}
