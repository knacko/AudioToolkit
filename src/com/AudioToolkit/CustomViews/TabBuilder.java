package com.AudioToolkit.CustomViews;

import com.AudioToolkitPro.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;

public class TabBuilder {

	TabHost mTabHost;
	TabWidget mTabWidget;
	Context mContext;
	Resources mResources;

	boolean hasOptions = false;

	public TabBuilder(Context c, TabHost th, Resources r) {
		mTabHost = th;
		mTabWidget = th.getTabWidget();
		mContext = c;
		mResources = r;
	}

	public void addTab(String s, int id) {

		mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getTabView(mContext, s)).setContent(id));

	}

	public void addOptions(final String s) {

		hasOptions = true;

		LinearLayout ll = new LinearLayout(mContext);
		ll.setGravity(Gravity.CENTER);
		
		Button b = new Button(mContext);//, null, R.style.theTheme);

		b.setGravity(Gravity.CENTER);
		b.setText("   ?   ");
		//b.setTextSize(b.getTextSize() + 1);
		b.setTypeface(null, Typeface.BOLD);
		b.setShadowLayer(3f, 0, -1, Color.BLACK);
		b.setTextColor(Color.parseColor("#f6bb0d"));
		b.setBackgroundDrawable(mResources.getDrawable(R.drawable.trans));
		ll.setBackgroundDrawable(mResources.getDrawable(R.drawable.tab_up));
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		        60,      
		        LayoutParams.FILL_PARENT
		);
		params.setMargins(0, -4, 0, 0);
		b.setLayoutParams(params);
		b.setPadding(0, 0, 0, 0);
			
						  
		b.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				ScrollView sv = new ScrollView(mContext);
				
				TextView msg = new TextView(mContext);
				msg.setText(Html.fromHtml(s));			
								
				msg.setPadding(20,0,20,0);				
				sv.addView(msg);
				
				AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
				
				ab.setTitle("How to use");
				ab.setView(sv);
				ab.setIcon(R.drawable.ic_menu_help);
				ab.setNeutralButton("Close", null);
				ab.show();

			}});
		
		ll.addView(b);
		
		mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(ll).setContent(new TabContentFactory() {
			public View createTabContent(String tag) {

				return new View(mContext);
			}}));

	}

	public void build() {

		View view;

		int tabCount = mTabWidget.getTabCount() - (hasOptions ? 1 : 0);
		
		for (int i = 0; i < tabCount; i++) {    

			int id = R.drawable.tab_indicator;

			/*
			if (tabCount == 1)
				id = R.drawable.tab_solo;	
			else if (i == 0)
				id = R.drawable.tab_right;
			else if (i == mTabWidget.getTabCount() - (hasOptions ? 1 : 0) - 1)
				id = R.drawable.tab_left;
			else 
				id = R.drawable.tab_middle;
			*/
						
			view = mTabWidget.getChildAt(i);
			view.setBackgroundDrawable(mResources.getDrawable(id));

			//for putting space around the tabs
			LinearLayout.LayoutParams currentLayout =	(LinearLayout.LayoutParams) view.getLayoutParams();
			//currentLayout.setMargins(0, -1, 0, 0);
			view.setLayoutParams(currentLayout);

		}

		if (hasOptions) {
			
			View options = mTabHost.getTabWidget().getChildAt(mTabWidget.getTabCount() - 1);
			//View options = mTabHost.getTabWidget().getChildTabViewAt(mTabWidget.getTabCount() - 1);
			//options.setBackgroundDrawable(mResources.getDrawable(R.drawable.transparent));
			
			Log.d("tab height", "height = " + options.getLayoutParams().height);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					65,      
			        LayoutParams.FILL_PARENT
			);
			params.setMargins(5, 0, 0, 0);
			options.setLayoutParams(params);
						//int height = options.getLayoutParams().height;
			//options.setLayoutParams(new LinearLayout.LayoutParams(height,height));
			 
			options.setEnabled(false);
		
		}
	}  
  
	private View getTabView(Context c, String s) {

		LinearLayout ll = new LinearLayout(c);
		ll.setGravity(Gravity.CENTER); 
		 
		TextView tabTV = new TextView(c);
		tabTV.setTextColor(mResources.getColorStateList(R.drawable.tab_text));
		tabTV.setText(s);
		tabTV.setGravity(Gravity.CENTER);
		tabTV.setPadding(0,2,0,9);	
		tabTV.setTypeface(null, Typeface.BOLD);
		tabTV.setShadowLayer(3f, 0, -1, Color.BLACK);

		ll.addView(tabTV);

		return ll;		
	}


}
