package com.thiago.appcompraevenda.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thiago.appcompraevenda.Constants
import com.thiago.appcompraevenda.R
import com.thiago.appcompraevenda.databinding.ItemAnuncioNovaVersionBinding
import com.thiago.appcompraevenda.detalhe_anuncio.DetalheAnuncio
import com.thiago.appcompraevenda.filtro.FiltrarAnuncio
import com.thiago.appcompraevenda.models.Anuncio


class AnuncioAdapter : RecyclerView.Adapter<AnuncioAdapter.HolderAnuncio>, Filterable {

    private lateinit var binding: ItemAnuncioNovaVersionBinding

    private var context: Context
    var anuncioArrayList: ArrayList<Anuncio>
    private var firebaeAuth: FirebaseAuth
    private var filtroLista: ArrayList<Anuncio>
    private var filtro: FiltrarAnuncio? = null

    constructor(context: Context, anuncioArrayList: ArrayList<Anuncio>) {
        this.context = context
        this.anuncioArrayList = anuncioArrayList
        firebaeAuth = FirebaseAuth.getInstance()
        this.filtroLista = anuncioArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAnuncio {
        binding = ItemAnuncioNovaVersionBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAnuncio(binding.root)
    }

    override fun getItemCount(): Int {
        return anuncioArrayList.size
    }

    override fun onBindViewHolder(holder: HolderAnuncio, position: Int) {
        val modeloAnuncio = anuncioArrayList[position]

        val titulo = modeloAnuncio.titulo
        val descripcion = modeloAnuncio.descricao
        val direccion = modeloAnuncio.direcao
        val condicion = modeloAnuncio.condicao
        val precio = modeloAnuncio.preco
        val tiempo = modeloAnuncio.tempo

        val formatoFecha = Constants.obterData(tiempo)

        cargarPrimeraImgAnuncio(modeloAnuncio, holder)

        comprobarFavorito(modeloAnuncio, holder)

        holder.Tv_titulo.text = titulo
        holder.Tv_descripcion.text = descripcion
        holder.Tv_direccion.text = direccion
        holder.Tv_condicion.text = condicion
        holder.Tv_precio.text = precio
        holder.Tv_fecha.text = formatoFecha

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalheAnuncio::class.java)
            intent.putExtra("idAnuncio", modeloAnuncio.id)
            context.startActivity(intent)
        }

        if (condicion.equals("Novo")) {
            holder.Tv_condicion.setTextColor(Color.parseColor("#48C9B0"))
        } else if (condicion.equals("Usado")) {
            holder.Tv_condicion.setTextColor(Color.parseColor("#5DADE2"))
        } else if (condicion.equals("Renovado")) {
            holder.Tv_condicion.setTextColor(Color.parseColor("#A569BD"))
        }


        holder.Ib_fav.setOnClickListener {
            val favorito = modeloAnuncio.favorito

            if (favorito) {
                //Favorito = true
                Constants.eliminarAnuncioFav(context, modeloAnuncio.id)
            } else {
                //Favorito = false
                Constants.adicionarAnuncioFav(context, modeloAnuncio.id)
            }
        }
    }

    private fun comprobarFavorito(modeloAnuncio: Anuncio, holder: AnuncioAdapter.HolderAnuncio) {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaeAuth.uid!!).child("Favoritos").child(modeloAnuncio.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorito = snapshot.exists()
                    modeloAnuncio.favorito = favorito

                    if (favorito) {
                        holder.Ib_fav.setImageResource(R.drawable.ic_anuncio_favorito)
                    } else {
                        holder.Ib_fav.setImageResource(R.drawable.ic_no_favorito)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    private fun cargarPrimeraImgAnuncio(
        modeloAnuncio: Anuncio,
        holder: AnuncioAdapter.HolderAnuncio
    ) {
        val idAnuncio = modeloAnuncio.id

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio).child("Imagens").limitToFirst(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val imagenUrl = "${ds.child("imagemUrl").value}"
                        try {
                            Glide.with(context)
                                .load(imagenUrl)
                                .placeholder(R.drawable.ic_imagem)
                                .into(holder.imagenIv)
                        } catch (e: Exception) {

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    inner class HolderAnuncio(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagenIv = binding.imagemIv
        var Tv_titulo = binding.TvTitulo
        var Tv_descripcion = binding.TvDescricao
        var Tv_direccion = binding.TvDirecao
        var Tv_condicion = binding.TvCondicao
        var Tv_precio = binding.TvPreco
        var Tv_fecha = binding.TvData
        var Ib_fav = binding.IbFav
    }

    override fun getFilter(): Filter {
        if (filtro == null) {
            filtro = FiltrarAnuncio(this, filtroLista)
        }
        return filtro as FiltrarAnuncio
    }


}