package com.example.emptytimefinder;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements OnClickListener{
	Button m_maketeam_button, m_maketimetable_button, m_loadtimetable_button;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_maketeam_button = (Button)findViewById(R.id.make_team_button);
        m_maketimetable_button = (Button)findViewById(R.id.make_timetable_button);
        m_loadtimetable_button = (Button)findViewById(R.id.load_timetable_button);
        
        
        m_maketeam_button.setOnClickListener(this);
        m_maketimetable_button.setOnClickListener(this);
        m_loadtimetable_button.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.make_team_button:
			Intent mIntent_maketeam = new Intent(MainActivity.this,MakeTeam.class);
			startActivity(mIntent_maketeam);
			break;
		case R.id.make_timetable_button:
			Intent mIntent_maketable = new Intent(MainActivity.this,MakeTimeTable.class);
			startActivity(mIntent_maketable);
			break;
		case R.id.load_timetable_button:
			Intent mIntent_loadtable = new Intent(MainActivity.this,LoadTimeTable.class);
			startActivity(mIntent_loadtable);
			break;
		}
	}

}
