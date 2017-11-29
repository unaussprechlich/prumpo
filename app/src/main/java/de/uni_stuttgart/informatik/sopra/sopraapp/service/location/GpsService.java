package de.uni_stuttgart.informatik.sopra.sopraapp.service.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

/**
 * Serving mostly-accurate GpsService-readings since 2017
 */
public class GpsService extends Service {

    // TODO: ask to enable GPS
    // TODO: pause service while app in background

    // Binder given to clients
    private IBinder mBinder = new LocalBinder();
    private Location lastLocation;

    LocationManager locationManager;

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public GpsService getService() {
            return GpsService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        locationManager  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return mBinder;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        return mBinder;
    }

    public LatLng getLastLocation() {
        if (lastLocation == null) return null;

        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }
}
