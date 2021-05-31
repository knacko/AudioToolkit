package com.AudioToolkit.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.AudioToolkit.Utility.Mathf;
import com.AudioToolkitPro.R;

public class Sliderbox extends LinearLayout{

	SeekBar sbBar;
	TextView tvProgress, tvAttr, tvValue, tvInfo;
	View tvSpacer;
	String attr = "", subtext="", unit="", databoxunit="", info="";
	int min, max; //these are the actual values of max and min
	int decimals;
	int scale, offset;
	double value; //the actual value of the slider
	boolean changeBounds = false;
	static Context mContext;
	long lastTouchTime = -1;
	boolean tracking = false; //flag for when the sliderbox is being touched
	boolean startHidden = false;
	boolean hideNegativeSign = false;

	OnGlobalLayoutListener listener;

	public interface OnValueChangeListener {
		public void onVariableChanged(double value);
	}

	OnValueChangeListener onValueChangedListener = null;

	public void setValueChangeListener(OnValueChangeListener onValueChangedListener) {
		this.onValueChangedListener = onValueChangedListener;
	}

	private void OnValueChanged(double value){
		if(onValueChangedListener!=null) {
			onValueChangedListener.onVariableChanged(value);
		}
	}

	public Sliderbox(Context context, AttributeSet attrs) {
		super(context, attrs);
		//do something with str 

		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.sliderbox, this, true);

		mContext = context;

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.sliderbox);

		attr = a.getString(R.styleable.sliderbox_slider_attr);
		subtext = a.getString(R.styleable.sliderbox_slider_subtext);
		if (subtext == null) subtext = "";

		info = a.getString(R.styleable.sliderbox_slider_info);		
		
		unit = a.getString(R.styleable.sliderbox_slider_units);

		if (unit == null || unit.length() == 0) {
			unit = "";
		} else {
			if (unit.charAt(0) == '-') {
				unit = unit.substring(1);
			} else {
				unit = " " + unit;
			}			
		};

		databoxunit = a.getString(R.styleable.sliderbox_slider_databoxunits);
		if (databoxunit == null) databoxunit = "";

		decimals = a.getInteger(R.styleable.sliderbox_slider_decimals,0);

		decimals = (int) Math.pow(10,decimals);

		scale = a.getInteger(R.styleable.sliderbox_slider_scale,1);
		max = a.getInteger(R.styleable.sliderbox_slider_max,100);
		min = a.getInteger(R.styleable.sliderbox_slider_min,0);

		changeBounds = a.getBoolean(R.styleable.sliderbox_slider_changebounds,false);
		hideNegativeSign = a.getBoolean(R.styleable.sliderbox_slider_hidenegativesign,false);

		tvAttr = (TextView) this.findViewById(R.id.tvAttr);
		tvAttr.setText(attr);
		
		tvInfo = (TextView) this.findViewById(R.id.tvInfo);
		tvInfo.setText(info);		
		
		tvValue = (TextView) this.findViewById(R.id.tvValue);

		tvProgress = (TextView) this.findViewById(R.id.tvValue);

		sbBar = (SeekBar) this.findViewById(R.id.sbBar);
		sbBar.setThumbOffset(0);
		setMax(max);
		sbBar.setMax(Math.abs(max-min)*decimals);

		sbBar.setOnTouchListener(new OnTouchListener() {        
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) 
				{
					long thisTime = System.currentTimeMillis();
					if (thisTime - lastTouchTime < 250) {
						Databox d = new Databox(mContext, attr, subtext, databoxunit);
						if ((a.getBoolean(R.styleable.sliderbox_slider_allownegative, false))) {
							d.allowNegative();
						}
						new CustomDialog(mContext, d, mhandle);
						lastTouchTime = -1;
						return false;
					}

					lastTouchTime = thisTime;					

					tracking = true;
				} 

				//updatePos();

				//After releasing, notify the generator to change audio output
				if (event.getAction() == MotionEvent.ACTION_UP) {
					OnValueChanged(getValue());
					tracking = false;
				}

				return false;
			}
		});

		sbBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       

			@Override       
			public void onStopTrackingTouch(SeekBar seekBar) {


			}
			@Override       
			public void onStartTrackingTouch(SeekBar seekBar) {


			}			
			@Override       
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {     

				if (fromUser) setValue(((double)progress / decimals) + min); //only call if set from user
				//else updateTextPos();;;

			}  

		});

		float val = a.getFloat(R.styleable.sliderbox_slider_initialvalue, min);
		setValue(val); 
		updateSeekBarPos();
		//addLayoutListener();

	}

	private void addLayoutListener() {

		listener = new OnGlobalLayoutListener(){

			public void onGlobalLayout() {

				refresh();
				if (sbBar.getRight() != 0) removeGlobalLayoutListener();
			}

		};

		this.getViewTreeObserver().addOnGlobalLayoutListener(listener); 

	}

	public void removeGlobalLayoutListener() {
		this.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
	}

	public void refresh() {		
		//	addLayoutListener();

		//Log.d("refreshing",attr+"");

		updateSeekBarPos();
		updateTextPos();
	}

	public void updateTextPos() {

		double v = hideNegativeSign ? Math.abs(getValue()) : getValue();
		
		String s = Mathf.truncate(v);

		tvProgress.setText(s + unit);	

		/*
		int xPos = (int) ((sbBar.getRight() - sbBar.getLeft() - tvProgress.getWidth()) * ((value-min)/(max-min)));

		//Mathf.getPointPercentage(value,min,max)) ;

		//int xPos = (int) ((sbBar.getRight() - sbBar.getLeft() - tvProgress.getWidth()) * (value/(max+min)) );

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
		lp.setMargins(xPos, 0, 0, 0);
		tvSpacer.setLayoutParams(lp);
		Log.d("Sliderbox(" + attr + ")","getRight() = " + sbBar.getRight() + " getLeft() = " + sbBar.getLeft() + " tvProgress.width() = " + tvProgress.getWidth() + " xPos = " + xPos
				+ " sbBar.getProgress() = " + sbBar.getProgress() + " sbBar.getMax() = " + sbBar.getMax()
				+ " getVal() = " + getValue()

				);*/
	}

	private void updateSeekBarPos() {
		sbBar.setProgress((int) ((value-min)*decimals));
	}

	public double getValue() {	

		return value;

		//return ((double) sbBar.getProgress() + min)/scale;
	}

	public void setValue(double value) {

		//sbBar.setProgress((int) ((value*scale)-min));

		this.value = value;

		refresh();

	}

	public Handler mhandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			double value = Mathf.str2dbl(msg.obj.toString());

			//(int) (Double.valueOf(msg.obj.toString()).doubleValue()*scale);
			if (changeBounds) {		
				if (value > max) {	//If allowed to change bounds and val is above, let it change
					setMax((int) Math.ceil(value));
				}

				if (value < min) {

					setMin((int) Math.floor(value));					

					//sbBar.setProgress(min);
					/*if (newval < 0) newval = 0;
					min = newval;
					sbBar.setMax(max-min);*/
				}

			} else {
				if (value > max) {	
					value = max;
					//sbBar.setProgress(max);
				}

				if (value < min) {
					value = min;
					//sbBar.setProgress(min);
				}
			}

			setValue(value);
			OnValueChanged(getValue());
			//sbBar.setProgress(newval-min);			
			//updateTextPos(); 
			//?OnValueChanged(getValue());

		}
	};


	void setMax(int max) {		
		this.max = max;
		sbBar.setMax((Math.abs(max-min)*decimals));
		System.out.println("Max: " + max + ", Min: " + min + ", dec: " + decimals + ", sbMax: " + sbBar.getMax());
	}
	
	void setMin(int min) {
		
		this.min = min;
		setMax(max);
		
	}
	
}
