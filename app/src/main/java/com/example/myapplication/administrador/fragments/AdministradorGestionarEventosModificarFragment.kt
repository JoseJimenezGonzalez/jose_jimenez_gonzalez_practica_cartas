package com.example.myapplication.administrador.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.data.model.Evento
import com.example.myapplication.databinding.FragmentAdministradorGestionarEventosModificarBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class AdministradorGestionarEventosModificarFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentAdministradorGestionarEventosModificarBinding? = null
    private val binding get() = _binding!!

    private var urlImagen: Uri? = null

    private  lateinit var pojoEvento: Evento

    private lateinit var foto: ImageView

    @Inject
    lateinit var dbRef: DatabaseReference

    @Inject
    lateinit var stoRef: StorageReference

    lateinit var job: Job

    private lateinit var listaEventos: MutableList<Evento>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdministradorGestionarEventosModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo

        foto = binding.ivImagen

        job = Job()

        listaEventos = obtenerListaEventos(dbRef)

        //Me traigo el objeto del otro fragment
        val bundle = arguments
        if (bundle != null) {
            // Obtener el objeto juego del Bundle
            pojoEvento = bundle.parcelable("evento")!!
        }

        configuracionInicialUI()
        configurarBotonImageViewAccesoGaleria()
        configurarBotonModificarEvento()
        configurarDatePicker()

        Glide.with(requireContext())
            .load(pojoEvento.urlImagenEvento)
            .apply(opcionesGlide(requireContext()))
            .transition(transicion)
            .into(binding.ivImagen)


    }

    private fun configurarBotonModificarEvento() {
        binding.btnModificarEvento.setOnClickListener {
            val nombre = binding.tietNombreEvento.text.toString()
            val formato = binding.tietFormatoTorneo.text.toString()
            val fecha = binding.tietFechaEvento.text.toString()
            val precio = binding.tietPrecioEvento.text.toString()
            val aforo = binding.tietAforoEvento.text.toString()
            val aforoOcupado = binding.tietAforoEventoOcupado.text.toString()
            //Booleanos
            var esNombreCorrecto = false
            var esFormatoCorrecto = false
            var esFechaCorrecta = false
            var esPrecioCorrecto = false
            var esAforoCorrecto = false
            var esAforoOcupadoCorrecto = false
            var existeEvento = false//falta
            //Comprobaciones
            if(nombre.isNotBlank()){
                binding.tietNombreEvento.error = null
                esNombreCorrecto = true
            }else{
                binding.tietNombreEvento.error = "Insertar nombre"
            }

            if(formato.isNotBlank()){
                binding.tietFormatoTorneo.error = null
                esFormatoCorrecto = true
            }else{
                binding.tietFormatoTorneo.error = "Insertar formato"
            }

            if(fecha.isNotBlank()){
                binding.tietFechaEvento.error = null
                esFechaCorrecta = true
            }else{
                binding.tietFechaEvento.error = "Insertar fecha"
            }

            if(precio.isNotBlank()){
                binding.tietPrecioEvento.error = null
                esPrecioCorrecto = true
            }else{
                binding.tietPrecioEvento.error = "Insertar precio"
            }

            if(aforo.isNotBlank()){
                binding.tietAforoEvento.error = null
                esAforoCorrecto = true
            }else{
                binding.tietAforoEvento.error = "Insertar aforo"
            }

            if(aforoOcupado.isNotBlank()){
                binding.tietAforoEventoOcupado.error = null
                esAforoOcupadoCorrecto = true
            }else{
                binding.tietAforoEventoOcupado.error = "Insertar aforo ocupado"
            }

            if(esNombreCorrecto && esFormatoCorrecto && esFechaCorrecta && esPrecioCorrecto && esAforoCorrecto && esAforoOcupadoCorrecto){
                val idEvento = pojoEvento.id
                var urlCoverFirebase = String()
                launch {
                    urlCoverFirebase = if(urlImagen == null){
                        pojoEvento.urlImagenEvento
                    }else{
                        guardarImagenCover(stoRef, pojoEvento.id!!, urlImagen!!)
                    }
                    dbRef.child("tienda").child("eventos").child(idEvento).setValue(
                        Evento(
                            idEvento, nombre, formato, fecha , precio.toDouble(), aforo.toInt(), aforoOcupado.toInt(), urlCoverFirebase
                        )
                    )
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "Se ha modificado el evento", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun configuracionInicialUI() {
        configurarNombre()
        configurarFormato()
        configurarFecha()
        configurarPrecio()
        configurarAforo()
        configurarAforoOcupado()
    }

    private fun configurarAforoOcupado() {
        val aforoOcupado = pojoEvento.aforoOcupado.toString()
        binding.tietAforoEventoOcupado.setText(aforoOcupado)
    }

    private fun configurarAforo() {
        val aforo = pojoEvento.aforo.toString()
        binding.tietAforoEvento.setText(aforo)
    }

    private fun configurarPrecio() {
        val precio = pojoEvento.precio.toString()
        binding.tietPrecioEvento.setText(precio)
    }

    private fun configurarFecha() {
        val fecha = pojoEvento.fecha
        binding.tietFechaEvento.setText(fecha)
    }

    private fun configurarFormato() {
        val formato = pojoEvento.formato
        binding.tietFormatoTorneo.setText(formato)
    }

    private fun configurarNombre() {
        val nombre = pojoEvento.nombre
        binding.tietNombreEvento.setText(nombre)
    }

    suspend fun guardarImagenCover(stoRef: StorageReference, id:String, imagen: Uri):String{

        val urlCoverFirebase: Uri = stoRef.child("eventos").child("mtg").child("imagenes").child(id)
            .putFile(imagen).await().storage.downloadUrl.await()

        return urlCoverFirebase.toString()
    }
    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            urlImagen = uri
            foto.setImageURI(uri)
        }
    }
    fun opcionesGlide(context: Context): RequestOptions {
        return RequestOptions()
            .placeholder(animacionCarga(context))
            .fallback(R.drawable.buried_joke)
            .error(R.drawable.error_404)
    }

    fun animacionCarga(contexto: Context): CircularProgressDrawable {
        val animacion = CircularProgressDrawable(contexto)
        animacion.strokeWidth = 5f
        animacion.centerRadius = 30f
        animacion.start()
        return animacion
    }

    private fun configurarDatePicker() {
        //Fecha lanzamiento
        binding.tietFechaEvento.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                val selectedDate = Date(selectedDateInMillis)
                binding.tietFechaEvento.setText(obtenerFechaLanzamientoFormateada(selectedDate))
            }

            picker.show(requireActivity().supportFragmentManager, "fecha")
        }
    }

    private fun obtenerFechaLanzamientoFormateada(fechaLanzamiento: Date): String {
        return SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(fechaLanzamiento)
    }

    private fun obtenerListaEventos(dbRef: DatabaseReference): MutableList<Evento> {
        val lista = mutableListOf<Evento>()

        dbRef.child("tienda")
            .child("eventos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach{hijo : DataSnapshot ->
                        val pojoEvento = hijo.getValue(Evento::class.java)
                        lista.add(pojoEvento!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        return lista
    }
    private fun configurarBotonImageViewAccesoGaleria(){
        //Cuando le da click en la imagen para guardar imagen del juego
        binding.ivImagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
    }

    val transicion = DrawableTransitionOptions.withCrossFade(500)

    //La extension de funcion parcelable utilizara el metodo adecuado segun la version de la API
    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}