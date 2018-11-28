package com.tc.mypath;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PagePrincipale extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    final Context context = this;
    private Button generer;
    private Button parametres;
    private Button deconnexion;
    private Button historique;
    private TextView nom;
    int erreur = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_principale);

        Intent myIntent = getIntent();
        int connecte = myIntent.getIntExtra("connecte",0);

        generer = (Button) findViewById(R.id.generer);
        parametres = (Button) findViewById(R.id.parametres);
        deconnexion = (Button) findViewById(R.id.deconnexion);
        historique = (Button) findViewById(R.id.historique);

        nom = (TextView) findViewById(R.id.nom);

        generer.setOnClickListener(genererListener);
        parametres.setOnClickListener(parametresListener);
        deconnexion.setOnClickListener(deconnexionListener);
        historique.setOnClickListener(historiqueListener);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            nom.setText("Bonjour "+ acct.getGivenName());
        }

        if(connecte==0){
            deconnexion.setText("Connexion");
        }

    }

    OnClickListener genererListener = new OnClickListener() {
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

    OnClickListener parametresListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(v.getContext(), ParametresActivity.class);
            startActivityForResult(myIntent,0);
        }
    };

    OnClickListener deconnexionListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            signOut();
        }
    };

    private void signOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent myIntent = new Intent(context, ConnexionActivity.class);
                        startActivityForResult(myIntent, 0);
                    }
                });
    }

    OnClickListener historiqueListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(context, HistoriqueActivity.class);
            startActivityForResult(myIntent, 0);
        }
    };


}
