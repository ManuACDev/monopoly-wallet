package com.example.polybank.services

import android.content.Context
import android.widget.Toast

class InteractionService(private val context: Context) {

    // Función para mostrar un toast con un mensaje y duración específica
    fun showToast(message: String, duration: Int) {
        Toast.makeText(context, message, duration).show()
    }
}