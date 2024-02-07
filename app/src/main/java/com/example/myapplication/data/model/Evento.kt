package com.example.myapplication.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Evento(
    val id: String = "",
    val nombre: String = "",
    val formato: String = "",
    val fecha: String = "",
    val precio: Double = 0.0,
    val aforo: Int = 0,
    val aforoOcupado: Int = 0,
    val urlImagenEvento: String = ""
): Parcelable
