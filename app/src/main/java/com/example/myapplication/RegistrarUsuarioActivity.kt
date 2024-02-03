package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.databinding.ActivityIniciarSesionBinding
import com.example.myapplication.databinding.ActivityRegistrarUsuarioBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrarUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarUsuarioBinding

    private lateinit var auth: FirebaseAuth

    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        configurarBotonIniciarSesion()

        configurarBotonRegistrarse()
    }

    private fun configurarBotonRegistrarse() {
        binding.btnRegistrarUsuario.setOnClickListener {
            //Primero nos traemos todos los datos del usuario
            val nombre = binding.tietNombre.text.toString()
            val apellidos = binding.tietApellidos.text.toString()
            val email = binding.tietCorreo.text.toString()
            val password = binding.tietPassword.text.toString()
            val repeatPassword = binding.tietRepetirPassword.text.toString()
            //Declaramos los chivatos de estas variables
            var esNombreCorrecto = false
            var esApellidoCorrecto = false
            var esEmailCorrecto = false
            var tienePasswordLongitudCorrecta = false
            var tieneRepetirPasswordLongitudCorrecta = false
            var sonPasswordIguales = false
            //Comprobamos los campos
            //Nombre
            if (nombre.isNotBlank()){
                binding.tietNombre.error = null
                esNombreCorrecto = true
            }else{
                binding.tietNombre.error = "No puede estar vacío"
            }
            //Apellidos
            if (apellidos.isNotBlank()){
                binding.tietApellidos.error = null
                esApellidoCorrecto = true
            }else{
                binding.tietApellidos.error = "No puede estar vacío"
            }
            //Correo
            esEmailCorrecto = esCorreoElectronico(email)
            if(esEmailCorrecto){
                binding.tietCorreo.error = null
            }else{
                binding.tietCorreo.error = "No tiene formato de correo"
            }
            //Passwords
            //Tienen que ser iguales
            if(password == repeatPassword){
                sonPasswordIguales = true
                binding.tietPassword.error = null
                binding.tietRepetirPassword.error = null
            }else{
                binding.tietPassword.error = "Las contraseñas tienen que ser iguales"
                binding.tietRepetirPassword.error = "Las contraseñas tienen que ser iguales"
            }

            //Longitud
            if(password.length < 8){
                binding.tietPassword.error = "Tiene que tener mínimo longitud 8"
            }else{
                binding.tietPassword.error = null
                tienePasswordLongitudCorrecta = true

            }

            if(repeatPassword.length < 8){
                binding.tietRepetirPassword.error = "Tiene que tener mínimo longitud 8"
            }else{
                binding.tietRepetirPassword.error = null
                tieneRepetirPasswordLongitudCorrecta = true

            }

            if(esNombreCorrecto && esApellidoCorrecto && esEmailCorrecto && sonPasswordIguales && tienePasswordLongitudCorrecta && tieneRepetirPasswordLongitudCorrecta){
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registro ok
                            Toast.makeText(baseContext, "Registro completado.", Toast.LENGTH_SHORT,).show()
                            //Vamos a registrarlo en nuetra base de datos
                            val user = auth.currentUser
                            val idUsuario = user?.uid
                            val esAdministrador = esUsuarioAdministrador(email)
                            val tipoUsuario = devolverTipoDeUsuario(esAdministrador)
                            registrarEnBaseDeDatos(idUsuario, nombre, apellidos, email, password, tipoUsuario)
                        } else {
                            // Ha fallado el registro
                            Toast.makeText(baseContext, "Error en el registro.", Toast.LENGTH_SHORT,).show()
                        }
                    }
            }
        }
    }

    private fun registrarEnBaseDeDatos(idUsuario: String?, nombre: String, apellidos: String, email: String, password: String, tipoDeUsuario: String) {
        dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("tienda").child("usuarios").child(idUsuario!!).setValue(
            Usuario(
                idUsuario,
                nombre,
                apellidos,
                email,
                password,
                tipoDeUsuario
            )
        )
    }

    private fun configurarBotonIniciarSesion() {
        binding.tvVolverIniciarSesion.setOnClickListener {
            val intent = Intent(this, IniciarSesionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun esCorreoElectronico(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun esUsuarioAdministrador(email: String): Boolean = email=="administrador@gmail.com"

    private fun devolverTipoDeUsuario(bol: Boolean): String = if (bol) "administrador" else "cliente"
}