package com.example.myapplication.administrador.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.R
import com.example.myapplication.data.model.Carta
import com.example.myapplication.databinding.FragmentAdministradorGestionarCartasAgregarBinding
import com.example.myapplication.databinding.FragmentAdministradorHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
@AndroidEntryPoint
class AdministradorGestionarCartasAgregarFragment() : Fragment(), CoroutineScope {

    private var _binding: FragmentAdministradorGestionarCartasAgregarBinding? = null
    private val binding get() = _binding!!

    private var urlImagen: Uri? = null

    private lateinit var cover: ImageView

    @Inject
    lateinit var dbRef: DatabaseReference

    @Inject
    lateinit var stoRef: StorageReference

    lateinit var job: Job

    private lateinit var listaCartas: MutableList<Carta>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdministradorGestionarCartasAgregarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo

        cover = binding.ivImagen

        job = Job()

        listaCartas = obtenerListaCartas(dbRef)

        configurarBotonImageViewAccesoGaleria()

        configurarBotonAgregarCarta()
    }

    private fun obtenerListaCartas(dbRef: DatabaseReference): MutableList<Carta> {
        val lista = mutableListOf<Carta>()

        dbRef.child("tienda")
            .child("cartas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach{hijo : DataSnapshot ->
                        val pojoCarta = hijo.getValue(Carta::class.java)
                        lista.add(pojoCarta!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        return lista
    }

    private fun configurarBotonAgregarCarta() {
        binding.btnAgregarCarta.setOnClickListener {
            val nombreCarta = binding.tietNombre.text.toString().trim()
            val nombreExpansion = binding.tetNombreEdicion.text.toString().trim()
            val precio = binding.tietPrecio.text.toString().trim()
            val stock = binding.tietStock.text.toString().trim()
            val disponibilidad = binding.tetDisponible.text.toString().trim()
            val color = binding.tetColor.text.toString().trim()
            //Booleanos
            var esFotoCorrecta = false
            var esNombreCorrecto = false
            var esEdicionCorrecta = false
            var esPrecioCorrecto = false
            var esStockCorrecto = false
            var esDisponibilidadCorrecta = false
            var esColorCorrecto = false
            var existeCarta = false
            //Falta hacer las comprobaciones

            if(nombreCarta.isNotBlank()){
                binding.tietNombre.error = null
                esNombreCorrecto = true
            }else{
                binding.tietNombre.error = "Insertar nombre"
            }

            if(nombreExpansion.isNotBlank()){
                binding.dmNombreExpansion.error = null
                esEdicionCorrecta = true
            }else{
                binding.dmNombreExpansion.error = "Insertar expansión"
            }

            if(precio.isNotBlank()){
                binding.tietPrecio.error = null
                esPrecioCorrecto = true
            }else{
                binding.tietPrecio.error = "Insertar precio"
            }

            if(stock.isNotBlank()){
                binding.tietStock.error = null
                esStockCorrecto = true
            }else{
                binding.tietStock.error = "Insertar stock"
            }

            if(disponibilidad.isNotBlank()){
                binding.dmDisponible.error = null
                esDisponibilidadCorrecta = true
            }else{
                binding.dmDisponible.error = "Insertar disponibilidad"
            }

            if(color.isNotBlank()){
                binding.dmColor.error = null
                esColorCorrecto = true
            }else{
                binding.dmColor.error = "Insertar color"
            }

            if(urlImagen == null){
                Toast.makeText(context, "Falta la seleccionar la imagen", Toast.LENGTH_SHORT).show()
            }else{
                esFotoCorrecta = true
            }

            if(existeCarta(listaCartas, nombreCarta)){
                Toast.makeText(context, "Ya existe esa carta", Toast.LENGTH_SHORT).show()
                existeCarta = true
            }

            if (esNombreCorrecto && esColorCorrecto && esPrecioCorrecto && esStockCorrecto && esDisponibilidadCorrecta && !existeCarta && esFotoCorrecta && esEdicionCorrecta){
                val idCarta = dbRef.child("tienda").child("cartas").push().key
                registrarCartaEnBaseDatos(idCarta, nombreCarta, nombreExpansion, precio.toDouble(), stock.toInt(), disponibilidad, color)
                Toast.makeText(context, "Se ha introducido la carta en la base de datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarCartaEnBaseDatos(
        idCarta: String?,
        nombreCarta: String,
        nombreExpansion: String,
        precio: Double,
        stock: Int,
        disponibilidad: String,
        color: String,
    ) {
        launch {
            val urlImageFirebase = guardarImagenCover(stoRef, idCarta!!, urlImagen!!)

            dbRef.child("tienda").child("cartas").child(idCarta).setValue(
                Carta(
                    idCarta, nombreCarta, nombreExpansion, precio , stock, disponibilidad, color, urlImageFirebase
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

        val urlCoverFirebase: Uri = stoRef.child("cartas").child("mtg").child("imagenes").child(id)
            .putFile(imagen).await().storage.downloadUrl.await()

        return urlCoverFirebase.toString()
    }

    fun existeCarta(listaCartas : List<Carta>, nombre:String):Boolean{
        return listaCartas.any{ it.nombreCarta.lowercase()==nombre.lowercase()}
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