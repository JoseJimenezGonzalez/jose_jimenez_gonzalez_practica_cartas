package com.example.myapplication.administrador.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.administrador.model.adapter.viewpager.AdministradorViewPagerAdaptadorVentas
import com.example.myapplication.databinding.FragmentAdministradorGestionarVentasBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdministradorGestionarVentasFragment : Fragment() {

    private var _binding: FragmentAdministradorGestionarVentasBinding? = null
    private val binding get() = _binding!!

    private val listaNombreOpciones = listOf("Procesados", "No procesados")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdministradorGestionarVentasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        val adapter = AdministradorViewPagerAdaptadorVentas(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "${listaNombreOpciones[position]}"
        }.attach()

    }

}