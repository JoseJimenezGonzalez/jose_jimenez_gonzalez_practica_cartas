package com.example.myapplication.administrador.model.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.administrador.fragments.AdministradorGestionarCartasAgregarFragment
import com.example.myapplication.administrador.fragments.AdministradorGestionarCartasVerFragment

class AdministradorViewPagerAdaptadorCartas(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdministradorGestionarCartasAgregarFragment()
            1 -> AdministradorGestionarCartasVerFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}