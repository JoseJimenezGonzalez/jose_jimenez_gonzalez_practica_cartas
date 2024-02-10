package com.example.myapplication.data.model

class DivisaActual {
    companion object {
        var dolar: Double? = 0.0
        fun pasarEurAUsd(precioEuros: Double): Double = precioEuros * dolar!!
    }
}