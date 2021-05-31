package com.AudioToolkit.Activities;

import java.util.Arrays;

import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkit.Objects.DatabaseHandler;
import com.AudioToolkitPro.R;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class speakersize extends TabActivity {

	Button modelSP, yearSP, bodySP, trimSP, makeSP;
	TableLayout speakersTL;
	Context mContext;
	TextView error;
	
	String make, model, year, body, trim;
	
	DatabaseHandler db;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.speakersize);

		mContext = this;

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("Car Database", R.id.speaker_database);
		
		tabBuilder.build();
	

		error = (TextView) findViewById(R.id.speaker_errorTV);
		
		makeSP = (Button) findViewById(R.id.speaker_makeSP);
		modelSP = (Button) findViewById(R.id.speaker_modelSP);
		yearSP = (Button) findViewById(R.id.speaker_yearSP);
		bodySP = (Button) findViewById(R.id.speaker_bodySP);
		trimSP = (Button) findViewById(R.id.speaker_trimSP);
		speakersTL = (TableLayout) findViewById(R.id.speaker_speakerTL);

		//Toast.makeText(this, "Opening Database....", Toast.LENGTH_LONG).show();

		//Add the prompts
		makeSP.setText("Select Make");
		modelSP.setText("Select Model");
		yearSP.setText("Select Year");
		bodySP.setText("Select Body");
		trimSP.setText("Select Trim");		

		clearMakeSP();
		clearModelSP();
		clearYearSP();
		clearBodySP();
		clearTrimSP();
		
		//Setup the database
		new AsyncTask<Void, Void, Void> () {

			@Override
			protected Void doInBackground(Void... params) {
				Log.d("DB Initialization","making DB accessible");
				db = new DatabaseHandler(mContext);
				db.getReadableDatabase();
				db.close();
				return null;
			}			

			@Override
			protected void onPostExecute(Void params) {
				setMakeSP(db.getMakes());
		     }			
			
		}.execute();
		
	}
	
	private void openDBerror() {

		//Toast.makeText(this, "Error in accessing database. Ensure that the external storage is not in use.", Toast.LENGTH_LONG).show();
		new AlertDialog.Builder(speakersize.this)  
		.setTitle("Error")  
		.setIcon(R.drawable.ic_error)  
		.setMessage("Database cannot be accessed.\n\nEnsure that the external storage is mounted to the device.")    
		.setPositiveButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				finish();
			}
		})  
		.show(); 
		
		clearMakeSP();
		clearModelSP();
		clearYearSP();
		clearBodySP();
		clearTrimSP();
		
	}

	public void setMakeSP(final String[] makes) {

	
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item, makes);
		Log.d("speakersize.onCreate(): ", "Makes (" + makes.length + "): " + Arrays.toString(makes));
		makeSP.setEnabled(true);
		
		//Setup the boxes other than makeSP		
		clearModelSP();
		clearYearSP();
		clearBodySP();
		clearTrimSP();

		makeSP.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				clearModelSP();
				clearYearSP();
				clearBodySP();
				clearTrimSP();

				new AlertDialog.Builder(mContext).setTitle("Select Make").setAdapter(adapter, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						make = makes[which];// parent.getItemAtPosition(which).toString();
						Log.d("makeSP listener","make: " + make);
						makeSP.setText(make);
						setModelSP(db.getModels(make));

						dialog.dismiss();
					}
				}).create().show();

			}});

	}

	private void setModelSP(final String[] models) {

		Log.d("setModelSP(): ", "Models (" + models.length + "): " + Arrays.toString(models));
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item, models);
		modelSP.setEnabled(true);

		modelSP.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				clearYearSP();
				clearBodySP();
				clearTrimSP();

				new AlertDialog.Builder(mContext).setTitle("Select Model").setAdapter(adapter, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						model = models[which];// parent.getItemAtPosition(which).toString();
						Log.d("modelSP listener"," model: " + model);
						modelSP.setText(model);
						setYearSP(db.getYear(make,model));

						dialog.dismiss();
					}
				}).create().show();
			}});
	}




	private void setYearSP(final String[] years) {

		Log.d("setYearSP(): ", "Year (" + years.length + "): " + Arrays.toString(years));
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item, years);
		yearSP.setEnabled(true);

		yearSP.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				clearBodySP();
				clearTrimSP();

				new AlertDialog.Builder(mContext).setTitle("Select Year").setAdapter(adapter, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						year = years[which];
						Log.d("yearSP listener"," year: " + year); 
						yearSP.setText(year);
						setBodySP(db.getBody(make,model,year));

						dialog.dismiss();
					}
				}).create().show();
			}});
	}

	private void setBodySP(final String[] bodys) {

		Log.d("setBodySP(): ", "Bodys (" + bodys.length + "): " + Arrays.toString(bodys));
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item, bodys);
		bodySP.setEnabled(true);

		bodySP.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				clearTrimSP();

				new AlertDialog.Builder(mContext).setTitle("Select Body").setAdapter(adapter, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						body = bodys[which];
						Log.d("bodySP listener"," body: " + body);
						bodySP.setText(body);
						setTrimSP(db.getTrim(make,model,year,body));

						dialog.dismiss();
					}
				}).create().show();
			}});
	}

	private void setTrimSP(final String[] trims) {

		Log.d("setTrimSP(): ", "Trims (" + trims.length + "): " + Arrays.toString(trims));
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item, trims);
		trimSP.setEnabled(true);

		trimSP.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				new AlertDialog.Builder(mContext).setTitle("Select Trim").setAdapter(adapter, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						trim = trims[which];
						Log.d("trimSP listener"," trim: " + trim);
						trimSP.setText(trim);
						getSpeakers(db.getSpeakers(make,model,year,body,trim));

						dialog.dismiss();
					}
				}).create().show();
			}});
	}

	protected void getSpeakers(String[] s) {
		//Log.d("getSpeakers","Getting speakers for: " + make + " - " + model + " - " + year + " - " + body + " - " + trim);

		try {
			//String[] s = db.getSpeakers(make,model,year,body,trim);
			String speakers = s[0];
			Log.d("getSpeakers(): ", "Speakers: " + Arrays.toString(s));

			String[] s_row = speakers.split(":");

			Log.d("getSpeakers()","Split by speaker: " + Arrays.toString(s_row));

			String[][] s_col = new String[s_row.length+1][3];

			String[] headers = {"Speaker","Size","Depth"}; 

			s_col[0] = headers;

			for (int i = 0 ; i < s_row.length; i++) {

				String[] split = s_row[i].split(",");
				String s_name = split[0];

				s_name = s_name.replaceAll("F1","Front");
				s_name = s_name.replaceAll("F2","Front");
				s_name = s_name.replaceAll("F3","Front");
				s_name = s_name.replaceAll("FT","Front Tweeter");
				s_name = s_name.replaceAll("R1","Rear");
				s_name = s_name.replaceAll("R2","Rear");
				s_name = s_name.replaceAll("R2","Rear");
				s_name = s_name.replaceAll("RT","Rear Tweeter");
				s_name = s_name.replaceAll("S1","Subwoofer");
				s_name = s_name.replaceAll("S2","Subwoofer");
				s_name = s_name.replaceAll("S2","Subwoofer");
				s_name = s_name.replaceAll("C1","Center");
				s_name = s_name.replaceAll("C2","Center");
				s_name = s_name.replaceAll("C3","Center");
				s_name = s_name.replaceAll("CT","Center Tweeter");

				split[0] = s_name;
				s_col[i+1] = split;

				Log.d("getSpeakers()","Split by column, speaker " + i + ": " + Arrays.toString(split));
			}

			speakersTL.removeAllViews();

			for (int i = 0; i < s_col.length; i++) {	//Add a row for each row in the data array

				TableRow tr = new TableRow(this);
				tr.setId(1000+i); 			

				for (int j = 0; j < s_col[i].length; j++) {	//Add a TV for each entry in the row
					TextView labelTV = new TextView(this);
					labelTV.setPadding(5, 0, j == 0 ? 20 : 5, 0);
					labelTV.setId(2000+j);
					labelTV.setText(s_col[i][j] + (j != 0 && i != 0? "\"" : ""));
					labelTV.setGravity(j == 0 ? Gravity.LEFT : Gravity.CENTER); //Center  sizes, left align name
					if (i == 0) { //Bold the headers and increase text size
						labelTV.setTypeface(null, Typeface.BOLD);
						labelTV.setTextSize(labelTV.getTextSize()+1);    
					}

					// System.out.println(labelTV.getText());
					tr.addView(labelTV);        
				}			

				speakersTL.addView(tr); 
			}



		} catch (ArrayIndexOutOfBoundsException E) {
			error.setVisibility(View.VISIBLE);
		}


	}
	
	private ArrayAdapter<String> getNullAdapter(String s) {
		String[] items = new String[] {s};
		ArrayAdapter<String> adapter_null = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter_null.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter_null;

	}
	
	public void clearMakeSP() {		

		makeSP.setText("Select Make");
		makeSP.setEnabled(false);
		error.setVisibility(View.GONE);
		speakersTL.removeAllViews();

	}

	public void clearModelSP() {		

		modelSP.setText("Select Model");
		modelSP.setEnabled(false);
		error.setVisibility(View.GONE);
		speakersTL.removeAllViews();

	}

	public void clearYearSP() {

		yearSP.setText("Select Year");
		yearSP.setEnabled(false);
		error.setVisibility(View.GONE);
		speakersTL.removeAllViews();

	}

	public void clearBodySP() {

		bodySP.setText("Select Body");
		bodySP.setEnabled(false);
		error.setVisibility(View.GONE);
		speakersTL.removeAllViews();
	}

	public void clearTrimSP() {

		trimSP.setText("Select Trim");	
		trimSP.setEnabled(false);
		error.setVisibility(View.GONE);
		speakersTL.removeAllViews();
	}

}