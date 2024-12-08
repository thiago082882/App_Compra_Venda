package com.thiago.appcompraevenda.fragmentos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thiago.appcompraevenda.Constants
import com.thiago.appcompraevenda.ListenerCategoria
import com.thiago.appcompraevenda.SelecioneLocal
import com.thiago.appcompraevenda.adapters.AnuncioAdapter
import com.thiago.appcompraevenda.adapters.CategoriaAdapter
import com.thiago.appcompraevenda.databinding.FragmentInicioBinding
import com.thiago.appcompraevenda.models.Anuncio
import com.thiago.appcompraevenda.models.Categoria

class InicioFragment : Fragment() {

    private lateinit var binding : FragmentInicioBinding


    private companion object{
        private const val MAX_DISTANCIA_MOSTRAR_ANUNCIO = 10
    }

    private lateinit var mContext : Context

    private lateinit var listaAnuncios: ArrayList<Anuncio>
    private lateinit var adaptadorAnuncio: AnuncioAdapter
    private lateinit var preferenciasLocacao: SharedPreferences

    private var latitudeAtual = 0.0
    private var longitudeAtual = 0.0
    private var enderecoAtual = ""

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentInicioBinding.inflate(LayoutInflater.from(mContext),container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenciasLocacao = mContext.getSharedPreferences("LOCACAO_SP", Context.MODE_PRIVATE)

        latitudeAtual = preferenciasLocacao.getFloat("ATUAL_LATITUDE", 0.0f).toDouble()
        longitudeAtual = preferenciasLocacao.getFloat("ATUAL_LONGITUDE", 0.0f).toDouble()
        enderecoAtual = preferenciasLocacao.getString("ATUAL_DIRECAO", "")!!

        if (latitudeAtual != 0.0 && longitudeAtual != 0.0) {
            binding.TvLocalizacao.text = enderecoAtual
        }

        carregarCategoria()
        carregarAnuncios("Todos")

        binding.TvLocalizacao.setOnClickListener {
            val intent = Intent(mContext, SelecioneLocal::class.java)
            seleccionarUbicacionARL.launch(intent)
        }

        binding.EtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(filtro: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    val consulta = filtro.toString()
                    adaptadorAnuncio.filter.filter(consulta)
                } catch (e: Exception) {

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.IbLimpar.setOnClickListener {
            val consulta = binding.EtBuscar.text.toString().trim()
            if (consulta.isNotEmpty()) {
                binding.EtBuscar.setText("")
                Toast.makeText(context, "O campo de busca foi limpo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Nenhuma consulta foi inserida", Toast.LENGTH_SHORT).show()
            }
        }
    }

        private val seleccionarUbicacionARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ resultado->
        if (resultado.resultCode == Activity.RESULT_OK){
            val data = resultado.data
            if (data!=null){
                latitudeAtual = data.getDoubleExtra("latitude", 0.0)
                longitudeAtual = data.getDoubleExtra("longitude",0.0)
                enderecoAtual = data.getStringExtra("direcao").toString()

                preferenciasLocacao.edit()
                    .putFloat("ATUAL_LATITUDE", latitudeAtual.toFloat())
                    .putFloat("ATUAL_LONGITUDE", longitudeAtual.toFloat())
                    .putString("ATUAL_DIRECAO", enderecoAtual)
                    .apply()

                binding.TvLocalizacao.text = enderecoAtual

                carregarAnuncios("Todos")
            }else{
                Toast.makeText(
                    context, "Cancelado", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun carregarCategoria(){
        val categoriaArrayList = ArrayList<Categoria>()
        for (i in 0 until Constants.categorias.size){
            val modeloCategoria = Categoria(Constants.categorias[i], Constants.categoriasIcone[i])
            categoriaArrayList.add(modeloCategoria)
        }

        val adaptadorCategoria = CategoriaAdapter(
            mContext,
            categoriaArrayList,
            object : ListenerCategoria {
                override fun onCategoriaClick(modeloCategoria: Categoria) {
                    val categoriaSeleccionada = modeloCategoria.categoria
                    carregarAnuncios(categoriaSeleccionada)
                }
            }
        )

        binding.categoriaRv.adapter = adaptadorCategoria
    }

    private fun carregarAnuncios(categoria : String){
        listaAnuncios = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaAnuncios.clear()
                for (ds in snapshot.children){
                    try {
                        val modeloAnuncio = ds.getValue(Anuncio::class.java)
                        val distancia = calcularDistanciaKM(
                            modeloAnuncio?.latitud ?: 0.0,
                            modeloAnuncio?.longitud ?: 0.0
                        )
                        if (categoria == "Todos"){
                            if (distancia <= MAX_DISTANCIA_MOSTRAR_ANUNCIO){
                                listaAnuncios.add(modeloAnuncio!!)
                            }
                        }else{
                            if (modeloAnuncio!!.categoria.equals(categoria)){
                                if (distancia <= MAX_DISTANCIA_MOSTRAR_ANUNCIO){
                                    listaAnuncios.add(modeloAnuncio)
                                }
                            }
                        }
                    }catch (e:Exception){

                    }
                }
                adaptadorAnuncio = AnuncioAdapter(mContext, listaAnuncios)
                binding.anunciosRv.adapter = adaptadorAnuncio

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun calcularDistanciaKM(latitud : Double , longitud : Double) : Double{
        val puntoPartida = Location(LocationManager.NETWORK_PROVIDER)
        puntoPartida.latitude = latitudeAtual
        puntoPartida.longitude = longitudeAtual

        val puntoFinal = Location(LocationManager.NETWORK_PROVIDER)
        puntoFinal.latitude = latitud
        puntoFinal.longitude = longitud

        val distanciaMetros = puntoPartida.distanceTo(puntoFinal).toDouble()
        return distanciaMetros/1000
    }

}