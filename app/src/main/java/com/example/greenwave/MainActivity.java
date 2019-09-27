package com.example.greenwave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MainActivity extends AppCompatActivity {
    MapView mapView = null;
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
    }
}
