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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Serving mostly-accurate GpsService-readings since 2017
 */
public class GpsService extends Service {

    // TODO: implement MORE capabilities MORE thoroughly

    private static Location lastLocation;
    private static LocationManager locationManager;

    public boolean locationWasDisabled = true;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
            locationWasDisabled = !isLocationEnabled();
        }

        @Override
        public void onProviderDisabled(String s) {
            locationWasDisabled = !isLocationEnabled();
        }
    };

    public LatLng lastKnownLocation() {
        if (lastLocation == null) return null;

        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    public void pauseGps() {
        locationManager.removeUpdates(locationListener);
    }

    public void resumeGps() {
        locationWasDisabled = !isLocationEnabled();

        if ((ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)) return;

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public boolean isLocationEnabled() {
        int locationMode;

            try {
                locationMode = Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                                        Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

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
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        resumeGps();

        return mBinder;
    }
}
