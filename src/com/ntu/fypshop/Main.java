package com.ntu.fypshop;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;
import com.google.android.maps.GeoPoint;
//import com.google.android.maps.GeoPoint;
import com.ntu.fypshop.TwitterApp.TwDialogListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
//import android.os.Message;
//import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
//import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
//import android.widget.RadioGroup;
//import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements OnClickListener {

	private static final String APP_ID = "222592464462347";
	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";
	// FbConnect fbConnect;

	private static GlobalVariable globalVar;

	// private UserParticulars userS;
	private String fnameS;
	private String lnameS;
	private String nameS;
	// private String emailS;
	private Boolean fbBtn, twitBtn;
	private Facebook facebook;

	private TwitterApp mTwitter;
	private LocationManager locationManager;
	private GPSLocationListener locationListener;
	private GeoPoint point = new GeoPoint(1304256, 103832538);

	Handler mHandler = new Handler();
	private ProgressDialog mProgress;

	protected String[] employees;
	protected Integer[] employeesid;

	static final int DIALOG_ERR_LOGIN = 0, INIT_NORM = 0, INIT_FB = 1, INIT_TWIT = 2;

	// private String genderS;
	// private String bdayS;

	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;
	String result = null;

	Button latest, nearby, hot;// , logout;
	ListView searchResult;

	// static final int DATE_DIALOG_ID = 0;
	// private TextView bday;
	// private Button btn;
	// private EditText fname;

	// private EditText lname;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.browse);

		if (APP_ID == null)
		{
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see Example.java");
		}

		latest = (Button) findViewById(R.id.btnLatest);
		nearby = (Button) findViewById(R.id.btnNearby);
		hot = (Button) findViewById(R.id.btnHot);
		// logout = (Button) findViewById(R.id.logoutBtn);
		searchResult = (ListView) findViewById(R.id.listBrowse);

		latest.setOnClickListener(this);
		nearby.setOnClickListener(this);
		hot.setOnClickListener(this);
		latest.setEnabled(false);

		globalVar = ((GlobalVariable) getApplicationContext());
		fbBtn = globalVar.getfbBtn();
		twitBtn = globalVar.getTwitBtn();

		facebook = globalVar.getFBState();
		mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);
		globalVar.setTwitState(mTwitter);

		mProgress = new ProgressDialog(this);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new GPSLocationListener();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		Log.d("FbButton: ", fbBtn.toString());
		// SharedPreferences sharedPref =
		// getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);

		if (fbBtn || facebook.isSessionValid())
		{
			// fbConnect = new FbConnect(APP_ID, this, getApplicationContext());
			init(INIT_FB);
		}

		else if (twitBtn || mTwitter.hasAccessToken())
		{
			init(INIT_TWIT);
			// if(mTwitter.hasAccessToken())
			// {
			// // name.setText("Hello " + sharedPref.getString("user_name", "")
			// + ",");
			// }
			// else
			// {
			// globalVar.setTwitBtn(false);
			// mTwitter.authorize();
			// }
		}
		
		else
		{
			init(INIT_NORM);
		}
		// else
		// {
		// SharedPreferences userDetails =
		// getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
		// String Uname = userDetails.getString("emailLogin", "");
		// String pass = userDetails.getString("pwLogin", "");
		// Log.d("Uname: ", Uname);
		// Log.d("Password: ", pass);
		// if (Uname == "" && pass.equals(""))
		// {
		// Intent launchLogin = new Intent(this, LoginPage.class);
		// startActivity(launchLogin);
		// }
		// else
		// {
		// ConnectDB connectCheck = new ConnectDB(Uname, pass, 1);
		// if (connectCheck.inputResult())
		// {
		// // name.setText("Hello " + connectCheck.getName() + ",");
		// init(INIT_NORM);
		// }
		// else
		// {
		// // do something else
		// Log.d("Authenticate User: ", "False");
		// showDialog(DIALOG_ERR_LOGIN);
		// }
		// }
		// }

		// IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction("com.package.ACTION_LOGOUT");
		// registerReceiver(new BroadcastReceiver()
		// {
		//
		// @Override
		// public void onReceive(Context context, Intent intent)
		// {
		// Log.d("onReceive", "Logout in progress");
		// // At this point you should start the login activity and finish
		// // this one
		// Intent loginIntent = new Intent(SearchShops.this, LoginPage.class);
		// startActivity(loginIntent);
		// finish();
		// }
		// }, intentFilter);
	}

	private void init(final int type)
	{
		globalVar = ((GlobalVariable) getApplicationContext());

		getProduct("Latest");
		// TODO Auto-generated method stub
		// logout.setOnClickListener(new View.OnClickListener()
		// {
		// public void onClick(View v)
		// {
		// doLogout(type);
		// }
		// });

		searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id)
			{
				Intent intent = new Intent(Main.this, Productdetail.class);
				intent.putExtra("lastproductid", employeesid[pos]);
				startActivity(intent);
			}
		});
	}

	// private void doLogout(int type)
	// {
	// if (type == INIT_NORM)
	// {
	// // Logout logic here...
	// globalVar = ((GlobalVariable) getApplicationContext());
	// globalVar.setName("");
	// globalVar.setHashPw("");
	// globalVar.setEm("");
	//
	// SharedPreferences login = getSharedPreferences("com.ntu.fypshop",
	// MODE_PRIVATE);
	// SharedPreferences.Editor editor = login.edit();
	// editor.putString("emailLogin", "");
	// editor.putString("pwLogin", "");
	// editor.commit();
	// }
	// else if (type == INIT_FB)
	// {
	// // Go to LoginPage
	// SharedPreferences login = getSharedPreferences("com.ntu.fypshop",
	// MODE_PRIVATE);
	// SharedPreferences.Editor editor = login.edit();
	// editor.putString("facebookName", "");
	// editor.commit();
	// globalVar = ((GlobalVariable) getApplicationContext());
	// Facebook mFacebook = globalVar.getFBState();
	// globalVar.setfbBtn(false);
	// SessionEvents.onLogoutBegin();
	// AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
	// asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
	// }
	// else
	// {
	// mTwitter.resetAccessToken();
	// globalVar.setTwitBtn(false);
	// }
	//
	// // Return to the login activity
	// Intent intent = new Intent(this, LoginPage.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// startActivity(intent);
	// }

	@Override
	public void onClick(View v)
	{
		if (v == latest)
		{
			getProduct("Latest");
			latest.setEnabled(false);
			nearby.setEnabled(true);
			hot.setEnabled(true);
		}
		else if (v == nearby)
		{
			getProduct("Nearby");
			nearby.setEnabled(false);
			latest.setEnabled(true);
			hot.setEnabled(true);
		}
		else if (v == hot)
		{
			getProduct("Hot");
			hot.setEnabled(false);
			nearby.setEnabled(true);
			latest.setEnabled(true);
		}
	}

	public void getProduct(String mode)
	{
		searchResult.setAdapter(null);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (mode.equals("Latest"))
		{
			nameValuePairs.add(new BasicNameValuePair("latest", "1"));
		}
		else if (mode.equals("Nearby"))
		{
			String lat = Double.toString(point.getLatitudeE6() / 1E6);
			String lng = Double.toString(point.getLongitudeE6() / 1E6);
			String radius = "1000";
			nameValuePairs.add(new BasicNameValuePair("nearby", "1"));
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lng", lng));
			nameValuePairs.add(new BasicNameValuePair("radius", radius));

		}
		else if (mode.equals("Hot"))
		{
			nameValuePairs.add(new BasicNameValuePair("hot", "1"));
		}
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://10.0.2.2/browse.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response != null)
			{
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}
		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error in http connection" + e.toString());
		}
		// convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = "0";
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.d("result: ", result);
		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		// paring data
		int ct_id;
		String ct_name;
		try
		{
			jArray = new JSONArray(result);
			JSONObject json_data = null;
			employees = new String[jArray.length()];
			employeesid = new Integer[jArray.length()];
			for (int i = 0; i < jArray.length(); i++)
			{
				json_data = jArray.getJSONObject(i);
				ct_id = json_data.getInt("id");
				ct_name = json_data.getString("filename");
				Log.d("ct_id: ", Integer.toString(ct_id));
				Log.d("ct_name: ", ct_name);
				employees[i] = ct_name;
				employeesid[i] = ct_id;
				Log.d("employeesid: ", Integer.toString(employeesid[i]));
			}
			ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, employees);
			searchResult.setAdapter(adapter);
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "No products Found", Toast.LENGTH_SHORT).show();
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}
	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener()
	{
		@Override
		public void onComplete(String value)
		{
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;

			// SharedPreferences sharedPref =
			// getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			// name.setText("Hello " + username + ",");
			// name.setText("Hello " + sharedPref.getString("user_name", "") +
			// ",");
			// mTwitterBtn.setText("  Twitter  (" + username + ")");
			// mTwitterBtn.setChecked(true);
			// mTwitterBtn.setTextColor(Color.WHITE);

			// Toast.makeText(TestConnect.this, "Connected to Twitter as " +
			// username, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(String value)
		{
			// mTwitterBtn.setChecked(false);
			//
			// Toast.makeText(TestConnect.this, "Twitter connection failed",
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel()
		{
			// Return to the login activity
			Intent intent = new Intent(Main.this, LoginPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	};

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle item selection
	// switch (item.getItemId()) {
	// case R.id.clothes:
	// showResults("Clothes");
	// return true;
	// case R.id.others:
	// showResults("Others");
	// return true;
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }

	public class LogoutRequestListener extends BaseRequestListener {
		public void onComplete(String response, final Object state)
		{

			// callback should be run in the original thread,
			// not the background thread
			mHandler.post(new Runnable()
			{
				public void run()
				{
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}

	private class GPSLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location)
		{
			if (location != null)
			{

				point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));

				// add marker
				// MapOverlay mapOverlay = new MapOverlay(MY_POINT);
				// mapOverlay.setPointToDraw(point);
				// listOfOverlays = mapView.getOverlays();
				// listOfOverlays.clear();
				// listOfOverlays.add(mapOverlay);

				// String address = ConvertPointToLocation(point);
				// Log.d("Address: ", address);

				// Drawable drawable =
				// getResources().getDrawable(R.drawable.red);

				// searchStores(point);

				// mapView.invalidate();
			}
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}
	}
}
