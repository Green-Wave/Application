package com.example.greenwave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.milestones.MilestoneManager;
import org.osmdroid.views.overlay.milestones.MilestonePathDisplayer;
import org.osmdroid.views.overlay.milestones.MilestonePixelDistanceLister;

import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import util.AppExecutors;

public class MainActivity extends AppCompatActivity {
    MapView mapView = null;
    TextView timer_tv, spped_tv;
    //ImageView trafficLight_iv;
    public Marker userMarker;
    public Marker trafficLightMarker;
    public GeoPoint startPoint;
    public IMapController mapController;
    public int sekunden = 15;
    public long sumSeconds = 0;
    private boolean speed_up =true;
    AnimationDrawable Animation;
    public double bikeLat = 51.950138981967264;
    public double bikeLon = 7.638094425201416;
    public double tlLat = 51.950939109824986;
    public double tlLon = 7.636930346488953;

    public boolean trafficlightGreen;
    public double speed;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5*1000;   // 5 seconds
    int[] secondsDelay ={53,45,46,44,45,37,40,37,61,46,36,37,53,37};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocation();


        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //inflate and create the map
        setContentView(R.layout.activity_main);

        // setting mapView
        mapView = (MapView) findViewById(R.id.map);
        mapView.setTilesScaledToDpi(true);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setMapOrientation(45.0f);
        mapView.setBuiltInZoomControls(false);

        timer_tv = findViewById(R.id.tv_timer);
        spped_tv = findViewById(R.id.tv_speed);
        //trafficLight_iv = findViewById(R.id.iv_trafficLight);

        userMarker = new Marker(mapView);
        userMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.you_are_here));


        //user location
        final Marker bikeMarker = new Marker(mapView);
        bikeMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.bike));
        final GeoPoint bikePoint = new GeoPoint(bikeLat, bikeLon);
        bikeMarker.setPosition(bikePoint);
        mapView.getOverlays().add(bikeMarker);
        bikeMarker.setTitle("You are here!");

        //center position and zoom
        mapController = mapView.getController();
        mapController.setCenter(bikePoint);
        mapController.setZoom(15.5);

        //trafficLight location
        trafficLightMarker = new Marker(mapView);
        trafficLightMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.a_rot));
        GeoPoint trafficLightPoint = new GeoPoint(tlLat, tlLon);
        trafficLightMarker.setPosition(trafficLightPoint);
        mapView.getOverlays().add(trafficLightMarker);
        trafficLightMarker.setTitle("Traffic light");
        trafficlightGreen = false;

        //destination location
        Marker destinationMarker = new Marker(mapView);
        destinationMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.destinationflag));
        GeoPoint destinationMarkerPoint = new GeoPoint(51.95159705550733, 7.636149823665619);
        destinationMarker.setPosition(destinationMarkerPoint);
        mapView.getOverlays().add(destinationMarker);
        destinationMarker.setTitle("Destination");

        //7.637563347816466
        // 51.95056384502477

        ArrayList<GeoPoint> mRouteHigh = new ArrayList<>();
        mRouteHigh.add(new GeoPoint(51.950138981967264, 7.638094425201416));//
        mRouteHigh.add(new GeoPoint(51.95159705550733, 7.636149823665619));


        Polyline roadOverlay = new Polyline();
        roadOverlay.setWidth(30);
        roadOverlay.setColor(0x800000FF);
        roadOverlay.setGeodesic(true);
        roadOverlay.setPoints(mRouteHigh);

        // To show arrows on the route to indicate direction
        final Paint arrowPaint = new Paint();
        arrowPaint.setColor(Color.BLACK);
        arrowPaint.setStrokeWidth(3.0f);
        arrowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        arrowPaint.setAntiAlias(true);
        final Path arrowPath = new Path(); // a simple arrow towards the right
        arrowPath.moveTo(- 10, - 10);
        arrowPath.lineTo(10, 0);
        arrowPath.lineTo(- 10, 10);
        arrowPath.close();
        final List<MilestoneManager> managers = new ArrayList<>();
        managers.add(new MilestoneManager(
                new MilestonePixelDistanceLister(50, 50),
                new MilestonePathDisplayer(0, true, arrowPath, arrowPaint)
        ));
        roadOverlay.setMilestoneManagers(managers);

        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();

        ImageView arrowsImage= null;
        if(speed_up){
            arrowsImage = (ImageView) findViewById(R.id.imageView);
            arrowsImage.setBackgroundResource(R.drawable.animation);
        }else{
            arrowsImage = (ImageView) findViewById(R.id.imageView_down);
            arrowsImage.setBackgroundResource(R.drawable.animation_down);
        }

        Animation = (AnimationDrawable) arrowsImage.getBackground();
        Animation.start();

        //update traffic light status
        //updateTraffic();
        //new MyTask().execute("1000");


        /*final Handler handler = new Handler();
        final Runnable runnable;
        int delay = 15*1000;*/

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                if(trafficlightGreen) {
                    //trafficLightMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.a_rot));
                    //trafficLight_iv.setImageResource(R.drawable.a_rot);
                    //trafficlightGreen = false;
                    mapView.invalidate();
                }

                else {
                    //trafficLightMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.a_green));
                    //trafficLight_iv.setImageResource(R.drawable.a_green);
                    //trafficlightGreen = true;
                    mapView.invalidate();
                }
                handler.postDelayed(runnable, delay);
            }
        }, delay);

        Thread thread = new Thread() {
            final userData biker = new userData(bikeLat,bikeLon,tlLon, tlLat,System.currentTimeMillis());
            public double newBikeLat= bikeLat;
            public double newBikeLon = bikeLon;
            double i = 5;
            double j = 5;
            int k = 1;
            double[] l ={0.2,0.3,0.4};
            int tlRunner = 0;
            int lRunner = 0;
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(1000);
                        double difLon = bikeLon - tlLon;
                        double difLonOneMeter = difLon/120;
                        double mRoute = bikeLat-tlLat;
                        double mRouteOneMeter = mRoute/120;
                        bikeMarker.setPosition(new GeoPoint(bikeLat-mRouteOneMeter*i,bikeLon-difLonOneMeter*i));
                        biker.setLocation(bikeLat-mRouteOneMeter*i,bikeLon-difLonOneMeter*i, System.currentTimeMillis());
                        System.out.println(Double.toString(biker.getSpeed()));
                        speed = biker.getSpeed();
                        i = i+j;
                        j = j - k *l[lRunner];
                        if(k==1){
                            k=-1;
                        }else{
                            k=1;
                        }
                        if(lRunner == 2){
                            lRunner= 0;
                        }else{
                            lRunner++;
                        }
                        tlRunner++;
                        if(tlRunner == 20){
                            trafficLightMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.a_green));
                            trafficlightGreen = true;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        Log.d("Mact", "timer1");

        timer();

    }

    public void timer(){
        Log.d("Mact", "timer2");

        new CountDownTimer(20*1000, 1000) {
            int timeDown = 20;
            public void onTick(long millisUntilFinished) {
                if(timeDown >= 10 ){
                    timer_tv.setText("00:"+String.valueOf(timeDown--));
                }else{
                    timer_tv.setText("00:0"+String.valueOf(timeDown--));
                }

                //spped_tv.setText(Double.toString(speed*3.6).substring(0,3)+" km/h");
                spped_tv.setText(Double.toString(speed*3.6));

                Log.d("Mact", "timer3");
            }

            public void onFinish() {
                //_tv.setText("done!");
            }
        }.start();
    }

    public void getLocation() {
        final userData biker = new userData(null,null,51.950939109824986, 7.636930346488953,null);
        final calculation_suggested_speed calculator = new calculation_suggested_speed(0, 0, 0);
        new AppExecutors().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                long mLocTrackingInterval = 1; // 5 sec
                float trackingDistance = 1;
                LocationAccuracy trackingAccuracy = LocationAccuracy.MEDIUM;

                LocationParams.Builder builder = new LocationParams.Builder()
                        .setAccuracy(trackingAccuracy)
                        .setDistance(trackingDistance)
                        .setInterval(mLocTrackingInterval);
                SmartLocation.with(getApplicationContext())
                        .location()
                        // .continuous()
                        .config(builder.build())
                        .start(new OnLocationUpdatedListener() {
                            public void onLocationUpdated(Location location) {
                                Log.d("MapFragment222", "invoked3");
                                startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                userMarker.setPosition(startPoint);
                                mapView.getOverlays().add(userMarker);
                                userMarker.setTitle("You are here!");
                                //mapController.setCenter(startPoint);
                                //mapController.setZoom(15.5);
                                mapView.invalidate();
                                biker.setLocation(startPoint.getLatitude(),startPoint.getLongitude(), System.currentTimeMillis());
                                calculator.setDist(biker.getDistanceToTL());
                                calculator.setTime(sekunden);
                                calculator.setSpeed(biker.getSpeed());
                                calculator.evaluateSpeed();
                            }
                        });
            }
        });
    }
}


