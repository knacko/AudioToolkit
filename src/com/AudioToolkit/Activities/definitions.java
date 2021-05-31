package com.AudioToolkit.Activities;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.AudioToolkit.CustomViews.BoundedLinearLayout;
import com.AudioToolkit.CustomViews.CustomDialog;
import com.AudioToolkit.CustomViews.Databox;
import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkitPro.R;

import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
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
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Typeface;

public class definitions extends TabActivity {

	Databox subs, vcs, imped, load;
	RadioButton vcSeries, vcParallel, subSeries, subParallel;
	Button calculate, clear, wiring_cis, wiring_sis, wiring_cip, wiring_sip;
	TabHost mTabHost;

	String help = "<big><u>Speaker Wiring</u></big><p>" +
			"Make sure your amplifier is capable of running at the given impedence level. Failure to do so may cause damage " +
			"to your speakers or amplifier.";

	public void onCreate(Bundle b) {

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.definitions);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());

		tabBuilder.addTab("Definitions", R.id.definitions);

		tabBuilder.build();

		DefinitionParser dp = new DefinitionParser();

		List<definition> defs = null;

		try {
			defs = dp.parse(this.getResources().openRawResource(R.raw.defs));
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ExpandableListAdapter mAdapter;
		ExpandableListView epView = (ExpandableListView) findViewById(R.id.def_list);

		mAdapter = new MyExpandableListAdapter(defs);
		epView.setGroupIndicator(null);
		epView.setAdapter(mAdapter);

	}

	public class DefinitionParser {
			
		private final String ns = null;

		public List<definition> parse(InputStream in) throws XmlPullParserException, IOException {
					
			try {
				XmlPullParser parser = Xml.newPullParser();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in, null);
				parser.nextTag();
				return readFeed(parser);
			} finally {
				in.close();
			}
		}

		private List<definition> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
			List<definition> entries = new ArrayList<definition>();

			parser.require(XmlPullParser.START_TAG, ns, "dictionary");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("definition")) {
					entries.add(readEntry(parser));
				} else {
					skip(parser);
				}
			}
			return entries;
		}

		// Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
		// off
		// to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
		private definition readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
			parser.require(XmlPullParser.START_TAG, ns, "definition");
			String name = null;
			String desc = null;
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String tagname = parser.getName();
				if (tagname.equals("name")) {
					name = readName(parser);
				} else if (tagname.equals("description")) {
					desc = readDesc(parser);
				} else {
					skip(parser);
				}
			}
						
			return new definition(name, desc);
		}

		// Processes title tags in the feed.
		private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, ns, "name");
			String title = readText(parser);
			parser.require(XmlPullParser.END_TAG, ns, "name");
			return title;
		}

		// Processes summary tags in the feed.
		private String readDesc(XmlPullParser parser) throws IOException, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, ns, "description");
			String summary = readText(parser);
			parser.require(XmlPullParser.END_TAG, ns, "description");
			return summary;
		}

		// For the tags title and summary, extracts their text values.
		private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
			String result = "";
			if (parser.next() == XmlPullParser.TEXT) {
				result = parser.getText();
				parser.nextTag();
			}
			return result;
		}

		// Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
		// if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
		// finds the matching END_TAG (as indicated by the value of "depth" being 0).
		private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				throw new IllegalStateException();
			}
			int depth = 1;
			while (depth != 0) {
				switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
				}
			}
		}
	}
//TODO: fix the image for selecting an item in list views
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		private List<definition> defs;
		
		public MyExpandableListAdapter(List<definition> defs) {
			super();
			this.defs = defs;
		}		
		
		public Object getChild(int groupPosition, int childPosition) {
			return defs.get(groupPosition).getDesc();
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition; 
		}

		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			LinearLayout L1 = new LinearLayout(definitions.this);
			L1.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L1.setGravity(Gravity.CENTER);
			L1.setOrientation(0); //horiz			

			BoundedLinearLayout L3 = new BoundedLinearLayout(definitions.this);
			L3.setLayoutParams(new BoundedLinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L3.setOrientation(0); //horiz

			String[] text = (getChild(groupPosition, childPosition).toString()).split("#",3);

			TextView textView1 = new TextView(definitions.this);
			textView1.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT));
			// Center the text vertically
			textView1.setGravity(Gravity.CENTER_VERTICAL);
			textView1.setTextColor(Color.parseColor("#FFFFFF"));//R.color.marcyred);
			// Set the text starting position
			textView1.setPadding(45, 10, 10, 10);
			textView1.setText(text[0]);

			L3.addView(textView1);
			L1.addView(L3);

			return L1;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			LinearLayout L1 = new LinearLayout(definitions.this);
			L1.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L1.setBackgroundColor(0x33000000);
			L1.setGravity(Gravity.CENTER);
			L1.setOrientation(0); //horiz			


			BoundedLinearLayout L3 = new BoundedLinearLayout(definitions.this);
			L3.setLayoutParams(new BoundedLinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			L3.setOrientation(0); //horiz			

			TextView textView = new TextView(definitions.this);
			textView.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT));
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
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
			return defs.get(groupPosition).getName();
		}

		public int getGroupCount() {
			return defs.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}		

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

		public boolean hasStableIds() {
			return true;
		}

	}

	private class definition {

		String name, description;

		public definition(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getDesc() {
			return description;
		}

	}


}