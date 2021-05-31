package com.AudioToolkit.Activities;


import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkit.Utility.Mathf;
import com.AudioToolkitPro.R;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class splMeter extends TabActivity
{
	/** Called when the activity is first created. */

	TextView splDataTV, splmax, splavg;

	Button splOnOffButton;
  
	Button splCalibUpButton; 
	Button splCalibDownButton;

	Button splUpdateUp;
	Button splUpdateDown;

	Button splReset;   

	static final int SPLval = 1;
	static final int MAXval = 2;
	static int PREFERENCES_GROUP_ID = 0;

	double totalSPL = 0.0;
	double totalSPLi = 0;
	double maxSPL = 0.0;

	boolean isFree = false;

	protected splEngine engine;

	Context mContext = splMeter.this;

	String help = "<big><u>Decibel Meter</u></big><p>" +
			"Limitations: <br>" + 
			"The microphones used in mobile devices are designed to capture speech, not gauge SPL levels." + 
			"They do not have a flat response curve, and sensitivity varies greatly between models." + 
			"As such, accurate SPL readings are impossible to generate.<p>" + 
			"It is best used for comparisons between stereos, or different spots within an area, as the relative values will be mostly accurate.<p>" + 
			"To reset the sensitivity or update rate, hold either arrow of what you'd like to reset for a few seconds.";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splmeter);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("Decibel Meter", R.id.spl_meter);
		tabBuilder.addOptions(help);
		
		tabBuilder.build();

		splDataTV = (TextView) findViewById(R.id.spl);
		splDataTV.setText("");

		splmax = (TextView) findViewById(R.id.splmax);
		splmax.setText("0.0");

		splavg = (TextView) findViewById(R.id.splavg);
		splavg.setText("0.0");

		splOnOffButton = (Button) findViewById(R.id.onoff);
		splCalibUpButton = (Button) findViewById(R.id.senseplus);
		splCalibDownButton = (Button) findViewById(R.id.senseminus);
		splUpdateUp = (Button) findViewById(R.id.spl_updateplus);
		splUpdateDown= (Button) findViewById(R.id.spl_updateminus);

		//Change graphics of buttons if they are not available in free version
		if (getString(R.string.freeApp).equals("true")) {
			splCalibUpButton.setBackgroundResource(R.drawable.btn_default_pressed_holo_dark_free);
			splCalibDownButton.setBackgroundResource(R.drawable.btn_default_pressed_holo_dark_free);
		}

		splOnOffButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{

				if (splOnOffButton.getText().equals("ON"))
				{
					if (getString(R.string.freeApp).equals("true")) {
						Toast.makeText(getApplicationContext(), (CharSequence) "To read above 50dB, you must upgrade to the pro version.\n\nClick the button on the front screen to get the pro version.", 10).show();
						isFree = true;
					}
          
					resume_meter();            
				}
				else
				{         
					pause_meter();
				}
			}
		});

		splCalibUpButton.setOnClickListener(new OnClickListener()
		{public void onClick(View v) {	

			if (getString(R.string.freeApp).equals("true")) {
				Toast.makeText(getApplicationContext(), (CharSequence) getString(R.string.freeAppMessage), 10).show();
				return;
			}

			engine.calibUp();

		}});

		splCalibDownButton.setOnClickListener(new OnClickListener()
		{public void onClick(View v){

			if (getString(R.string.freeApp).equals("true")) {
				Toast.makeText(getApplicationContext(), (CharSequence) getString(R.string.freeAppMessage), 10).show();
				return;
			}

			engine.calibDown();	

		}});

		splCalibUpButton.setOnLongClickListener(new OnLongClickListener()
		{public boolean onLongClick(View arg0) {	engine.resetCalib();	return true;	}});

		splCalibDownButton.setOnLongClickListener(new OnLongClickListener()
		{public boolean onLongClick(View arg0) {	engine.resetCalib();	return true;	}});

		splUpdateUp.setOnClickListener(new OnClickListener()
		{public void onClick(View v){	engine.updateUp();	}});		

		splUpdateDown.setOnClickListener(new OnClickListener()
		{public void onClick(View v){engine.updateDown();}});

		splUpdateUp.setOnLongClickListener(new OnLongClickListener()
		{public boolean onLongClick(View arg0) {	engine.resetUpdate();	return true;	}});

		splUpdateDown.setOnLongClickListener(new OnLongClickListener()
		{public boolean onLongClick(View arg0) {	engine.resetUpdate();return true;}});

		// start meter in ON state

		splOnOffButton.setText("ON");
		splDataTV.setText("0.0");



		/*
		if (!engine.checkState()) {
			audioFailure("");
			return;			
		}
		 */

	}

	@Override
	public void onResume()
	{
		super.onResume();
		engine = new splEngine(mContext, mhandle);
		start_meter();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		pause_meter();
		stop_meter();
	}

	/**
	 * Starts the SPL Meter
	 */
	public void start_meter()
	{
		engine.start_engine();
		engine.start();
	}

	public void resume_meter() {
		totalSPL = 0;
		totalSPLi = 0;
		splOnOffButton.setText("OFF");  
		engine.resume_engine();		
	}
	
	public void pause_meter() {
		splOnOffButton.setText("ON");
		engine.pause_engine();
	}
	
	
	/**
	 * Stops the SPL Meter
	 */
	public void stop_meter() {		
		engine.stop_engine();	}

	/**
	 * Handler for displaying messages
	 */
	double spl;

	public Handler mhandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case SPLval:
				spl = Double.valueOf(msg.obj.toString()).doubleValue();
				splDataTV.setText("" + spl);
				totalSPL += spl; 
				totalSPLi++;	
				splavg.setText(Mathf.round(totalSPL/totalSPLi,1) + "");				
				break;
			case MAXval:
				spl = Double.valueOf(msg.obj.toString()).doubleValue();
				splmax.setText("" + spl);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	public void audioFailure(String stackTrace) {

		new AlertDialog.Builder(splMeter.this)
		.setMessage("Would you like to send your device specs to the developer to help solve the problem?\n\nYou will be redirected to your e-mail client if so.")
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {

				String message = "Device specs:" +
						"\nManufacturer: " + Build.MANUFACTURER +
						"\nModel: " + Build.MODEL +
						"\nProduct: " + Build.PRODUCT +
						"\nBrand: " + Build.BRAND +
						"\nDevice: " + Build.DEVICE +
						"\nBoard: " + Build.BOARD +
						"\nCPU: " + Build.CPU_ABI + "|" + Build.CPU_ABI2 +
						"\nBootloader: " + Build.BOOTLOADER +
						"\nHardware: " + Build.HARDWARE +
						"\nType: " + Build.TYPE +
						"\nTags: " + Build.TAGS +
						"\nTime: " + Build.TIME +

						"\n\nAudioRecord specs:" +
						"\nState: " + engine.getState() +
						"\nMin. Buffersize: " + engine.getBufferSize();

				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

				emailIntent.setType("plain/text");

				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"knacko.me@gmail.com"});

				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AudioToolkit Error");

				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);

				splMeter.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

				splMeter.this.finish();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
				splMeter.this.finish();
			}})
			.setTitle("Error on Initialization")
			.show();

	}

	public class splEngine extends Thread
	{
		private static final int SOURCE = MediaRecorder.AudioSource.MIC;
		private final int SAMPLERATE = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM);//44100;

		private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
		private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
		private final int BUFFERSIZE = Math.max(SAMPLERATE,AudioRecord.getMinBufferSize(SAMPLERATE,CHANNEL,ENCODING));
		private static final int SPLval = 1; //Messages
		private static final int MAXval = 2;
		private static final double P0 = 0.000002;
		public volatile boolean isRunning = false;
		public volatile boolean isActive = false;
		private Handler handle;
		Context context;
		String PREFS_NAME = "AudioToolkit";

		private static final int CALIB_DEFAULT = -130;
		private static final int UPDATE_DEFAULT = 2000;
		private static final int CALIB_INCREMENT = 3;
		private static final int UPDATE_INCREMENT = 200;
		private int calibrationValue, BUFFSIZE, newbuffsize;
		SharedPreferences settings;
		SharedPreferences.Editor editor;

		private double maxValue = 0.0;

		AudioRecord recordInstance = new AudioRecord(SOURCE, SAMPLERATE, CHANNEL, ENCODING, BUFFERSIZE);

		public splEngine(Context context, Handler h)
		{
			System.out.println("SAmple rate: " + SAMPLERATE);
			this.handle = h;
			this.context = context;
			maxValue = 0.0;   
			settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_WORLD_READABLE); 
			editor = settings.edit();
			BUFFSIZE = settings.getInt("update", UPDATE_DEFAULT);
			calibrationValue = settings.getInt("calib", CALIB_DEFAULT);
		}

		public boolean checkState() {		
			if (recordInstance.getState() == 1) return true;
			return false;
		}

		public int getBufferSize() {

			return BUFFERSIZE;

		}

		/**
		 * starts the engine.
		 */
		public void start_engine()
		{         
			this.isActive = true;
		}

		public void resume_engine() {
			this.isRunning = true;
			recordInstance.startRecording();			
			newbuffsize = BUFFSIZE;
		}
		
		public void pause_engine() {
			this.isRunning = false;			
			try {							
				recordInstance.stop();
			} catch (Exception e) {} 

			Message msg = handle.obtainMessage(SPLval, 0.0);
			handle.sendMessage(msg);
		}
		
		
		/**
		 * stops the engine
		 */
		public void stop_engine()
		{
			this.isActive = false;
		}

		/**
		 * Increase the calibration by an fixed increment
		 */
		public void calibUp()
		{
			calibrationValue += CALIB_INCREMENT;

			editor.putInt("calib", calibrationValue);
			editor.commit();

		}

		/**
		 * Descrease the calibration by a fixed value
		 */
		public void calibDown()
		{
			calibrationValue -= CALIB_INCREMENT;			

			editor.putInt("calib", calibrationValue);
			editor.commit();
		}

		public void updateUp() {
			newbuffsize = newbuffsize - UPDATE_INCREMENT;
			if (newbuffsize <= 0) newbuffsize = UPDATE_INCREMENT;			

			System.out.println("buffsize: " + newbuffsize);

			editor.putInt("update", newbuffsize);
			editor.commit();
		}

		public void updateDown() {
			newbuffsize = newbuffsize + UPDATE_INCREMENT;
			if (newbuffsize > SAMPLERATE) newbuffsize = SAMPLERATE-UPDATE_INCREMENT;

			System.out.println("buffsize: " + newbuffsize);

			editor.putInt("update", newbuffsize);
			editor.commit();
		}

		public void resetUpdate()
		{
			newbuffsize = UPDATE_DEFAULT;

			editor.putInt("update", UPDATE_DEFAULT);
			editor.commit();
		}

		public void resetCalib()
		{
			calibrationValue = CALIB_DEFAULT;

			editor.putInt("calib", CALIB_DEFAULT);
			editor.commit();
		}


		/*
		 * The main thread. Records audio and calculates the SPL The heart of the
		 * Engine.
		 */
		public void run()
		{
			try
			{
				System.out.println("Meter is started");
				android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				//recordInstance = new AudioRecord(SOURCE, SAMPLERATE, CHANNEL, ENCODING, BUFFERSIZE);

				double splValue;
				double rmsValue;

				while (this.isActive) {

					while (this.isRunning)
					{
						rmsValue = 0.0;

						short[] tempBuffer = new short[BUFFSIZE];
						int length = recordInstance.read(tempBuffer, 0, BUFFSIZE);


						for (int i = 0; i < BUFFSIZE - 1; i++) {
							rmsValue += tempBuffer[i] * tempBuffer[i];
						}

						rmsValue = rmsValue / length;
						rmsValue = Math.sqrt(rmsValue);

						splValue = 20 * Math.log10(rmsValue / P0);
						splValue = splValue + calibrationValue;
						splValue = Mathf.round(splValue, 1);

						if (maxValue < splValue)
						{
							maxValue = splValue;
							Message msg = handle.obtainMessage(MAXval, maxValue);
							handle.sendMessage(msg);
						}

						Message msg = handle.obtainMessage(SPLval, splValue);
						handle.sendMessage(msg);

						BUFFSIZE = newbuffsize;
					}		
					this.sleep(250);	
					System.out.println("Recorder: Paused");
				}
				System.out.println("Recorder: Stopped");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("******************broken******************");
			}
		}
	}

}
