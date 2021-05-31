package com.AudioToolkit.Activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


import com.AudioToolkit.CustomViews.CustomDialog;
import com.AudioToolkit.CustomViews.Databox;
import com.AudioToolkit.CustomViews.EncSelector;
import com.AudioToolkit.CustomViews.SubSelector;
import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkit.Objects.Enclosure;
import com.AudioToolkit.Objects.Subwoofer;
import com.AudioToolkit.Utility.Mathf;
import com.AudioToolkitPro.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class encbuilder extends TabActivity{


	//Response
	Databox resp_numsubsET;
	SubSelector resp_subsel;
	EncSelector resp_encsel;

	Context mContext = encbuilder.this;

	/*
	 * TODO: Enclosure - Bandpass enclosures
	 * TODO: Enclosure - Port volume  - specs for spl and sq
	 * TODO: Enclosure - Baffle and bracing builder
	 * TODO: Enclosure - Database for enclosures and subwoofers
	 */

	boolean adjusting = true;

	String options = "<big><u>Enclosure Builder</u></big><p>" +
			"This calculator is best used for planning purposes.<p>The numbers given are for the external dimensions of the enclosure (including the port, if applicable), " + 
			"but assumes that there isn't any bracing, polyfil, thick walled ports, flared ports, odd dimensions " + 
			"(very biased in one direction) or reverse mounting. <p>You can accomodate, though, by changing the volume slightly.<p>"	+
			"A rule of thumb for port area is 10-12 sq.in per cu.ft.<p>" + 
			"The equations used in the response graph are for a typical rectangular enclosure. Different shapes (wedge, spherical, etc), " +
			"odd dimensions or very short/very long ports may affect the actual response.";

	@Override
	public void onCreate(Bundle b) {

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.encbuilder);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());

		tabBuilder.addTab("Database", R.id.encbuilder_database);
		tabBuilder.addTab("Response",R.id.encbuilder_response);
		tabBuilder.addTab("Port Design",R.id.encbuilder_portspecs);
		tabBuilder.addOptions(options);
		tabBuilder.build();

		//----------------------------------------------------------------------------------------------------- Response
		Button resp_calculate = (Button) findViewById(R.id.resp_calculate);
		Button resp_clear = (Button) findViewById(R.id.resp_clear);
		resp_numsubsET = (Databox) findViewById(R.id.resp_numsubs);
		resp_subsel = (SubSelector) findViewById(R.id.resp_subsel);
		resp_encsel = (EncSelector) findViewById(R.id.resp_encsel);

		resp_calculate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (!resp_numsubsET.tryParse() | !resp_subsel.hasSubwoofer() | !resp_encsel.hasEnclosure()) return;
				
				calculateResponse((int) resp_numsubsET.parse(), resp_subsel.getSubwoofer(), resp_encsel.getEnclosure());			

			}});

		resp_clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				resp_subsel.clear();
				resp_encsel.clear();
				resp_numsubsET.clear();

			}});


	}

	private void calculateResponse(int numsubs, Subwoofer sub, Enclosure enc) {


		double fHigh = 1000, fLow = 10, fPoints = 199;

		double[] Freq = new double[(int) fPoints];

		double fStep = Math.pow(10,Math.log10(fHigh/fLow)/(fPoints-1)); 

		Log.d("calculateResponse","Fstep: " + fStep);

		Freq[0] = fLow;

		for (int i = 1; i < fPoints; i++) {
			Freq[i] = Freq[i-1]*fStep;
		}				


		try {

			double numSubs, Vb, Vas, Qes, Qts, Fs, PEmax, Sd, Xmax, Fb = 0;

			numSubs = resp_numsubsET.parse();
			Vb = enc.getVb();
			Vas = sub.getVas();
			Qes = sub.getQes();
			Qts = sub.getQts();
			Fs = sub.getFs();
			PEmax = sub.getPemax();
			Sd = sub.getSd();
			Xmax = sub.getXmax();

			if (enc.getType() == 1) {
				Fb = enc.getFb();
			}


			double c = 345;
			double Ro = 1.18;

			double Vd = Sd*Xmax*0.0001/1000;	
			Vb /= numSubs;

			System.out.println("Vd: " + Vd);

			GraphView graphView = new LineGraphView(encbuilder.this, "");
			GraphViewData[] graphData = new GraphViewData[(int) fPoints];					

			double SPLmax = -100, SPLlast = 0;

			double F3 = Math.sqrt(Vas/Vb)*Fs;//0.26*Fs*Math.pow(Qts, -1.4);//////
			double n0 = 9.64*Math.pow(10,-10)*Math.pow(Fs,3)*Vas/Qes;
			double Qtc = Math.sqrt((Vas/Vb)+1)*Qts;//Math.sqrt(Qts*Qts*((Vas/Vb)+1));
			double SPL = 112 + 10*Math.log(n0);		
			double K1 = (4*Math.pow(Math.PI,3)*Ro/c)*Math.pow(Fs,4)*Vd*Vd;
			double K2 = 112+10*Math.log(K1);
			double Par = 3*Math.pow(F3,4)*Math.pow(Vd, 2);
			double Per = Par/n0;
			double PeakSPL = SPL+10*Math.log(PEmax);
			double Ql = 7;

			double Fn2, F, Fn4, A, B, C, D, E, dBmag, Pmax, SPLt, SPLd;
			int SPLmaxi = 0;
			double SPLrange = 0;

			for (int i  = 0; i < fPoints; i++) {

				F = Freq[i];
				Fn2 = Math.pow(F/Fs,2);
				Fn4 = Fn2*Fn2;
				A = (Fb/Fs)*(Fb/Fs);
				B = A/Qts+Fb/(Ql*Fs);
				C = 1+A+(Vas/Vb)+Fb/(Fs*Qts*Ql);
				D = 1/Qts+Fb/(Fs*Ql);
				E = (97.0/49.0)*A;
				dBmag = 10*Math.log10((Math.pow(Fn4,2))/((Math.pow(Fn4-C*Fn2+A,2))+(Fn2*Math.pow(D*Fn2-B,2))));
				//Pmax = (K1/n0)*(Math.pow(Fn4-C*Fn2+A,2)+Fn2*Math.pow(D*Fn2-B,2))/(Fn4-E*Fn2+(A*A));
				//SPLd = K2+10*Math.log(Math.pow(Fn4,2)/(Fn4-E*Fn2+A*A));
				//SPLt = PeakSPL+dBmag;


				//SPLlast = Math.min(SPLd, SPLt)-SPLtare;
				if (dBmag > SPLmax) {
					SPLmax = dBmag;
					SPLmaxi = i;
				}

				/*if (i == 0) {
					SPLrange = dBmag;
					System.out.println("n0: " + n0);
					System.out.println("Fn2: " + Fn2);
					System.out.println("Fn4: " + Fn4);
					System.out.println("A: " + A);
					System.out.println("B: " + B);
					System.out.println("C: " + C);
					System.out.println("D: " + D);
					System.out.println("E: " + E);
					System.out.println("dBmag: " + dBmag);
				} */

				if (dBmag < -3) {
					F3 = Freq[i];							
				}

				graphData[i] = new GraphViewData(i, dBmag);	
				//System.out.println(trun(Freq[i],1) + ", " + trun(dBmag,1));

			}

			String s = null;


			//Sealed enclosure
			if (enc.getType() == 0) {
				s =  "F3: " + Mathf.truncate(F3,1) + " Hz\n" + 
						"Peak SPL: +" + Mathf.truncate(Math.abs(SPLmax),1) + " dB at " + Mathf.truncate(Freq[SPLmaxi],1) + " Hz";
			} else {
				Fb = (Qtc/Qts)*Fs;
				s =					"Fc: " + Mathf.truncate(Fb,1)  + " Hz    F3: " + Mathf.truncate(F3,1) + " Hz\n"+
						"Peak SPL: +" + Mathf.truncate(Math.abs(SPLmax),1) + " dB at " + Mathf.truncate(Freq[SPLmaxi],1) + " Hz";
			}
			//	}

			graphView.addSeries(new GraphViewSeries(graphData));

			int graphMaxY = 9, graphRange = 31, graphRangeSkip = 1;

			if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
				graphRangeSkip = 3;
			}

			graphView.setManualYAxisBounds(graphMaxY, graphMaxY-graphRange+1);//ManualYAxisBounds(0,100);

			String[] graphYLabels = new String[graphRange];

			for (int i = 0; i < graphRange; i++) {
				if (i%graphRangeSkip != 0) {graphYLabels[i] = "";}
				else {
					graphYLabels[i] = graphMaxY-i+"dB";
				}
			}

			graphView.setVerticalLabels(graphYLabels);					

			int graphXbars = 8;
			String[] graphXLabels = new String[graphXbars+1];


			for (int i=1; i < graphXbars; i++) {
				if ((i+1)%Math.max(1,graphRangeSkip-1) == 1) {graphXLabels[i] = "";}
				else {
					graphXLabels[i] = Math.round(Freq[((int) (i*(fPoints/(graphXbars))))-1])+"Hz";
				}
			}

			graphXLabels[0] = "";//Math.Mathf.round(Freq[0]) + "Hz";
			graphXLabels[graphXbars] = "";//"1kHz";

			graphView.setHorizontalLabels(graphXLabels);

			new CustomDialog(mContext,graphView, s); 

			return;


		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}

	}

}
