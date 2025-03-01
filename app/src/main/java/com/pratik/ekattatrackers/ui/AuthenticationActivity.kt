package com.pratik.ekattatrackers.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pratik.ekattatrackers.GoogleAuthClient
import com.pratik.ekattatrackers.databinding.ActivityAuthenticationBinding
import kotlinx.coroutines.launch

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var googleAuthClient: GoogleAuthClient
    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleAuthClient = GoogleAuthClient(this)
        binding.animationView.playAnimation()

        // If the user is already signed in, go to HomeActivity
        if (googleAuthClient.isSingedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnGoogleSignIn.setOnClickListener {
            lifecycleScope.launch {
                val success = googleAuthClient.signIn()
                if (success) {
                    Toast.makeText(this@AuthenticationActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AuthenticationActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@AuthenticationActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}