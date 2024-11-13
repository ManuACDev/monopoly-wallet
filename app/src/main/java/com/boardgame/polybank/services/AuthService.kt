package com.boardgame.polybank.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Obtener usuario actual
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Registrar usuario con email y contraseña
    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID not found")

            // Agregar el usuario a la colección de Firestore
            val userMap = mapOf(
                "Name" to name,
                "Email" to email,
                "Uid" to uid
            )

            firestore.collection("Users").document(uid).set(userMap).await()
            Result.success(Unit)
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