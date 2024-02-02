package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.administrador.PrincipalAdministradorActivity
import com.example.myapplication.cliente.PrincipalClienteActivity
import com.example.myapplication.databinding.ActivityIniciarSesionBinding

class IniciarSesionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIniciarSesionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        binding = ActivityIniciarSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotonRegistroUsuario()

        configurarInicioSesion()

    }

    private fun configurarInicioSesion() {
        binding.btnIniciarSesion.setOnClickListener {
            val correo = binding.tietCorreo.text.toString()
            val password = binding.tietPassword.text.toString()
            if(correo == "cliente" && password == "cliente"){
                val intent = Intent(this, PrincipalClienteActivity::class.java)
                startActivity(intent)
            }else if(correo == "administrador" && password == "administrador"){
                val intent = Intent(this, PrincipalAdministradorActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarBotonRegistroUsuario() {
        binding.tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegistrarUsuarioActivity::class.java)
            startActivity(intent)
        }
    }
}