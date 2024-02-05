package com.example.myapplication.administrador.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.administrador.model.adapter.viewpager.ViewPagerAdapterCartas
import com.example.myapplication.administrador.model.adapter.viewpager.ViewPagerAdapterEventos
import com.example.myapplication.databinding.FragmentAdministradorGestionarEventosBinding
import com.example.myapplication.databinding.FragmentClienteEventosBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdministradorGestionarEventosFragment : Fragment() {

    private var _binding: FragmentAdministradorGestionarEventosBinding? = null
    private val binding get() = _binding!!

    private val listaNombreOpciones = listOf("Añadir", "Ver", "Modificar")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdministradorGestionarEventosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        val adapter = ViewPagerAdapterEventos(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "${listaNombreOpciones[position]}"
        }.attach()

    }

}