package com.example.xmppdemouam;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.view.Menu;
import android.view.MenuItem;

public class RegisterActivity extends Activity {
	
	Button btnGCMRegister;
	Button btnXmppRegiser;
	Button btnSendMessage;
	GoogleCloudMessaging gcm;
	Context context;
	String regId;
	AsyncTask<Void, Void, String> sendTask;
	AtomicInteger ccsMsgId = new AtomicInteger();

	static final String GOOGLE_PROJECT_ID = "156429511339";
	public static final String REG_ID = "";
	private static final String APP_VERSION = "1.0";

	static final String TAG = "RegisterActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		context = getApplicationContext();

		btnGCMRegister = (Button) findViewById(R.id.btnGCMRegister);
		btnGCMRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(regId)) {
					regId = registerGCM();
					TextView txtIdRegistro = (TextView)findViewById(R.id.registerid);
					txtIdRegistro.setText(regId);
				} else {
					Toast.makeText(getApplicationContext(),
								   "Ya se encuentra registrado en el servidor GCM!",
							       Toast.LENGTH_LONG).show();
				}
			}
		});

		btnXmppRegiser = (Button) findViewById(R.id.btnXmppRegiser);
		btnXmppRegiser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(regId)) {
					Toast.makeText(getApplicationContext(), "RegId vacío!",
							Toast.LENGTH_LONG).show();
				} else {
					sendMessage("REGISTER");
					TextView txtIdRegistro = (TextView)findViewById(R.id.registerid);
					txtIdRegistro.setText(regId);
				}
			}
		});

		btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
		btnSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(regId)) {
					Toast.makeText(getApplicationContext(), "RegId vacío!",
							Toast.LENGTH_LONG).show();
				} else {
					sendMessage("ECHO");
					TextView txtIdRegistro = (TextView)findViewById(R.id.registerid);
					txtIdRegistro.setText(regId);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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
	
	private void sendMessage(final String action) {

		sendTask = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {

				Bundle data = new Bundle();
				data.putString("ACTION", action);
				data.putString("CLIENT_MESSAGE", "Hola servidor XMPP!");
				String id = Integer.toString(ccsMsgId.incrementAndGet());

				try {
					gcm.send(GOOGLE_PROJECT_ID + "@gcm.googleapis.com", id, data);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "Mensaje envíado";
			}

			@Override
			protected void onPostExecute(String result) {
				sendTask = null;
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();
			}

		};
		sendTask.execute(null, null, null);

	}
	
	public String registerGCM() {
		gcm = GoogleCloudMessaging.getInstance(this);
		
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {
			registerInBackground();
		} 
		else {
			Toast.makeText(getApplicationContext(), "Dispositivo ya registrado",
					       Toast.LENGTH_LONG).show();
			
			TextView txtIdRegistro = (TextView)findViewById(R.id.registerid);
			txtIdRegistro.setText(regId);
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(RegisterActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		
		String registrationId = prefs.getString(REG_ID, "");
		
		if (registrationId.isEmpty()) {
			return "";
		}
		
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
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
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(GOOGLE_PROJECT_ID);
					msg = "Dispositivo registrado. Registration ID: " + regId;

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(getApplicationContext(),
						       "Registrado con el servidor GCM" + msg, Toast.LENGTH_LONG)
						.show();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				RegisterActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}
}
