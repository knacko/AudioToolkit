package com.AudioToolkit.Activities;

import com.AudioToolkit.CustomViews.TabBuilder;
import com.AudioToolkitPro.R;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class suggestions extends TabActivity{

	/* TODO: Suggestions - Add-on a vote for future updates
	 * 
	 */
	
	EditText sugg_subject, sugg_body;

	public void onCreate(Bundle b) {
		
		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.suggestions);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		TabBuilder tabBuilder = new TabBuilder(this, getTabHost(), getResources());
		
		tabBuilder.addTab("Suggestions", R.id.sugg_sugg);
		
		tabBuilder.build();
				
		Button sugg_clear = (Button) findViewById(R.id.sugg_clear);
		Button sugg_send = (Button) findViewById(R.id.sugg_send);
		sugg_subject = (EditText) findViewById(R.id.sugg_subject);
		sugg_body = (EditText) findViewById(R.id.sugg_body);

		sugg_send.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                
                emailIntent.setType("plain/text");
           
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"knacko.me@gmail.com"});
         
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AudioToolkit Suggestion: " + sugg_subject.getText());
         
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" + sugg_body.getText());
 
                suggestions.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				
			}});
		
		sugg_clear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				
				sugg_subject.setText("");
				sugg_body.setText("");
								
			}});
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.aboutmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			new AlertDialog.Builder(this).setTitle("Info").setMessage(
					getString(R.string.aboutTagline)).setIcon(R.drawable.w3_small_logo).setNeutralButton("Close", null).show();
			break;

		}
		return true;
	}
}
