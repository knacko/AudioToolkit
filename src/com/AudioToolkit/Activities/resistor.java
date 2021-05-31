package com.AudioToolkit.Activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.TabActivity;
import android.content.Context;
import java.lang.StringBuffer;

import com.AudioToolkit.CustomViews.CustomDialog;
import com.AudioToolkit.CustomViews.Databox;
import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkit.Utility.Mathf;
import com.AudioToolkitPro.R;



public class resistor extends TabActivity {

	int d1 = 2, d2 = 0, m = 5, t = 0;
	ImageView d1up, d2up, mup, tup, d1down, d2down, mdown, tdown;
	TextView  d1TV, d2TV, mTV, tTV;
	Databox resval, vinput, vdrop, ampled, numled, resohm, reswatt;
	RadioButton series, parallel;
	Button calculate, clear;
	Boolean multiflag = false;
	TabHost mTabHost;

	int[] d1color = {0xff000000, 0xff844200, 0xffFF0000, 0xffff8400, 0xffffff00, 0xff00e700, 0xff0000ff, 0xffad00ad, 0xffa5a5a5, 0xffffffff};
	int[] d2color = d1color;
	int[] mcolor = {0xffe7d6d6 /*silver*/, 0xffffba00 /*gold*/, 0xff000000, 0xff844200, 0xffFF0000, 0xffff8400, 0xffffff00, 0xff00e700, 0xff0000ff, 0xffad00ad};
	int[] tcolor = {0xffe7d6d6 /*silver*/, 0xffffba00 /*gold*/, 0xffff0000, 0xff844200, 0xff00e700, 0xff0000ff, 0xffad00ad, 0xffa5a5a5};

	double[] tstr = {10,5,2,1,0.5,0.25,0.1,0.05};

	Toast pro;

	String help = "<big><u>LED and Resistors</u></big><p>" +
			"Use the specs given with the LEDs to achieve best results.<p>" + 
			"For LEDs in parallel, use the single LED calculator and wire them in parallel (dotted line in the diagram).";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.resistor);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("LED Circuit", R.id.led);
		tabBuilder.addTab("Resistor Value",R.id.resistor);
		//tabBuilder.addOptions(help);
		
		tabBuilder.build();

		d1up = (ImageView) findViewById(R.id.d1up);
		d2up = (ImageView) findViewById(R.id.d2up);
		mup = (ImageView) findViewById(R.id.mup);
		tup = (ImageView) findViewById(R.id.tup);

		d1down = (ImageView) findViewById(R.id.d1down);
		d2down = (ImageView) findViewById(R.id.d2down);
		mdown = (ImageView) findViewById(R.id.mdown);
		tdown = (ImageView) findViewById(R.id.tdown);

		d1TV = (TextView) findViewById(R.id.d1);
		d2TV = (TextView) findViewById(R.id.d2);
		mTV = (TextView) findViewById(R.id.m);
		tTV = (TextView) findViewById(R.id.t);

		resval = (Databox) findViewById(R.id.resval);
		resohm = (Databox) findViewById(R.id.resohm);
		reswatt = (Databox) findViewById(R.id.reswatt);
		vinput = (Databox) findViewById(R.id.vinput);
		vdrop = (Databox) findViewById(R.id.vdrop);
		ampled = (Databox) findViewById(R.id.ampled);
		numled = (Databox) findViewById(R.id.numled);

		calculate =  (Button) findViewById(R.id.calculate);
		clear =  (Button) findViewById(R.id.clear);
		Button res_diagrams = (Button) findViewById(R.id.res_diagrams);

		//single = (RadioButton) findViewById(R.id.single);
		series = (RadioButton) findViewById(R.id.series);
		parallel = (RadioButton) findViewById(R.id.parallel);

		//Change graphics of buttons if they are not available in free version
		if (getString(R.string.freeApp).equals("true")) {

			Context c = getApplicationContext();			

			vdrop.setAsFree(c,3);
			ampled.setAsFree(c,35);			

		}

		/*
		LinearLayout adspace = (LinearLayout) findViewById(R.id.adspace);
		AdView adView = new AdView(this, AdSize.BANNER, "a14ed711c40c04c");
		adspace.addView(adView);
		AdRequest request = new AdRequest();
   	    request.addTestDevice(AdRequest.TEST_EMULATOR);
   	    request.addTestDevice("3234E1E443EACEC141A8A39E568A9288");
		adView.loadAd(request);
		 */
		setColors();
		resval.setText(getRText());

		calculate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				double vin, vd, aled, nled;

				if (!vinput.tryParse() | !vdrop.tryParse() | !ampled.tryParse() | (series.isChecked() ? !numled.tryParse() : true)) return;
				vin = vinput.parse();
				vd = vdrop.parse();
				aled = ampled.parse();

				vin = Math.abs(vin);
				vd = Math.abs(vd);
				aled = Math.abs(aled);

				vinput.setValue(vin,1);
				vdrop.setValue(vd,1);
				ampled.setValue(aled,1);

				if (parallel.isChecked()) {

					if (vd > vin) {
						Toast.makeText(getApplicationContext(), (CharSequence) "Input voltage not high enough to support the circuit.", 3).show();
						return;
					}

					double ohms = (vin-vd)/(aled/1000);
					double power = vd*(aled/1000);
					resohm.setValue(ohms,1);
					reswatt.setValue(power,1);
					return;
				}

				if (series.isChecked()) {

					nled = numled.parse();

					nled = Math.abs(nled);					
					numled.setValue(nled,1);

					if (vd*nled > vin) {
						Toast.makeText(getApplicationContext(), (CharSequence) "Input voltage not high enough to support the circuit.", 3).show();
						return;
					}

					double ohms = (vin-(vd*nled))/(aled/1000);
					double power = vd*(aled/1000);
					resohm.setValue(ohms,1);
					reswatt.setValue(power,1);
					return;
				}

				/*
				if (parallel.isChecked()) {
					try {								
						nled = Double.parseDouble(numled.getET().getText().toString());
					}  catch (NumberFormatException er) {
						return;
					}

					nled = Math.abs(nled);					
					numled.setText(trun(nled));

					if (vd > vin) {
						Toast.makeText(getApplicationContext(), (CharSequence) "Input voltage not high enough to support the circuit.", 3).show();
						return;
					}

					double ohms = (vin-vd)/((aled/1000)*nled);
					double power = vd*(aled/1000)*nled;
					resohm.setText(trun(ohms));
					reswatt.setText(trun(power));
					return;
				}*/

			}});


		clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				vinput.clear();
				vdrop.clear();
				ampled.clear();
				numled.clear();
				resohm.clear();
				reswatt.clear();

			}});	

		res_diagrams.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				new CustomDialog(resistor.this,R.drawable.sch_res_led, "LED and Resistor Circuit","R - Resistor\nLEDn - the nth LED"); return;

			}});

		series.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				numled.setVisibility(View.VISIBLE);	
			}});

		
		parallel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				numled.setVisibility(View.GONE);	
			}});
		 

		d1up.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				d1++;
				if (d1 > d1color.length-1) d1 = 1;
				setColors();
				resval.setText(getRText());
				return;
			}});

		d2up.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				d2++;
				if (d2 > d2color.length-1) d2 = 0;
				setColors();
				resval.setText(getRText());
				return;
			}});

		mup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				m++;
				if (m > mcolor.length-1) m = 0;
				setColors();
				resval.setText(getRText());
				return;
			}});

		tdown.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				t++;
				if (t > tcolor.length-1) t = 0;
				setColors();
				resval.setText(getRText());
				return;
			}});

		d1down.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				d1--;
				if (d1 < 1) d1 = d1color.length-1;
				setColors();
				resval.setText(getRText());
				return;
			}});

		d2down.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				d2--;
				if (d2 < 0) d2 = d2color.length-1;
				setColors();
				resval.setText(getRText());
				return;
			}});

		mdown.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				m--;
				if (m < 0) m = mcolor.length-1;
				setColors();
				resval.setText(getRText());
				return;
			}});

		tup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				t--;
				if (t < 0) t = tcolor.length-1;
				setColors();
				resval.setText(getRText());
				return;
			}});
	}

	private void setColors() {

		tTV.setBackgroundColor(tcolor[t]);
		mTV.setBackgroundColor(mcolor[m]);
		d2TV.setBackgroundColor(d2color[d2]);
		d1TV.setBackgroundColor(d1color[d1]);
		return;
	}


	private String getRText() {

		String val = "";

		val += d1;

		val += d2;

		if (!(d1 == 0 && d2 == 0)) {

			switch(m) { // d2 != 0
			case 0: val = "0." + val; break;
			case 1: val = new StringBuffer(val).insert(1, ".").toString(); break;
			case 2: break;
			case 3: val += "0"; break;
			case 4: val = new StringBuffer(val).insert(1, ".").toString() + "K"; break;//+= "00"; break;
			case 5: val += "K"; break;
			case 6: val += "0K"; break;
			case 7: val = new StringBuffer(val).insert(1, ".").toString() + "M"; break;//+= "00K"; break;
			case 8: val += "M"; break;
			case 9: val += "0M"; break;
			}
		}

		val += " ± " + Mathf.truncate(tstr[t],3) + "%";

		val = val.replaceAll("000K", "M");
		val = val.replaceAll("000 ", "K ");

		if (val.charAt(0) == '0' && val.charAt(1) != '.') val = val.substring(1);

		return val;
	}

	

}

