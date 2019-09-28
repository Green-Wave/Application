package com.example.greenwave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import util.AppExecutors;

public class MainActivity extends AppCompatActivity {
    MapView mapView = null;
    public Marker userMarker;
    public Marker trafficLightMarker;
    public GeoPoint startPoint;
    public IMapController mapController;
    public int sekunden = 15;
    public long sumSeconds = 0;

    public boolean trafficlightGreen;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 3*1000;

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

        userMarker = new Marker(mapView);
        userMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.you_are_here));


        //user location
        Marker bikeMarker = new Marker(mapView);
        bikeMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.bike));
        GeoPoint bikePoint = new GeoPoint(51.953384, 7.642753);
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
        GeoPoint trafficLightPoint = new GeoPoint(51.953323, 7.641664);
        trafficLightMarker.setPosition(trafficLightPoint);
        mapView.getOverlays().add(trafficLightMarker);
        trafficLightMarker.setTitle("Traffic light");
        trafficlightGreen = false;

        //destination location
        Marker destinationMarker = new Marker(mapView);
        destinationMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.destinationflag));
        GeoPoint destinationMarkerPoint = new GeoPoint(51.953258, 7.640949);
        destinationMarker.setPosition(destinationMarkerPoint);
        mapView.getOverlays().add(destinationMarker);
        destinationMarker.setTitle("Destination");


        ArrayList<GeoPoint> mRouteHigh = new ArrayList<>();
        mRouteHigh.add(new GeoPoint(51.953384, 7.642753));//
        mRouteHigh.add(new GeoPoint(51.953323, 7.641664));
        mRouteHigh.add(new GeoPoint(51.953258, 7.640949));

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


        //update traffic light status
        //updateTraffic();
        //new MyTask().execute("1000");


/*        final Handler handler = new Handler();
        final Runnable runnable;
        int delay = 15*1000;*/

        handler.postDelayed( runnable = new Runnable() {
            public void run() {

                if(trafficlightGreen) {
                    trafficLightMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.a_rot));
                    trafficlightGreen = false;
                    mapView.invalidate();
                }

                else {
                    trafficLightMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.a_green));
                    trafficlightGreen = true;
                    mapView.invalidate();
                }
                handler.postDelayed(runnable, delay);
            }
        }, delay);

    }

    public void getLocation() {
        final userData biker = new userData(null,null,51.953323, 7.641664,null);
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


