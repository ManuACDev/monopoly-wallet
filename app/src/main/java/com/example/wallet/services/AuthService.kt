package com.example.wallet.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Obtener usuario actual
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Registrar usuario con email y contraseña
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Iniciar sesión con email y contraseña
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cerrar sesión
    fun logout() {
        auth.signOut()
    }

    // Verificar si hay un usuario autenticado
    fun isUserAuthenticated(): Boolean {
        return currentUser != null
    }
}