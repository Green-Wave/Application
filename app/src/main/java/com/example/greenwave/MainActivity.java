package com.example.greenwave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MainActivity extends AppCompatActivity {
    MapView mapView = null;
    AnimationDrawable upArrowAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //inflate and create the map
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);


        Marker startMarker = new Marker(mapView);
        startMarker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.you_are_here));
        GeoPoint startPoint = new GeoPoint(51.9521884,7.6382983);
        startMarker.setPosition(startPoint);
        mapView.getOverlays().add(startMarker);
        startMarker.setTitle("You are here!");

        IMapController mapController = mapView.getController();
        mapController.setCenter(startPoint);
        mapController.setZoom(15.5);

        ImageView upArrowImage = (ImageView) findViewById(R.id.upArrows);
        upArrowImage.setBackgroundResource(R.drawable.animated_arrows);
        upArrowAnimation = (AnimationDrawable) upArrowImage.getBackground();

        upArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upArrowAnimation.start();
            }
        });

    }
}
