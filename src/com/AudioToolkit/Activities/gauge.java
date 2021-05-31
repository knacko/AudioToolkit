package com.AudioToolkit.Activities;

import com.AudioToolkit.CustomViews.Databox;
import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkitPro.R;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TabActivity;
import android.graphics.Typeface;

public class gauge extends TabActivity {

	Databox amps, gauge, length, voltage, runs;
	Button calculate, clear;
	Toast error;
	TabHost mTabHost;
 
	// 
	String[][] wiredata = 		{{"\n AWG "," Diameter \n(inches)","Area\n (kcmil) ","Ohms\n (per 1000') "},
			{"0","0.325","105.534","0.098"},
			{"1","0.289","83.694","0.124"},
			{"2","0.258","66.373","0.156"},
			{"3","0.229","52.634","0.197"},
			{"4","0.204","41.743","0.249"},
			{"5","0.182","33.102","0.313"},
			{"6","0.162","26.250","0.395"},
			{"7","0.144","20.822","0.498"},
			{"8","0.128","16.511","0.628"},
			{"9","0.114","13.087","0.792"},
			{"10","0.102","10.382","0.999"},
			{"11","0.091","8.226","1.260"},
			{"12","0.081","6.530","1.588"},
			{"13","0.072","5.184","2.003"},
			{"14","0.064","4.107","2.525"},
			{"15","0.057","3.260","3.184"},
			{"16","0.051","2.583","4.016"},
			{"17","0.045","2.052","5.064"},
			{"18","0.040","1.624","6.385"},
			{"19","0.036","1.289","8.051"},
			{"20","0.032","1.022","10.150"},
			{"21","0.029","0.812","12.800"},
			{"22","0.025","0.640","16.140"},
			{"23","0.023","0.511","20.360"},
			{"24","0.020","0.404","25.670"},
			{"","","",""},
			{"\n AWG "," Diameter \n(mm)","Area\n (mm^2) ","Ohms\n (per km) "},
			{"0","8.251","53.475","0.322"},
			{"1","7.348","42.409","0.406"},
			{"2","6.544","33.632","0.513"},
			{"3","5.827","26.670","0.646"},
			{"4","5.189","21.151","0.815"},
			{"5","4.621","16.773","1.028"},
			{"6","4.115","13.301","1.296"},
			{"7","3.665","10.551","1.635"},
			{"8","3.264","8.366","2.061"},
			{"9","2.906","6.631","2.599"},
			{"10","2.588","5.260","3.277"},
			{"11","2.304","4.168","4.134"},
			{"12","2.053","3.309","5.210"},
			{"13","1.829","2.627","6.572"},
			{"14","1.628","2.081","8.284"},
			{"15","1.450","1.652","10.446"},
			{"16","1.291","1.309","13.176"},
			{"17","1.151","1.040","16.614"},
			{"18","1.024","0.823","20.948"},
			{"19","0.912","0.653","26.414"},
			{"20","0.812","0.518","33.301"},
			{"21","0.724","0.412","41.995"},
			{"22","0.643","0.324","52.953"},
			{"23","0.574","0.259","66.798"},
			{"24","0.511","0.205","84.219"}};

	double[] gaugeRes = {0.098,0.124,0.156,0.197,0.249,0.313,0.395,0.498,0.628,
			0.792,0.999,1.260,1.588,2.003,2.525,3.184,4.016,5.064,6.385,
			8.051,10.150,12.800,16.140,20.360,25.670};

	String help = "<big><u>Wire Gauge</u></big><p>" +
			"For load, check the fuse rating of the amplifier, or calculate the load with the power conversion tool.<p>" +
			"For length, use the total length of the power and ground wires.<p>" +
			"The gauge finder is set for a maximum voltage drop of 2.5%.<p>" + 
			"kcmil = d^2*1000 (cu.in)<p>" + 
			"The values given for AWG specs are assuming a solid copper wire. A high count stranded wire will be very similar.";
	
	public void onCreate(Bundle b) {


		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gauge);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("Gauge Solver", R.id.wiregauge);
		tabBuilder.addTab("AWG Specs",R.id.wirespecs);
		tabBuilder.addOptions(help);
		
		tabBuilder.build();
	
		error = Toast.makeText(getApplicationContext(), (CharSequence) "Value is out of range.", 3);

		/*
		//Ad
		LinearLayout adspace = (LinearLayout) findViewById(R.id.adspace);
		AdView adView = new AdView(this, AdSize.BANNER, "a14ed711c40c04c");
		adspace.addView(adView);
		AdRequest request = new AdRequest();
   	    request.addTestDevice(AdRequest.TEST_EMULATOR);
   	    request.addTestDevice("3234E1E443EACEC141A8A39E568A9288");
		adView.loadAd(request);
		 */

		voltage = (Databox) findViewById(R.id.voltage);
		amps = (Databox) findViewById(R.id.amps);
		length = (Databox) findViewById(R.id.length);
		gauge = (Databox) findViewById(R.id.gauge);
		runs = (Databox) findViewById(R.id.runs);
		calculate = (Button) findViewById(R.id.calculate);
		clear = (Button) findViewById(R.id.clear);

		calculate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				int solvecase = 0;
				double L = 0, N = 0, R = 0, A = 0;

				//Get Voltage
				if (!voltage.tryParse()) return;
				double V = voltage.parse();

				if (V < 0) V = Math.abs(V);
				voltage.setValue(V,1);
				System.out.println("Vdrop: " + V);
				V *= .025; //voltage drop

				//Get amperage, solvecase = 1 if not
				if (amps.tryParse(false)) {
					A = amps.parse();
					if (A < 0) A = Math.abs(A);
					amps.setValue(A,1);
				} else {
					if (solvecase != 0) return;
					solvecase = 1;
				} 

				if (length.tryParse(false)) {
					L = length.parse();
					if (L < 0) L = Math.abs(L);
					length.setValue(L,1);
				} else {
					if (solvecase != 0) return;
					solvecase = 2;
				} 				
				
				if (gauge.tryParse(false)) {
					R = gauge.parse();
					R = Math.round(R); 
					if (R > 24) R = 24;
					if (R < 0) R = 0;
					gauge.setValue(R,1);
					R = gaugeRes[(int) R]; //get gauge off of the chart based on the int value of the gauge box

					R = R/1000;
				}  else {
					if (solvecase != 0) return;
					solvecase = 3;
				}

				if (runs.tryParse(false)) {
					N = runs.parse();
					if (N < 1) N = 1;
					N = Math.round(N);
					runs.setValue(N,1);
				}  else {
					if (solvecase != 0) return;
					solvecase = 4;
				}

				if (solvecase == 0) return;
				
				if (solvecase == 1) {// Missing amps

					A = (N*V)/(L*R);
					amps.setValue(A,1);

				} else if (solvecase == 2) {// Missing length

					L = (N*V)/(A*R);	
					length.setValue(L,1);


				} else if (solvecase == 3) {// Missing AWG

					R = ((N*V)/(A*L))*1000;

					System.out.println("Gauge Resistance: " + R);

					if (R > gaugeRes[gaugeRes.length-1]) {error.show(); return;}

					if (R < gaugeRes[0]) {error.show(); return;}

					int i = 0;
					for (i = 0; i < gaugeRes.length; i++) {
						if (R < gaugeRes[i]) break;
					}

					gauge.setValue(i,1);

				} else if (solvecase == 4) {// Missing runs

					N = (A*L*R)/V;
					runs.setValue(Math.ceil(Math.max(N,1)),1);

				}





				/*final int maxvals[][] = {{	0,	4,	7,	10,	13,	16,	19,	22,	28,	0}, 
						{	20,	14,	12,	12,	10,	10,	8,	8,	8,	0},
						{	35,	12,	10,	8,	8,	6,	6,	6,	4,	0},
						{	50,	10,	8,	8,	6,	6,	4,	4,	4,	0},
						{	65,	8,	8,	6,	4,	4,	4,	4,	2,	0},
						{	85,	6,	6,	4,	4,	2,	2,	2,	0,	0},
						{	105,6,	6,	4,	2,	2,	2,	2,	0,	0},
						{	125,4,	4,	4,	2,	2,	0,	0,	0,	0},
						{	150,2,	2,	2,	2,	0,	0,	0,	0,	0},
						{   200,0,	0,	0,	0,	0,	0,	0,	0,	0}};

				if (solvecase == 1) {// Missing amps

					for (x = 2; x < 9 && L >= maxvals[0][x]; x++) {} //Find index of length of wire
					x--;
					for (y = 2; y < 9 && maxvals[y][x] >= N; y++) {} //Find index of gauge
					y--;					

					//System.out.println("********** N = " + maxvals[y][x] + " < " + N + "(" + x + "), L = " + maxvals[0][x] + " < " + L + "(" + y + ")");

					if (maxvals[y][x] < N  && y == 1) {error.show(); return;}		

					amps.setText(""+trun(maxvals[y][0]));

				} else if (solvecase == 2) {// Missing length

					for (y = 2; y < 9 && maxvals[y][0] < A; y++) {} //find amps
					y--;
					for (x = 2; x < 9 && maxvals[y][x] > N; x++) {} // find gauge
					x--;

					if (maxvals[y][x] < N && x == 1) {error.show(); return;}

					length.setText(""+trun(maxvals[0][x]));

				} else if (solvecase == 3) {// Missing AWG

					for (y = 0; y < 8 && maxvals[y][0] <= A; y++) {} //find amps
					for (x = 0; x < 8 && maxvals[0][x] <= L; x++) {} //find length

					String AWG = maxvals[y][x] + "";
					if (x == 8 && y == 8) {error.show(); return;}

					gauge.setText(trun(maxvals[y][x]) + "");

				}*/

				return;

			}});

		clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				voltage.clear();
				runs.clear();
				amps.clear();
				length.clear();
				gauge.clear();

			}});

		//Populate the specs page
		TableLayout tl = (TableLayout) findViewById(R.id.wiretable);

		for (int i = 0; i < wiredata.length; i++) {	//Add a row for each row in the data array

			TableRow tr = new TableRow(this);
			tr.setId(1000+i); 			

			for (int j = 0; j < wiredata[i].length; j++) {	//Add a TV for each entry in the row
				TextView labelTV = new TextView(this);
				labelTV.setId(2000+j);
				labelTV.setText(wiredata[i][j]);
				labelTV.setGravity(0x11); //Center it
				if (i == 0 || i == 27) labelTV.setTypeface(null, Typeface.BOLD); //Bold the headers
				// System.out.println(labelTV.getText());
				tr.addView(labelTV);        
			}			

			tl.addView(tr);  

		}		
	}



}
