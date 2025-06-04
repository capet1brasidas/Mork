package com.example.fit5046a2.data.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit5046a2.data.entity.User
import com.example.fit5046a2.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userRepo: UserRepository

    init {
        userRepo = UserRepository(application)
    }

    // StateFlow to hold all users
    private val _firebaseUsers = MutableStateFlow<List<User>>(emptyList())
    val firebaseUsers: StateFlow<List<User>> = _firebaseUsers

    // StateFlow to hold the current logged-in user
    private val _currentUserStateFlow = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUserStateFlow

    // StateFlow to hold all emails of users
    private val _userEmails = MutableStateFlow<List<String>>(emptyList())
    val userEmails: StateFlow<List<String>> = _userEmails


    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        // Insert into local Room database
        userRepo.insert(user)

        // Sync to Firebase Firestore

        val userMap = mapOf(
            "first_name" to user.first_name,
            "last_name" to user.last_name,
            "email" to user.email,
            "password" to user.password,
            "DOB" to user.DOB,
            "position" to user.position,
            "image" to user.image,
            "joinDate" to user.joinDate
        )
        firebaseDB.collection("users").document(user.email.toString()).set(userMap)
    }

    fun loadCurrentUser() {
        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        android.util.Log.d("UserViewModel", "Firebase auth state: ${firebaseUser != null}")
        
        val email = firebaseUser?.email ?: run {
            android.util.Log.e("UserViewModel", "No authenticated user found")
            return
        }
        android.util.Log.d("UserViewModel", "Loading user data for email: $email")

        firebaseDB.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                android.util.Log.d("UserViewModel", "Firestore document exists: ${document.exists()}")
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    android.util.Log.d("UserViewModel", "User object converted: ${user != null}")
                    if (user != null) {
                        _currentUserStateFlow.value = user
                        android.util.Log.d("UserViewModel", "Current user updated in state flow")
                        viewModelScope.launch(Dispatchers.IO) {
                            userRepo.insert(user)
                            android.util.Log.d("UserViewModel", "User inserted into local database")
                        }
                    }
                } else {
                    android.util.Log.e("UserViewModel", "No user document found in Firestore")
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("UserViewModel", "Failed to sync user: ${e.message}")
            }
    }

    // Fetch all users from Firebase and update the StateFlow
    fun loadAllUsersFromFirebase() {
        firebaseDB.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userList = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)
                }
                _firebaseUsers.value = userList
                // Update the user emails
                _userEmails.value = userList.map { it.email }
            }
            .addOnFailureListener { exception ->

                android.util.Log.e("UserViewModel", "Failed to load users: ${exception.message}")

                _firebaseUsers.value = emptyList()
                _userEmails.value = emptyList()
            }
    }

    fun updateUser(user: User) = viewModelScope.launch(Dispatchers.IO) {

        // Sync updates to Firebase Firestore

        val userMap = mapOf(
            "first_name" to user.first_name,
            "last_name" to user.last_name,
            "email" to (user.email),
            "password" to user.password,
            "DOB" to user.DOB,
            "position" to user.position,
            "image" to user.image,
            "joinDate" to user.joinDate
        )
        firebaseDB.collection("users").document(user.email).update(userMap)
            .addOnSuccessListener {
                _currentUserStateFlow.value = user
            }
    }

    // New function to fetch user emails
    fun loadUserEmails() {
        // This will load emails from Firebase when required
        loadAllUsersFromFirebase()
    }

}
