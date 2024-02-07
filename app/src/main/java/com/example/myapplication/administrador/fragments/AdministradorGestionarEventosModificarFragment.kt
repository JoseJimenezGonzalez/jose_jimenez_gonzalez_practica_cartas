package com.example.myapplication.administrador.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.administrador.model.adapter.viewpager.ViewPagerAdapterEventos
import com.example.myapplication.databinding.FragmentAdministradorGestionarEventosBinding
import com.example.myapplication.databinding.FragmentAdministradorGestionarEventosModificarBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdministradorGestionarEventosModificarFragment : Fragment() {

    private var _binding: FragmentAdministradorGestionarEventosModificarBinding? = null
    private val binding get() = _binding!!

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


    }
}