package com.hcmus.picbox.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    GoogleMap map;
    LatLng position;

    public MapFragment(LatLng position){
        this.position=position;
        getMapAsync(this);
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map=googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        map.addMarker(new MarkerOptions().position(this.position));
        CameraPosition cp = new CameraPosition.Builder().target(this.position).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    @Override
    public void onPause() {
        super.onPause();
        getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMapAsync(this);
    }
}
