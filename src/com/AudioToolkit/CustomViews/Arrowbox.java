package com.AudioToolkit.CustomViews;

import com.AudioToolkit.Utility.Mathf;
import com.AudioToolkit.Utility.RepeatListener;
import com.AudioToolkitPro.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

public class Arrowbox extends TableRow{
	
	TextView attr;
	Button plus, minus;
	Databox value;
	CheckBox lock;
	double timeOfLastTouch = 0, timeSinceLastTouch = 0, min = 0;
	double lastVal;
	
	Context mContext;

	int iterator = 1;
	double maxIterator = 5;

	public Arrowbox(Context context) {
		super(context);

		initialize();

	}

	public Arrowbox(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;		

		initialize();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.arrowbox);

		//Set attr and units to the defaults in the xml
		setAttr(a.getString(R.styleable.arrowbox_arrow_attr));
		//setValue(0.5);

		a.recycle();
	}

	private void initialize() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.arrowbox, this, true);

		attr = (TextView) findViewById(R.id.attr);
		minus = (Button) findViewById(R.id.minus);
		plus = (Button) findViewById(R.id.plus);
		value  = (Databox) findViewById(R.id.value);
		lock = (CheckBox) findViewById(R.id.lock);

		setIncrementListener(minus, -1);
		setIncrementListener(plus, 1);

	}

	private void setIncrementListener(Button btn, final int dir) {

		btn.setOnTouchListener(new RepeatListener(500, 200, 0, 0, new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (isLocked()) return;

				double inc = Math.min(maxIterator, 0.1 * Math.pow(1.125,iterator));
				
				double val = (inc != maxIterator ? getValue() : Math.floor(getValue()));
				
				setValue(val + (inc/value.getConv())*dir);

				iterator++;

				OnValueChanged(getValue(), dir);				
			}
		}) {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) 				   
					iterator = 1;				
				
				super.onTouch(view, motionEvent);
				
				return false;
			}

		});

		/*	
		btn.setOnTouchListener(new OnTouchListener() {
		    public boolean onTouch(View v, MotionEvent event) {
		        switch(event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		        	if (lock.isChecked()) return true;
		        	if (timeOfLastTouch == 0) timeOfLastTouch = System.currentTimeMillis(); 
		        	setValue(increment(getValue(), dir*(System.currentTimeMillis() - timeOfLastTouch)));	
		        	OnValueChanged(getValue());
		        	System.out.println("button down");
		            return true;

		        case MotionEvent.ACTION_UP:
		            timeOfLastTouch = 0;
		            return true;

		        }
		        return false;
		    }
		});*/		
	}

	public interface OnValueChangeListener {
		public void onVariableChanged(double value, double increment);
	}

	OnValueChangeListener onValueChangedListener = null;

	public void setValueChangeListener(OnValueChangeListener onValueChangedListener) {
		this.onValueChangedListener = onValueChangedListener;
	}

	private void OnValueChanged(double value, double increment){
		if(onValueChangedListener!=null) {

			onValueChangedListener.onVariableChanged(value, increment);

		}
	}

	private double increment(double val, double inc) {

		inc = Math.pow(0.0725,(0.0011043*inc));

		inc *= 10;
		inc = Math.round(inc)/10;		

		return val + inc;
	}	

	public void setAttr(String s) {
		attr.setText(s);
	}

	public String getAttr() {
		return "" + attr.getText();
	}

	public boolean isLocked() {
		return lock.isChecked();
	}

	public double getValue() {
		
		if (!value.tryParse()) return 0;
		
		return value.parse();
	}

	public void setValue(double val) {

		lastVal = getValue();
		if (val < min) val = min;		
		value.setValue(val,1);
	}

	public void setValue(String s) {
		setValue(Mathf.str2dbl(s));
	}

	public void setMin(double d) {
		min = d;
	}

	public double getMin() {
		return min;
	}

	public void revertValue() {
		setValue(lastVal);	
		System.out.println("arrowbox value reverted");
	}

	public boolean atMin() {
		return getValue() == getMin();
	}
	
	public boolean canChange(double increment) {
		//true only is it isn't locked, or the tested increment is > 0 when the arrowbox is at it's min value
		return !(isLocked() || (increment > 0 && atMin()));
	}

}
