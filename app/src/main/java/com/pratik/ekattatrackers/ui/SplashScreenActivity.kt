package com.pratik.ekattatrackers.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pratik.ekattatrackers.GoogleAuthClient
import com.pratik.ekattatrackers.R
import com.pratik.ekattatrackers.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleAuthClient = GoogleAuthClient(this)

        // If the user is already signed in, go to HomeActivity
        Handler(Looper.getMainLooper()).postDelayed({
            if (googleAuthClient.isSingedIn()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        }, 2000)

    }
}