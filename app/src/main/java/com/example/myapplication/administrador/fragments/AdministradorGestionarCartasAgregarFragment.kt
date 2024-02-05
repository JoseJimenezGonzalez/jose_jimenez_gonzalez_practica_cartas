package com.example.myapplication.administrador.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.R
import com.example.myapplication.data.model.Carta
import com.example.myapplication.databinding.FragmentAdministradorGestionarCartasAgregarBinding
import com.example.myapplication.databinding.FragmentAdministradorHomeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class AdministradorGestionarCartasAgregarFragment() : Fragment(), CoroutineScope {

    private var _binding: FragmentAdministradorGestionarCartasAgregarBinding? = null
    private val binding get() = _binding!!

    private var urlImagen: Uri? = null

    private lateinit var cover: ImageView

    lateinit var dbRef: DatabaseReference

    lateinit var stRef: StorageReference

    lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdministradorGestionarCartasAgregarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo

        cover = binding.ivImagen

        job = Job()

        configurarBotonImageViewAccesoGaleria()

        configurarBotonAgregarCarta()
    }

    private fun configurarBotonAgregarCarta() {
        binding.btnAgregarCarta.setOnClickListener {
            val nombreCarta = binding.tietNombre.text.toString()
            val nombreExpansion = binding.tetNombreEdicion.text.toString()
            val rareza = binding.tetRareza.text.toString()
            val stock = binding.tietStock.text.toString()
            val disponibilidad = binding.tetDisponible.text.toString()
            val color = binding.tetColor.text.toString()
            //Falta hacer las comprobaciones
            //Imaginemos que el usuario no es subnormal y rellena los campos
            //val idGenerado: String? = dbRef.child("PS2").child("juegos").push().key
            dbRef = FirebaseDatabase.getInstance().reference
            val idCarta = dbRef.child("tienda").child("cartas").push().key
            registrarCartaEnBaseDatos(idCarta, nombreCarta, nombreExpansion, rareza, stock, disponibilidad, color)

        }
    }

    private fun registrarCartaEnBaseDatos(
        idCarta: String?,
        nombreCarta: String,
        nombreExpansion: String,
        rareza: String,
        stock: String,
        disponibilidad: String,
        color: String
    ) {
        launch {
            dbRef = FirebaseDatabase.getInstance().reference
            dbRef.child("tienda").child("cartas").child(idCarta!!).setValue(
                Carta(
                    idCarta, nombreCarta, nombreExpansion, rareza, stock, disponibilidad, color
                )
            )
        }
    }

    private fun configurarBotonImageViewAccesoGaleria() {
        //Cuando le da click en la imagen para guardar imagen del juego
        binding.ivImagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
    }

    suspend fun guardarImagenCover(stoRef: StorageReference, id:String, imagen: Uri):String{

        val urlCoverFirebase: Uri = stoRef.child("tienda").child("cartas").child(id)
            .putFile(imagen).await().storage.downloadUrl.await()

        return urlCoverFirebase.toString()
    }

    private val accesoGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                urlImagen = uri
                cover.setImageURI(uri)
            }
        }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}