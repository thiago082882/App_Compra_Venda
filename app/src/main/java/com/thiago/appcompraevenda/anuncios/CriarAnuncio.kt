package com.thiago.appcompraevenda.anuncios

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.thiago.appcompraevenda.Constants
import com.thiago.appcompraevenda.R
import com.thiago.appcompraevenda.SelecioneLocal
import com.thiago.appcompraevenda.adapters.AdaptadorImagemSelecionada
import com.thiago.appcompraevenda.databinding.ActivityCriarAnuncioBinding
import com.thiago.appcompraevenda.models.ModeloImagemSelecionada

class CriarAnuncio : AppCompatActivity() {
    private lateinit var binding: ActivityCriarAnuncioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    private var imageUri: Uri? = null

    private lateinit var imagensSelecionadaArrayList: ArrayList<ModeloImagemSelecionada>
    private lateinit var adaptadorImagemSelecionada: AdaptadorImagemSelecionada


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val adaptadorCat = ArrayAdapter(this, R.layout.item_categoria, Constants.categorias)
        binding.EtCategoria.setAdapter(adaptadorCat)

        val adaptadorCon = ArrayAdapter(this, R.layout.item_condicao, Constants.condicao)
        binding.EtCondicao.setAdapter(adaptadorCon)


        imagensSelecionadaArrayList = ArrayList()

        carregarImagens()

        binding.agregarImg.setOnClickListener {

            mostrarOpcoes()

        }
        binding.EtLocalizacao.setOnDismissListener {
            val intent = Intent(this, SelecioneLocal::class.java)
            selecioneLocal_ARL.launch(intent)
        }
        binding.BtnCriarAnuncio.setOnClickListener {
     validarDados()
        }

    }

    private var marca = ""
    private var categoria = ""
    private var condicao = ""
    private var direcao = ""
    private var preco = ""
    private var titulo = ""
    private var descricao = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private fun validarDados() {
        marca = binding.EtMarca.text.toString().trim()
        categoria= binding.EtCategoria.text.toString().trim()
        condicao= binding.EtCondicao.text.toString().trim()
        direcao= binding.EtLocalizacao.text.toString().trim()
        preco= binding.EtPreco.text.toString().trim()
        titulo= binding.EtTitulo.text.toString().trim()
        descricao= binding.EtDescricao.text.toString().trim()

        if(marca.isEmpty()){
            binding.EtMarca.error = "Ingresse uma marca"
            binding.EtMarca.requestFocus()
        }   else if(categoria.isEmpty()) {
            binding.EtCategoria.error = "Ingresse uma categoria"
            binding.EtCategoria.requestFocus()
        } else  if(condicao.isEmpty()){
            binding.EtCondicao.error = "Ingresse uma Condição"
            binding.EtCondicao.requestFocus()
        }
     else  if(direcao.isEmpty()){
        binding.EtLocalizacao.error = "Ingresse uma localização"
        binding.EtLocalizacao.requestFocus()
    }
       else if(preco.isEmpty()){
            binding.EtPreco.error = "Ingresse um Preço"
            binding.EtPreco.requestFocus()
        }
       else if(titulo.isEmpty()){
            binding.EtTitulo.error = "Ingresse um Titulo"
            binding.EtTitulo.requestFocus()
        }
      else  if(descricao.isEmpty()){
            binding.EtDescricao.error = "Ingresse uma Descrição"
            binding.EtDescricao.requestFocus()
        }else if(imageUri == null){
            Toast.makeText(this, "Colque ao menos uma imagem", Toast.LENGTH_SHORT).show()

        }else{
            agregarAnuncio()
        }
    }
    private val selecioneLocal_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                if (data != null){
                    latitude = data.getDoubleExtra("latitude", 0.0)
                    longitude = data.getDoubleExtra("longitude", 0.0)
                    direcao = data.getStringExtra("direcao") ?: ""

                    binding.EtLocalizacao.setText(direcao)
                }
            }else{
                Toast.makeText(this, "Cancelado",Toast.LENGTH_SHORT).show()
            }
        }
    private fun agregarAnuncio() {
      progressDialog.setMessage("Agregando Anuncio")
        progressDialog.show()

        val tempo = "${Constants.obterTempoDispositivo()}"
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        val keyId = ref.push().key
        val hashMap =HashMap<String,Any>()
        hashMap["id"] = "${keyId}"
        hashMap["uid"] = "${firebaseAuth.uid}"
        hashMap["marca"] = "${marca}"
        hashMap["categoria"] = "${categoria}"
        hashMap["condicao"] = "${condicao}"
        hashMap["direcao"] = "${direcao}"
        hashMap["preco"] = "${preco}"
        hashMap["titulo"] = "${titulo}"
        hashMap["descricao"] = "${descricao}"
        hashMap["status"] = "${Constants.anuncio_disponivel}"
        hashMap["tempo"] = "${tempo}"
        hashMap["latitude"] = "${latitude}"
        hashMap["longitude"] = "${longitude}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
carregarImagensStorage(keyId)
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarImagensStorage(keyId: String) {

        for(i in imagensSelecionadaArrayList.indices){
            val modeloImgSel =imagensSelecionadaArrayList[i]
            val nomeImg = modeloImgSel.id
            val rotaNomeImg = "Anuncios/${nomeImg}"

            val storageReference = FirebaseStorage.getInstance().getReference(rotaNomeImg)
            storageReference.putFile(modeloImgSel.imageUri!!)
                .addOnSuccessListener {taskSnapshot->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while(!uriTask.isSuccessful);
                    val urlImgCarregada = uriTask.result

                    if(uriTask.isSuccessful){
                        val hashMap = HashMap<String,Any>()
                        hashMap["id"]="${modeloImgSel.imageUri}"
                        hashMap["imagemUrl"]="${urlImgCarregada}"

                        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
                        ref.child(keyId).child("Imagens")
                            .child(nomeImg)
                            .updateChildren(hashMap)

                    }
                    progressDialog.dismiss()
                    Toast.makeText(this, "Anuncio publicado", Toast.LENGTH_SHORT).show()
                    limparCampos()

                }
                .addOnFailureListener {e->
                    Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun limparCampos(){
        imagensSelecionadaArrayList.clear()
        adaptadorImagemSelecionada.notifyDataSetChanged()
        binding.EtMarca.setText("")
        binding.EtCategoria.setText("")
        binding.EtCondicao.setText("")
        binding.EtLocalizacao.setText("")
        binding.EtPreco.setText("")
        binding.EtTitulo.setText("")
        binding.EtDescricao.setText("")
    }
    private fun mostrarOpcoes() {
        val popUpMenu = PopupMenu(this, binding.agregarImg)

        popUpMenu.menu.add(Menu.NONE, 1, 1, "Câmera")
        popUpMenu.menu.add(Menu.NONE, 2, 2, "Galeria")

        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    solicitarPermissaoCamera.launch(arrayOf(android.Manifest.permission.CAMERA))
                } else {
                    solicitarPermissaoCamera.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )


                }
            } else if (itemId == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    imageGalery()

                } else {
                    solicitarPermissaoArmarzanamento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private val solicitarPermissaoCamera =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->

            var grantTodo = true
            for (seGrant in result.values) {
                grantTodo = grantTodo && seGrant
            }

            if (grantTodo) {
                imageCamera()
            } else {
                Toast.makeText(this, "Acesso a camera e armazenamento negado", Toast.LENGTH_SHORT)
                    .show()

            }

        }

    private fun imageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagem")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descricao_imagem")
        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultCamera_ARL.launch(intent)

    }

    private val resultCamera_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val tempo = "${Constants.obterTempoDispositivo()}"
                val modeloImgSel = ModeloImagemSelecionada(
                    tempo, imageUri, null, false
                )
                imagensSelecionadaArrayList.add(modeloImgSel)
                carregarImagens()
            } else {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }


        }
    private val solicitarPermissaoArmarzanamento =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGrant ->

            if (isGrant) {
                imageGalery()
            } else {
                Toast.makeText(this, "Acesso ao armazenamento negado", Toast.LENGTH_SHORT).show()

            }
        }

    private fun carregarImagens() {

        adaptadorImagemSelecionada = AdaptadorImagemSelecionada(this, imagensSelecionadaArrayList)
        binding.RVImagens.adapter = adaptadorImagemSelecionada

    }

    private fun imageGalery() {
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        resultGalery_ARL.launch(intent)
    }

    private val resultGalery_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data

                val tempo = "${Constants.obterTempoDispositivo()}"
                val modeloImgSel = ModeloImagemSelecionada(
                    tempo, imageUri, null, false
                )
                imagensSelecionadaArrayList.add(modeloImgSel)
                carregarImagens()

            } else {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
}