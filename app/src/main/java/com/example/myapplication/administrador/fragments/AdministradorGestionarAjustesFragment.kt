package com.example.myapplication.administrador.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.IniciarSesionActivity
import com.example.myapplication.R
import com.example.myapplication.data.model.ApiService
import com.example.myapplication.data.model.DivisaActual
import com.example.myapplication.databinding.FragmentAdministradorGestionarAjustesBinding
import com.example.myapplication.databinding.FragmentAdministradorHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class AdministradorGestionarAjustesFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentAdministradorGestionarAjustesBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var job: Job

    private lateinit var sharedPreferences: SharedPreferences

    lateinit var editor: SharedPreferences.Editor

    private val nombrePref = "mis_preferencias"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdministradorGestionarAjustesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        job = Job()
        auth = FirebaseAuth.getInstance()

        sharedPreferences = requireContext().getSharedPreferences(nombrePref, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        cerrarSesion()
        configurarBotonesDivisas()
        configurarBotonesCambioTema()

    }

    private fun cerrarSesion() {
        binding.btnNoMantenerSesion.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(context, IniciarSesionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarBotonesDivisas(){
        binding.btnDolares.setOnClickListener {
            binding.tbDivisas.check(R.id.btnDolares)
            Log.e("boton dolares","hola")
            // Configurar Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("https://open.er-api.com/v6/latest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Crear la instancia de la interfaz ApiService
            val apiService = retrofit.create(ApiService::class.java)

            // Realizar la llamada a la API en un hilo de trabajo en segundo plano
            launch {
                try {
                    val response = apiService.obtenerDatos()
                    Log.e("respuesta", response.toString())
                    if (response.isSuccessful) {
                        val dolar = response.body()!!.rates["USD"]
                        //Meto el dolar en el companion object
                        DivisaActual.dolar = dolar
                        Log.e("Dolares en ajustes", dolar.toString())
                    } else {
                        // Manejar el error de la respuesta de la API
                        Log.e("API", response.toString())
                    }
                } catch (e: Exception) {
                    // Manejar errores de red u otros errores
                    Log.e("excepcion Api", e.message.toString())

                }
            }
        }
        binding.btnEuro.setOnClickListener {
            binding.tbDivisas.check(R.id.btnEuro)
            DivisaActual.dolar = 0.0
        }
    }

    private fun toggleTheme() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else -> {
                // El modo de noche no está configurado, usa el modo de noche como predeterminado
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun configurarBotonesCambioTema() {
        binding.btnModoNoche.setOnClickListener {
            toggleTheme()
            val editor = sharedPreferences.edit()
            editor.putBoolean("modo_dia", false) // Cambiar a false cuando se selecciona el modo noche
            editor.apply()
        }
        binding.btnModoDia.setOnClickListener {
            toggleTheme()
            val editor = sharedPreferences.edit()
            editor.putBoolean("modo_dia", true) // Cambiar a true cuando se selecciona el modo día
            editor.apply()
        }
    }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

}