package com.vedic.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.vedic.R;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
            }
        }, 200);
    }
}
