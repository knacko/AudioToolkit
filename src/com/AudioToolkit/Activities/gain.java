package com.AudioToolkit.Activities;

import java.util.Arrays;

import com.AudioToolkit.CustomViews.Databox;
import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkit.Objects.SoundGenerator;
import com.AudioToolkitPro.R;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.app.TabActivity;
import android.content.Context;

public class gain extends TabActivity {

	//Gain
	Databox inputWatts, inputLoad, outputLoad;
	RadioButton tweeter, midrange, midbass, subwoofer;
	SoundGenerator soundgen = new SoundGenerator(null);
  
	//Power conv
	Databox watts, volts, ohms, amps;
	Button conv_calculate, conv_clear;
 
	//Clipping
	Button on;
	splEngine soundrec; 
	SeekBar freqSB;
	TextView clipping, freqTV; 
	private static final int CLIPPING = 1; //Messages
	private static final int STOP = 2;
	final int W_SINE = 0;
	final int W_CLIPPED_SINE = 4;
	int freq;
	ToggleButton clippedTB;

	TabHost mTabHost;

	String help = "<big><u>Gain calculator</u></big><p>" +
			"<b>PLEASE NOTE: Some manufacturers are notorious for exaggerating the output rating of their amplifiers. It should be obvious that" +
			" a 2000W amp costs more than $50. Do not attempt this procedure if your amp is of a questionable rating.</b><p>" +	
			"<u>You will need:</u> <br>" +
			" - a multimeter<br>" +
			" - the rated output (W) of your amplifier<br>" +
			" - the final load (Ohms) of your speakers<br>" +
			" - a connection from the headphone output of your device to the head unit (2.5mm or 3.5mm jack, typically) <p>" +
			"<u>How to set</u><br>" + 
			"1. Connect the leads of you multimeter to the speaker outputs of the amplifier.<p>" +
			"2. Turn of all signal modification from your head unit. This include bass boost, equalizers and crossovers.<p>" +
			"3. Use the calculator to find your optimal speaker output and set the multimeter to the appropriate range in VAC.<p>" +
			"4. Set the head unit to about 3/4 of full volume and load up your test tones.<p>" +
			"5. Start the test tone (50hz for subs, 1000hz for highs), and watch the reading on the multimeter.<p>" +
			"6. Increase or decrease your gain until the reading on the multimeter matches the reading on the calculator.<p>" +
			"7. Your gains are now properly set to match the rated output of the amplifier." ;
	
	public void onCreate(Bundle b) {

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		/*
		 * TODO: Gain - Check for clipping from microphone
		 */

		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gain);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("Amp. Gain", R.id.gain_gain);
		tabBuilder.addTab("Ohm's Law", R.id.conversion);
		tabBuilder.addOptions(help);
		
		tabBuilder.build();

		soundgen = new SoundGenerator(new Handler());
		soundgen.start();
		
		//************Gain screen

		inputWatts = (Databox) findViewById(R.id.inputwatts);
		inputLoad = (Databox) findViewById(R.id.inputload);
		outputLoad = (Databox) findViewById(R.id.outputload);
		Button calculate = (Button) findViewById(R.id.calculate);
		Button generate = (Button) findViewById(R.id.generate);
		Button stop = (Button) findViewById(R.id.stop);
		Button clear = (Button) findViewById(R.id.clear);
		midrange = (RadioButton) findViewById(R.id.midrange);
		subwoofer = (RadioButton) findViewById(R.id.subwoofer);
		midrange.setEnabled(false); //midrange initially selected
		
		calculate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				
				if (!inputWatts.tryParse() | !inputLoad.tryParse()) return;
				double watts = inputWatts.parse();
				double load = inputLoad.parse();

					outputLoad.setValue(Math.sqrt(watts*load),1);	

			}});
		
		midrange.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				subwoofer.setEnabled(true);
				midrange.setEnabled(false);

			}});
		
		subwoofer.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				midrange.setEnabled(true);
				subwoofer.setEnabled(false);

			}});

		generate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				
				if (midrange.isChecked()) {
					soundgen.setHz1(1000);
					soundgen.setType(W_SINE);
				}
				if (subwoofer.isChecked()) {
					soundgen.setHz1(50);
					soundgen.setType(W_SINE);
				}
				
				soundgen.setLeftPhase(0);
				soundgen.setRightPhase(0);
				soundgen.setAmplitude(0);
				soundgen.setBalance(0);

			}});

		stop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				soundgen.stopTone();

			}});


		clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				inputWatts.clear();
				inputLoad.clear();
				outputLoad.clear();

			}});

		
		//******************Power screen
		

		watts = (Databox) findViewById(R.id.watts);
		volts = (Databox) findViewById(R.id.volts);
		ohms = (Databox) findViewById(R.id.ohms);
		amps = (Databox) findViewById(R.id.amps);
		conv_calculate = (Button) findViewById(R.id.conv_calculate);
		conv_clear = (Button) findViewById(R.id.conv_clear);
		
		conv_calculate.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {

				double p=0,e=0,r=0,i=0;
				int check = 0;


				if (watts.tryParse(false)) {
					p = watts.parse();
				} else {
					p = Double.MIN_VALUE;
					check++;
				}
				
				if (volts.tryParse(false)) {
					e = volts.parse();
				} else {
					e = Double.MIN_VALUE;
					check++;
				}

				if (ohms.tryParse(false)) {
					r = ohms.parse();
				} else {
					r = Double.MIN_VALUE;
					check++;
				}

				if (amps.tryParse(false)) {
					i = amps.parse();
				} else {
					i = Double.MIN_VALUE;
					check++;
				}

				if (check > 2) {
					Toast.makeText(getApplicationContext(), (CharSequence) "Too few inputs. Please enter 2 values.", 3).show();
					return;
				} else if (check < 2)	{
					Toast.makeText(getApplicationContext(), (CharSequence) "Too many inputs. Only enter 2 values.", 3).show();
					return;
				}
				
				if (p == Double.MIN_VALUE) {
					if (e == Double.MIN_VALUE) {
						p = r*Math.pow(i,2);
						e = r*i;
					} else if (r == Double.MIN_VALUE) {
						p = e*i;
						r = e/i;
					} else if (i == Double.MIN_VALUE) {
						p = Math.pow(e,2)/r;
						i = e/r;
					} 
				}

				if (e == Double.MIN_VALUE) {
					if (r == Double.MIN_VALUE) {
						e = p/i;
						r = p/Math.pow(i,2);
					} else if (i == Double.MIN_VALUE) {
						e = Math.sqrt(p*r);
						i = Math.sqrt(p/r);
					} 
				}

				if (r == Double.MIN_VALUE) {
					if (i == Double.MIN_VALUE) {
						r = Math.pow(e,2)/p;
						i = p/e;
					} 
				}

				watts.setValue(p,2);
				volts.setValue(e,2);
				ohms.setValue(r,2);
				amps.setValue(i,2);		

			}});


		conv_clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				watts.clear();
				volts.clear();
				ohms.clear();
				amps.clear();


			}});
		
		
		
		
		
		
		
		
		
		
		
		
		
		//*****************Clipping screen

		/*
		on = (Button) findViewById(R.id.gain_onoff);
		clipping = (TextView) findViewById(R.id.gain_clip);
		clippedTB = (ToggleButton) findViewById(R.id.gain_clippedTB);

		on.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (getString(R.string.freeApp).equals("true")) {
					Toast.makeText(getApplicationContext(), (CharSequence) getString(R.string.freeAppMessage), 10).show();
					return;	
				}

				if (on.getText().equals("ON"))
				{
					on.setText("OFF");            
					startall();            
				} else {         
					on.setText("ON");
					stopall();
				}

			}});	
			*/	
	}

	protected void startall() {

		int wavehz = 250;

		if (clippedTB.isChecked()) {
			soundgen = new SoundGenerator(mhandle, W_CLIPPED_SINE);
		} else {			
			soundgen = new SoundGenerator(mhandle, W_SINE);
		}		
		
		soundgen.setHz1(wavehz);
		//TODO: fix this: soundgen.start(); 

		soundrec = new splEngine(this, mhandle);
		soundrec.start_engine();

	}
	
	

	protected void stopall() {		

		try {
			soundgen.stopTone();
		} catch (Exception e) {
			System.out.println("Could stop the sound gen");
			
		}

		try {
			soundrec.stop_engine();
		} catch (Exception e) {
			System.out.println("Could stop the sound rec");			
		}

	}


	@Override
	public void onPause()
	{
		super.onPause();
		soundgen.stopTone();
		soundgen.killThread();
		Log.d("onPause()","Context left, stopping tones");
		//System.out.println("Stopped the tones");
	}   

	@Override
	public void onResume()
	{
		super.onResume();
		soundgen = new SoundGenerator(mhandle);  
		soundgen.start();
	}
	


	public Handler mhandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case CLIPPING:

				if (("" + msg.obj).equals("true")) {
					clipping.setText("Clipping");
					clipping.setTextColor(0xFFFF0000);
				} else {
					clipping.setText("Clean");
					clipping.setTextColor(0xFFFFFFFF);
				}

				break;
			case STOP:			
				clipping.setText("Stopped");
				clipping.setTextColor(0xFFFFFFFF);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	public class splEngine extends Thread
	{

		private static final int SOURCE = MediaRecorder.AudioSource.MIC;
		private static final int SAMPLERATE = 44100;
		private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
		private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
		private final int BUFFERSIZE = 10*AudioRecord.getMinBufferSize(SAMPLERATE,CHANNEL,ENCODING);

		private static final int CLIPPING = 1; //Messages
		private static final int STOP = 2;
		public volatile boolean isRunning = false;
		private Handler handle;
		Context context;

		AudioRecord recordInstance = new AudioRecord(SOURCE, SAMPLERATE, CHANNEL, ENCODING, BUFFERSIZE);

		public splEngine(Context context, Handler h)
		{
			this.handle = h;
			this.context = context;
		}

		/**
		 * starts the engine.
		 */
		public void start_engine()
		{         
			this.isRunning = true;
			this.start();         
		}

		/**
		 * stops the engine
		 */
		public void stop_engine()
		{
			this.isRunning = false;
			try {							
				recordInstance.stop();
			} catch (Exception e) {
				System.out.println("Cannot stop");
				
			} 

			Message msg = handle.obtainMessage(STOP, 0);
			handle.sendMessage(msg);
		}

		/*
		 * The main thread. Records audio and calculates the SPL The heart of the
		 * Engine.
		 */
		public void run()
		{
			try
			{
				android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				//recordInstance = new AudioRecord(SOURCE, SAMPLERATE, CHANNEL, ENCODING, BUFFERSIZE);

				recordInstance.startRecording();	

				boolean isClipping = false;
				double avgmax = 0;
				
				short[] temp = new short[BUFFERSIZE];
				int templength = recordInstance.read(temp, 0, BUFFERSIZE);
				
				Arrays.sort(temp, 0, templength);
				
				//Find the number of peaks for the frequency over the number of samples taken to find average
				int top99 = (int) (Math.round((2*2*250*(templength)/(double)SAMPLERATE)));
				
				System.out.println("buffersize: " + BUFFERSIZE + " tempb: " + templength + " top99: " + top99);
								
				int max = 0;
				
				for (int i = 0; i < top99 - 1; i++) {
					avgmax += Math.abs(temp[i]);
					if (Math.abs(temp[i]) > max) max = Math.abs(temp[i]);
				}
				
				avgmax = avgmax/top99; //70% of max is 50th percentile for sine wave
				
				System.out.println("average: " + avgmax + " max: " + max);
				
				short[] buffer = new short[BUFFERSIZE];
				double avg = 0;
				
				while (this.isRunning)
				{

					int length = recordInstance.read(buffer, 0, BUFFERSIZE);
					
					avg = 0;
					isClipping = false;
					
					for (int i = 0; i < length - 1; i++) {						
						
						//System.out.println("i: " + i + " v: " + buffer[i]/avgmax);
						avg += Math.abs(buffer[i]/avgmax);
						
						//if (Math.abs(tempBuffer[i]) > avgmax) valsAbove++;						
					}
					
					avg = avg/(buffer.length);
					
					System.out.println("avg: " + avg);
					
					if (avg > 0.636) isClipping = true;
					
					//System.out.println("valsAbove: " + valsAbove + " Total vals: " + BUFFERSIZE);
					
					
					//if (valsAbove > (BUFFERSIZE/2)) isClipping = true; //vals above 70% + sensitivity > half of total number of vals, signal is clipping  
					
					//Check for clipping

					Message msg = handle.obtainMessage(CLIPPING, isClipping ? "true" : "false");
					handle.sendMessage(msg);

				}

				recordInstance.stop();
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("******************broken******************");
			}
		}

	}



}

