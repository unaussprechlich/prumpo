package de.uni_stuttgart.informatik.sopra.sopraapp.feature.location;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Serving mostly-accurate GpsService-readings since 2017.
 */
public class GpsService {

    private final Criteria criteria = new Criteria();

    private Context context;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Location lastLocation;

    private boolean locationWasDisabled = true;

    private boolean hadPermission = false;

    private List<LocationCallbackListener> subscribers = new ArrayList<>();

    private RetryRunUntil retryRunUntil;

    // to manage single location callbacks
    private boolean singleCallbackOver = false;
    public GpsService(Application app) {

        context = app;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setCostAllowed(true);
    }

    /**
     * Call this method after receiving a {@code GpsService} instance,
     * to start receiving location data.
     *
     * @return {@code false} if {@code ACCESS_FINE_LOCATION} permissions were missing,
     * (i.e. the starting attempt failed), {@code true} otherwise.
     */
    public boolean startGps() {
        // in case the settings changed since the last stop
        locationWasDisabled = !isLocationEnabled();

        // TODO: try to make sure it's enabled on startup!
        if ((ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            hadPermission = false;
            retryRunUntil.run();
            return false;
        }

        hadPermission = true;

        // create fresh LocationListener
        locationListener = new LocationListenerService();

        // bind new gps callback
        locationManager.requestLocationUpdates(0, 0, criteria, locationListener, Looper.getMainLooper());

        return true;
    }

    public boolean startGps(RetryRunUntil retryRunUntil) {
        this.retryRunUntil = retryRunUntil;
        return startGps();
    }

    /**
     * This method must be called in either
     * {@link Activity#onDestroy} or {@link Activity#onStop()}.
     * <p>
     * This guarantees graceful listener-bindings of this service and avoids service-leaks.
     */
    public void stopGps() {
        // clear for accuracy reasons
        lastLocation = null;

        retryRunUntil.stop();

        /* unbinding callbacks reduces battery-usage dramatically */
        if (locationListener != null)
            locationManager.removeUpdates(locationListener);

        // orphan object to disable callbacks ASAP
        locationListener = null;


    }

    /**
     * After running this method {@link GpsService#singleLocationCallback}
     * won't run any supplied callbacks.
     */
    public void stopSingleCallback() {
        singleCallbackOver = true;
    }

    /**
     * After running this method {@link GpsService#singleLocationCallback}
     * won't run any supplied callbacks. (Ongoing requests must be rebound, as well).
     */
    public void stopAllCallbacks() {
        // notify all subscribers, that the current location update failed
        for (LocationCallbackListener subscriber : subscribers) {
            subscriber.onLocationNotFound();
        }

        // subscribers must resubscribe manually after the callback was stopped
        subscribers.clear();

        stopSingleCallback();
    }

    /**
     * Indicates that location services of the device need to be enabled.
     *
     * @return <src>true</src> if the last refresh of GpsService found missing location services.
     */
    public boolean wasLocationDisabled() {
        return locationWasDisabled;
    }

    public LatLng lastKnownLatLng() {
        if (lastLocation == null) return null;

        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    public Location lastKnownLocation() {
        return lastLocation;
    }

    public void ongoingLocationCallback(LocationCallbackListener listener) {
        subscribers.add(listener);
    }

    /**
     * Provides callback-bindings for a single {@code Location} reading, or nothing,
     * if a certain time limit is reached. The callbacks are mutually exclusive.
     *
     * @param callback  {@code LocationCallbackListener} that encapsulates what to do on callback
     * @param failAfter the time in milliseconds allowed to pass, before
     *                  {@link LocationCallbackListener#onLocationNotFound()} is called.
     */
    public void singleLocationCallback(LocationCallbackListener callback, long failAfter) {
        singleCallbackOver = false;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            callback.onLocationNotFound();
            return;
        }

        locationManager.requestSingleUpdate(
                criteria,
                new LocationListener() {

                    {
                        Handler handler = new Handler();
                        handler.postDelayed(this::fail, failAfter);
                    }

                    private void fail() {
                        if (singleCallbackOver) return;

                        singleCallbackOver = true;

                        if (lastLocation == null) {
                            callback.onLocationNotFound();
                            return;
                        }

                        callback.onLocationFound(lastLocation);
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        if (singleCallbackOver) return;

                        singleCallbackOver = true;
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

    /**
     * Checks if any localization-services are enabled, at all.
     *
     * @return <src>true</src> if at least one provider enabled, <src>false</src> otherwise.
     */
    private boolean isLocationEnabled() {
        int locationMode;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    private final class LocationListenerService implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;

            for (LocationCallbackListener subscriber : subscribers) {
                subscriber.onLocationFound(location);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            locationWasDisabled = !isLocationEnabled();
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

    public static class RetryRunUntil implements Runnable {

        Runnable nextTry;

        AtomicBoolean succeeded;
        long period;

        public RetryRunUntil(Runnable runnable, AtomicBoolean succeeded, long period) {
            nextTry = runnable;
            this.succeeded = succeeded;
            this.period = period;
        }

        void stop() {
            succeeded.set(true);
        }

        @Override
        public void run() {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (succeeded.get()) return;
                nextTry.run();
                retry();
            }, period);
        }

        public void retry() {
            if (succeeded.get()) return;

            run();
        }
    }
}
