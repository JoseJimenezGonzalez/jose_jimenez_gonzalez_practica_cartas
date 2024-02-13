package com.example.myapplication.administrador

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.data.model.Estado
import com.example.myapplication.data.model.ReservarCarta
import com.example.myapplication.databinding.ActivityPrincipalAdministradorBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@AndroidEntryPoint
class PrincipalAdministradorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalAdministradorBinding

    private lateinit var navController: NavController

    @Inject
    lateinit var dbRef: DatabaseReference

    @Inject
    lateinit var stRef: StorageReference

    private lateinit var androidId: String
    private lateinit var generador: AtomicInteger

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalAdministradorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        crearCanalNotificaciones()
        androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        dbRef = FirebaseDatabase.getInstance().reference
        generador = AtomicInteger(0)


        //CONTROLADOR NOTIFICACIONES
        dbRef.child("tienda").child("reservas_carta")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojoReservaCarta = snapshot.getValue(ReservarCarta::class.java)
                    if (!pojoReservaCarta!!.idNotificacion.equals(androidId) && pojoReservaCarta!!.estadoNotificacion == Estado.COMPRADO) {
                        dbRef.child("tienda").child("reservas_carta").child(pojoReservaCarta.idCarta!!)
                            .child("estado_noti").setValue(Estado.NOTIFICADO)
                        generarNotificacion(generador.incrementAndGet(), pojoReservaCarta,
                            "Se ha vendido " + pojoReservaCarta.nombreCarta,
                            "Nueva venta",
                            PrincipalAdministradorActivity::class.java)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    //
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    //

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_administrador) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavigationViewAdministrador.setupWithNavController(navController)
    }

    @SuppressLint("ServiceCast")
    private fun generarNotificacion(
        id_noti: Int,
        pojo: Parcelable,
        contenido: String,
        titulo: String,
        destino: Class<*>
    ) {
        val id = "Canal de prueba"
        val actividad = Intent(applicationContext, destino).apply{
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //Para evitar bugs en la aplicacion
        }

        actividad.putExtra("juego", pojo)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, actividad, PendingIntent.FLAG_IMMUTABLE)

        val notificacion = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.ic_cart_shopping)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("Sistema de notificación")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id_noti, notificacion)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun crearCanalNotificaciones() {
        val nombre = "canal_basico"
        val id = "Canal de prueba"
        val descripcion = "Notificacion basica"
        val importancia = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(id, nombre, importancia).apply {
            description = descripcion
        }

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }
}