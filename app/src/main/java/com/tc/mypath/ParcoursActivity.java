package com.tc.mypath;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParcoursActivity extends AppCompatActivity {

    final Context context = this;
    private Button dislike;
    private Button start;
    private Button pause;
    private Button stop;
    int erreur = 0;
    private LinearLayout parcoursLayout;
    private LinearLayout boutonsLayout;
    private ImageView parcours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours);

        dislike = (Button) findViewById(R.id.dislike);
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);

        start.setOnClickListener(startListener);
        pause.setOnClickListener(pauseListener);
        dislike.setOnClickListener(dislikeListener);
        stop.setOnClickListener(stopListener);

        Intent myIntent = getIntent();
        double distance = myIntent.getDoubleExtra("distance", 0.00);

        parcoursLayout = findViewById(R.id.parcoursLayout);
        boutonsLayout = findViewById(R.id.boutonsLayout);

        parcours= new ImageView(this);

        if(distance < 7){
            parcours.setImageResource(R.drawable.parcours5_1km);
            parcoursLayout.addView(parcours);
        }
        else if(distance >= 7 && distance < 10){
            parcours.setImageResource(R.drawable.parcours9_0km);
            parcoursLayout.addView(parcours);
        }
        else if(distance >= 10 && distance < 12.5){
            parcours.setImageResource(R.drawable.parcours11_3km);
            parcoursLayout.addView(parcours);
        }
        else if(distance >= 12.5 && distance < 15.5){
            parcours.setImageResource(R.drawable.parcours14_3km);
            parcoursLayout.addView(parcours);
        }
        else{
            parcours.setImageResource(R.drawable.parcours17_3km);
            parcoursLayout.addView(parcours);
        }

    }

    OnClickListener startListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            start.setVisibility(View.GONE);
            dislike.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    };

    OnClickListener pauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            pause.setVisibility(View.GONE);
            start.setText("Reprendre");
            start.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
        }
    };

    OnClickListener dislikeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //REOUVRIR LE POP UP DE GENERATION DE PARCOURS
        }
    };

    OnClickListener stopListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //AFFICHER LE FEEDBACK
            Intent myIntent = new Intent(v.getContext(), PagePrincipale.class);
            startActivityForResult(myIntent,0);
        }
    };
}
