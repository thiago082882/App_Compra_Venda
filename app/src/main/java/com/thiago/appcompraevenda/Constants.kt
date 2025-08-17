package com.thiago.appcompraevenda

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

object Constants {

    const val  anuncio_disponivel = "Disponivel"
    const val  anuncio_vendido = "Vendido"

    val categorias = arrayOf(
        "Todos",
        "Celulares",
        "Computadores/Laptops",
        "Eletrônicos e Eletrodomésticos",
        "Veiculos",
        "Consoles e videogames",
        "Casa e Móveis",
        "Beleza e Cuidado Pessoal",
        "Livros",
        "Esportes",
        "Brinquedos e figuras",
        "Animais"

    )
    val categoriasIcone = arrayOf(
        R.drawable.ic_categoria_todos,
        R.drawable.ic_categoria_mobiles,
        R.drawable.ic_categoria_computadores,
        R.drawable.ic_categoria_eletrodomesticos,
        R.drawable.ic_categoria_veiculos,
        R.drawable.ic_categoria_consoles,
        R.drawable.ic_categoria_mobilia,
        R.drawable.ic_categoria_beleza,
        R.drawable.ic_categoria_livros,
        R.drawable.ic_categoria_esportes,
        R.drawable.ic_categoria_brinquedos,
        R.drawable.ic_categoria_mascotes
    )
    val condicao = arrayOf(
        "Novo",
        "Usado",
        "Renovado"
    )



    fun obterTempoDispositivo() : Long{
        return  System.currentTimeMillis()

    }

    fun obterData(tempo:Long):String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tempo

        return  DateFormat.format("dd/MM/yyyy",calendario).toString()

    }

    fun obterDataHora(tiempo: Long) : String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tiempo
        return DateFormat.format("dd/MM/yyyy hh:mm:a", calendario).toString()
    }

    fun adicionarAnuncioFav (context : Context, idAnuncio : String){
        val firebaseAuth = FirebaseAuth.getInstance()
        val tiempo = Constants.obterTempoDispositivo()

        val hashMap = HashMap<String, Any>()
        hashMap["idAnuncio"] = idAnuncio
        hashMap["tempo"] = tiempo

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idAnuncio)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(context,
                    "Anuncio adicionado a Favoritos",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context,
                    "${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
    fun eliminarAnuncioFav (context: Context, idAnuncio: String){
        val firebaseAuth = FirebaseAuth.getInstance()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idAnuncio)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,
                    "Anuncio eliminado de Favoritos",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context,
                    "${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

}