package activities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserManager {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    var currentUserRole: String? = null
    var isAuthenticated = false
        private set

    fun loginUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isAuthenticated = true
                fetchUserRole(auth.currentUser?.uid ?: "", {
                    onSuccess()
                }, {
                    onFailure("Failed to fetch user role.")
                })
            } else {
                onFailure(task.exception?.message ?: "Authentication failed.")
            }
        }
    }

    fun registerUser(email: String, password: String, name: String, lastName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    saveUserDetails(user.uid, name, lastName, {
                        onSuccess()
                    }, {
                        onFailure("Failed to save user details.")
                    })
                } else {
                    onFailure("User creation succeeded but user data is null.")
                }
            } else {
                onFailure(task.exception?.message ?: "Registration failed.")
            }
        }
    }


    private fun fetchUserRole(userId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userRef = database.getReference("Users").child(userId)
        userRef.child("role").get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                currentUserRole = dataSnapshot.value as String?
                onSuccess()
            } else {
                onFailure()
            }
        }.addOnFailureListener {
            onFailure()
        }
    }

    fun fetchAndDisplayUserName(userId: String, onResult: (String, String) -> Unit) {
        database.getReference("Users").child(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val lastName = snapshot.child("lastName").value.toString()
                    onResult(name, lastName)
                } else {
                    onResult("", "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult("", "")
            }
        })
    }

    private fun saveUserDetails(userId: String, name: String, lastName: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userRef = database.getReference("Users").child(userId)
        val userDetails = mapOf("name" to name, "lastName" to lastName)
        userRef.setValue(userDetails).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun saveUserRole(userId: String, role: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userRef = database.getReference("Users").child(userId)
        userRef.child("role").setValue(role).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUserRole = role
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun isEmployee(): Boolean {
        return currentUserRole == "Empleado"
    }

    companion object {
        private var instance: UserManager? = null
        fun getInstance(): UserManager {
            if (instance == null) {
                instance = UserManager()
            }
            return instance!!
        }
    }
}
