package com.thiago.appcompraevenda.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.thiago.appcompraevenda.ListenerCategoria
import com.thiago.appcompraevenda.databinding.ItemCategoriaInicioBinding
import com.thiago.appcompraevenda.models.Categoria
import java.util.Random

class CategoriaAdapter(
    private  val context : Context,
    private  val categoriasList : ArrayList<Categoria>,
    private  val onClick  : ListenerCategoria
):Adapter<CategoriaAdapter.HolderCategoria>() {

    private  lateinit var  binding :  ItemCategoriaInicioBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
       binding = ItemCategoriaInicioBinding.inflate(
           LayoutInflater.from(context),parent,false)
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriasList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val categoriaList = categoriasList[position]
       val icone = categoriaList.icon
        val categoria = categoriaList.categoria
        val random  = Random()
        val color = Color.argb(
            255,
            random.nextInt(255),
            random.nextInt(255),
            random.nextInt(255)

        )

        holder.categoriaIconeIv.setImageResource(icone)
        holder.categoriaTv.text = categoria
        holder.categoriaIconeIv.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
            onClick.onCategoriaClick(categoriaList)

        }

    }

    inner  class HolderCategoria(itemView : View) : ViewHolder(itemView){
        var categoriaIconeIv = binding.categoriaIconoIv
        var categoriaTv = binding.TvCategoria

    }

}