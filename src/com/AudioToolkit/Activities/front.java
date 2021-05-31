package com.AudioToolkit.Activities;

import com.AudioToolkit.Objects.DatabaseHandler;
import com.AudioToolkitPro.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class front extends Activity {

	
	/* TODO: Front - Spectrum Analyzer - take mic input and output waveform in graph
	 * TODO: Front - Sheet Optimizer - Given certain stocks and parts needed, generate a cut sheet 
	 * TODO: Front - Time Analyzer - Determine distance to speakers
	 * TODO: Front - Auto EQ - Determine frequency response of space to create a flat curve for EQ
	 * TODO: Front - Hearing EQ - tune the EQ to your specific hearing curve, multiple passes where the user can rate frequencies relative the previous
	 * TODO: Front - Cabin Resonant Freq - finder resonant frequency of cabin
	 * TODO: Front - Relay circuits - switch between series and parallel, mono to stereo amp
	 * TODO: Front - Help screen - able to click the individual input to find help
	 * TODO: All - Inset content within scrollview
	 */ 

	SharedPreferences settings;
	SharedPreferences.Editor editor;
	Context mContext = front.this;
	String PREFS_NAME = "AudioToolkit";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.front);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Setup the database
		new AsyncTask<Void, Void, Void> () {

			@Override
			protected Void doInBackground(Void... params) {
				Log.d("DB Initialization","making DB accessible");
				DatabaseHandler db = new DatabaseHandler(mContext);
				db.getReadableDatabase();
				db.close();
				return null;
			}			

		}.execute();
		
		Log.d("onCreate()","Loading components");
		Button gain = (Button) findViewById(R.id.gain);
		Button gauge = (Button) findViewById(R.id.gauge);
		Button definitions = (Button) findViewById(R.id.definitions);
		Button tone = (Button) findViewById(R.id.tone);
		Button wiring = (Button) findViewById(R.id.wiring);
		Button crossover = (Button) findViewById(R.id.crossover);
		Button resistor = (Button) findViewById(R.id.resistor);
		Button enclosure = (Button) findViewById(R.id.enclosure);
		Button dbmeter = (Button) findViewById(R.id.dbmeter);
		Button spectrum = (Button) findViewById(R.id.spectrum);
		Button speakersize = (Button) findViewById(R.id.speakersize);
		Button suggestions = (Button) findViewById(R.id.suggestions);

		Button getPro = (Button) findViewById(R.id.getPro);

		//ImageView logo = (ImageView) findViewById(R.id.frontlogo);

		settings = mContext.getSharedPreferences(PREFS_NAME,Context.MODE_WORLD_READABLE); 
		editor = settings.edit();	
		
		//Remove the row if the app the pro version
		if (getString(R.string.freeApp).equals("false")) {
			Log.d("onCreate()","Pro app verified");
			
			getPro.setVisibility(View.GONE);
			//logo.setImageResource(R.drawable.logo_pro);
			
			//Show the update screen if it's the first run after updates
			PackageInfo pInfo;
			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
				if ( settings.getLong( "lastRunVersionCode", 0) != pInfo.versionCode ) {
										
					new AlertDialog.Builder(this).setTitle("Updates").setMessage(
							getString(R.string.updates)).setIcon(R.drawable.ic_menu_upload).setNeutralButton("Close", null).show();

					editor.putLong("lastRunVersionCode", pInfo.versionCode);
					editor.commit();
				
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			suggestions.setVisibility(View.GONE);
		}

		getPro.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("getPro()","Button clicked");
				Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse("market://details?id=com.AudioTo" + "olkitPro"));
				browse.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity( browse );			
			}});


		gain.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {

				Log.d("gain()","Button clicked");
				Intent i = new Intent(getApplicationContext(), gain.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		gauge.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("gauge()","Button clicked");
				Intent i = new Intent(getApplicationContext(), gauge.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		
		definitions.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("definitions()","Button clicked");
				Intent i = new Intent(getApplicationContext(), definitions.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		tone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("tone()","Button clicked");
				Intent i = new Intent(getApplicationContext(), tone.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		wiring.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("wiring()","Button clicked");
				Intent i = new Intent(getApplicationContext(), subwiring.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		crossover.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("crossover()","Button clicked");
				Intent i = new Intent(getApplicationContext(), crossover.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		resistor.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("resistor()","Button clicked");
				Intent i = new Intent(getApplicationContext(), resistor.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		suggestions.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("suggestion()","Button clicked");
				Intent i = new Intent(getApplicationContext(), suggestions.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		spectrum.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), spectrum.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		dbmeter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("dbmeter()","Button clicked");
				Intent i = new Intent(getApplicationContext(), splMeter.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		enclosure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("enclosure()","Button clicked");
				Intent i = new Intent(getApplicationContext(), encbuilder.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});

		speakersize.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d("speakersize()","Button clicked");
				Intent i = new Intent(getApplicationContext(), speakersize.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("options()","Button clicked");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.frontmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			new AlertDialog.Builder(this).setTitle("Info").setMessage(
					getString(R.string.aboutTagline)).setIcon(R.drawable.w3_small_logo).setNeutralButton("Close", null).show();
			break;
		case R.id.updates:
			new AlertDialog.Builder(this).setTitle("Updates").setMessage(
					getString(R.string.updates)).setIcon(R.drawable.ic_menu_upload).setNeutralButton("Close", null).show();
			break;
		}
		return true;
	}

}