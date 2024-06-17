package com.example.medicinetracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class launcher extends AppCompatActivity {

    private static final long SPLASH_DELAY_AFTER_BLUR_IN = 1000L; // Delay after blur in animation (1 second)
    private static final long SPLASH_DELAY_AFTER_BLUR_OUT = 1000L; // Delay after blur out animation (0.5 seconds)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);

        ImageView splashLogoImageView = findViewById(R.id.splash_logo);


        Animation blurInAnimation = AnimationUtils.loadAnimation(this, R.anim.blur_in);
        Animation blurOutAnimation = AnimationUtils.loadAnimation(this, R.anim.blur_out);


        splashLogoImageView.startAnimation(blurInAnimation);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                splashLogoImageView.startAnimation(blurOutAnimation);

                startActivity(new Intent(launcher.this, Login.class));
                finish();
            }, SPLASH_DELAY_AFTER_BLUR_OUT);
        }, SPLASH_DELAY_AFTER_BLUR_IN);
    }
}