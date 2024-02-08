package com.example.myapplication.administrador.model.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.administrador.fragments.AdministradorGestionarEventosAgregarFragment
import com.example.myapplication.administrador.fragments.AdministradorGestionarEventosVerFragment
import com.example.myapplication.administrador.fragments.AdministradorGestionarVentasNoProcesadosFragment
import com.example.myapplication.administrador.fragments.AdministradorGestionarVentasProcesadosFragment

class AdministradorViewPagerAdaptadorVentas(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdministradorGestionarVentasNoProcesadosFragment()
            1 -> AdministradorGestionarVentasProcesadosFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}