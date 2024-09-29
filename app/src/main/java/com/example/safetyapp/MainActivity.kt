package com.example.safetyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var name: EditText
    lateinit var username: EditText
    lateinit var password1: EditText
    lateinit var confirmPassword: EditText
    lateinit var registerButton: Button
    lateinit var loginText: TextView
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        name = findViewById<EditText>(R.id.name)
        username = findViewById<EditText>(R.id.username)
        password1 = findViewById<EditText>(R.id.password)
        confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        registerButton = findViewById<Button>(R.id.registerButton)
        loginText = findViewById<TextView>(R.id.loginText)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (firebaseAuth.currentUser != null) {
            onLoginRegisterSuccess()
        }

        registerButton.setOnClickListener(View.OnClickListener {
            val email = username.text.toString().trim()
            val password = password1.text.toString().trim()
            val userName = name.text.toString().trim()

            if (password.length >= 8) {
                var hasCapitalLetter = false
                var hasSmallLetter = false
                var hasNumber = false
                var hasSymbol = false
                for (char in password) {
                    if (char.isUpperCase()) {
                        hasCapitalLetter = true
                    } else if (char.isLowerCase()) {
                        hasSmallLetter = true
                    } else if (char.isDigit()) {
                        hasNumber = true
                    } else if (!char.isLetterOrDigit()) {
                        hasSymbol = true
                    }
                }
                if (hasCapitalLetter && hasSmallLetter && hasNumber && hasSymbol) {
                    if (password == confirmPassword.text.toString()) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("FirebaseAuth", "User created successfully")

                                    // Create a user document in Firestore
                                    val user = task.result.user
                                    val userData = hashMapOf(
                                        "username" to email,
                                        "password" to password,
                                        "name" to userName // Add the "name" attribute
                                    )
                                    firestore.collection("users").document(user!!.uid).set(userData)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d("Firestore", "User document created successfully")
                                            } else {
                                                Log.e("Firestore", "Error creating user document: ${task.exception?.message}")
                                            }
                                        }

                                    Toast.makeText(this, "User Created.", Toast.LENGTH_SHORT).show()
                                    onLoginRegisterSuccess()
                                } else {
                                    Log.e("FirebaseAuth", "Error creating user: ${task.exception?.message}")
                                    Toast.makeText(this, "User Not Created.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Password and Confirm Password do not match!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Password must have at least one capital letter, one small letter, one number, and one symbol!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show()
            }
        })

        val spannableString = SpannableString("Already have an account? Login")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(this@MainActivity, MainActivity2::class.java)
                startActivity(intent)
            }
        }
        spannableString.setSpan(clickableSpan, 24, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        loginText.text = spannableString
        loginText.movementMethod = LinkMovementMethod.getInstance()
    }

    fun onLoginRegisterSuccess() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userData = task.result.data
                        if (userData != null) {
                            Log.d("Firestore", "User data retrieved successfully")
                            // You can use the user data here
                        } else {
                            Log.e("Firestore", "Error retrieving user data")
                        }
                    } else {
                        Log.e("Firestore", "Error retrieving user data: ${task.exception?.message}")
                    }
                }
        }

        val intent = Intent(this, MainActivity3::class.java)
        startActivity(intent)
        finish()
    }
}