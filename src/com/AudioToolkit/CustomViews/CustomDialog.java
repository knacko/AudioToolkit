package com.AudioToolkit.CustomViews;

import com.AudioToolkit.Activities.encbuilder;
import com.AudioToolkitPro.R;
import com.jjoe64.graphview.GraphView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CustomDialog{
	
	Dialog dialog;
	
	//TODO: change background, make toast with close button
		
	public CustomDialog(Context mContext, int i) {

		dialog = getDialog(mContext);
		dialog.setContentView(i);
		dialog.show();		
		
	}
	
	public CustomDialog(Context mContext, int imageID, String name, String components) {

		dialog = getDialog(mContext);

		dialog.setContentView(R.layout.dia_image);
		//dialog.setTitle(name);
		//dialog.setCancelable(true);
		//dialog.setNeutralButton("Close", null).show();

		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(name);

		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		image.setImageResource(imageID);//R.drawable.android);

		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText(components);
		if (components.equals("")) text.setVisibility(View.GONE);

		dialog.show();

		return;
	
	}
	
	public CustomDialog (Context mContext, GraphView graphView, String s) {

		dialog = getDialog(mContext);

		dialog.setContentView(R.layout.dia_graph);
		//dialog.setTitle("Frequency Response");
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		
		final LinearLayout graph = (LinearLayout) dialog.findViewById(R.id.dia_graphroot);
		graph.addView(graphView,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, width));

		TextView text = (TextView) dialog.findViewById(R.id.dia_graphtext);
		text.setText(s);
		
		dialog.show();

	}
	
	
	
	
	/*
	 * Dialog for showing the popup for the sliderbox
	 */
	
	public CustomDialog(Context mContext, final Databox d, final Handler h) {
		
		dialog = getDialog(mContext);

		dialog.setContentView(R.layout.dia_databox);
		
		LinearLayout view = (LinearLayout) dialog.findViewById(R.id.layout_root);
		view.setGravity(Gravity.RIGHT);
		
		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		ll.setPadding(0,0,0,10);
		
		Button set = new Button(mContext);
		set.setText("Set");	
		set.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	    
		set.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				Message msg = h.obtainMessage(0, d.parse());
				h.sendMessage(msg);		
				
				dialog.cancel();

			}});
				
		Button cancel = new Button(mContext);
		cancel.setText("Cancel");		
		cancel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		cancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				dialog.cancel();

			}});
		
		
		ll.addView(set);		
		ll.addView(cancel);

		view.addView(d);
		view.addView(ll);
				
		dialog.show();

	}
	
	private Dialog getDialog(Context mContext) {
		
		Dialog d = new Dialog(mContext, R.style.theTheme);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		return d;
	}
		
	public void show() {
		dialog.show();
	}
	
	public void cancel() {
		dialog.cancel();
	}
	
	public Dialog getDialog() {
		return dialog;
	}
	
}
