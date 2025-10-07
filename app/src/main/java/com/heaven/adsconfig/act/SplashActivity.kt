package com.heaven.adsconfig.act

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
 import com.heaven.adsconfig.R
import com.heaven.adsconfig.act.MainActivity
import com.heaven.adsconfig.core.AdsConfigManager
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            try {
                AdsConfigManager.load(this@SplashActivity)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }

    }
}
