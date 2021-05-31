package com.AudioToolkit.Activities;

import com.AudioToolkit.CustomViews.*;
import com.AudioToolkit.Objects.SoundGenerator;
import com.AudioToolkitPro.R;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;

public class tone extends TabActivity {

	/* 
	 * TODO: Tone Gen. - Stage - tones to test the stage (left and right, circular)
	 * TODO: Tone Gen. - Waveforms - Display waveforms or sweep frequency in a graph
	 * TODO: Tone Gen. - Service - Charnge tone generator to a service, so can be used outside of program
	 * TODO: Tone Gen. - Graph to show the current frequency
	 */

	Boolean wave = false, band = false, sweep = false, noise = false;
	Sliderbox wavehzSB, balSB, leftphaseSB, rightphaseSB, amplitudeSB, sweepdurSB, sweephz1SB, sweephz2SB,  
	banddurSB, bandhz1SB, bandhz2SB, bandbandSB;
	SoundGenerator soundgen;
	Toast pro;
	TabHost mTabHost; 
	//double wavehz, wavedur, sweephz1, sweephz2, sweepdur, noisedur, bandnum, banddur, bandhz1, bandhz2;
	//boolean sweepIsLooping, bandIsLooping;
	RadioButton loopingRB, reversingRB, bandloopRB, bandrevRB;

	ToggleButton gensine, gensquare, gentri, gensaw, genexp, genlinear, gencubic, genwhite, genblue, genpink, genadd, genmult;

	LinearLayout advOptions, waveadvoptions, sweepadvoptions, bandadvoptions, noiseadvoptions, stageadvoptions;

	RelativeLayout mainlayout;

	TextView prog;

	final int W_SINE = 0;
	final int W_SQUARE = 1;
	final int W_TRIANGLE = 2;
	final int W_SAWTOOTH = 3;
	final int S_LINEAR = 10;
	final int S_CUBIC = 11;
	final int S_EXP = 12;
	final int N_WHITE = 20;
	final int N_PINK = 21;
	final int N_BLUE = 22;
	final int B_ADD = 30;
	final int B_MULT = 31;

	final int GEN = 100;
	final int ERROR = 101;
	final int DONE = 102;

	boolean genFLAG = false, isAdvOpen = false, onNoiseTab = false;

	String help = "<big><u>Tone Generator</u></big><p>" +
			"Double-clicking on the slider knob will allow you to use the keypad to enter in a specific value.<p>" + 
			"The output of Android devices seem to start clipping above 50% volume. For the cleanest signal, you'll typically need to keep the volume below this level.\n\n" + 
			"It is advisable to turn down the volume before starting the tones, as full volume could possibly cause damage " +
			"to a system that is not setup correctly.<p>" +
			"Due to the limitation of the output for some Android devices, some tones may not be possible, especially through the on-board speaker.";
	
	public void onCreate(Bundle b) {

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tone);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("Wave", R.id.wave);
		tabBuilder.addTab("Sweep",R.id.sweep);
		tabBuilder.addTab("Bands",R.id.bands);
		tabBuilder.addTab("Noise",R.id.noise);
		tabBuilder.addTab("Stage",R.id.stage);
		tabBuilder.addOptions(help);
		
		tabBuilder.build();

		//Initialize
		soundgen = new SoundGenerator(mhandle);  
		soundgen.start();

		/*
		if (!soundgen.checkState()) {					//Kill it if the audiotrack doesn't initialize correctly
			audioFailure("");
			return;
		}
		 */

		setupWaveButtons();
		setupSweepButtons();
		setupNoiseButtons();
		setupBandButtons();
		setupAdvButtons();

	}

	private void setupButtonListener(final ToggleButton t, final int toneType) {
		
		t.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				setAllToFalse();
				Log.d("Tone: " + toneType,"Button toggled");

				if (!t.isChecked()) { 
					soundgen.stopTone();	
					Log.d("Tone: " + toneType,"Button already checked");
					
					return;
				}

				Log.d("Tone: " + toneType,"Button not checked, starting soundgen");

				flagToneType(toneType, true);
				
				setWave();
				setBand();
				setSweep();
				setAdvOptions();
				soundgen.setType(toneType);

				
				togglesOff();

				t.setChecked(true);				

			}});		
	}
	
	private void setupWaveButtons() {
		
		//Waves
		wavehzSB = (Sliderbox) findViewById(R.id.wavefreqslider);
		gensine = (ToggleButton) findViewById(R.id.gensin);
		gensquare = (ToggleButton) findViewById(R.id.gensquare);
		gentri = (ToggleButton) findViewById(R.id.gentri);
		gensaw = (ToggleButton) findViewById(R.id.gensaw);

		//Wave buttons

		wavehzSB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				if (wave) soundgen.setHz1(value);				
			}
		});

		setupButtonListener(gensine, W_SINE);
		setupButtonListener(gensquare,W_SQUARE);
		setupButtonListener(gentri,W_TRIANGLE);
		setupButtonListener(gensaw,W_SAWTOOTH);
		
	}

	
	
	private void setupSweepButtons() {

		//Sweeps
		sweepdurSB = (Sliderbox) findViewById(R.id.sweepdurslider);
		sweephz1SB = (Sliderbox) findViewById(R.id.sweephz1slider);
		sweephz2SB = (Sliderbox) findViewById(R.id.sweephz2slider);
		genlinear = (ToggleButton) findViewById(R.id.genlinear);
		gencubic = (ToggleButton) findViewById(R.id.gencubic);
		genexp = (ToggleButton) findViewById(R.id.genexp);
		loopingRB = (RadioButton) findViewById(R.id.tone_looping);
		reversingRB = (RadioButton) findViewById(R.id.tone_reversing);


		//Sweep buttons

		sweephz1SB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				if (sweep) soundgen.setHz1(value);				
			}
		});
		sweephz2SB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				if (sweep) soundgen.setHz2(value);				
			}
		});
		sweepdurSB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				if (sweep) 	soundgen.setDur(value);				
			}
		});

		loopingRB.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				if (sweep) soundgen.setLooping(true);
			}});

		reversingRB.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				if (sweep) soundgen.setLooping(false);
			}});

		setupButtonListener(genlinear,S_LINEAR);
		setupButtonListener(gencubic,S_CUBIC);
		setupButtonListener(genexp,S_EXP);
		
	}
	
	private void setupBandButtons() {

		//Bands
		banddurSB = (Sliderbox) findViewById(R.id.banddurslider);
		bandhz1SB = (Sliderbox) findViewById(R.id.bandhz1slider);
		bandhz2SB = (Sliderbox) findViewById(R.id.bandhz2slider);
		bandbandSB = (Sliderbox) findViewById(R.id.bandbandslider);
		genadd = (ToggleButton) findViewById(R.id.genadd);
		genmult = (ToggleButton) findViewById(R.id.genmult);
		bandloopRB = (RadioButton) findViewById(R.id.band_looping);
		bandrevRB = (RadioButton) findViewById(R.id.band_reversing);



		//bands


		bandhz1SB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				if (band) 	soundgen.setHz1(value);				
			}
		});
		bandhz2SB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				if (band) 	soundgen.setHz2(value);				
			}
		});
		banddurSB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				if (band) 	soundgen.setDur(value);				
			}
		});
		bandbandSB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				if (band) soundgen.setBands(value);				
			}
		});

		bandloopRB.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				if (band) soundgen.setLooping(true);
			}});

		bandrevRB.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				if (band) soundgen.setLooping(false);
			}});


		setupButtonListener(genadd,B_ADD);
		setupButtonListener(genmult,B_MULT);
		
	}
	private void setupNoiseButtons() {


		//Noise
		genwhite = (ToggleButton) findViewById(R.id.genwhite);
		genpink = (ToggleButton) findViewById(R.id.genpink);
		genblue = (ToggleButton) findViewById(R.id.genblue);

		//Noise buttons

		setupButtonListener(genwhite, N_WHITE);
		setupButtonListener(genpink,N_PINK);
		setupButtonListener(genblue,N_BLUE);
		
	}

	private void setupAdvButtons() {

		//Advanced options box
		LinearLayout advBTN = (LinearLayout) findViewById(R.id.advBTN);
		final ImageView advIMG = (ImageView) findViewById(R.id.advIMG);
		balSB = (Sliderbox) findViewById(R.id.balslider);
		leftphaseSB = (Sliderbox) findViewById(R.id.leftphaseslider);
		rightphaseSB = (Sliderbox) findViewById(R.id.rightphaseslider);
		amplitudeSB = (Sliderbox) findViewById(R.id.amplitudeslider);
		balSB.setVisibility(View.GONE);
		leftphaseSB.setVisibility(View.GONE);
		rightphaseSB.setVisibility(View.GONE);
		amplitudeSB.setVisibility(View.GONE);
		waveadvoptions = (LinearLayout) findViewById(R.id.waveadvoptions);
		sweepadvoptions = (LinearLayout) findViewById(R.id.sweepadvoptions);
		bandadvoptions = (LinearLayout) findViewById(R.id.bandadvoptions);
		noiseadvoptions = (LinearLayout) findViewById(R.id.noiseadvoptions);
		stageadvoptions = (LinearLayout) findViewById(R.id.stageadvoptions);

		//Advanced buttons
		advOptions = (LinearLayout) findViewById(R.id.advoptions);

		balSB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				soundgen.setBalance(value);				
			}
		});

		leftphaseSB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				soundgen.setLeftPhase(value);				
			}
		});

		rightphaseSB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				soundgen.setRightPhase(value);				
			}
		});

		amplitudeSB.setValueChangeListener(new Sliderbox.OnValueChangeListener() {
			public void onVariableChanged(double value) {
				soundgen.setAmplitude(value);				
			}
		});		
		advBTN.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {					
				if (isAdvOpen) {
					advIMG.setImageResource(R.drawable.btn_radio_off);
					balSB.setVisibility(View.GONE);
					leftphaseSB.setVisibility(View.GONE);
					rightphaseSB.setVisibility(View.GONE);
					amplitudeSB.setVisibility(View.GONE);
					isAdvOpen = false;
				} else {
					advIMG.setImageResource(R.drawable.btn_radio_on);
					balSB.setVisibility(View.VISIBLE);
					if (!onNoiseTab) leftphaseSB.setVisibility(View.VISIBLE);
					if (!onNoiseTab) rightphaseSB.setVisibility(View.VISIBLE);
					amplitudeSB.setVisibility(View.VISIBLE);
					isAdvOpen = true;
				}					
			}	
		});

		//set the tab change actions

		getTabHost().setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				int i = getTabHost().getCurrentTab();

				ViewGroup parent = (ViewGroup) advOptions.getParent();
				parent.removeView(advOptions);

				onNoiseTab = false;

				if (isAdvOpen) {
					leftphaseSB.setVisibility(View.VISIBLE);
					rightphaseSB.setVisibility(View.VISIBLE);
				}

				if (i == 0) { //wave
					waveadvoptions.addView(advOptions);       

				}
				else if (i == 1) { //sweep
					sweepadvoptions.addView(advOptions); 

				}
				else if (i == 2) { // bands
					bandadvoptions.addView(advOptions); 

				}
				else if (i == 3) { //noise
					onNoiseTab = true;
					noiseadvoptions.addView(advOptions); 
					if (isAdvOpen) {
						leftphaseSB.setVisibility(View.GONE);
						rightphaseSB.setVisibility(View.GONE);
					}
				}
				else if (i == 4) { //stage

					stageadvoptions.addView(advOptions); 
				}
			}
		});
	}


	private void setWave() {

		if (!wave) return; 
		
		Log.d("setWave()", "Getting wave values");
		
		soundgen.setHz1(wavehzSB.getValue());
	}

	private void setSweep() {

		if (!sweep) return; 
		
		Log.d("setSweep()", "Getting sweep values");
		
		soundgen.setLooping(loopingRB.isChecked());
		soundgen.setHz1(sweephz1SB.getValue());
		soundgen.setHz2(sweephz2SB.getValue());
		soundgen.setDur(sweepdurSB.getValue());

	}

	private void setBand() {

		if (!band) return; 
		
		Log.d("setBand()", "Getting band values");
		
		soundgen.setLooping(bandloopRB.isChecked());
		soundgen.setHz1(bandhz1SB.getValue());
		soundgen.setHz2(bandhz2SB.getValue());
		soundgen.setDur(banddurSB.getValue());
		soundgen.setBands(bandbandSB.getValue());

	}

	private void setAdvOptions() {

		Log.d("setAdvOptions()", "Getting advanced value");
		
		soundgen.setLeftPhase(leftphaseSB.getValue());
		soundgen.setRightPhase(rightphaseSB.getValue());
		soundgen.setAmplitude(amplitudeSB.getValue());
		soundgen.setBalance((balSB.getValue()+1)/2);

	}

	@Override
	public void onPause()
	{
		super.onPause();
		togglesOff();
		soundgen.stopTone();
		soundgen.killThread();
		Log.d("onPause()","Context left, stopping tones");
		//System.out.println("Stopped the tones");
	}   
  
	/**
	 * Handler for displaying messages
	 */
	public Handler mhandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ERROR:
				// System.out.println("error message sent");
				audioFailure(msg.obj.toString());
				break;
			default:
				super.handleMessage(msg);
				break;
			}			 
		}
	};	

	private void togglesOff() {

		gensine.setChecked(false);
		gensquare.setChecked(false);
		gentri.setChecked(false);
		gensaw.setChecked(false);
		genexp.setChecked(false);
		genlinear.setChecked(false);
		gencubic.setChecked(false);
		genwhite.setChecked(false);
		genblue.setChecked(false);
		genpink.setChecked(false);
		genadd.setChecked(false);
		genmult.setChecked(false);	

	}

	private void setAllToFalse() {
		wave = false;
		sweep = false;
		band = false;
		noise = false;
	}
	
	private void flagToneType(int type, boolean val) {
		
		if (type < 10) {wave = val; return;}		
		if (type < 20) {sweep = val; return;}	
		if (type < 30) {noise = val; return;}	
		if (type < 40) {band = val; return;}	
		
		Log.d("flagToneType()","NO TONE SELECTED, ERROR");
		
	}

	public void audioFailure(final String stackTrace) {

		new AlertDialog.Builder(tone.this)
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

						"\n\nAudioTrack specs:" +
						"\nState: " + soundgen.getState() +
						"\nMin. Buffersize: " + soundgen.getBufferSize() + 
						"\nNative Stream Rate: " + soundgen.getNativeOutputSampleRate() + 
						"\nError: " + stackTrace;

				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

				emailIntent.setType("plain/text");

				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"knacko.me@gmail.com"});

				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AudioToolkit Error");

				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);

				tone.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

				tone.this.finish();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
				tone.this.finish();
			}})
			.setTitle("Error on Initialization")
			.show();	

	}

}