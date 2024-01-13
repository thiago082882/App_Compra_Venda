package com.thiago.appcompraevenda

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thiago.appcompraevenda.databinding.ActivityEditarPerfilBinding
import com.thiago.appcompraevenda.databinding.ActivityMainBinding

class EditarPerfil : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        carregarInfo()


        binding.FABmudarimg.setOnClickListener { selectImage() }


    }

    private fun carregarInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nomes = "${snapshot.child("nome").value}"
                    val imagem = "${snapshot.child("urlImagemPerfil").value}"
                    val f_nasc = "${snapshot.child("fecha_nac").value}"
                    val telefone = "${snapshot.child("telefone").value}"
                    val cod_telefone = "${snapshot.child("codigoTelefone").value}"

                    binding.ETNomes.setText(nomes)
                    binding.EtDNasc.setText(f_nasc)
                    binding.EtTelefone.setText(telefone)

                    try {
                        Glide.with(applicationContext)
                            .load(imagem)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.imgPerfil)

                    } catch (e: Exception) {
                        Toast.makeText(this@EditarPerfil, "${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    try {

                        val codigo = cod_telefone.replace("+", "").toInt()
                        binding.selectCod.setCountryForPhoneCode(codigo)

                    } catch (e: Exception) {
                        Toast.makeText(this@EditarPerfil, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun selectImage() {
        val popUpMenu = PopupMenu(this, binding.FABmudarimg)

        popUpMenu.menu.add(Menu.NONE, 1, 1, "Câmera")
        popUpMenu.menu.add(Menu.NONE, 2, 2, "Galeria")

        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    grantPermissionCamera.launch(arrayOf(android.Manifest.permission.CAMERA))
                } else {
                    grantPermissionCamera.launch(
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
                    grantPermissionStorage.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            return@setOnMenuItemClickListener true
        }

    }

    private val grantPermissionCamera =
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
                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.img_perfil)
                        .into(binding.imgPerfil)

                } catch (e: Exception) {

                }
            } else {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }

        }

    private val grantPermissionStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGrant ->

            if (isGrant) {
                imageGalery()
            } else {
                Toast.makeText(this, "Acesso ao armazenamento negado", Toast.LENGTH_SHORT).show()

            }
        }

    private fun imageGalery() {
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        resultGalery_ARL.launch(intent)
    }

    private  val resultGalery_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
  val data = result.data
                imageUri = data!!.data
                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.img_perfil)
                        .into(binding.imgPerfil)

                } catch (e: Exception) {

                }
            }else{
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
}