package cmu.ece.BaihuQian.ECGDisplayUI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

public class UserBehaviorActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener{
	public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DETECTION_INTERVAL_SECONDS = 5;
    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    
    private PendingIntent mActivityRecognitionPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;
    
    private boolean mInProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_behavior);

		/*
         * Instantiate a new activity recognition client. Since the
         * parent Activity implements the connection listener and
         * connection failure listener, the constructor uses "this"
         * to specify the values of those parameters.
         */
        mActivityRecognitionClient =
                new ActivityRecognitionClient(this, this, this);
        /*
         * Create the PendingIntent that Location Services uses
         * to send activity recognition updates back to this app.
         */
        Intent intent = new Intent(
                this, ActivityRecognitionIntentService.class);
        /*
         * Return a PendingIntent that starts the IntentService.
         */
        mActivityRecognitionPendingIntent =
                PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        mInProgress = false;
        startUpdates();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_behavior, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// TODO Auto-generated method stub
		mInProgress = false;
		if(connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		// TODO Auto-generated method stub
		/*
         * Request activity recognition updates using the preset
         * detection interval and PendingIntent. This call is
         * synchronous.
         */
        mActivityRecognitionClient.requestActivityUpdates(
                DETECTION_INTERVAL_MILLISECONDS,
                mActivityRecognitionPendingIntent);
        /*
         * Since the preceding call is synchronous, turn off the
         * in progress flag and disconnect the client
         */
        //mInProgress = false;
        //mActivityRecognitionClient.disconnect();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		// Turn off the request flag
        mInProgress = false;
        // Delete the client
        mActivityRecognitionClient = null;
	}
	private boolean servicesConnected() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(ConnectionResult.SUCCESS == resultCode) {
			return true;
		}
		else {
			return false;
		}
	}
	public void startUpdates() {
		if(!servicesConnected()) {
			return;
		}
		if(!mInProgress) {
			mInProgress = true;
			mActivityRecognitionClient.connect();
		}
	}
}
