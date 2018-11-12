package com.tc.mypath;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ParcoursActivity extends AppCompatActivity {

    final Context context = this;
    private Button dislike;
    private Button start;
    private Button pause;
    private Button stop;
    int erreur = 0;
    private RelativeLayout parcoursLayout;
    private LinearLayout boutonsLayout;
    private ImageView parcours;
    private Chronometer chrono;
    private long time = 0;
    double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours);

        dislike = (Button) findViewById(R.id.dislike);
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);

        chrono = (Chronometer) findViewById(R.id.chrono);

        start.setOnClickListener(startListener);
        pause.setOnClickListener(pauseListener);
        dislike.setOnClickListener(dislikeListener);
        stop.setOnClickListener(stopListener);

        Intent myIntent = getIntent();
        distance = myIntent.getDoubleExtra("distance", 0.00);

        parcoursLayout = findViewById(R.id.parcoursLayout);
        boutonsLayout = findViewById(R.id.boutonsLayout);

        parcours = (ImageView) findViewById(R.id.imageParcours);

        if(distance < 7){
            parcours.setImageResource(R.drawable.parcours5_1km);
        }
        else if(distance >= 7 && distance < 10){
            parcours.setImageResource(R.drawable.parcours9_0km);
        }
        else if(distance >= 10 && distance < 12.5){
            parcours.setImageResource(R.drawable.parcours11_3km);
        }
        else if(distance >= 12.5 && distance < 15.5){
            parcours.setImageResource(R.drawable.parcours14_3km);
        }
        else{
            parcours.setImageResource(R.drawable.parcours17_3km);
        }
    }

    OnClickListener startListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            start.setVisibility(View.GONE);
            dislike.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            chrono.setBase(SystemClock.elapsedRealtime() - time);
            chrono.start();
        }
    };

    OnClickListener pauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            pause.setVisibility(View.GONE);
            start.setText("Reprendre");
            start.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            time = SystemClock.elapsedRealtime() - chrono.getBase();
            chrono.stop();
        }
    };

    OnClickListener dislikeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.parametres_parcours);

            Button validerButton = (Button) dialog.findViewById(R.id.validerParams);
            final EditText distance = (EditText) dialog.findViewById(R.id.distance);
            final LinearLayout distanceLayout = (LinearLayout) dialog.findViewById(R.id.distanceLayout);

            validerButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(distance.getText().toString().equals("") || distance.getText().toString().equals("0")){
                        if(erreur == 0){
                            TextView error= new TextView(context);
                            error.setText("Vous devez entrer une distance supérieure à 0");
                            error.setTextColor(Color.parseColor("#FF0000"));
                            distanceLayout.addView(error);
                            erreur=1;
                        }

                    }
                    else{
                        erreur=0;
                        Intent myIntent = new Intent(v.getContext(), ParcoursActivity.class);
                        myIntent.putExtra("distance", Double.parseDouble(distance.getText().toString()));
                        startActivityForResult(myIntent,0);
                        dialog.hide();
                    }
                }
            });

            dialog.show();
        }
    };

    OnClickListener stopListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //time = ( SystemClock.elapsedRealtime() - chrono.getBase())/1000;
            time = time/1000;
            String tempsText = time/3600 + ":" + (int)((time%3600)/60) + ":" + (int)((time%3600)%60);
            chrono.stop();
            Intent myIntent = new Intent(v.getContext(), FeedbackActivity.class);
            myIntent.putExtra("time", tempsText);
            myIntent.putExtra("distance", distance);
            myIntent.putExtra( "tempsSecondes", time);
            startActivityForResult(myIntent,0);
        }
    };
}
