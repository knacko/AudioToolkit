package com.AudioToolkit.Activities;

import com.AudioToolkit.CustomViews.CustomDialog;
import com.AudioToolkit.CustomViews.Databox;
import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkit.Utility.Mathf;
import com.AudioToolkitPro.R;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;


public class crossover extends TabActivity {

	 
	/*
	 * TODO: Crossover - active crossovers, tubes, l-pad
	 */
		
	//High low
	Databox freqhighlow, loadhighlow; 
	RadioButton hlpass6db , hlpass12db , hlpass18db ;

	//NBPass
	Databox freq1nbpass, freq2nbpass, loadnbpass;
	RadioButton bpass6db , bpass12db , bpass18db ;

	//Zobel
	Databox freqzobel, loadzobel;

	double c1, c2, c3, c4, c5, c6, l1, l2, l3, l4, l5, l6, r1;
	
	double l1_hp, l2_hp, l3_hp, l4_hp, l5_hp, c1_hp, c2_hp, c3_hp, c4_hp, c5_hp, l1_lp, l2_lp, l3_lp, l4_lp, l5_lp, c1_lp, c2_lp, c3_lp, c4_lp, c5_lp; 

	Context mContext = crossover.this;
	
	String options = "<big><u>Crossover Calculator</u></big><p>" +
			"Narrow bandpass is used if the low pass frequency less than 20x the high pass frequency. Not doing so will likely result in distortion.<p>" +
			"<big>Caps:</big><br>" +  
			"- Mylar (Best)<br>" + 
			"- Polypropelene<br>" + 
			"- Non-polarized electrolytic (low pass only)<p>" + 
			"<big>Inductors:</big><br>" +  
			"- Copper foil air core (Best)<br>" + 
			"- Copper wound air core<br>" + 
			"- Iron core (for high inductances)<br>";
	
	
	public void onCreate(Bundle b) { 

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crossover);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("High/Low", R.id.highlow);
		tabBuilder.addTab("Bandpass",R.id.nbpass);
		tabBuilder.addTab("Zobel",R.id.zobel);
		tabBuilder.addOptions(options);
		
		tabBuilder.build();
				
		//Highlow
		freqhighlow = (Databox) findViewById(R.id.freqhighlow);
		loadhighlow = (Databox) findViewById(R.id.loadhighlow);

		Button clearhighlow = (Button) findViewById(R.id.clearhighlow);
		Button hpasscalc = (Button) findViewById(R.id.xover_hpass_calc);
		Button lpasscalc = (Button) findViewById(R.id.xover_lpass_calc);
		
		hlpass6db = (RadioButton) findViewById(R.id.xover_hlpass_6db);
		hlpass12db = (RadioButton) findViewById(R.id.xover_hlpass_12db);
		hlpass18db = (RadioButton) findViewById(R.id.xover_hlpass_18db);


		hpasscalc.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (!calcHighLow()) return;

				if (hlpass6db.isChecked()) showDialog(R.drawable.sch_high6db, "High Pass - 6dB/Octave","C1 : " + trun(c1) + " µfd");
				
				if (hlpass12db.isChecked()) showDialog(R.drawable.sch_high12db, "High Pass - 12dB/Octave","C2: " + trun(c2) + " µfd\nL2: " + trun(l2) + " mHy");
					
				if (hlpass18db.isChecked()) showDialog(R.drawable.sch_high18db, "High Pass - 18dB/Octave","C4: " + trun(c4) + " µfd\nC5: " + trun(c5) + " µfd\nL5: " + trun(l5) + " mHy");
				
				return;

			}});
		
		lpasscalc.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (!calcHighLow()) return;

				if (hlpass6db.isChecked()) showDialog(R.drawable.sch_low6db, "Low Pass - 6dB/Octave","L1: " + trun(l1) + "mHy");
				
				if (hlpass12db.isChecked()) showDialog(R.drawable.sch_low12db, "Low Pass - 12dB/Octave","C2: " + trun(c2) + "µfd\nL2: " + trun(l2) + "mHy");
					
				if (hlpass18db.isChecked()) showDialog(R.drawable.sch_low18db, "Low Pass - 18dB/Octave","C3: " + trun(c3) + "µfd\nL3: " + trun(l3) + "mHy\nL4: " + trun(l4) + "mHy");
				
				return;

			}});
		
		clearhighlow.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				freqhighlow.clear();
				loadhighlow.clear();

			}});

		//NBPss
		freq1nbpass = (Databox) findViewById(R.id.freq1nbpass);
		freq2nbpass = (Databox) findViewById(R.id.freq2nbpass);
		loadnbpass = (Databox) findViewById(R.id.loadnbpass);
		
		Button clearnbpass = (Button) findViewById(R.id.clearnbpass);
		Button nbpasscalc = (Button) findViewById(R.id.xover_nbpass_calc);
		Button bpasscalc = (Button) findViewById(R.id.xover_bpass_calc);
		
		bpass6db = (RadioButton) findViewById(R.id.xover_bpass_6db);
		bpass12db = (RadioButton) findViewById(R.id.xover_bpass_12db);
		bpass18db = (RadioButton) findViewById(R.id.xover_bpass_18db);
		

		bpasscalc.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (!calcbpass()) return;
 
				if (bpass6db.isChecked()) showDialog(R.drawable.sch_bpass6db, "Bandpass - 6dB/Octave","C1: " + trun(c1_hp) + " µfd\nL1: " + trun(l1_lp) + " mHy");
				
				if (bpass12db.isChecked()) showDialog(R.drawable.sch_bpass12db, "Bandpass - 12dB/Octave","C2: " + trun(c2_lp) + " µfd\nC3: " + trun(c2_hp) + " µfd\nL2: " + trun(l2_lp) + " mHy\nL3: " + trun(l2_hp) + " mHy");
					
				if (bpass18db.isChecked()) showDialog(R.drawable.sch_bpass18db, "Bandpass - 18dB/Octave","C3: " + trun(c3_lp) + " µfd\nC4: " + trun(c4_hp) + " µfd\nC5: " + trun(c5_hp) + " µfd\nL3: " + trun(l3_lp) + "mHy\nL4: " + trun(l4_lp) + "mHy\nL5: " + trun(l5_hp) + " mHy");
				
				return;

			}});
		
		nbpasscalc.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (!calcnbpass()) return;

				if (bpass6db.isChecked()) showDialog(R.drawable.sch_nbp6db, "Narrow Bandpass - 6dB/Octave","C1: " + trun(c1) + " µfd\nL1: " + trun(l1) + " mHy");
				
				if (bpass12db.isChecked()) showDialog(R.drawable.sch_nbp12db, "Narrow Bandpass - 12dB/Octave","C2: " + trun(c2) + "µfd\nC3: " + trun(c3) + "µfd\nL2: " + trun(l2) + "mHy\nL3: " + trun(l3) + "mHy");
					
				if (bpass18db.isChecked()) showDialog(R.drawable.sch_nbp18db, "Narrow Bandpass - 18dB/Octave","C4: " + trun(c4) + " µfd\nC5: " + trun(c5) + " µfd\nC6: " + trun(c6) + " µfd\nL4: " + trun(l4) + " mHy\nL5: " + trun(l5) + " mHy\nL6: " + trun(l6) + " mHy");
				
				return;

			}});

		clearnbpass.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				freq1nbpass.clear();
				freq2nbpass.clear();
				loadnbpass.clear();

			}});


		//Zobel
		freqzobel = (Databox) findViewById(R.id.freqzobel);
		loadzobel = (Databox) findViewById(R.id.loadzobel);
		Button calczobel = (Button) findViewById(R.id.calczobel);
		Button clearzobel = (Button) findViewById(R.id.clearzobel);

		calczobel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (!calczobel()) return;  

				showDialog(R.drawable.sch_zobel, "Zobel Filter - 6dB/Octave","C1: " + trun(c1) + " µfd\nR1: " + trun(r1) + " ohms");
				
				return;

			}});

		clearzobel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				freqzobel.clear();
				loadzobel.clear();

			}});
		
		
		//Change graphics of buttons if they are not available in free version
		if (getString(R.string.freeApp).equals("true")) {
			
			Context c = getApplicationContext();
			
			loadhighlow.setAsFree(c,9);
			
			loadzobel.setAsFree(c,9);			

			loadnbpass.setAsFree(c,9);
						
		}
		

	}

	/**
	 * Generates a dialog box to display the picture and component values
	 * 
	 * @param imageID - the particular image to use
	 * @param name - the title for the box
	 * @param components - the text below the picture
	 */
	private void showDialog (int imageID, String name, String components) {

		new CustomDialog(crossover.this, imageID, name, components);
		
	}

	/**
	 * Calculates the values for a high or low crossover to the global crossover variables
	 * 
	 * @return true if successful, false if not
	 */
	public boolean calcHighLow() {

		double frequency, load;

		if (!loadhighlow.tryParse() | !freqhighlow.tryParse()) return false;
		frequency = freqhighlow.parse();
		load = loadhighlow.parse();

		l1 = (1000*load)/((Math.PI*2)*frequency);
		c1 = 1000000/((Math.PI*2)*load*frequency);
		l2 = l1*1.414;
		c2 = c1*.707;
		l3 = l1*1.5;
		c3 = c1*1.33;
		l4 = l1*.5;
		c4 = c1*.667;
		l5 = l1*.75;
		c5 = c1*2;

		return true;			

	}
	
	/**
	 * Calculates the values for a bandpass crossover to the global crossover variables
	 * 
	 * @return true if successful, false if not
	 */
	public boolean calcbpass() {

		double f1, f2, ld;
		
		if (!freq1nbpass.tryParse() | !freq2nbpass.tryParse() | !loadnbpass.tryParse()) return false;
		f1 = freq1nbpass.parse();
		f2 = freq2nbpass.parse();
		ld = loadnbpass.parse();

		if (f1 > f2) { //swap if low freq is higher
			double temp = f1;
			f1 = f2;
			f2 = temp;					
			freq1nbpass.setValue(f1,1);
			freq2nbpass.setValue(f2,1);
		}
		
		if (f2/f1 < 20) {
			Toast.makeText(getApplicationContext(), (CharSequence) "The frequency range is less than 2 decades, so a narrow bandpass crossover may be more appropriate.\n\n"
					+ "See options menu for more info.", 5).show();			
			
			
			
		}
		
		l1_hp = (1000*ld)/((Math.PI*2)*f1);
		c1_hp = 1000000/((Math.PI*2)*ld*f1);
		l2_hp = l1_hp*1.414;
		c2_hp = c1_hp*.707;
		l3_hp = l1_hp*1.5;
		c3_hp = c1_hp*1.33;
		l4_hp = l1_hp*.5;
		c4_hp = c1_hp*.667;
		l5_hp = l1_hp*.75;
		c5_hp = c1_hp*2;
		l1_lp = (1000*ld)/((Math.PI*2)*f2);
		c1_lp = 1000000/((Math.PI*2)*ld*f2);
		l2_lp = l1_lp*1.414;
		c2_lp = c1_lp*.707;
		l3_lp = l1_lp*1.5;
		c3_lp = c1_lp*1.33;
		l4_lp = l1_lp*.5;
		c4_lp = c1_lp*.667;
		l5_lp = l1_lp*.75;
		c5_lp = c1_lp*2;

		return true;			

	}

	
	/**
	 * Calculates the values for a narrow bandpass crossover to the global crossover variables
	 * 
	 * @return true if successful, false if not
	 */
	public boolean calcnbpass() {

		double f1, f2, ld;

		if (!freq1nbpass.tryParse() | !freq2nbpass.tryParse() | !loadnbpass.tryParse()) return false;
		f1 = freq1nbpass.parse();
		f2 = freq2nbpass.parse();
		ld = loadnbpass.parse();

		if (f1 > f2) { //swap if low freq is higher
			double temp = f1;
			f1 = f2;
			f2 = temp;					
			freq1nbpass.setValue(f1,1);
			freq2nbpass.setValue(f2,1);
		}

		if (f2/f1 > 20) {
			Toast.makeText(getApplicationContext(), (CharSequence) "The frequency range is greater than 2 decades, so a bandpass crossover may be more appropriate.\n\n"
					+ "See options menu for more info.", 5).show();					
		}


		//6db
		l1 = ld/((f2-f1)*6.283); //l1 in mhy
		c1 = 1/(39.472*(l1/1000)*(f1*f2)); //c1 in ufd

		//12db
		c2 = .707/(ld*6.283*(f2-f1)); //c1
		l3 = 1/(c2*Math.pow(6.283*(f2-f1),2)); //l2
		c3 = 1/(39.472*l3*(f1*f2)); //c2
		l2 = 1/(39.472*c2*f1*f2); //l1

		//18db
		l6 = (.5*ld)/(6.283*(f2-f1)); //l3
		c5 = 1/(1.4884*Math.pow(6.283*(f1-f2),2)*l6); //c2
		c6 = 1/(39.472*f1*f2*l6); // c3
		l5 = 1/(39.472*f1*f2*c5); //l2
		l4 = 1/(.499849*(Math.pow(6.283*(f2-f1),2))*c5); //l1
		c4 = 1/(39.472*f1*f2*l4); //c1					
		
		l1 *= 1000;
		c1 *= 1000;
		c2 *= 1000000;
		l3 *= 1000;
		c3 *= 1000000;
		l2 *= 1000;
		l6 *= 1000;
		c5 *= 1000000;
		c6 *= 1000000;
		l5 *= 1000;
		l4 *= 1000;
		c4 *= 1000000;
		
		return true;
	}

	/**
	 * Calculates the values for a zobel filters to the global crossover variables
	 * 
	 * @return true if successful, false if not
	 */
	public boolean calczobel() {

		double frequency, load;
		
		if (!freqzobel.tryParse() | !loadzobel.tryParse()) return false;
		frequency = freqzobel.parse();
		load = loadzobel.parse();
		
		c1 = 1000000 / (6.283 * load * frequency);
		r1 = 1.25 * load;							

		return true;

	}

	
	/**
	 * Truncates a double to 3 digits, simplified version to inprove readability
	 * 
	 * @param d - value to truncate
	 * @return - string of the truncated value
	 */
	private String trun (double d) {

		return Mathf.truncate(d, 3);
		
	}
}
