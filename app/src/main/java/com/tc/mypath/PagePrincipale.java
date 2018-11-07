package com.tc.mypath;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;

public class PagePrincipale extends AppCompatActivity {

    final Context context = this;
    private Button generer;
    private Button parametres;
    int erreur = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_principale);

        generer = (Button) findViewById(R.id.generer);
        parametres = (Button) findViewById(R.id.parametres);

        generer.setOnClickListener(genererListener);
        parametres.setOnClickListener(parametresListener);

    }

    OnClickListener genererListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.parametres_parcours);

            Button validerButton = (Button) dialog.findViewById(R.id.validerParams);
            final EditText distance = (EditText) dialog.findViewById(R.id.distance);
            final LinearLayout distanceLayout = (LinearLayout) dialog.findViewById(R.id.distanceLayout);

            // if button is clicked, close the custom dialog
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

    OnClickListener parametresListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(v.getContext(), ParametresActivity.class);
            startActivityForResult(myIntent,0);
        }
    };
}
