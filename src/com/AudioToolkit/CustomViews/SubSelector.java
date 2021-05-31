package com.AudioToolkit.CustomViews;

import java.util.Arrays;

import com.AudioToolkit.Objects.*;
import com.AudioToolkitPro.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SubSelector extends LinearLayout{

	Button subbuilder_db_subsel;
	Button subbuilder_db_subedit;
	Button subbuilder_db_subdel;


	Databox qts, qes, fs, pemax, sd, xmax, vas;		
	EditText name;
	Button dia_calc, dia_clear; 

	Subwoofer sub = null;

	DatabaseHandler db;

	Context mContext;

	boolean isFull = false;

	String buttonText = "Choose Subwoofer";

	public SubSelector(Context c) {
		super (c);

		mContext = c;		

		initialize();

		setup();

	}

	public SubSelector (Context c, AttributeSet attrs) {
		super(c, attrs); //attrs should be null

		mContext = c;

		initialize();

		TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.subselector);

		isFull = (a.getBoolean(R.styleable.subselector_subsel_full, false));

		if (!isFull) { //remove boxes if unneeded
			subbuilder_db_subedit.setVisibility(View.GONE);
			subbuilder_db_subdel.setVisibility(View.GONE);
		}

		a.recycle();

		setup();
	}

	private void setup() {	

		subbuilder_db_subsel.setText(buttonText);		

		subbuilder_db_subsel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				subbuilder_db_subsel.setTextColor(getResources().getColorStateList(R.drawable.btn_custom_text)); //in case is in error
				
				final String[] temp = db.getSubwooferNames();

				if (temp.length == 0 && !isFull) {
					String t = (subbuilder_db_subsel.getText() != buttonText) ?
							"Subwoofer no longer exists in database" :
								"No subwoofers in database";
					subbuilder_db_subsel.setText(buttonText);	
					Toast.makeText(mContext, t, 10).show();
					return;
				}

				final String[] subs = new String[temp.length+1];		

				if (temp.length > 0) System.arraycopy(temp, 0, subs, 1, temp.length);
				subs[0] = "Add new...";		

				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
						android.R.layout.simple_spinner_dropdown_item, isFull ? subs : temp); //Add 'add new' if flag is set

				Log.d("subbuilder.onCreate(): ", "Subwoofers (" + subs.length + "): " + Arrays.toString(subs));

				new AlertDialog.Builder(mContext).setTitle("Choose Subwoofer").setAdapter(adapter, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						String subname = isFull ? subs[which] : temp[which];// parent.getItemAtPosition(which).toString();

						if (which == (isFull ? 0 : -1)) {
							editSubDialog(null);
							subbuilder_db_subsel.setText(buttonText);							
							subbuilder_db_subedit.setEnabled(false);
							subbuilder_db_subdel.setEnabled(false);
							sub = null;
							return;
						}

						sub = db.getSubwoofer(subname);

						Log.d("subs listener","sub: " + sub.getName() + " (" + sub.getId() + ")");
						subbuilder_db_subsel.setText(sub.getName());

						subbuilder_db_subedit.setEnabled(true);
						subbuilder_db_subdel.setEnabled(true);

						dialog.dismiss();
					}
				}).create().show();
			}});

		subbuilder_db_subedit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				editSubDialog(sub);					

			}});

		subbuilder_db_subdel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				AlertDialog.Builder ab = new AlertDialog.Builder(mContext);

				// set title
				ab.setTitle("Are you sure?");

				// set dialog message
				ab
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						Toast.makeText(mContext, sub.getName() + " deleted from database", 10).show();
						db.deleteSubwoofer(sub);
						subbuilder_db_subsel.setText(buttonText);							
						subbuilder_db_subedit.setEnabled(false);
						subbuilder_db_subdel.setEnabled(false);
						sub = null;
					}
				})
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});

				// show it
				ab.show();
			}});

		subbuilder_db_subedit.setEnabled(false);
		subbuilder_db_subdel.setEnabled(false);

	}

	private void initialize() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.subselector, this, true);

		subbuilder_db_subsel = (Button) findViewById(R.id.subbuilder_db_subsel);
		subbuilder_db_subedit = (Button) findViewById(R.id.subbuilder_db_subedit);
		subbuilder_db_subdel = (Button) findViewById(R.id.subbuilder_db_subdel);

		db = new DatabaseHandler(mContext);
	}

	public boolean hasSubwoofer() {

		if (sub == null)  {
			Log.d("hasSubwoofer()", "No subwoofer selected");
			subbuilder_db_subsel.setTextColor(getResources().getColorStateList(R.drawable.btn_custom_errortext));
			return false;
		}

		if (db.getSubwoofer(sub.getId()) == null) {
			Toast.makeText(mContext, "Subwoofer no longer exists in database", 10).show();
			Log.d("hasSubwoofer()", "Subwoofer has been deleted in another selector");
			clear();
			return false;
		}

		return true;
	}

	public Subwoofer getSubwoofer() {
		return sub;  
	}

	private void editSubDialog (final Subwoofer s) {

		final Dialog dialog = new Dialog(mContext, R.style.theTheme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dia_subwoofer);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialog.show();

		dia_calc = (Button) dialog.findViewById(R.id.subdia_calc);
		dia_clear = (Button) dialog.findViewById(R.id.subdia_clear);

		vas = (Databox) dialog.findViewById(R.id.vas);
		qts = (Databox) dialog.findViewById(R.id.qts);
		qes = (Databox) dialog.findViewById(R.id.qes);
		fs = (Databox) dialog.findViewById(R.id.fs);
		pemax = (Databox) dialog.findViewById(R.id.pemax);
		sd = (Databox) dialog.findViewById(R.id.sd);
		xmax = (Databox) dialog.findViewById(R.id.xmax);
		name = (EditText) dialog.findViewById(R.id.name);

		if (s != null) {

			name.setText(s.getName());
			vas.setValue(s.getVas(),2);
			qts.setValue(s.getQts(),3);
			qes.setValue(s.getQes(),3);
			fs.setValue(s.getVas(),1);
			vas.setValue(s.getVas(),2);
			pemax.setValue(s.getPemax(), 1);
			xmax.setValue(s.getXmax(),1);
			sd.setValue(s.getSd(),2);

		}		

		dia_clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				Log.d("editsub","dialog cancelled");
				dialog.cancel();

			}});

		dia_calc.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {	

				if (!vas.tryParse() | !qes.tryParse() |
						!qts.tryParse() | !fs.tryParse() |
						!sd.tryParse() | !xmax.tryParse() | 
						!pemax.tryParse() | name.length() == 0) 
					return;

				if (s == null && db.checkDupSubName(name.getText().toString())) {
					Toast.makeText(mContext, "Name already exists in database", 10).show();
					return;
				} 

				sub = new Subwoofer(
						(s != null ? s.getId() : -1),
						name.getText().toString(),
						qes.parse(),
						qts.parse(),
						vas.parse(),
						fs.parse(),
						pemax.parse(),
						xmax.parse(),
						sd.parse());

				db.addSubwoofer(sub);

				Toast.makeText(mContext, (s == null) ? "Subwoofer added to database." : "Subwoofer updated.", 10).show();

				subbuilder_db_subsel.setText(sub.getName());
				subbuilder_db_subedit.setEnabled(true);
				subbuilder_db_subdel.setEnabled(true);

				dialog.cancel();
			}});

	}
	
	public void clear() {

		subbuilder_db_subsel.setTextColor(getResources().getColorStateList(R.drawable.btn_custom_text)); //in case is in error
		subbuilder_db_subsel.setText(buttonText);
		subbuilder_db_subedit.setEnabled(false);
		subbuilder_db_subdel.setEnabled(false);
		sub = null;
	}

}
