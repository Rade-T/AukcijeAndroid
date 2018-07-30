package com.a2015.sf32.aukcije.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.a2015.sf32.aukcije.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean splashEnable = sp.getBoolean("splash_enable_pref", true);

        if (splashEnable) {
            String duration_str = sp.getString("splash_duration_pref", "5000");
            int duration = Integer.parseInt(duration_str);
            final SplashscreenTask task = new SplashscreenTask(duration);
            task.execute();

            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.splashscreen_layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SplashScreenActivity.this, LogInActivity.class));
                    task.cancel(true);
                    finish();
                }
            });
        } else {
            startActivity(new Intent(SplashScreenActivity.this, LogInActivity.class));
            finish();
        }
    }

    private class SplashscreenTask extends AsyncTask<Void, Void, Void> {
        private int duration;

        public SplashscreenTask(int duration) {
            this.duration = duration;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(this.duration);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(SplashScreenActivity.this, LogInActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
