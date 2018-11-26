package com.tc.mypath;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

public class HistoriqueActivity extends AppCompatActivity {

    private ImageView imageHistorique1;
    private ImageView imageHistorique2;
    private ImageView imageHistorique3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        imageHistorique1 = (ImageView) findViewById(R.id.imageHistorique1);
        imageHistorique2 = (ImageView) findViewById(R.id.imageHistorique2);
        imageHistorique3 = (ImageView) findViewById(R.id.imageHistorique3);

        setImageParcours("/data/data/com.tc.mypath/app_Images/ParcoursHistorique1.jpg",imageHistorique1);
        setImageParcours("/data/data/com.tc.mypath/app_Images/ParcoursHistorique2.jpg",imageHistorique2);
        setImageParcours("/data/data/com.tc.mypath/app_Images/ParcoursHistorique3.jpg",imageHistorique3);

    }

    void setImageParcours(String path, ImageView v){

        Bitmap bmap = BitmapFactory.decodeFile(path);
        if (bmap!=null){
            v.setImageBitmap(bmap);
            v.setVisibility(View.VISIBLE);
        }
    }
}
