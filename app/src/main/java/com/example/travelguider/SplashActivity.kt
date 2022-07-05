package com.example.travelguider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.WindowManager.LayoutParams as LayoutParams1

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            LayoutParams1.FLAG_FULLSCREEN,
            LayoutParams1.FLAG_FULLSCREEN
        )
text_animate.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slidedown))
        text_animate2.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slideup))

        Handler().postDelayed({
            startActivity(Intent(this,GettingStartedActivity::class.java))
            finish()
                                },2500
        )
    }
}