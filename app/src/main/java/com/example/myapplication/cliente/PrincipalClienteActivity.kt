package com.example.myapplication.cliente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityPrincipalClienteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrincipalClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalClienteBinding

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

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