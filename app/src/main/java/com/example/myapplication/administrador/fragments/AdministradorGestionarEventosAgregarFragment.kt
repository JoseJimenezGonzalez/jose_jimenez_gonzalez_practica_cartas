package com.example.myapplication.administrador.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.administrador.model.adapter.viewpager.ViewPagerAdapterCartas
import com.example.myapplication.data.model.Carta
import com.example.myapplication.databinding.FragmentAdministradorGestionarCartasBinding
import com.example.myapplication.databinding.FragmentAdministradorGestionarEventosAgregarBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdministradorGestionarEventosAgregarFragment : Fragment() {

    private var _binding: FragmentAdministradorGestionarEventosAgregarBinding? = null
    private val binding get() = _binding!!

    private var urlImagen: Uri? = null

    private lateinit var foto: ImageView

    lateinit var dbRef: DatabaseReference

    lateinit var stoRef: StorageReference

    lateinit var job: Job

    private lateinit var listaEventos: MutableList<Evento>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdministradorGestionarEventosAgregarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        foto = binding.ivImagen
        dbRef = FirebaseDatabase.getInstance().reference


        job = Job()

        listaEventos = obtenerListaEventos(dbRef)

        configurarBotonImageViewAccesoGaleria()
        configurarDatePicker()
        configurarBotonRegistrarEvento()

    }

    private fun configurarBotonRegistrarEvento() {
        binding.btnAgregarEvento.setOnClickListener {
            val nombre = binding.tietNombreEvento.text.toString().trim()
            val formato = binding.tietFormatoTorneo.text.toString().trim()
            val fechaTorneo = binding.tietFechaEvento.text.toString().trim()
            val precioEvento = binding.tietPrecioEvento.text.toString().trim()
            val aforoEvento = binding.tietAforoEvento.text.toString().trim()
            //Booleanos para indicar si es correcto
            var esNombreCorrecto = false
            var esFormatoCorrecto = false
            var esFechaCorrecto = false
            var esPrecioCorrecto = false
            var esAforoCorrecto = false
            var esFotoCorrecta = false
            //Condicionales
            if(nombre.isNotBlank()){
                binding.tietNombreEvento.error = null
                esNombreCorrecto = true
            }else{
                binding.tietNombreEvento.error = "Insertar nombre"
            }

            if(formato.isNotBlank()){
                binding.tietFormatoTorneo.error = null
                esFormatoCorrecto = true
            }else{
                binding.tietFormatoTorneo.error = "Insertar formato"
            }

            if(fechaTorneo.isNotBlank()){
                binding.tietFechaEvento.error = null
                esFechaCorrecto = true
            }else{
                binding.tietFechaEvento.error = "Insertar fecha"
            }

            if(precioEvento.isNotBlank()){
                binding.tietPrecioEvento.error = null
                esPrecioCorrecto = true
            }else{
                binding.tietPrecioEvento.error = "Insertar precio"
            }

            if(aforoEvento.isNotBlank()){
                binding.tietAforoEvento.error = null
                esAforoCorrecto = true
            }else{
                binding.tietAforoEvento.error = "Insertar aforo"
            }

            if(urlImagen == null){
                Toast.makeText(context, "Falta la seleccionar la imagen", Toast.LENGTH_SHORT).show()
            }else{
                esFotoCorrecta = true
            }
            //Si todas las condiciones estan bien
        }
    }

    private fun configurarDatePicker() {
        //Fecha lanzamiento
        binding.tietFechaEvento.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                val selectedDate = Date(selectedDateInMillis)
                binding.tietFechaEvento.setText(obtenerFechaLanzamientoFormateada(selectedDate))
            }

            picker.show(requireActivity().supportFragmentManager, "fecha")
        }
    }

    private fun obtenerFechaLanzamientoFormateada(fechaLanzamiento: Date): String {
        return SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(fechaLanzamiento)
    }
}