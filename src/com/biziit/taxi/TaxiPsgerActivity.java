package com.biziit.taxi;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class TaxiPsgerActivity extends FragmentActivity {

	private static final String TAG = "TaxiPsgerActivity";
	private static final String LAST_LAT = "last_latutide";
	private static final String LAST_LNG = "last_longitude";
	
	protected Location location;
	protected LocationManager lm;
	private Handler UiHandler;
	
	public SharedPreferences preferces;
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private LocationSource mLocationSource;
    private Geocoder geoCoder;
	private LatLng currentLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi_psger);
		
		UiHandler = new Handler();
        String seviceName = Context.LOCATION_SERVICE;
        lm = (LocationManager)getSystemService(seviceName);
        
        Criteria criteria = new Criteria();  
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);  
        criteria.setAltitudeRequired(false);  
        criteria.setBearingRequired(false);  
        criteria.setCostAllowed(false);  
        criteria.setPowerRequirement(Criteria.POWER_HIGH);  
        String provider = lm.getBestProvider(criteria, true);
        Log.d(TAG,"LocationProvider: " + provider);
        location = lm.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        lm.requestLocationUpdates(provider, 2000, 10, locationListener);
        setUpMapIfNeeded();
        geoCoder = new Geocoder(TaxiPsgerActivity.this);
		ConnectionDetector connDetetor = new ConnectionDetector(getApplicationContext());
		if (!connDetetor.isNetworkConnected()) {
			showToast(R.string.conn_network_fail);
		}
		IntentFilter intentFilter = new IntentFilter(); 
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
		registerReceiver(connectionReceiver, intentFilter);
		preferces = getPreferences(Activity.MODE_PRIVATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.taxi_psger, menu);
		return true;
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    
    @Override
    protected void onDestroy() {
		if (connectionReceiver != null) {
			unregisterReceiver(connectionReceiver);
		}
		super.onDestroy();
    }
    
	public void showToast(final String s) {
		UiHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), s,
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void showToast(final int resId) {
		UiHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), getResources().getString(resId), 
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private final LocationListener locationListener = new LocationListener() {

		public void onLocationChanged(String provider) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnavled(String probider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}
	};
	
	private void updateWithNewLocation(Location location) {
        // TODO Auto-generated method stub
        String latLongString;
        TextView myLocationText;
        //myLocationText = (TextView)findViewById(R.id.myLocationText);
        if (location != null){
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                latLongString = "Î³¶È£º" + lat +", ¾­¶È: " + lng;
                Log.d(TAG,"lat:" + lat +", lng:" + lng);
//                try {
//    				int latitude = (int) location.getLatitude();
//    				int longitude = (int) location.getLongitude();
//    				List<Address> list = geoCoder.getFromLocation(latitude, longitude, 2);
//    				for (int i = 0; i < list.size(); i++) {
//    					Address address = list.get(i);
//    					Toast.makeText(
//    							TaxiPsgerActivity.this,
//    							address.getCountryName() + address.getAdminArea()
//    									+ address.getFeatureName(), Toast.LENGTH_LONG)
//    							.show();
//    				}
//    			} catch (IOException e) {
//    				Toast.makeText(TaxiPsgerActivity.this, e.getMessage(), Toast.LENGTH_LONG)
//    						.show();
//    			}
                currentLatLng = new LatLng(lat, lng);                
        }else {
                latLongString = getResources().getString(R.string.get_location_fail);
        }
        Log.i(TAG,latLongString);
        showToast(latLongString);
	}
	
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setLocationSource(mLocationSource);
//        mMap.setOnMapLongClickListener(mLocationSource);
        mMap.setMyLocationEnabled(true);
        if (currentLatLng != null) {
        	mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(currentLatLng, 14)));
        }
    }
    
	@Override  
	public void onConfigurationChanged(Configuration config) {
	    super.onConfigurationChanged(config);
	}
	
	BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				Log.i(TAG, "unconnect");
				// unconnect network
			} else {
				// connect network
			}
		}
	};
}
