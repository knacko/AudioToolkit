package com.AudioToolkit.Activities;


import com.AudioToolkit.CustomViews.BoundedLinearLayout;
import com.AudioToolkit.CustomViews.CustomDialog;
import com.AudioToolkit.CustomViews.Databox;
import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkitPro.R;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.TabActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

public class subwiring extends TabActivity {

	Databox subs, vcs, imped, load;
	RadioButton vcSeries, vcParallel, subSeries, subParallel;
	Button calculate, clear, wiring_cis, wiring_sis, wiring_cip, wiring_sip;
	TabHost mTabHost;

	Context mContext = subwiring.this;
	
	String help = "<big><u>Speaker Wiring</u></big><p>" +
			"Make sure your amplifier is capable of running at the given impedence level. Failure to do so may cause damage " +
			"to your speakers or amplifier.";
	
	public void onCreate(Bundle b) {

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.subwiring);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("Final Load", R.id.wiring_load);
		tabBuilder.addTab("Diagrams",R.id.wiring_diagram);
		tabBuilder.addOptions(help);
		
		tabBuilder.build();


		subs = (Databox) findViewById(R.id.subs);
		vcs = (Databox) findViewById(R.id.vcs);
		imped = (Databox) findViewById(R.id.imped);
		load = (Databox) findViewById(R.id.load);

		subSeries = (RadioButton) findViewById(R.id.subSeries);
		subParallel = (RadioButton) findViewById(R.id.subParallel);		
		vcSeries = (RadioButton) findViewById(R.id.vcSeries);
		vcParallel = (RadioButton) findViewById(R.id.vcParallel);

		calculate = (Button) findViewById(R.id.calculate);
		clear = (Button) findViewById(R.id.clear);
		/*wiring_sis = (Button) findViewById(R.id.wiring_sis);
		wiring_sip = (Button) findViewById(R.id.wiring_sip);
		wiring_cis = (Button) findViewById(R.id.wiring_cis);
		wiring_cip = (Button) findViewById(R.id.wiring_cip);*/

		calculate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				double l = 0, s, v, i;

				if (!subs.tryParse() | !vcs.tryParse() | !imped.tryParse()) return;
				
				
				 s = subs.parse();
				 v = vcs.parse();
				 i = imped.parse();			

				subs.setValue(s,1);
				vcs.setValue(v,1);
				imped.setValue(i,1);

				if (vcSeries.isChecked() && subSeries.isChecked()) l = s*v*i; 
				else if (vcSeries.isChecked() && subParallel.isChecked()) l = 1/(s*(1/(v*i))); 
				else if (vcParallel.isChecked() && subSeries.isChecked()) l = s*(i/v); 
				else if (vcParallel.isChecked() && subParallel.isChecked()) l = 1/((s*v)/i);

				load.setValue(l,1);

			}});


		clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				subs.clear();
				vcs.clear();
				imped.clear();

			}});
 
		ExpandableListAdapter mAdapter;
		ExpandableListView epView = (ExpandableListView) findViewById(R.id.wiring_explist);

		mAdapter = new MyExpandableListAdapter();
		epView.setGroupIndicator(null);
		epView.setAdapter(mAdapter);

		epView.setOnGroupClickListener(new OnGroupClickListener() {
			public boolean onGroupClick(ExpandableListView arg0, View arg1,
					int groupPosition, long arg3) {
				//arg1.setBackgroundColor(Color.parseColor("#77ff0000"));
				return false;
			}
		});  

		epView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent,
					View v, int groupPosition, int childPosition,
					long id) {
				if (groupPosition == 0 && childPosition == 1) {
					showDialog(R.drawable.sch_1_svc, "SVC","");
				} else
				if (groupPosition == 0 && childPosition == 2) {
					showDialog(R.drawable.sch_1_dvcs, "DVC in Series","");
				} else
				if (groupPosition == 0 && childPosition == 3) {
					showDialog(R.drawable.sch_1_dvcp, "DVC in Parallel","");
				} else
				
				//2 subs
				if (groupPosition == 1 && childPosition == 1) {
					showDialog(R.drawable.sch_2s_svc, "Subs in Series, SVC","");
				} else
				if (groupPosition == 1 && childPosition == 2) {
					showDialog(R.drawable.sch_2p_svc, "Subs in Parallel, SVC","");
				} else
				if (groupPosition == 1 && childPosition == 3) {
					showDialog(R.drawable.sch_2s_dvcs, "Subs in Series\nDVC in Series","");
				} else
				if (groupPosition == 1 && childPosition == 4) {
					showDialog(R.drawable.sch_2s_dvcp, "Subs in Series\nDVC in Parallel","");
				} else
				if (groupPosition == 1 && childPosition == 5) {
					showDialog(R.drawable.sch_2p_dvcs, "Subs in Parallel\nDVC in Series","");
				} else
				if (groupPosition == 1 && childPosition == 6) {
					showDialog(R.drawable.sch_2p_dvcp, "Subs in Parallel\nDVC in Parallel","");
				} else
				
				//3 subs
				if (groupPosition == 2 && childPosition == 1) {
					showDialog(R.drawable.sch_3s_svc, "Subs in Series, SVC","");
				} else
				if (groupPosition == 2 && childPosition == 2) {
					showDialog(R.drawable.sch_3p_svc, "Subs in Parallel, SVC","");
				} else
				if (groupPosition == 2 && childPosition == 3) {
					showDialog(R.drawable.sch_3s_dvcs, "Subs in Series\nDVC in Series","");
				} else
				if (groupPosition == 2 && childPosition == 4) {
					showDialog(R.drawable.sch_3s_dvcp, "Subs in Series\nDVC in Parallel","");
				} else
				if (groupPosition == 2 && childPosition == 5) {
					showDialog(R.drawable.sch_3p_dvcs, "Subs in Parallel\nDVC in Series","");
				} else
				if (groupPosition == 2 && childPosition == 6) {
					showDialog(R.drawable.sch_3p_dvcp, "Subs in Parallel\nDVC in Parallel","");
				} else
						
				//4 subs
				if (groupPosition == 3 && childPosition == 1) {
					showDialog(R.drawable.sch_4s_svc, "Subs in Series, SVC","");
				} else
				if (groupPosition == 3 && childPosition == 2) {
					showDialog(R.drawable.sch_4p_svc, "Subs in Parallel, SVC","");
				} else
				if (groupPosition == 3 && childPosition == 3) {
					showDialog(R.drawable.sch_4s_dvcs, "Subs in Series\nDVC in Series","");
				} else
				if (groupPosition == 3 && childPosition == 4) {
					showDialog(R.drawable.sch_4s_dvcp, "Subs in Series\nDVC in Parallel","");
				} else
				if (groupPosition == 3 && childPosition == 5) {
					showDialog(R.drawable.sch_4p_dvcs, "Subs in Parallel\nDVC in Series","");
				} else
				if (groupPosition == 3 && childPosition == 6) {
					showDialog(R.drawable.sch_4p_dvcp, "Subs in Parallel\nDVC in Parallel","");
				}
					
					
				return false;
			}
		});


	}

	private void showDialog (int imageID, String name, String components) {

		new CustomDialog(subwiring.this,imageID, name,components);	
		
	}


	/**
	 * A simple adapter which maintains an ArrayList of photo resource Ids. Each
	 * photo is displayed as an image. This adapter supports clearing the list
	 * of photos and adding a new photo.
	 *
	 */
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		// Sample data set. children[i] contains the children (String[]) for
		// groups[i].
		private String[] groups = { "One Subwoofer", "Two Subwoofers",
				"Three Subwoofers","Four Subwoofers" };
		private String[][] children = {{ " #Voice Coils#1"," #Single"," #Double, Series"," #Double, Parallel" },
		{"Subwoofers#Voice Coils#1","Series#Single","Parallel#Single",
			"Series#Double, Series","Series#Double, Parallel","Parallel#Double, Series","Parallel#Double, Parallel"},
		{"Subwoofers#Voice Coils#1","Series#Single","Parallel#Single",
			"Series#Double, Series","Series#Double, Parallel","Parallel#Double, Series","Parallel#Double, Parallel"},
		{"Subwoofers#Voice Coils#1","Series#Single","Parallel#Single",
			"Series#Double, Series","Series#Double, Parallel","Parallel#Double, Series","Parallel#Double, Parallel"}};

		public Object getChild(int groupPosition, int childPosition) {
			return children[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition; 
		}

		public int getChildrenCount(int groupPosition) {
			int i = 0;
			try {
				i = children[groupPosition].length;

			} catch (Exception e) {
			}

			return i;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			LinearLayout L1 = new LinearLayout(subwiring.this);
			L1.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L1.setOrientation(0); //horiz			
			
			LinearLayout L2 = new LinearLayout(subwiring.this);
			L2.setLayoutParams(new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L2.setGravity(Gravity.CENTER);
			L2.setOrientation(0); //horiz				
			
			BoundedLinearLayout L3 = new BoundedLinearLayout(subwiring.this);
			L3.setLayoutParams(new BoundedLinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L3.setOrientation(0); //horiz
						
			String[] text = (getChild(groupPosition, childPosition).toString()).split("#",3);
			
			TextView textView1 = new TextView(subwiring.this);
			textView1.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT));
			// Center the text vertically
			textView1.setGravity(Gravity.CENTER_VERTICAL);
			textView1.setTextColor(Color.parseColor("#FFFFFF"));//R.color.marcyred);
			// Set the text starting position
			textView1.setPadding(45, 10, 10, 10);
			textView1.setText(text[0]);
			
			TextView textView2 = new TextView(subwiring.this);
			textView2.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT));
			// Center the text vertically
			textView2.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
			textView2.setTextColor(Color.parseColor("#FFFFFF"));//R.color.marcyred);
			// Set the text starting position
			textView2.setPadding(10, 10, 45, 10);
			textView2.setText(text[1]);
			
			if (text.length > 2) {
				textView1.setTypeface(null, Typeface.BOLD);
				textView2.setTypeface(null, Typeface.BOLD);
			}
			
			L3.addView(textView1);
			L3.addView(textView2);						
			L2.addView(L3);
			L1.addView(L2);
			
			return L1;
		}
		
		
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			LinearLayout L1 = new LinearLayout(mContext);
			L1.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L1.setBackgroundColor(0x33000000);
			L1.setGravity(Gravity.CENTER);
			L1.setOrientation(0); //horiz			


			BoundedLinearLayout L3 = new BoundedLinearLayout(mContext);
			L3.setLayoutParams(new BoundedLinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L3.setOrientation(0); //horiz			

			TextView textView = new TextView(mContext);
			textView.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT));
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
			textView.setTextColor(Color.parseColor("#FFFFFF"));//R.color.marcyred);
			textView.setTypeface(null, Typeface.BOLD);

			// Set the text starting position
			textView.setPadding(10, 10, 45, 10);
			textView.setText(getGroup(groupPosition).toString());

			L3.addView(textView);		
			L1.addView(L3);

			return L1;



		}
		
		
		
		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		public int getGroupCount() {
			return groups.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

	}


}