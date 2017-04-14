package com.keysim.keycam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	SurfaceView dummy;
	private Bitmap bmp;
	byte[] data_file;
	private ImageView photo;
	private TextView mDisplay;
	EditText pass;
	String answer = "";
	Handler mHandler;
	HashMap<String, String> request;
	String file_name = "";
	String delete_file = "";
	Context mContext;
	ProgressDialog dialog = null;
	Camera mCamera;
	private Parameters params;
	Camera.PictureCallback mCall = null;
	Vibrator wizz;
	Boolean busy = false;
	private MediaPlayer mPlayer = null;
	Intent home = null;
	String login = "";
	private TextToSpeech mTts;
	String mLight;
	Boolean front_cam = false;
	Boolean authorized = false;
	String externalDir;

	@Override  
	public void onCreate(Bundle savedInstanceState)  
	{  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_main);

		photo = (ImageView) findViewById(R.id.photo);
		mDisplay = (TextView) findViewById(R.id.message);
		pass = (EditText) findViewById(R.id.pass);
		
		Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.private_photo);
		externalDir = Environment.getExternalStorageDirectory().toString();
		File file = new File(externalDir, "private.jpg");
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(file);
			bm = Bitmap.createScaledBitmap(bm, 480, 640, true);
			bm.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
		    outStream.flush();
		    outStream.close();
		} catch (FileNotFoundException e) {
			Log.e(Config.LOG_TAG, "Error : saving private.png");
		} catch (IOException e) {
			Log.e(Config.LOG_TAG, "Error : saving private.png");
		}
		mContext = getApplicationContext();
		dummy=new SurfaceView(mContext);
		setCam();
		
		wizz = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		AudioManager audioManager = 
			    (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
		Log.e(Config.LOG_TAG, "Max volume : " + maxVolume);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

		Intent intent = new Intent(mContext, Registration.class);
		startActivity(intent);
		mTts = new TextToSpeech(mContext, new OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
		            int result = mTts.setLanguage(Locale.FRANCE);
		            if (result == TextToSpeech.LANG_MISSING_DATA
		                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
		                Log.e(Config.LOG_TAG, "This Language is not supported");
		            }
		        } else {
		            Log.e(Config.LOG_TAG, "Initilization voice Failed!");
		        }
			}
		});
		registerReceiver(mHandleMessageReceiver, new IntentFilter("com.google.android.c2dm.intent.RECEIVE"));
	}
	private void setCam()
	{
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
		mCamera=Camera.open();
		params = mCamera.getParameters();
		params.setRotation(90);
		if(mLight != null)
			params.setFlashMode(mLight);
		params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		mCamera.setParameters(params);
		mCall = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				FileOutputStream fos;
				ByteArrayOutputStream cleanBmp = new ByteArrayOutputStream();
				bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				bmp.compress(CompressFormat.JPEG, 50, cleanBmp);
				data_file = cleanBmp.toByteArray();
				try {
					fos = new FileOutputStream(externalDir + "/tmp.jpg");
					cleanBmp.writeTo(fos);
					fos.close();
				}  catch (IOException e) {}
				photo.setImageBitmap(bmp);
				new Thread(new Runnable() {
					public void run() {
						uploadFile();
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(MainActivity.this, "Sent.", Toast.LENGTH_SHORT).show();
							}
						});
					}
				}).start();
			}
		};
	}
	private void frontCam() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
		int cameraCount = 0;
		CameraInfo ci = new CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
			Camera.getCameraInfo( camIdx, ci );
			if ( ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
				try {
					mCamera = Camera.open( camIdx );
				} catch (RuntimeException e) {
					Log.e(Config.LOG_TAG, "Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}
		params = mCamera.getParameters();
		params.setRotation(270);
		mCamera.setParameters(params);
	}
	public void takePicture(){

		if(mCamera!=null){
			try{
				mCamera.setPreviewDisplay(dummy.getHolder());    
				mCamera.startPreview();
				mCamera.takePicture(null, null, mCall);
			} catch (IOException e) {
			}
		}
	}
	private void playSound(int resId) {
	    if(mPlayer != null) {
	        mPlayer.stop();
	        mPlayer.release();
	    }
	    mPlayer = MediaPlayer.create(this, resId);
	    mPlayer.start();
	}
	@Override
	public void onPause() {
		super.onPause();
		if(isFinishing()){
			   Log.e(Config.LOG_TAG, "HAAAAAAAAAAA");
	      // store data don't kill me pleaseS
	   }
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mCamera != null)
			mCamera.release();
		if(mPlayer != null) {
	        mPlayer.stop();
	        mPlayer.release();
	    }
		if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
	}
	
	public void uploadFile() {
		final HashMap<String, String> data = new HashMap<String, String>();
		data.put("file_name", file_name);
		data.put("login", login);
		data.put("pwd", pass.getText().toString());
		if(!delete_file.isEmpty())
			data.put("delete_file", delete_file);
		try
		{
			String result;
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Config.URL_POST_CAM);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			Iterator<String> dataIt =  data.keySet().iterator();
			while(dataIt.hasNext()) {
				String key=(String)dataIt.next();
				String value=(String)data.get(key);
				entityBuilder.addTextBody(key, value);
			}
			File file = null;
			if(authorized){
				file = new File(externalDir + "/tmp.jpg");
				if(data_file != null)
					entityBuilder.addBinaryBody("img", file);
			}
			else{
				file = new File(externalDir + "/private.jpg");
				entityBuilder.addBinaryBody("img", file);
			}
			
			HttpEntity entity = entityBuilder.build();
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			result = EntityUtils.toString(httpEntity);
			Log.v(Config.LOG_TAG, "result = " + result);
			busy = false;
		}
		catch(Exception e)
		{
			Log.v(Config.LOG_TAG, "error = " + e.getMessage());
		}
	}
	
	@Override
	public void onBackPressed() {
	    moveTaskToBack(true);
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String log_msg = "";
			if (bundle != null) {
				request = new HashMap<String, String>();
				Set<String> keys = bundle.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
					log_msg = log_msg + key + "=" + bundle.get(key).toString() + "\n";
					request.put(key, bundle.get(key).toString());
					Log.e(Config.LOG_TAG, key);
				}
			}
			if(request.get("photo") != null)
			{
				file_name = request.get("photo");
				if(request.get("delete_file") != null)
					delete_file = request.get("delete_file");
				if(!busy)
				{
					authorized = false;
					String input = pass.getText().toString();
					if(input.isEmpty())
						authorized = true;
					else if(request.get("pwd") == null)
						authorized = false;
					else if(input.equals(request.get("pwd")))
						authorized = true;
					busy = true;
					login = request.get("login");
					takePicture();
				}
			}
			else if(request.get("turn_off") != null)
				finish();
			else if(request.get("light") != null)
			{
				if(!front_cam){
					if(mLight == null || mLight.equals(Parameters.FLASH_MODE_OFF))
						mLight = Parameters.FLASH_MODE_TORCH;
					else
						mLight = Parameters.FLASH_MODE_OFF;
					params.setFlashMode(mLight);
					mCamera.setParameters(params);
				}
			}
			else if(request.get("light_on") != null)
			{
				if(!front_cam){
					params.setFlashMode(mLight = Parameters.FLASH_MODE_TORCH);
					mCamera.setParameters(params);
				}
			}
			else if(request.get("light_off") != null)
			{
				if(!front_cam){
					params.setFlashMode(mLight = Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(params);
				}
			}
			else if(request.get("wizz") != null){
				wizz.vibrate(500);
			}
			else if(request.get("ding") != null){
				playSound(R.raw.coin);
			}
			else if(request.get("pegasus") != null){
				playSound(R.raw.pegasus);
			}
			else if(request.get("hide") != null){
				home = new Intent(Intent.ACTION_MAIN);
				home.addCategory(Intent.CATEGORY_HOME);
				home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(home);
			}
			else if(request.get("front") != null){
				front_cam = true;
				frontCam();
			}
			else if(request.get("back") != null){
				front_cam = false;
				setCam();
			}
			else if(request.get("say") != null){
				mTts.speak(request.get("say"), TextToSpeech.QUEUE_FLUSH, null);
			}
			else if(request.get("chaton") != null){
				mTts.speak("Chaton veut un câlin !", TextToSpeech.QUEUE_FLUSH, null);
			}
			mDisplay.setText(log_msg);
		}
	};
}