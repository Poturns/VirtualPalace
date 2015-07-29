package kr.poturns.virtualpalace.sensor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class LocationAgent extends BaseAgent implements LocationListener {

    public static final int DATA_INDEX_LATITUDE = 1;
    public static final int DATA_INDEX_LONGITUDE = 2;
    public static final int DATA_INDEX_ALTITUDE = 3;

    private static final long DISTANCE_CONFIDENCE_MAXIMUM_DEVIATION = 1000;         // 단위: m = 1km
    private static final long TIME_CONFIDENCE_MAXIMUM_DEVIATION = 30 * 60 * 1000;   // 단위: ms = 30분
    private static final long TIME_CONFIDENCE_MAXIMUM_DIFFERENCE = 5 * 60 * 1000;   // 단위: ms = 5분

    private final Context mContextF;
    private final LocationManager mLocationManagerF;

    private Location mLatestLocation;

    private final OnDataCollaborationListener mCollaborationListenerF = new OnDataCollaborationListener() {
        @Override
        public void onCollaboration(int thisType, int targetType, double[] thisData, double[] targetData) {
            switch (targetType) {
                case TYPE_AGENT_BATTERY:
                    int percentage = (int) targetData[BatteryAgent.DATA_INDEX_LEVEL];

                    break;

            }
        }
    };

    public LocationAgent(Context context) {
        mContextF = context;
        mLocationManagerF = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    @Override
    public void startListening() {
        Location gpsLocation = mLocationManagerF.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location networkLocation = mLocationManagerF.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        mLatestLocation = compareBestLocation(gpsLocation, networkLocation);

        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(true);
        mLocationManagerF.requestSingleUpdate(criteria, this, mContextF.getMainLooper());
    }

    @Override
    public void stopListening() {

    }

    @Override
    public int getAgentType() {
        return TYPE_AGENT_LOCATION;
    }

    /**
     * @return
     */
    @Override
    public double[] getLatestData() {
        if (mLatestLocation == null)
            return new double[0];

        return new double[]{
                mLatestLocation.getElapsedRealtimeNanos(),
                mLatestLocation.getLatitude(),
                mLatestLocation.getLongitude(),
                mLatestLocation.getAltitude()
        };
    }

    public void setCollaborationWith(BaseAgent agent) {
        setCollaborationWith(agent, mCollaborationListenerF);
    }

    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        analyseBestLocation(location);
        mLatestMeasuredTimestamp = System.currentTimeMillis();

        onDataMeasured();
    }

    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {@link LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {@link LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {@link LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *                 <p/>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p/>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }

    private Location compareBestLocation(Location locationA, Location locationB) {
        long timeA = locationA.getElapsedRealtimeNanos();
        long timeB = locationB.getElapsedRealtimeNanos();

        if (Math.abs(timeA - timeB) < TIME_CONFIDENCE_MAXIMUM_DIFFERENCE) {
            float accuracyA = locationA.getAccuracy();
            float accuracyB = locationB.getAccuracy();

            return accuracyA > accuracyB ? locationA : locationB;

        } else
            return timeA > timeB ? locationA : locationB;
    }

    private void analyseBestLocation(Location location) {
        location.getAccuracy();
        location.getLatitude();
        location.getLongitude();
        location.getElapsedRealtimeNanos();
    }

    public static void requestTurningOnGps(final Context contextF) {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        contextF.startActivity(intent);
                        break;
                }
            }
        };

        new AlertDialog.Builder(contextF)
                .setTitle(0)
                .setMessage(0)
                .setPositiveButton(0, clickListener)
                .setNegativeButton(0, null)
                .show();
    }
}
