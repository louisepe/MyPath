package com.tc.mypath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParcoursActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours);
        Intent myIntent = getIntent();
        double distance = myIntent.getDoubleExtra("distance", 0.00);

        LinearLayout parcoursLayout = findViewById(R.id.parcoursLayout);
        ImageView parcours= new ImageView(this);
        if(distance < 7){
            parcours.setImageResource(R.drawable.parcours5_1km);;
            parcoursLayout.addView(parcours);
        }
        else if(distance >= 7 && distance < 10){
            parcours.setImageResource(R.drawable.parcours9_0km);;
            parcoursLayout.addView(parcours);
        }
        else if(distance >= 10 && distance < 12.5){
            parcours.setImageResource(R.drawable.parcours11_3km);;
            parcoursLayout.addView(parcours);
        }
        else if(distance >= 12.5 && distance < 15.5){
            parcours.setImageResource(R.drawable.parcours14_3km);;
            parcoursLayout.addView(parcours);
        }
        else{
            parcours.setImageResource(R.drawable.parcours17_3km);;
            parcoursLayout.addView(parcours);
        }

    }
}
