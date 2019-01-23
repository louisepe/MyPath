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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.Request.Method.POST;

public class ParcoursActivity extends AppCompatActivity {

    final Context context = this;
    private Button dislike;
    private Button start;
    private Button pause;
    private Button stop;
    int erreur = 0;
    private RelativeLayout parcoursLayout;
    private LinearLayout boutonsLayout;
    private TextView loading;
    private Chronometer chrono;
    private long time = 0;
    double distance;
    private MapView mapView;
    final IMapController MapController = null;
    private LocationManager locationManager;
    private LocationListener listener;
    RoadManager roadManager;
    //private Button button;
    MyLocationNewOverlay myLocationOverlay;
    private Context contextMap;
    private TextView mTextView;
    private double latitude;
    private double longitude;
    private int relaunch = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours);

        Intent myIntent = getIntent();
        distance = myIntent.getDoubleExtra("distance", 0.00);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dislike = (Button) findViewById(R.id.dislike);
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);

        chrono = (Chronometer) findViewById(R.id.chrono);

        mTextView = (TextView) findViewById(R.id.test);
        loading = (TextView) findViewById(R.id.parcoursLoading);

        //Localisation
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        final IMapController mapController = mapView.getController();
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
        roadManager = new OSRMRoadManager(this);
        //final ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        //waypoints.add(startPoint);
        //GeoPoint endPoint = new GeoPoint(37.78997, -122.40087199999999);
        //waypoints.add(endPoint);



        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //CE MORCEAU DE CODE PERMET DE TRACER LE CHEMIN AU FUR ET A MESURE
                /**final ArrayList<GeoPoint> walkedPoints = new ArrayList<GeoPoint>();
                 GeoPoint endPoint = new GeoPoint(location);
                 walkedPoints.add(endPoint);
                 Road road = roadManager.getRoad(walkedPoints);
                 Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                 mapView.getOverlays().add(roadOverlay);
                 mapView.invalidate();**/
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                //POUR CENTRER LA CARTE SUR LES COORDONNEES ENVOYEES PAR L'API (Ca sert a rien mais ca parmet de montrer que ca marche de recup les coordonnees)
                /**getPoint();
                 GeoPoint centerPoint = new GeoPoint(latitude,longitude);
                 mapController.setCenter(centerPoint);
                 mapController.animateTo(centerPoint);*/
                if (relaunch == 1 && longitude!=0){
                    sendInfos(distance*1000, longitude, latitude);
                    relaunch = 0;
                }

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
        //TODO Faire une VRAIE vérification de permissions parce-que la mdr
        if ( Build.VERSION.SDK_INT >= 30 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        locationManager.requestLocationUpdates("gps",5000,0,listener);

        //FIN LOCALISATION
        start.setOnClickListener(startListener);
        pause.setOnClickListener(pauseListener);
        dislike.setOnClickListener(dislikeListener);
        stop.setOnClickListener(stopListener);

        parcoursLayout = findViewById(R.id.parcoursLayout);
        boutonsLayout = findViewById(R.id.boutonsLayout);



    }

    void renameFile (String oldFile, String newFile){
        File oldfile = new File(oldFile);
        File newfile = new File(newFile);
        oldfile.renameTo(newfile);
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
            final String tempsText = time/3600 + ":" + (int)((time%3600)/60) + ":" + (int)((time%3600)%60);
            chrono.stop();

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.save_parcours);

            Button yesButton = (Button) dialog.findViewById(R.id.yes);
            Button noButton = (Button) dialog.findViewById(R.id.no);
            final LinearLayout distanceLayout = (LinearLayout) dialog.findViewById(R.id.distanceLayout);

            yesButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bmap = mapView.getDrawingCache();
                    mapView.setDrawingCacheEnabled(true);
                    bmap = mapView.getDrawingCache(true);
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
            });

            noButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(v.getContext(), FeedbackActivity.class);
                    myIntent.putExtra("time", tempsText);
                    myIntent.putExtra("distance", distance);
                    myIntent.putExtra( "tempsSecondes", time);
                    startActivityForResult(myIntent,0);
                }
            });

            dialog.show();



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

    public void getPoint(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:5000/coord";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        mapView.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                        parseCoord(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });


// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void sendInfos(Double distance, double longitude, double latitude){
        // Instantiate the RequestQueue.
        String url ="http://10.0.2.2:5000/infosUser";
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("latitude", latitude);
            jsonBody.put("longitude", longitude);
            jsonBody.put("distance",distance);
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_RESPONSE", response);
                    getPoint();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_RESPONSE", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    1000000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void parseCoord(String data){
        String[] tabCoord = data.split(" ");
        String[] coord = new String[2];
        double lat=0;
        double lon=0;

        final ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        for (int i = 0; i<tabCoord.length; i++){
            tabCoord[i] = tabCoord[i].replace("[","");
            tabCoord[i] = tabCoord[i].replace("]","");
            tabCoord[i] = tabCoord[i].replace("\"(","");
            tabCoord[i] = tabCoord[i].replace(")\",","");
            tabCoord[i] = tabCoord[i].replace(")\"","");
            coord = tabCoord[i].split(",");
            lat = Double.parseDouble(coord[0]);
            lon = Double.parseDouble(coord[1]);
            GeoPoint endPoint = new GeoPoint(lat, lon);
            waypoints.add(endPoint);
        }
        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();
    }

}