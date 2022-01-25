package com.ceinsys.togethringtaskdemo.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.ceinsys.togethringtaskdemo.databinding.ActivityLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginScreen : AppCompatActivity() {

    private var binding: ActivityLoginScreenBinding? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("69283998347-ca6ikibfvbqov414bvabt1o48n59ecph.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()

        binding?.googleSignIn?.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        launchSomeActivity.launch(signInIntent)
    }

    var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val exception = task.exception
                if (task.isSuccessful) {
                    try {
                        val account = task.getResult(ApiException::class.java)
                        Log.e("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        Log.e("SignInActivity", "Google sign in failed", e)
                    }
                } else {
                    Log.e("SignInActivity", exception.toString())
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result.let {
                        Log.e("UserdisplayName", "---------" + it.user?.displayName)
                        Log.e("UserEmail", "---------" + it.user?.email)
                        Log.e("UserPhotoUrl", "---------" + it.user?.photoUrl)

                        it.user?.uid?.let { it -> mainViewModel.saveUserID(it) }
                        it.user?.displayName?.let { it1 -> mainViewModel.saveName(it1) }
                        it.user?.email?.let { it2 -> mainViewModel.saveEmail(it2) }
                        it.user?.photoUrl?.let { it3 -> mainViewModel.savePhoto(it3.toString()) }
                        mainViewModel.saveLogin(true)

                        // Sign in success, update UI with the signed-in user's information
                        Log.e("SignInActivity", "signInWithCredential:success")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("SignInActivity", "signInWithCredential:failure")
                }
            }

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}