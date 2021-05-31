package com.AudioToolkit.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AudioToolkit.Utility.Mathf;
import com.AudioToolkitPro.R;

public class Databox extends LinearLayout {

	//TODO: Long press on convert button to bring up list
	//TODO: Get conversions by string only, values stored, throws error for wrong units
	//TODO: remove white text box in landscape : http://stackoverflow.com/questions/4336762/disabling-the-fullscreen-editing-view-for-soft-keyboard-input-in-landscape
	Button convert;
	TextView tvAttr, tvUnits, tvSubtext;
	EditText etBox;
	LinearLayout etBoxHolder; 
	int max, min, currentUnit = 0;
	double[] unitconv = { 1 };
	String[] unitname;
	boolean edittable = true;
	int largeBoxSize = 200;
	int smallBoxSize = 85;

	int maxWidth = 700;

	// Create a basic databox for dialog boxes, no functionality for the
	// conversion yet
	public Databox(Context context, String attr, String subtext, String unitname) {
		super(context); // attrs should be null

		initialize();

		setAttr(attr);
		setSubtext(subtext);
		setUnits(unitname);

		convert.setVisibility(View.GONE);
	}

	public Databox(Context context, AttributeSet attrs) {
		super(context, attrs);

		initialize();

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.databox);

		// Set attr and units to the defaults in the xml
		setAttr(a.getString(R.styleable.databox_attr));

		// Check to see if there is a subtext, remove if not
		String subtext = a.getString(R.styleable.databox_subtext);
		tvSubtext.setText(subtext == null ? "" : subtext);

		// Check to see if the edittext should be edittable
		if (!(a.getBoolean(R.styleable.databox_edittable, true))) {
			etBox.setKeyListener(null);
			etBox.setFocusable(false);
			etBox.setClickable(false);
			edittable = false;
		}

		// Check to see if the edittext should allow negative numbers
		if ((a.getBoolean(R.styleable.databox_allownegative, false))) {
			etBox.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
		}

		// Check if the textbox should be large
		if ((a.getBoolean(R.styleable.databox_largebox, false))) {
			ViewGroup.LayoutParams params = etBox.getLayoutParams();
			params.width = largeBoxSize;
			etBox.setLayoutParams(params);
		}

		min = (a.getInteger(R.styleable.databox_min, Integer.MIN_VALUE));
		max = (a.getInteger(R.styleable.databox_max, Integer.MAX_VALUE));

		// Get the names for the conversions
		if (a.getString(R.styleable.databox_units) != null) {
			unitname = (a.getString(R.styleable.databox_units)).split(",");
			setUnits(unitname[0]);
		}

		// Get the units for the conversion
		if (a.getString(R.styleable.databox_unitconv) != null) {
			String[] unitconvs = (a.getString(R.styleable.databox_unitconv))
					.split(",");
			unitconv = new double[unitconvs.length];
			for (int i = 0; i < unitconvs.length; i++) {
				unitconv[i] = Double.parseDouble(unitconvs[i]);
			}
		}




		// Add in the click action to change the units


		View.OnClickListener clicker = new View.OnClickListener() {
			public void onClick(View arg0) {
				if (unitname.length > 1)
					changeUnits();
			}
		};

		convert.setOnClickListener(clicker);

		// Check to see if the attr and subtext should be removed, adds clicker
		// to the etbox instead
		boolean boxOnly = (a.getBoolean(R.styleable.databox_boxonly, false));		



		if (boxOnly) {

			convert.setVisibility(View.GONE);
			tvAttr.setVisibility(View.GONE);
			tvSubtext.setVisibility(View.GONE);
			etBox.setOnClickListener(clicker);
			etBox.setLayoutParams(new LayoutParams(smallBoxSize,LayoutParams.WRAP_CONTENT));
			etBox.setGravity(Gravity.CENTER);



			RelativeLayout.LayoutParams params = 
					new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			etBoxHolder.setLayoutParams(params);



		}		

		//If it can convert and it's not a box only
		if (!(unitname.length > 1)) convert.setVisibility(View.GONE);
		else if (!boxOnly){

			int bottom = etBox.getPaddingBottom();
			int top = etBox.getPaddingTop();
			int right = etBox.getPaddingRight();
			int left = etBox.getPaddingLeft();
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) etBox
					.getLayoutParams();
			params.width -= 22;
			etBox.setLayoutParams(params);		    
			etBox.setBackgroundResource(R.drawable.edittext_convert_drawable);
			etBox.setPadding(left, top, right, bottom);

		}



		a.recycle();
	}

	private void initialize() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater) getContext().getSystemService(infService);
		li.inflate(R.layout.databox, this, true);

		tvAttr = (TextView) findViewById(R.id.tvAttr); 
		tvUnits = (TextView) findViewById(R.id.tvUnits);
		tvSubtext = (TextView) findViewById(R.id.tvSubtext);
		etBox = (EditText) findViewById(R.id.etBox);
		etBoxHolder = (LinearLayout) findViewById(R.id.etBoxHolder);
		convert = (Button) findViewById(R.id.convert); 

		Log.d("text color","text color is " + tvAttr.getCurrentTextColor());
	}

	public void setAttr(String title) {
		tvAttr.setText(title);
	}

	public void setSubtext(String text) {
		tvSubtext.setText(text);
	}

	public void setUnits(String unit) {
		tvUnits.setText(unit);
	}

	public String getUnit() {
		return unitname[currentUnit];
	}

	public void setText(String s) {

		if (!edittable)
			etBox.setText(s);

	}

	public double getConv() {
		return unitconv[currentUnit];
	}

	public void setValue(double d, int decimals) {

		d *= unitconv[currentUnit];

		etBox.setText(Mathf.truncate(d, decimals));
	}

	public void changeUnits() {

		if (!tryParse(false)) {
			currentUnit = (++currentUnit) % unitconv.length; // Increments the
			// currentUnit,
			// restricted to
			// num in
			// unitcon
			setUnits(unitname[currentUnit]);
			return; // check to see if the box is filled
		}

		double val = parse();

		currentUnit = (++currentUnit) % unitconv.length; // Increments the
		// currentUnit,
		// restricted to num
		// in unitcon
		setUnits(unitname[currentUnit]);

		setValue(val, 2);

	}

	public boolean tryParse() {

		if (Mathf.testStr2Dbl(etBox.getText().toString()))
			return true;

		// show error icon
		etBox.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.error, 0);

		etBox.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				etBox.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

				etBox.removeTextChangedListener(this);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		return false;

	}

	// Same check, but won't trigger the error
	public boolean tryParse(boolean nothing) {

		if (Mathf.testStr2Dbl(etBox.getText().toString()))
			return true;

		return false;
	}

	public void setError() {

		// show error icon
		etBox.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.error_input, 0);

		etBox.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				etBox.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

				etBox.removeTextChangedListener(this);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});
	}

	public double parse() {

		double val;

		val = Mathf.str2dbl(etBox.getText().toString());

		val /= unitconv[currentUnit]; // convert into the designated base unit

		return val;
	}

	public void setEnabled(boolean isEnabled) {

		etBox.setEnabled(isEnabled);

	}

	public void clamp() {

		if (parse() < min)
			setValue((double) min, 1);
		if (parse() > max)
			setValue((double) max, 1);

	}

	public void allowNegative() {
		etBox.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL
				| InputType.TYPE_NUMBER_FLAG_SIGNED);
	}

	public void setAsFree(final Context c, double d) {

		etBox.setKeyListener(null);
		etBox.setFocusable(false);
		etBox.setClickable(false);

		setValue(d, 1);

		etBox.setBackgroundResource(R.drawable.textfield_free_holo_dark);

		etBox.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Toast.makeText(
						c,
						(CharSequence) getResources().getString(
								R.string.freeAppMessage), 10).show();
			}
		});
	}

	public void clear() { 
		etBox.getText().clear();;
	}

}
