package com.biziit.taxi;

import java.io.IOException;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.biziit.taxi.mapapi.ConverUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TaxiPsgerActivity extends FragmentActivity {

	private static final int LOCATION_CODE = 1;
	private static final String TAG = "TaxiPsgerActivity";
	private static final String LAST_LAT = "last_latutide";
	private static final String LAST_LNG = "last_longitude";
	
	private static final int CONTINUE_MSG = 1;
	protected static final String LOGIN_NAME = "login_name";
	protected Location location;
	protected LocationManager lm;
	private Handler UiHandler  = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch(msg.what) {
				case CONTINUE_MSG :
					double[] lat = (double[]) msg.obj;
					Log.d(TAG,"geo result, lng: " + lat[0] + ", lat: " + lat[1]);
					if (needLogin()) {
						startLoginActivity();
					} else {
						
					}
					saveCurrentLat(lat[0], lat[1]);
					break;
			}
		}

	};
	
	public SharedPreferences preferces;
	double lastLatitude, lastLongtitude;
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private LocationSource mLocationSource;
    private Geocoder geoCoder;
	private LatLng currentLatLng;

	private EditText etAddress;
	private Button continueButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi_psger);
		
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        Criteria criteria = new Criteria();  
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);  
        criteria.setAltitudeRequired(false);  
        criteria.setBearingRequired(false);  
        criteria.setCostAllowed(false);  
        criteria.setPowerRequirement(Criteria.POWER_HIGH);  
        String provider = lm.getBestProvider(criteria, true);
        Log.d(TAG,"LocationProvider: " + provider);
        if(!LocationManager.GPS_PROVIDER.equals(provider)&&!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            new AlertDialog.Builder(this)
			.setTitle(provider==null?R.string.enableLocation:R.string.enableGPS)
			.setPositiveButton("Goto", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
		            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_CODE);
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).show();
        }
        setUpMapIfNeeded();
		ConnectionDetector connDetetor = new ConnectionDetector(getApplicationContext());
		if (!connDetetor.isNetworkConnected()) {
			showToast(R.string.conn_network_fail);
		}
		IntentFilter intentFilter = new IntentFilter(); 
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
		registerReceiver(connectionReceiver, intentFilter);
		preferces = getPreferences(Activity.MODE_PRIVATE);
		lastLatitude = Double.parseDouble(preferces.getString(LAST_LAT, "3.0800"));
		lastLongtitude = Double.parseDouble(preferces.getString(LAST_LNG, "101.4200"));
		etAddress = (EditText) findViewById(R.id.editText_start_address);
		continueButton = (Button)findViewById(R.id.conitue_button);  
    	continueButton.setOnClickListener(new OnClickListener() {
    		@Override  
            public void onClick(View v) {
    			Runnable queryLat = new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String startAddress = etAddress.getEditableText().toString().trim();
						Log.d(TAG,"startAddress: " + startAddress);
						double[] results = ConverUtil.getLocationInfo(startAddress);
//						Log.d(TAG,"geo result: " + results[0] + ", " + results[1]);
						Message msg = UiHandler.obtainMessage(CONTINUE_MSG,1,1,results);
						UiHandler.sendMessage(msg);
					}
				};
				new Thread(queryLat).start();
            }
    	});
	}

	protected void startLoginActivity() {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
		} catch (ActivityNotFoundException exception) {
			Log.e(TAG,exception.toString());
		}
	}

	protected void saveCurrentLat(double lng, double lat) {
		// TODO Auto-generated method stub
		Editor editor = preferces.edit();
		if (null != editor) {
			editor.putString(LAST_LNG, Double.toString(lng));
			editor.putString(LAST_LAT, Double.toString(lat));
		}
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
    public void onStart() {
      super.onStart();
      EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance().activityStop(this); // Add this method.
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
        //myLocationText = (TextView)findViewById(R.id.myLocationText);
        if (location != null){
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                latLongString = "Î³¶È£º" + lat +", ¾­¶È: " + lng;
                Log.d(TAG,"lat:" + lat +", lng:" + lng);
                try {
    				int latitude = (int) location.getLatitude();
    				int longitude = (int) location.getLongitude();
    				List<Address> list = geoCoder.getFromLocation(latitude, longitude, 2);
					Log.d(TAG,"list.size: " + list.size());
    				for (int i = 0; i < list.size(); i++) {
    					Address address = list.get(i);
    					Toast.makeText(
    							TaxiPsgerActivity.this,
    							address.getCountryName() + address.getAdminArea()
    									+ address.getFeatureName(), Toast.LENGTH_LONG)
    							.show();
    					Log.d(TAG,"adress: " + address);
    				}
    			} catch (IOException e) {
    				Toast.makeText(TaxiPsgerActivity.this, e.getMessage(), Toast.LENGTH_LONG)
    						.show();
    			}
                currentLatLng = new LatLng(lat, lng);                
        }else {
                latLongString = getResources().getString(R.string.get_location_fail);
        }
        Log.i(TAG,latLongString);
//        showToast(latLongString);
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
        } else {
        	currentLatLng = new LatLng(lastLatitude, lastLongtitude);
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
	private boolean needLogin() {
		// TODO Auto-generated method stub
		String loginNameString = preferces.getString(LOGIN_NAME, "");
		if (!"".equals(loginNameString)) {
			return false;
		}
		return true;
	}
}
