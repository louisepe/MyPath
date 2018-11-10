package com.tc.mypath;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.solver.widgets.ConstraintHorizontalLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class FeedbackActivity extends AppCompatActivity {

    final Context context = this;
    private Button validerFeedback;
    private TextView temps;
    private TextView distanceParcourue;
    private TextView vitesse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Intent myIntent = getIntent();
        String time = myIntent.getStringExtra("time");
        double distance = myIntent.getDoubleExtra("distance", 0.0);
        long tempsSecondes = myIntent.getLongExtra("tempsSecondes", 0);

        validerFeedback = (Button) findViewById(R.id.validerFeedback);

        temps = (TextView) findViewById(R.id.duree);
        distanceParcourue = (TextView) findViewById(R.id.distanceParcourue);
        vitesse = (TextView) findViewById(R.id.vitesse);

        temps.setText("Durée : " + time );
        distanceParcourue.setText("Distance parcourue : " + distance + "km");
        vitesse.setText("Vitesse moyenne : " + (distance/tempsSecondes)*3600 + "km/h");

        validerFeedback.setOnClickListener(validerListener);
    }

    View.OnClickListener validerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(v.getContext(), PagePrincipale.class);
            startActivityForResult(myIntent,0);
        }
    };
}
