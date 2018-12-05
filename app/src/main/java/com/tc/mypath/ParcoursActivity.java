package com.tc.mypath;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
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

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

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
    private MapView mapView;
    IMapController MapController = null;
    private LocationManager locationManager;
    private LocationListener listener;
    //private Button button;
    MyLocationNewOverlay myLocationOverlay;
    private Context contextMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dislike = (Button) findViewById(R.id.dislike);
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);

        chrono = (Chronometer) findViewById(R.id.chrono);

        //Localisation
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(18.5);
        contextMap=getApplicationContext();
        this.myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(contextMap),mapView);
        this.myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(this.myLocationOverlay);
        myLocationOverlay.enableFollowLocation();
        final TextView textView = (TextView) findViewById(R.id.text);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //ICI ON TRACE LA ROUTE
        //GeoPoint startPoint = new GeoPoint(37.4227933, -122.085872);
        final RoadManager roadManager = new OSRMRoadManager(this);
        final ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        //waypoints.add(startPoint);
        //GeoPoint endPoint = new GeoPoint(37.78997, -122.40087199999999);
        //waypoints.add(endPoint);




        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint endPoint = new GeoPoint(location);
                waypoints.add(endPoint);
                Road road = roadManager.getRoad(waypoints);
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                mapView.getOverlays().add(roadOverlay);
                mapView.invalidate();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        //FIN LOCALISATION
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

    void renameFile (String oldFile, String newFile){
        File oldfile = new File(oldFile);
        File newfile = new File(newFile);
        oldfile.renameTo(newfile);
    }

    OnClickListener startListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO Faire une VRAIE vérification de permissions parce-que la mdr
            if ( Build.VERSION.SDK_INT >= 30 &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return  ;
            }
            locationManager.requestLocationUpdates("gps",5000,0,listener);
            start.setVisibility(View.GONE);
            dislike.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            parcours.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
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
            Bitmap bmap=((BitmapDrawable)parcours.getDrawable()).getBitmap();
            ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
            File file = wrapper.getDir("Images",MODE_PRIVATE);
            renameFile("/data/data/com.tc.mypath/app_Images/ParcoursHistorique2.jpg","/data/data/com.tc.mypath/app_Images/ParcoursHistorique3.jpg");
            renameFile("/data/data/com.tc.mypath/app_Images/ParcoursHistorique1.jpg","/data/data/com.tc.mypath/app_Images/ParcoursHistorique2.jpg");
            file = new File(file, "ParcoursHistorique1"+".jpg");
            try{
                OutputStream stream= null;
                stream = new FileOutputStream(file);
                bmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                stream.flush();

            }
            catch(IOException e){
                e.printStackTrace();
            }

            Intent myIntent = new Intent(v.getContext(), FeedbackActivity.class);
            myIntent.putExtra("time", tempsText);
            myIntent.putExtra("distance", distance);
            myIntent.putExtra( "tempsSecondes", time);
            startActivityForResult(myIntent,0);
        }
    };

    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mapView!=null)
            mapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        Configuration.getInstance().save(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mapView!=null)
            mapView.onPause();
    }
}
