package com.example.myapplication.cliente

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.data.model.UsuarioActual
import com.example.myapplication.databinding.ActivityPrincipalClienteBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
class PrincipalClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalClienteBinding

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    private var idUsuario = "usuario"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //tema

        if(UsuarioActual.usuarioActual != null){
            val usuarioActual: Usuario = UsuarioActual.usuarioActual!!
            idUsuario = usuarioActual.idUsuario
        }
        //Codigo
        val nombrePref = idUsuario

        // Obtener el estado del modo día/noche almacenado en SharedPreferences
        val sharedPreferences = getSharedPreferences(nombrePref, Context.MODE_PRIVATE)
        val modoDia = sharedPreferences.getBoolean("modo_dia", true)

        // Aplicar el tema según el estado almacenado en SharedPreferences
        if (modoDia) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }


        initUI()
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_cliente) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavigationViewCliente.setupWithNavController(navController)
    }
}