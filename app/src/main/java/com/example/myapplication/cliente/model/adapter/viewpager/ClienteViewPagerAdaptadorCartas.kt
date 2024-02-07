package com.example.myapplication.cliente.model.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.cliente.fragments.ClienteComprarCartasFragment
import com.example.myapplication.cliente.fragments.ClienteVerCartasFragment

class ClienteViewPagerAdaptadorCartas (fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle){

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClienteComprarCartasFragment()
            1 -> ClienteVerCartasFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}