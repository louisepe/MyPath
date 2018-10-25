package com.tc.mypath;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;

public class PagePrincipale extends AppCompatActivity {

    final Context context = this;
    private Button generer;
    private Button parametres;

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

           /** Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });**/

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
