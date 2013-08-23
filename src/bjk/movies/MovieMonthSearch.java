package bjk.movies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Calendar;

public class MovieMonthSearch extends Activity
{
	Cursor moviesCursor;
	ListView m_monthView;
	Spinner m_monthsSpinner;
	final Intent m_returningIntent = new Intent();
	
	private String[] INSTANCES = new String[] { 
			MovieConnector.Constants.MOVIE_NAME_COL, 
			MovieConnector.Constants._ID,
			MovieConnector.Constants.MOVIE_CINEMA_DATE_AS_STRING_COL };
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthsearch);
        
        m_monthsSpinner = (Spinner)findViewById(R.id.ddlMonths);
        m_monthView = (ListView)findViewById(R.id.lstResults);
        
        //Set Cursor initially
        moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, INSTANCES, "", null, "ORDER BY " + MovieConnector.Constants.MOVIE_NAME_COL + " ASC");
        
        //Sets up spinner
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, 
        		GetMonths());
        
        
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_monthsSpinner.setAdapter(aa);
        
        //Get the current date based on today's date
        Calendar currentCal = Calendar.getInstance();
        m_monthsSpinner.setSelection(currentCal.get(Calendar.MONTH));
        
        //Spinner Selected Listener
        m_monthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				RepopulateList(arg2);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				
			}
		});
        //Menu Item Click Listener
        m_monthView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				TextView idField = (TextView)arg1.findViewById(R.id.rowMovieID);
				m_returningIntent.putExtra("SelectedID", Integer.parseInt(idField.getText().toString()));
				MovieMonthSearch.this.setResult(ActivityCodes.RESPONSE_OK, m_returningIntent);
				finish();
			}
		});
    }
	@Override
    public void onDestroy()
    {
    	moviesCursor.close();
    	super.onDestroy();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
    {
    	new MenuInflater(getApplication()).inflate(R.menu.othermenu, menu);
    	return(super.onCreateOptionsMenu(menu));
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{
    		case R.id.cancel:
    			MovieMonthSearch.this.setResult(ActivityCodes.RESPONSE_CANCEL);
    			finish();
    			return true;
    			
    		case R.id.about:
    			setUpDisclaimer();
    			return true;
    	}
    	return(super.onOptionsItemSelected(item));
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			MovieMonthSearch.this.setResult(ActivityCodes.RESPONSE_CANCEL);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//Private Methods
	private String[] GetMonths()
	{
		String[] MonthNames = { "January", "February", "March", 
				"April", "May", "June", "July", "August", 
				"September", "October", "November", "December" };
		
		return MonthNames;
	}
	private void RepopulateList(int index)
	{
		Calendar first = GetRange(index, false), last = GetRange(index, true);
		
		moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, INSTANCES, 
				"WHERE " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " >= " + first.getTimeInMillis() + " AND " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " <= " + last.getTimeInMillis(), 
				null, 
				"ORDER BY " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " ASC");
		
		ListAdapter monthAdapter = new SimpleCursorAdapter(this, 
				R.layout.cursorrow, 
				moviesCursor, 
				INSTANCES,
				new int[] {R.id.rowMovieName, R.id.rowMovieID, R.id.rowMovieDetail});
		
		m_monthView.setAdapter(monthAdapter);
	}
	private Calendar GetRange(int monthIndex, boolean LastDay)
	{
		if(LastDay)
		{
			Calendar lastDay = Calendar.getInstance();
			lastDay.set(Calendar.HOUR_OF_DAY, 23);
			lastDay.set(Calendar.MINUTE, 59);
			lastDay.set(Calendar.SECOND, 0);
			lastDay.set(Calendar.MILLISECOND, 0);
			lastDay.set(Calendar.MONTH, monthIndex);
			lastDay.set(Calendar.DAY_OF_MONTH, 1);
			lastDay.set(Calendar.DATE, 1);
			
			//Now reset since this works for all dates it seems, my, this was annoying
			lastDay.set(Calendar.DAY_OF_MONTH, lastDay.getActualMaximum(Calendar.DATE));
			lastDay.set(Calendar.DATE, lastDay.getActualMaximum(Calendar.DATE));
			return lastDay;
		}
		else
		{
			Calendar firstDay = Calendar.getInstance();
			firstDay.set(Calendar.HOUR_OF_DAY, 0);
			firstDay.set(Calendar.MINUTE, 0);
			firstDay.set(Calendar.SECOND, 0);
			firstDay.set(Calendar.MILLISECOND, 0);
			firstDay.set(Calendar.MONTH, monthIndex);
			firstDay.set(Calendar.DAY_OF_MONTH, 1);
			return firstDay;
		}
	}
	//A Disclaimer Screen
    private void setUpDisclaimer()
    {
    	new AlertDialog.Builder(MovieMonthSearch.this).setTitle("About")
		.setMessage("Icons Provided By The \"Axialis Team\"\n\nhttp://www.axialis.com/free/icons/").setNeutralButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//This will close on its own
			}
		}).show();
    }
}