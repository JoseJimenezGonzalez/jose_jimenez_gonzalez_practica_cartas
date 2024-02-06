package com.example.myapplication.administrador.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.data.model.Carta
import com.example.myapplication.databinding.FragmentAdministradorGestionarCartasModificarBinding

class AdministradorGestionarCartasModificarFragment : Fragment() {

    private var _binding: FragmentAdministradorGestionarCartasModificarBinding? = null
    private val binding get() = _binding!!

    private  lateinit var pojoCarta:Carta

    private var urlImagen: Uri? = null

    private lateinit var imagen: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdministradorGestionarCartasModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo

        //Me traigo el objeto del otro fragment
        val bundle = arguments
        if (bundle != null) {
            // Obtener el objeto juego del Bundle
            pojoCarta = bundle.parcelable("carta")!!
        }

        configuracionInicialUI()
        configurarBotonImageViewAccesoGaleria()

        Glide.with(requireContext())
            .load(pojoCarta.urlImagenCarta)
            .apply(opcionesGlide(requireContext()))
            .transition(transicion)
            .into(binding.ivImagen)

    }

    private fun configuracionInicialUI() {
        configurarDropDownEdicion()
        configurarDropDownColor()
        configurarDropDownDisponibilidad()
        configurarNombre()
        configurarPrecio()
        configurarStock()

    }

    private fun configurarStock() {
        val stock = pojoCarta.stock.toString()
        binding.tietStock.setText(stock)
    }

    private fun configurarPrecio() {
        val precio = pojoCarta.precio.toString()
        binding.tietPrecio.setText(precio)
    }

    private fun configurarNombre() {
        val nombre = pojoCarta.nombreCarta
        binding.tietNombre.setText(nombre)
    }

    private fun configurarDropDownDisponibilidad() {
        //Falta implementar
        val opcionesDisponibilidad = resources.getStringArray(R.array.disponibilidad_cartas)
        val disponibilidad = pojoCarta.disponibilidad
        val posicionDisponibilidad = opcionesDisponibilidad.indexOf(disponibilidad)
        Log.e("Posicion", posicionDisponibilidad.toString())
        if (posicionDisponibilidad != -1) {
            // El elemento está en el array
            binding.tetDisponible.setText(opcionesDisponibilidad[posicionDisponibilidad], false)
            binding.tetDisponible.setSelection(0)
        } else {
            // El elemento no está en el array
            Log.e("Error", "El elemento no está en el array")
        }
    }

    private fun configurarDropDownEdicion() {
        //Falta implementar
        val edicionesMagic = resources.getStringArray(R.array.colecciones_cartas)
        val edicion = pojoCarta.nombreExpansion
        val posicionEdicion = edicionesMagic.indexOf(edicion)
        Log.e("Posicion", posicionEdicion.toString())
        if (posicionEdicion != -1) {
            // El elemento está en el array
            binding.tetNombreEdicion.setText(edicionesMagic[posicionEdicion], false)
            binding.tetNombreEdicion.setSelection(0)
        } else {
            // El elemento no está en el array
            Log.e("Error", "El elemento no está en el array")
        }
    }

    private fun configurarDropDownColor() {
        //Falta implementar
        val coloresMagic = resources.getStringArray(R.array.color_cartas)
        val color = pojoCarta.color
        val posicionColor = coloresMagic.indexOf(color)
        Log.e("Posicion", posicionColor.toString())
        if (posicionColor != -1) {
            // El elemento está en el array
            binding.tetColor.setText(coloresMagic[posicionColor], false)
            binding.tetColor.setSelection(0)
        } else {
            // El elemento no está en el array
            Log.e("Error", "El elemento no está en el array")
        }
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            urlImagen = uri
            imagen.setImageURI(uri)
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

    val transicion = DrawableTransitionOptions.withCrossFade(500)

    private fun configurarBotonImageViewAccesoGaleria(){
        //Cuando le da click en la imagen para guardar imagen del juego
        binding.ivImagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
    }

    //La extension de funcion parcelable utilizara el metodo adecuado segun la version de la API
    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

}