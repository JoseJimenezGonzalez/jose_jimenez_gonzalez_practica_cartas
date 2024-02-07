package com.example.myapplication.administrador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityPrincipalAdministradorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrincipalAdministradorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalAdministradorBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalAdministradorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_administrador) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavigationViewAdministrador.setupWithNavController(navController)
    }
}