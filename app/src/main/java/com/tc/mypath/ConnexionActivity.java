package com.tc.mypath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class ConnexionActivity extends AppCompatActivity {

      private GoogleSignInClient mGoogleSignInClient;
      private static int RC_SIGN_IN = 100;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_connexion);

          GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestEmail()
          .build();

          mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

          findViewById(R.id.connexion).setOnClickListener(connexionListener);

      }

      @Override
      public void onStart() {
          super.onStart();

          GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
          if (account != null) {
              Intent myIntent = new Intent(this, PagePrincipale.class);
              startActivityForResult(myIntent, 0);
          }

      }

    View.OnClickListener connexionListener = new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            signIn();
          }
      };

    private void signIn(){
      Intent signInIntent = mGoogleSignInClient.getSignInIntent();
      startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Intent myIntent = new Intent(this, PagePrincipale.class);
            startActivityForResult(myIntent, 0);
        } catch (ApiException e) {

        }
    }


}
