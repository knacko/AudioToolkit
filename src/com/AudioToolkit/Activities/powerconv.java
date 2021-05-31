package com.AudioToolkit.Activities;


import com.AudioToolkit.CustomViews.*;
import com.AudioToolkitPro.R;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.app.TabActivity;
import android.content.Context;

public class powerconv extends TabActivity {

	/* 
	 *  TODO: Power Eqns - Equation Solver - solve any equation given inputs, need some way to display the equations
	 */

	Databox watts, volts, ohms, amps;
	Button calculate, clear;
	CheckBox wlock, vlock, alock, rlock;
	TabHost mTabHost;
	Context mContext = powerconv.this;	
	
	public void onCreate(Bundle b) {

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.powerconv);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("Ohm's Law Solver", R.id.conversion);
		tabBuilder.addTab("Equations",R.id.equation);
		
		tabBuilder.build();

		watts = (Databox) findViewById(R.id.watts);
		volts = (Databox) findViewById(R.id.volts);
		ohms = (Databox) findViewById(R.id.ohms);
		amps = (Databox) findViewById(R.id.amps);
		calculate = (Button) findViewById(R.id.calculate);
		clear = (Button) findViewById(R.id.clear);

		//equations
		Button powerconv_ohms = (Button) findViewById(R.id.powerconv_ohms);
		Button powerconv_waves = (Button) findViewById(R.id.powerconv_waves);
		Button powerconv_circuit_parallel = (Button) findViewById(R.id.powerconv_circuit_parallel);
		Button powerconv_circuit_series = (Button) findViewById(R.id.powerconv_circuit_series);
		Button powerconv_enc_sealed = (Button) findViewById(R.id.powerconv_enc_sealed);
		Button powerconv_enc_ported = (Button) findViewById(R.id.powerconv_enc_ported);
		Button powerconv_enc_ebp = (Button) findViewById(R.id.powerconv_enc_ebp);
		Button powerconv_conv_length = (Button) findViewById(R.id.powerconv_conv_length);
		Button powerconv_conv_volume = (Button) findViewById(R.id.powerconv_conv_volume);
		Button powerconv_conv_speed = (Button) findViewById(R.id.powerconv_conv_speed);
		Button powerconv_conv_mass = (Button) findViewById(R.id.powerconv_conv_mass);
		Button powerconv_conv_area = (Button) findViewById(R.id.powerconv_conv_area);
		Button powerconv_shape_volume = (Button) findViewById(R.id.powerconv_shape_volume);
		Button powerconv_shape_area = (Button) findViewById(R.id.powerconv_shape_area);

		String Rt = "\\text{Resistor}_{total} = R_1 + R_2 +\\text{ ... }+ R_N";
		String It = "\\text{Inductor}_{total} = L_1 + L_2 +\\text{ ... }+ L_N";
		String Ct = "\\text{Capacitor}_{total} = \\frac{1}C_1 + \\frac{1}C_2 +\\text{ ... }+ \\frac{1}C_N";
		
		//loadWebview((WebView) findViewById(R.id.tex), Ct);
		//loadWebview((WebView) findViewById(R.id.It), It);
		//loadWebview((WebView) findViewById(R.id.Ct), Ct);
		
		
		/*
		wlock = (CheckBox) findViewById(R.id.wlock);
		vlock = (CheckBox) findViewById(R.id.vlock);
		alock = (CheckBox) findViewById(R.id.alock);
		rlock = (CheckBox) findViewById(R.id.rlock);	
		 */

		//Ad	
		/*
		LinearLayout adspace = (LinearLayout) findViewById(R.id.adspace);
		AdView adView = new AdView(this, AdSize.BANNER, "a14ed711c40c04c");
		adspace.addView(adView);
		AdRequest request = new AdRequest();
   	    request.addTestDevice(AdRequest.TEST_EMULATOR);
   	    request.addTestDevice("3234E1E443EACEC141A8A39E568A9288");
		adView.loadAd(request);
		 */
		calculate.setOnClickListener(new View.OnClickListener() {
			
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

				if (check != 2) {
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


		clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				/*
				if (!wlock.isChecked()) watts.setText("");
				if (!vlock.isChecked()) volts.setText("");
				if (!rlock.isChecked()) ohms.setText("");
				if (!alock.isChecked()) amps.setText("");
				 */

				watts.clear();
				volts.clear();
				ohms.clear();
				amps.clear();


			}});


		powerconv_ohms.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_ohmslaw, "Ohm's Law","");	
			}});

		powerconv_circuit_series.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_circuit_series, "Components in Series","");				
			}});

		powerconv_circuit_parallel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_circuit_parallel, "Components in Parallel",""); 				
			}});

		powerconv_waves.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_waves, "Waves + Wavelength","");				
			}});

		powerconv_shape_area.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_shape_area, "Area",""); 				
			}});

		powerconv_shape_volume.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_shape_volume, "Volume",""); 				
			}});

		powerconv_enc_sealed.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_enc_sealed, "Sealed Enclosure",""); 				
			}});

		powerconv_enc_ported.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_enc_ported, "Ported Enclosure","");				
			}});

		powerconv_enc_ebp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_enc_ebp, "Efficiency Bandwidth Product",""); 				
			}});

		powerconv_conv_length.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_conv_length, "Length Conversion","");				
			}});

		powerconv_conv_area.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_conv_area, "Area Conversion",""); 				
			}});

		powerconv_conv_volume.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_conv_volume, "Volume Conversion",""); 			
			}});

		powerconv_conv_speed.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_conv_speed, "Speed Conversion","");				
			}});

		powerconv_conv_mass.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new CustomDialog(mContext,R.drawable.eqn_conv_mass, "Mass Conversion","");				
			}});
	}

	/*
	private void loadWebview(WebView w, String tex) {
		
		w.getSettings().setJavaScriptEnabled(true);
		w.getSettings().setBuiltInZoomControls(true);
		w.loadDataWithBaseURL("http://bar", "<script type='text/x-mathjax-config'>"
		                      +"MathJax.Hub.Config({ " 
							  	+"showMathMenu: false, "
							  	+"jax: ['input/TeX','output/HTML-CSS'], "
							  	+"extensions: ['tex2jax.js'], " 
							  	+"TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
							  	  +"'noErrors.js','noUndefined.js'] } "
							  +"});</script>"
		                      +"<script type='text/javascript' "
							  +"src='file:///android_asset/MathJax/MathJax.js'"
							  +"></script><span id='math'></span>","text/html","utf-8","");
		
		w.loadUrl("javascript:document.getElementById('math').innerHTML='\\\\["
		           +doubleEscapeTeX(tex)+"\\\\]';");
		w.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
		w.setBackgroundColor(0x00000000);
	}
	
	private String doubleEscapeTeX(String s) {
		String t="";
		for (int i=0; i < s.length(); i++) {
			if (s.charAt(i) == '\'') t += '\\';
			if (s.charAt(i) != '\n') t += s.charAt(i);
			if (s.charAt(i) == '\\') t += "\\";
		}
		return t;
	}
	*/


}
