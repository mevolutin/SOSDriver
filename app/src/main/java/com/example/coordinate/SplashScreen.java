package com.example.coordinate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_splashscreen);


        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {

                    try {
                        sleep(700);

                        Intent i = new Intent(com.example.coordinate.SplashScreen.this,Login.class);
                        startActivity(i);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        });

         myThread.start();




    }

}
