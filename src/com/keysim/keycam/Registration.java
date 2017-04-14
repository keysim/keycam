package com.keysim.keycam;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Registration extends Activity {
	String regid;
	GoogleCloudMessaging gcm;
	Context context;
	ProgressDialog dialog = null;
	EditText login;
	Button btn_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		context = getApplicationContext();
		login = (EditText) findViewById(R.id.txt_login);
		btn_login = (Button) findViewById(R.id.btn_login);
		
		//deleteRegistrationId(context);
		regid = getRegistrationId(context);
		if (!regid.isEmpty())
			finish();
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(login.getText().length() > 0){
					dialog = ProgressDialog.show(Registration.this, "", "Login...");
			        getRegId();
				}
			}
		});
	}
	public void getRegId(){
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					regid = gcm.register(Config.PROJECT_NUMBER);
					storeRegistrationId(getApplicationContext(), regid);
					log_me(regid);
				} catch (IOException ex) {
				}
				return null;
			}
		}.execute(null, null, null);
	}
	@SuppressWarnings("unused")
	private void deleteRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    Log.i(Config.LOG_TAG, "Delete regId");
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(Config.PROPERTY_REG_ID, "");
	    editor.commit();
	}
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(Config.LOG_TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(Config.PROPERTY_REG_ID, regId);
	    editor.putInt(Config.PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	public void log_me(String id)
	{
		final HashMap<String, String> data = new HashMap<String, String>();

		data.put("id", id);
		data.put("login", login.getText().toString());
		new Thread(new Runnable() {
			public void run() {
				Log.e(Config.LOG_TAG, getResult(data));
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(Registration.this, "Welcome.", Toast.LENGTH_SHORT).show();
					}
				});
				dialog.dismiss();
				finish();
			}
		}).start();
	}
	protected String getResult(HashMap<String, String> data) {
		byte[] toByte;
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Config.URL_POST_REGISTER);

		try
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			Iterator<String> dataIt =  data.keySet().iterator();
			while(dataIt.hasNext()) {
				String key=(String)dataIt.next();
				String value=(String)data.get(key);
				nameValuePair.add(new BasicNameValuePair(key, value));
			}

			post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
				toByte = EntityUtils.toByteArray(response.getEntity());
				result = new String(toByte, "UTF-8");
			}
		}
		catch(Exception e){
			Log.e(Config.LOG_TAG, "Simon = " + e.getMessage());
		}
		return result;
	}
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(Config.PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(Config.LOG_TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(Config.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(Config.LOG_TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
}
