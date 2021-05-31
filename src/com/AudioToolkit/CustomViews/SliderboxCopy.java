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
import android.widget.TextView;
import com.AudioToolkitPro.R;

public class SliderboxCopy extends LinearLayout{

	SeekBar sbBar;
	TextView tvProgress;
	View tvSpacer;
	String attr = "", subtext="", unit="", databoxunit="";
	int min, max, scale, offset;
	double value;
	boolean changeBounds = false;
	static Context mContext;
	long lastTouchTime = -1;

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

	public SliderboxCopy(Context context, AttributeSet attrs) {
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

		scale = a.getInteger(R.styleable.sliderbox_slider_scale,1);
		max = a.getInteger(R.styleable.sliderbox_slider_max,Integer.MAX_VALUE);
		min = a.getInteger(R.styleable.sliderbox_slider_min,1);

		changeBounds = a.getBoolean(R.styleable.sliderbox_slider_changebounds,false);

		tvProgress = (TextView) this.findViewById(R.id.tvValue);

		//tvSpacer = (View) this.findViewById(R.id.tvSpacer);
		sbBar = (SeekBar) this.findViewById(R.id.sbBar);
		sbBar.setThumbOffset(0);
		sbBar.setMax(max-min);
		sbBar.setOnTouchListener(new OnTouchListener() {        
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) 
				{
					long thisTime = System.currentTimeMillis();
					if (thisTime - lastTouchTime < 500) {
						Databox d = new Databox(mContext, attr, subtext, databoxunit);
						if ((a.getBoolean(R.styleable.sliderbox_slider_allownegative, false))) {
							d.allowNegative();
						}
						new CustomDialog(mContext, d, mhandle);
						lastTouchTime = -1;
						return false;
					}

					lastTouchTime = thisTime;					

				} 

				updatePos();

				if (event.getAction() == MotionEvent.ACTION_UP) OnValueChanged(getValue());

				return false;
			}
		});

		float val = a.getFloat(R.styleable.sliderbox_slider_initialvalue, min);
		setValue(val); 

		this.getViewTreeObserver().addOnGlobalLayoutListener( 
				new OnGlobalLayoutListener(){

					public void onGlobalLayout() {
						updatePos();
					}

				});
	}

	public void updatePos() {

		String s = getValue() + "";

		if (s.endsWith(".0") && scale == 1) s = s.substring(0, s.length()-2);
		
		tvProgress.setText(attr + ": " + s + unit);

		int xPos = (int) ((sbBar.getRight() - sbBar.getLeft()- tvProgress.getWidth()) * (double) sbBar.getProgress() / sbBar.getMax());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
		lp.setMargins(xPos, 0, 0, 0);
		tvSpacer.setLayoutParams(lp);
		/*Log.d("Sliderbox()","getRight() = " + sbBar.getRight() + " getLeft() = " + sbBar.getLeft() + " tvProgress.width() = " + tvProgress.getWidth() + " xPos = " + xPos
				+ " sbBar.getProgress() = " + sbBar.getProgress() + " sbBar.getMax() = " + sbBar.getMax()
				+ " pro/max = " + ((double) sbBar.getProgress() / sbBar.getMax()) + " getVal() = " + getValue()

				);*/
	}

	public double getValue() {		
		return ((double) sbBar.getProgress() + min)/scale;
	}

	public void setValue(double value) {

		sbBar.setProgress((int) ((value*scale)-min));
	//	updatePos();

	}

	public Handler mhandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			int newval = (int) (Double.valueOf(msg.obj.toString()).doubleValue()*scale);
			if (changeBounds) {		
				if (newval > max) {	//If allowed to change bounds and val is above, let it change
					max = newval;
					sbBar.setMax(max);
				}

				if (newval < min) {
					sbBar.setProgress(min);
					/*if (newval < 0) newval = 0;
					min = newval;
					sbBar.setMax(max-min);*/
				}
			} else {
				if (newval > max) {	
					sbBar.setProgress(max);
				}

				if (newval < min) {	
					sbBar.setProgress(min);
				}

			}

			sbBar.setProgress(newval-min);			
			updatePos(); 
			OnValueChanged(getValue());
			/*switch (msg.what)
			{
			case SPLval:
				spl = Double.valueOf(msg.obj.toString()).doubleValue();
				spl = isFree ? Math.min(50,spl) : spl;	//Limit to 50dB when in free mode
				splDataTV.setText("" + spl);
				totalSPL += spl; 
				totalSPLi++;	
				splavg.setText(Mathf.round(totalSPL/totalSPLi,1) + "");				
				break;
			case MAXval:
				spl = Double.valueOf(msg.obj.toString()).doubleValue();
				spl = isFree ? Math.min(50,spl) : spl;	//Limit to 50dB when in free mode
				splmax.setText("" + spl);
				break;
			default:
				break;
			}*/
		}
	};



}
