package com.AudioToolkit.CustomViews;

import java.util.Arrays;
import com.AudioToolkit.Objects.*;
import com.AudioToolkitPro.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EncSelector extends LinearLayout{

	//String Text = mySpinner.getSelectedItem().toString();

	Context mContext;

	double minDimension = 5;

	LinearLayout specs, port_specs, rect, wedge, cyl;
	RelativeLayout bottom;
	Arrowbox rect_length, rect_height, rect_width, wedge_height, wedge_top, wedge_width, wedge_bottom, cyl_radius, cyl_length;
	Databox thickness, volume, port_freq, port_area, port_num, port_length;
	EditText enc_name;
	Button calculate, clear, diagram, type, cancel, save;
	RadioButton sealed, ported;

	boolean adjusting = false;
	boolean isFull = false;

	Button encbuilder_db_encsel;
	Button encbuilder_db_encedit;
	Button encbuilder_db_encdel;

	String buttonText = "Choose Enclosure";

	DatabaseHandler db;

	Enclosure enclosure = null;

	public EncSelector(Context c) {
		super (c);

		mContext = c;		

		initialize();

		setup();

	}



	public EncSelector (Context c, AttributeSet attrs) {
		super(c, attrs); //attrs should be null

		mContext = c;

		initialize();

		TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.encselector);

		isFull = (a.getBoolean(R.styleable.encselector_encsel_full, false));

		a.recycle();

		if (!isFull) { //remove boxes if unneeded
			encbuilder_db_encedit.setVisibility(View.GONE);
			encbuilder_db_encdel.setVisibility(View.GONE);
		}

		setup();


	}

	private void setup() {

		encbuilder_db_encsel.setText(buttonText);		

		encbuilder_db_encsel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				encbuilder_db_encsel.setTextColor(getResources().getColorStateList(R.drawable.btn_custom_text)); //in case is in error
				
				final String[] temp = db.getEnclosureNames();

				if (temp.length == 0 && !isFull) {
					String t = (encbuilder_db_encsel.getText() != buttonText) ?
							"Enclosure no longer exists in database" :
								"No enclosures in database";
					encbuilder_db_encsel.setText(buttonText);	
					Toast.makeText(mContext, t, 10).show();
					return;
				}

				final String[] enclosures = new String[temp.length+1];		

				if (temp.length > 0) System.arraycopy(temp, 0, enclosures, 1, temp.length);
				enclosures[0] = "Add new...";		

				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
						android.R.layout.simple_spinner_dropdown_item, isFull ? enclosures : temp); //Add 'add new' if flag is set

				Log.d("encbuilder.onCreate(): ", "Enclosures (" + enclosures.length + "): " + Arrays.toString(enclosures));

				new AlertDialog.Builder(mContext).setTitle("Choose Enclosure").setAdapter(adapter, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						String encname = isFull ? enclosures[which] : temp[which];// parent.getItemAtPosition(which).toString();

						if (which == (isFull ? 0 : -1)) {
							editEnclosureDialog(null);
							encbuilder_db_encsel.setText(buttonText);							
							encbuilder_db_encedit.setEnabled(false);
							encbuilder_db_encdel.setEnabled(false);
							enclosure = null;
							return;
						}

						enclosure = db.getEnclosure(encname);

						Log.d("subs listener","sub: " + enclosure.getName() + " (" + enclosure.getId() + ")");
						encbuilder_db_encsel.setText(enclosure.getName());

						encbuilder_db_encedit.setEnabled(true);
						encbuilder_db_encdel.setEnabled(true);

						dialog.dismiss();
					}
				}).create().show();
			}});

		encbuilder_db_encedit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				editEnclosureDialog(enclosure);					

			}});

		encbuilder_db_encedit.setEnabled(false);

		encbuilder_db_encdel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				AlertDialog.Builder ab = new AlertDialog.Builder(mContext);

				// set title
				ab.setTitle("Are you sure?");

				// set dialog message
				ab
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {

						Toast.makeText(mContext, enclosure.getName() + " deleted from database", 10).show();
						db.deleteEnclosure(enclosure);
						encbuilder_db_encsel.setText(buttonText);							
						encbuilder_db_encedit.setEnabled(false);
						encbuilder_db_encdel.setEnabled(false);
						enclosure = null;
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

		encbuilder_db_encdel.setEnabled(false);

	}

	private void initialize() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.encselector, this, true);

		encbuilder_db_encsel = (Button) findViewById(R.id.encbuilder_db_encsel);
		encbuilder_db_encedit = (Button) findViewById(R.id.encbuilder_db_encedit);
		encbuilder_db_encdel = (Button) findViewById(R.id.encbuilder_db_encdel);

		db = new DatabaseHandler(mContext);
	}

	public boolean hasEnclosure() {

		if (enclosure == null)  {
			Log.d("hasEnclosure()", "No enclosure selected");
			encbuilder_db_encsel.setTextColor(getResources().getColorStateList(R.drawable.btn_custom_errortext));
			return false;
		}

		if (db.getEnclosure(enclosure.getId()) == null) {
			Toast.makeText(mContext, "Enclosure no longer exists in database", 10).show();
			Log.d("hasEnclosure()", "Enclosure has been deleted in another selector");
			clear();
			return false;
		}

		return true;
	}

	public Enclosure getEnclosure() {
		return enclosure;
	}

	private void editEnclosureDialog(final Enclosure e) {
/*
		final Dialog dialog = new Dialog(mContext, R.style.theTheme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.enclosuredialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
*/
		final CustomDialog enclosureDialog = new CustomDialog(mContext,R.layout.dia_enclosure); 	
		
		Dialog dialog = enclosureDialog.getDialog();

		//------------------------------------------------------------------------------------------ Rectangular Enclosure

		enc_name = (EditText) dialog.findViewById(R.id.enc_name);

		type = (Button) dialog.findViewById(R.id.type);

		sealed = (RadioButton) dialog.findViewById(R.id.sealed);
		ported = (RadioButton) dialog.findViewById(R.id.ported);

		thickness = (Databox) dialog.findViewById(R.id.thickness);
		volume = (Databox) dialog.findViewById(R.id.volume);
		volume.changeUnits(); //To put default in as cubic feet while keeping base as inches

		bottom = (RelativeLayout) dialog.findViewById(R.id.bottom);
		bottom.setVisibility(View.GONE);

		port_length = (Databox) dialog.findViewById(R.id.port_length);
		port_length.setVisibility(View.GONE);
		port_num = (Databox) dialog.findViewById(R.id.port_num);
		port_area = (Databox) dialog.findViewById(R.id.port_area);
		port_freq = (Databox) dialog.findViewById(R.id.port_freq);

		save = (Button) dialog.findViewById(R.id.save);
		clear = (Button) dialog.findViewById(R.id.clear);
		calculate = (Button) dialog.findViewById(R.id.calculate);
		cancel = (Button) dialog.findViewById(R.id.cancel);
		diagram = (Button) dialog.findViewById(R.id.diagram);
		diagram.setEnabled(false);

		specs = (LinearLayout) dialog.findViewById(R.id.specs);
		port_specs = (LinearLayout) dialog.findViewById(R.id.port_specs);
		port_specs.setVisibility(View.GONE);

		rect = (LinearLayout) dialog.findViewById(R.id.rect);
		wedge = (LinearLayout) dialog.findViewById(R.id.wedge);
		cyl = (LinearLayout) dialog.findViewById(R.id.cyl);
		rect.setVisibility(View.GONE);
		wedge.setVisibility(View.GONE);
		cyl.setVisibility(View.GONE);

		rect_length = (Arrowbox)dialog.findViewById(R.id.rect_length);
		rect_width = (Arrowbox)dialog.findViewById(R.id.rect_width);
		rect_height = (Arrowbox)dialog.findViewById(R.id.rect_height);

		wedge_height = (Arrowbox)dialog.findViewById(R.id.wedge_height);
		wedge_width = (Arrowbox)dialog.findViewById(R.id.wedge_width);
		wedge_top = (Arrowbox)dialog.findViewById(R.id.wedge_top);
		wedge_bottom = (Arrowbox)dialog.findViewById(R.id.wedge_bottom);

		cyl_length = (Arrowbox)dialog.findViewById(R.id.cyl_length);
		cyl_radius = (Arrowbox)dialog.findViewById(R.id.cyl_radius);

		if (e != null) {

			enc_name.setText(e.getName());
			thickness.setValue(e.getThickness(),2);
			volume.setValue(e.getVb(),2);

			if (e.getType() == 0) { //sealed
				sealed.setChecked(true);			
			} else {
				ported.setChecked(true);
				port_freq.setValue(e.getFb(),2);
				port_area.setValue(e.getPortarea(),2);
				port_num.setValue(e.getNumports(),0);
				port_length.setValue(e.getPortlength(),2);				
				port_specs.setVisibility(View.VISIBLE);					
				port_length.setVisibility(View.VISIBLE);								
			}

			diagram.setEnabled(true);

			switch (e.getShape()) {

			case 0:
				type.setText("Rectangular");
				
				String[] sr = e.getSize().split("x");

				Log.d("e.getSize()",sr[0] + " " + sr[1] + " "+sr[2]);				
								
				rect_length.setValue(sr[0]);
				rect_width.setValue(sr[1]);
				rect_height.setValue(sr[2]);
				rect.setVisibility(View.VISIBLE);

				break;
			case 1:
				type.setText("Wedge");

				String[] sw = e.getSize().split("x");
				
				wedge_top.setValue(sw[0]);			
				wedge_bottom.setValue(sw[1]);
				wedge_width.setValue(sw[2]);
				wedge_height.setValue(sw[3]);
				wedge.setVisibility(View.VISIBLE);

				break;
			case 2:
				type.setText("Cylinder");
				String[] sc = e.getSize().split("x");
				
				cyl_length.setValue(sc[0]);
				cyl_radius.setValue(sc[1]);
				cyl.setVisibility(View.VISIBLE);
				break;
			default:
			}
			bottom.setVisibility(View.VISIBLE);
			
		}

		type.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item, new String[] {"Rectangular", "Wedge", "Cylinder"});

				new AlertDialog.Builder(mContext).setTitle("Enclosure Type").setAdapter(adapter, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						switch (which) {

						case 0:
							type.setText("Rectangular");
							break;
						case 1:
							type.setText("Wedge");
							break;
						case 2:
							type.setText("Cylinder");

						}

						clear(false);

						diagram.setEnabled(true);
						dialog.dismiss();
					}
				}).create().show();

			}});

		diagram.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				String e = type.getText().toString();

				if (e == "Rectangular") 

					new CustomDialog(mContext,R.drawable.sch_enc_rect, "Enclosure - Rectangular","");					
				else if (e == "Wedge") 
					new CustomDialog(mContext,R.drawable.sch_enc_wedge, "Enclosure - Wedge","");					
				else if (e == "Cylinder") 
					new CustomDialog(mContext,R.drawable.sch_enc_cylinder, "Enclosure - Cylinder","");			

			}});

		calculate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				//Check if all the boxes are filled
				if(sealed.isChecked()) {
					if(!volume.tryParse() | !thickness.tryParse() | enc_name.length() == 0) return;
				} else {	
					if(!volume.tryParse() | !thickness.tryParse() | !port_freq.tryParse() |
							!port_area.tryParse() | !port_num.tryParse() | enc_name.length() == 0) return;					
				}

				if (ported.isChecked()) {

					double portfreq = port_freq.parse();
					double numports = port_num.parse();
					double portarea = port_area.parse();		

					double portdia = 2*Math.sqrt((portarea)/Math.PI)*2.54;
					double plength = (23562.5*portdia*portdia*numports)/(portfreq*portfreq*volume.parse()*0.016387)-(1*portdia);

					System.out.println("Initial vol: " + volume);
					//volume += (plength/2.54)*portarea*numports;

					System.out.println("Initial vol + port: " + volume);

					port_length.setValue(plength/2.54,1);

					port_length.setVisibility(View.VISIBLE);
				}

				//Show the proper box
				String enc_type = type.getText().toString();

				if (enc_type == "Rectangular") 
					calculateRect();					
				else if (enc_type == "Wedge") 
					calculateWedge();				
				else if (enc_type == "Cylinder") 
					calculateCyl();			

				if (ported.isChecked()) port_length.setVisibility(View.VISIBLE);
				bottom.setVisibility(View.VISIBLE);

			}});

		cancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				enclosureDialog.cancel();
			}});

		save.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if (e == null &&db.checkDupEncName(enc_name.getText().toString())) {
					Toast.makeText(mContext, "Name already exists in database", 10).show();
					return;
				} 

				int id = (e != null ? e.getId() : -1);
				String name = enc_name.getText()+"";
				int enc = sealed.isChecked() ? 0 : 1;
				double Vb = volume.parse();
				double thick = thickness.parse();
				double Fb = port_freq.parse();
				double portarea = port_area.parse();
				int numports = (int) port_num.parse();
				double portlength = port_length.parse();
				double portratio = 0;


				String enc_type = type.getText().toString();

				int shape = 0;
				String size = "";

				if (enc_type == "Rectangular") { 
					shape = 0;
					size += rect_length.getValue() + "x";
					size += rect_width.getValue() + "x";
					size += rect_height.getValue();
				}
				else if (enc_type == "Wedge") {
					shape = 1;
					size += wedge_top.getValue() + "x";
					size += wedge_bottom.getValue() + "x";
					size += wedge_width.getValue() + "x";
					size += wedge_height.getValue();
				}
				else if (enc_type == "Cylinder") {
					shape = 2;
					size += cyl_length.getValue() + "x";
					size += cyl_radius.getValue();
				}

				Enclosure enclosure = new Enclosure(id, name, enc, shape, Vb, thick, size, Fb, portarea, numports, portlength, portratio);

				db.addEnclosure(enclosure);

				Toast.makeText(mContext, (e == null) ? "Enclosure added to database." : "Enclosure updated.", 10).show();					

				encbuilder_db_encsel.setText(enclosure.getName());
				encbuilder_db_encedit.setEnabled(true);
				encbuilder_db_encdel.setEnabled(true);
				
				
				enclosureDialog.cancel();
			}});


		clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				clear(true);
			}}); 

		sealed.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				port_specs.setVisibility(View.GONE);	
				port_length.setVisibility(View.GONE);
				bottom.setVisibility(View.GONE);
				rect.setVisibility(View.GONE);
				wedge.setVisibility(View.GONE);
				cyl.setVisibility(View.GONE);
				ported.setEnabled(true);
				sealed.setEnabled(false);
			}});

		sealed.setEnabled(false);

		ported.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				port_specs.setVisibility(View.VISIBLE);	
				rect.setVisibility(View.GONE);
				wedge.setVisibility(View.GONE);
				bottom.setVisibility(View.GONE);
				cyl.setVisibility(View.GONE);
				port_length.setVisibility(View.GONE);
				sealed.setEnabled(true);
				ported.setEnabled(false);
			}});

		rect_length.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{rect_width, rect_height}))
					equalizeRect(increment, false, true, true);				
				else					
					rect_length.revertValue();	

			}
		});

		rect_width.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{rect_length, rect_height}))
					equalizeRect(increment, true, false, true);				
				else					
					rect_width.revertValue();

			}
		});

		rect_height.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{ rect_length, rect_width}))
					equalizeRect(increment, true, true, false);				
				else					
					rect_height.revertValue();

			}
		});

		wedge_bottom.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{wedge_height, wedge_top, wedge_width}))
					equalizeWedge(increment, false, true, true, true);		
				else					
					wedge_bottom.revertValue();


			}
		});

		wedge_height.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{wedge_bottom, wedge_top, wedge_width}))
					equalizeWedge(increment, true, false, true, true);		
				else					
					wedge_height.revertValue();

			}
		});

		wedge_top.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{wedge_bottom, wedge_height, wedge_width}))
					equalizeWedge(increment, true, true, false, true);		
				else					
					wedge_top.revertValue();

			}
		});

		wedge_width.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{wedge_bottom, wedge_top, wedge_height}))
					equalizeWedge(increment, true, true, true, false);		
				else					
					wedge_width.revertValue();

			}
		});

		cyl_length.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{cyl_radius}))
					equalizeCyl(increment, false, true);	
				else					
					cyl_length.revertValue();

			}
		});

		cyl_radius.setValueChangeListener(new Arrowbox.OnValueChangeListener() {
			public void onVariableChanged(double value, double increment) {

				if(canChangeArrowbox(increment, new Arrowbox[]{cyl_length}))
					equalizeCyl(increment, true, false);	
				else					
					cyl_radius.revertValue();

			}
		});

	}

	private boolean canChangeArrowbox(double increment, Arrowbox[] otherBoxes) {

		boolean canChange = false;

		for(Arrowbox otherBox : otherBoxes) {
			canChange |= otherBox.canChange(increment);
			Log.d("canChangeArrowbox","canChange: " + canChange);			
		}

		return canChange;
	}

	private void calculateRect() {

		double x = Math.cbrt(ported.isChecked() ? 
				volume.parse() + port_length.parse()*port_area.parse()*port_num.parse() :
					volume.parse());

		double length = x;	//make golden ratio
		double height = x*0.61803398;
		double width = x*1.61803399;
		double thick = thickness.parse() * 2;

		rect_length.setValue(length + thick);
		rect_width.setValue(width + thick);
		rect_height.setValue(height + thick);	

		rect_length.setMin(minDimension + thick);
		rect_width.setMin(minDimension + thick);
		rect_height.setMin(minDimension + thick);

		Log.d("calculate rect","v: " + length*width*height + " l: " + length + " w: " + width + " h: " + height);	

		rect.setVisibility(View.VISIBLE);

	}

	private void clear(boolean clearAll) {

		if (clearAll) {
			type.setText("Enclosure Type...");
			enc_name.setText("");
			thickness.clear();
			volume.clear();
			port_num.clear();
			port_freq.clear();
			port_area.clear();
			diagram.setEnabled(false);
		}

		rect.setVisibility(View.GONE);
		wedge.setVisibility(View.GONE);
		cyl.setVisibility(View.GONE);
		port_length.setVisibility(View.GONE);
		bottom.setVisibility(View.GONE);

	}




	private void calculateWedge() {

		double x = Math.cbrt(ported.isChecked() ? 
				volume.parse() + port_length.parse()*port_area.parse()*port_num.parse() :
					volume.parse());

		double bottom = x;	//make golden ratio
		double height = x*0.61803398;
		double width = x*1.61803399;
		double thick = thickness.parse() * 2;

		double wedgemod = bottom*.2;
		double top = bottom-wedgemod;
		bottom += wedgemod;

		wedge_bottom.setValue(bottom + thick);
		wedge_top.setValue(top + thick);
		wedge_height.setValue(height + thick);	
		wedge_width.setValue(width + thick);	

		wedge_bottom.setMin(minDimension + thick);
		wedge_top.setMin(minDimension + thick);
		wedge_height.setMin(minDimension + thick);
		wedge_width.setMin(minDimension + thick);

		wedge.setVisibility(View.VISIBLE);

	}

	private void calculateCyl() {

		double vol = Math.cbrt(ported.isChecked() ? 
				volume.parse() + port_length.parse()*port_area.parse()*port_num.parse() :
					volume.parse());

		double length = Math.sqrt(vol)/2.5;
		double radius = Math.sqrt(vol/(length*Math.PI)); 
		double thick = thickness.parse() * 2;

		cyl_length.setValue(length +  thick);
		cyl_radius.setValue(radius +  thick);	

		cyl_length.setMin(minDimension + thick);
		cyl_radius.setMin(minDimension + thick);

		cyl.setVisibility(View.VISIBLE);
	}

	private void equalizeRect(double increment, boolean length, boolean width, boolean height) {

		length &= rect_length.canChange(increment);
		width &= rect_width.canChange(increment);
		height &= rect_height.canChange(increment);
		
		adjusting = true;

		double thickness = (this.thickness.parse()*2);
		double l = rect_length.getValue() - thickness;
		double w = rect_width.getValue() - thickness;
		double h = rect_height.getValue() - thickness;

		double v = ported.isChecked() ? 
				this.volume.parse() + port_length.parse()*port_area.parse()*port_num.parse() :
					this.volume.parse();		

				double inc = Math.abs(increment / 100);

				while (v > l*w*h) {
					if (length)	l += inc;
					if (width) w += inc;
					if (height) h += inc;	
					Log.d("equalize rect","v: " + v + " > l: " + l + " w: " + w + " h: " + h + " = " + l*w*h);					
				}

				while (v < l*w*h) {
					if (length)	l -= inc;
					if (width) w -= inc;
					if (height) h -= inc;	
					Log.d("equalize rect","v: " + v + " < l: " + l + " w: " + w + " h: " + h + " = " + l*w*h);							
				}					


				rect_width.setValue(w + thickness);
				rect_length.setValue(l + thickness);
				rect_height.setValue(h + thickness);

				adjusting = false;

	}

	private void equalizeWedge(double increment,  boolean bottom, boolean height, boolean top, boolean width) {

		bottom &= wedge_bottom.canChange(increment);
		top &= wedge_top.canChange(increment);
		height &= wedge_height.canChange(increment);
		width &= wedge_width.canChange(increment);

		adjusting = true;

		double thickness = (this.thickness.parse()*2);
		double h = wedge_height.getValue() - thickness;
		double w = wedge_width.getValue() - thickness;
		double b = wedge_bottom.getValue() - thickness;
		double t = wedge_top.getValue() - thickness;

		double v = ported.isChecked() ? 
				this.volume.parse() + port_length.parse()*port_area.parse()*port_num.parse() :
					this.volume.parse();		

				double inc = Math.abs(increment / 100);


				if (v > (w*h*(b+t)/2)) {
					while (v > (w*h*(b+t)/2)) {
						if (top) t += inc;
						if (width) w += inc;
						if (height) h += inc;
						if (bottom) b += inc;					
					}				
				} else {
					while (v < (w*h*(b+t)/2)) {
						if (top) t -= inc;
						if (width) w -= inc;
						if (height) h -= inc;
						if (bottom) b -= inc;					
					}
				}

				wedge_top.setValue(t + thickness);		
				wedge_width.setValue(w + thickness);
				wedge_bottom.setValue(b + thickness);	
				wedge_height.setValue(h + thickness);

				adjusting = false;

	}

	private void equalizeCyl(double increment, boolean length, boolean radius) {

		length &= cyl_length.canChange(increment);
		radius &= cyl_radius.canChange(increment);

		adjusting = true;

		double thickness = (this.thickness.parse()*2);
		double l = cyl_length.getValue() - thickness;
		double r = cyl_radius.getValue() - thickness;

		double v = ported.isChecked() 
				? this.volume.parse() + port_length.parse()*port_area.parse()*port_num.parse()
						: this.volume.parse();		

				double inc = Math.abs(increment / 100);


				if (v > l*Math.PI*r*r) {
					while (v > l*Math.PI*r*r) {
						if (length) l += inc;
						if (radius) r += inc;
					}
				} else {
					while (v < l*Math.PI*r*r) {
						if (length) l -= inc;
						if (radius) r -= inc;
					}
				}	

				adjusting = false;
				
				cyl_length.setValue(l + thickness);		
				cyl_radius.setValue(r + thickness);
				
	}
	
	public void clear() {

		encbuilder_db_encsel.setTextColor(getResources().getColorStateList(R.drawable.btn_custom_text)); //in case is in error
		encbuilder_db_encsel.setText(buttonText);
		encbuilder_db_encedit.setEnabled(false);
		encbuilder_db_encdel.setEnabled(false);
		enclosure = null;
	}
	
}
