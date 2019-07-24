package com.example.rider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    CardView sharelocation,tracklocation,addmembers,checkmembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharelocation= findViewById(R.id.sharelocation);
        tracklocation= findViewById(R.id.tracklocation);
        addmembers= findViewById(R.id.register);
        checkmembers= findViewById(R.id.verfiedusers);
        ShareLocation();

    }

    public void ShareLocation()
    {
        sharelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gotolocation= new Intent(getApplicationContext(),MapFragment.class);
                startActivity(gotolocation);
            }
        });
    }























}
