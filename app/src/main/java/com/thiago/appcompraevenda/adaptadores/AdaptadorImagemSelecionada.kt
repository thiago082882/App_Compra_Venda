package com.thiago.appcompraevenda.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.thiago.appcompraevenda.R
import com.thiago.appcompraevenda.databinding.ItemImagensSelecionadasBinding
import com.thiago.appcompraevenda.modelos.ModeloImagemSelecionada

class AdaptadorImagemSelecionada(
    private val context: Context,
    private val imagensSelecionadaArrayList: ArrayList<ModeloImagemSelecionada>

): RecyclerView.Adapter<AdaptadorImagemSelecionada.HolderImagensSelecionada>() {
    private lateinit var  binding : ItemImagensSelecionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagensSelecionada {
       binding = ItemImagensSelecionadasBinding.inflate(LayoutInflater.from(context),parent,false)
   return  HolderImagensSelecionada(binding.root)
    }

    override fun getItemCount(): Int {
       return  imagensSelecionadaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagensSelecionada, position: Int) {
      val modelo = imagensSelecionadaArrayList[position]
        val imagemUri = modelo.imageUri

        try {
            Glide.with(context)
                .load(imagemUri)
                .placeholder(R.drawable.item_imagen)
                .into(holder.item_imagem)
        }catch (e:Exception){

        }

        holder.btn_encerrar.setOnClickListener {
            imagensSelecionadaArrayList.remove(modelo)
            notifyDataSetChanged()
        }
    }
    inner  class HolderImagensSelecionada(itemView:View):ViewHolder(itemView){
        var item_imagem = binding.itemImagen
        var btn_encerrar = binding.cerrarItem
    }
}