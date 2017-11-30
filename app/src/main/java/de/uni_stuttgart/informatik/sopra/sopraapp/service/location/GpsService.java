package de.uni_stuttgart.informatik.sopra.sopraapp.service.location;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

/**
 * Serving mostly-accurate GpsService-readings since 2017
 */
public class GpsService extends Service {

    // TODO: implement selection of best provider possible!

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public final class LocalBinder extends Binder {

        public GpsService getService() {
            return GpsService.this;
        }
    }

    private static LocationManager locationManager;
    private static Location lastLocation;

    private static boolean locationWasDisabled = true;
    private static boolean hadPermission = false;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private static LocationListener locationListener;

    public LatLng lastKnownLocation() {
        if (lastLocation == null) return null;

        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    /**
     * Call this method after receiving a {@code GpsService} instance,
     * to start receiving location data.
     *
     * @return  {@code false} if {@code ACCESS_FINE_LOCATION} permissions were missing,
     *          (i.e. the starting attempt failed), {@code true} otherwise.
     */
    public boolean startGps() {
        // in case the settings changed since the last stop
        locationWasDisabled = !isLocationEnabled();

        if ((ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            hadPermission = false;
            return false;
        }

        hadPermission = true;

        // bind new gps callback
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        return true;
    }

    /**
     * This method must be called in either
     * {@link Activity#onDestroy} or {@link Activity#onStop()}.
     * <p>
     *      This guarantees graceful listener-bindings of this service and avoids service-leaks.
     */
    public void stopGps() {
        // clear for accuracy reasons
        lastLocation = null;

        if (locationManager == null) return;

        // unbinding callback reduces battery-usage dramatically
        locationManager.removeUpdates(locationListener);

        // orphan object to disable callback ASAP
        locationListener = null;
    }

    /**
     * Checks if any localization-services are enabled, at all.
     *
     * @return <src>true</src> if at least one provider enabled, <src>false</src> otherwise.
     */
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
     * Indicates that location services of the device need to be enabled.
     *
     * @return  <src>true</src> if the last refresh of GpsService found missing location services.
     */
    public boolean wasLocationDisabled() {
        return locationWasDisabled;
    }

    /**
     * Provides callback-bindings for a single {@code Location} reading, or nothing,
     * if a certain time limit is reached. The callbacks are mutually exclusive.
     *
     * @param callback  {@code LocationCallbackListener} that encapsulates what to do on callback
     *
     * @param failAfter the time in milliseconds allowed to pass, before
     *                  {@link LocationCallbackListener#onLocationNotFound()} is called.
     */
    public void singleLocationCallback(LocationCallbackListener callback, long failAfter) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            callback.onLocationNotFound();
            return;
        }

        locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                new LocationListener() {

                    private boolean callbackOver = false;

                    {
                        Handler handler = new Handler();
                        handler.postDelayed(this::fail, failAfter);
                    }

                    private void fail() {
                        if (callbackOver) return;

                        callbackOver = true;
                        callback.onLocationNotFound();
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        if (callbackOver) return;

                        callbackOver = true;
                        callback.onLocationFound(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                },
                Looper.getMainLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // in case the settings changed since the last check
        locationWasDisabled = !isLocationEnabled();

        locationListener = new ServiceLocationListener();

        if (locationManager == null) {
            locationManager = (LocationManager)
                    getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        }

        return mBinder;
    }

    private final class ServiceLocationListener implements LocationListener {

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
    }
}
