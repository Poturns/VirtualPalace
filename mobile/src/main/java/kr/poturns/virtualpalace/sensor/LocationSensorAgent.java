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
 * <b> 위치 센서 AGENT </b>
 *
 * @author Yeonho.Kim
 */
public class LocationSensorAgent extends BaseSensorAgent implements LocationListener {

    // * * * C O N S T A N T S * * * //
    public static final int DATA_INDEX_LATITUDE = 1;
    public static final int DATA_INDEX_LONGITUDE = 2;
    public static final int DATA_INDEX_ALTITUDE = 3;

    private static final long DISTANCE_CONFIDENCE_MAXIMUM_DEVIATION = 1000;         // 단위: m = 1km
    private static final long TIME_CONFIDENCE_MAXIMUM_DEVIATION = 30 * 60 * 1000;   // 단위: ms = 30분
    private static final long TIME_CONFIDENCE_MAXIMUM_DIFFERENCE = 5 * 60 * 1000;   // 단위: ms = 5분

    private final Context mContextF;
    private final LocationManager mLocationManagerF;

    private final OnDataCollaborationListener mCollaborationListenerF = new OnDataCollaborationListener() {
        @Override
        public void onCollaboration(int thisType, int targetType, double[] thisData, double[] targetData) {
            switch (targetType) {
                case TYPE_AGENT_BATTERY:
                    int percentage = (int) targetData[BatterySensorAgent.DATA_INDEX_LEVEL];

                    break;

                case TYPE_AGENT_NETWORK:

                    break;
            }
        }
    };


    // * * * F I E L D S * * * //
    private Location mLatestLocation;


    // * * * C O N S T R U C T O R S * * * //
    public LocationSensorAgent(Context context) {
        mContextF = context;
        mLocationManagerF = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    // * * * I N H E R I T S * * * //
    @Override
    public void startListening() {
        super.startListening();

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
        super.stopListening();
    }

    @Override
    public int getAgentType() {
        return TYPE_AGENT_LOCATION;
    }

    @Override
    public double[] getLatestData() {
        if (mLatestLocation == null)
            return new double[4];

        return new double[]{
                mLatestLocation.getElapsedRealtimeNanos(),
                mLatestLocation.getLatitude(),
                mLatestLocation.getLongitude(),
                mLatestLocation.getAltitude()
        };
    }

    /**
     * {@link #setCollaborationWith(BaseSensorAgent, OnDataCollaborationListener)} with Default-Listener
     * @param agent
     */
    public void setCollaborationWith(BaseSensorAgent agent) {
        setCollaborationWith(agent, mCollaborationListenerF);
    }

    @Override
    public void onLocationChanged(Location location) {
        analyseBestLocation(location);

        mLatestMeasuredTimestamp = System.currentTimeMillis();
        mLatestLocation = compareBestLocation(mLatestLocation, location);

        onDataMeasured();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }



    // * * * M E T H O D S * * * //
    private Location compareBestLocation(Location locationA, Location locationB) {
        if (locationA == null && locationB == null)
            return null;
        else
        if (locationA == null)
            return locationB;
        else
        if (locationB == null)
            return locationA;

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
        // TODO :
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
