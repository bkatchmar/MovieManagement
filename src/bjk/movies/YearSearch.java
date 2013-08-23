package bjk.movies;

import java.util.Calendar;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class YearSearch extends Activity
{
	Button m_btnAll, m_btnBest;
	EditText m_txtMovieSearch;
	Cursor m_moviesCursor;
	ListView m_lstResults;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.yearsearch);
        
        //Set Cursor initially
        m_moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID}, "", null, "ORDER BY " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " ASC");
        
        //Set up widgets
        m_btnAll = (Button)findViewById(R.id.btnAll);
        m_btnBest = (Button)findViewById(R.id.btnBest);
        m_txtMovieSearch = (EditText)findViewById(R.id.txtWhatYear);
        m_lstResults = (ListView)findViewById(R.id.lstResults);
        
        //All Click Listener
        m_btnAll.setOnClickListener(new View.OnClickListener()
        {
        	@Override
			public void onClick(View v)
        	{
        		if(m_txtMovieSearch.getText().toString().length() == 4)
        		{
	        		int enteredYear = Integer.parseInt(m_txtMovieSearch.getText().toString());
	        		Calendar begining = GetRange(enteredYear, false), last = GetRange(enteredYear, true);
	        		
	        		m_moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, 
	        				new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID, MovieConnector.Constants.MOVIE_CINEMA_DATE_AS_STRING_COL}, 
	        				"WHERE " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " >= " + begining.getTimeInMillis() + " AND " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " <= " + last.getTimeInMillis(), 
	        				null, 
	        				"ORDER BY " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " ASC");
	        		
	        		ListAdapter monthAdapter = new SimpleCursorAdapter(YearSearch.this, 
	        				R.layout.cursorrow, 
	        				m_moviesCursor, 
	        				new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID, MovieConnector.Constants.MOVIE_CINEMA_DATE_AS_STRING_COL},
	        				new int[] {R.id.rowMovieName, R.id.rowMovieID, R.id.rowMovieDetail});
	        		
	        		m_lstResults.setAdapter(monthAdapter);
        		}
        		else
        		{
        			setUpErrorScreen("Must be a proper year");
        		}
        	}
		});
        //Best Of Year Click Listener
        m_btnBest.setOnClickListener(new View.OnClickListener()
        {
        	@Override
			public void onClick(View v)
        	{
        		int enteredYear = Integer.parseInt(m_txtMovieSearch.getText().toString());
        		Calendar begining = GetRange(enteredYear, false), last = GetRange(enteredYear, true);
        		
        		m_moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, 
        				new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID, MovieConnector.Constants.MOVIE_CINEMA_DATE_AS_STRING_COL}, 
        				"WHERE " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " >= " + begining.getTimeInMillis() + " AND " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " <= " + last.getTimeInMillis() + " AND " + MovieConnector.Constants.RATING_COL + " > 0", 
        				null, 
        				"ORDER BY " + MovieConnector.Constants.RATING_COL + " DESC");
        		
        		ListAdapter monthAdapter = new SimpleCursorAdapter(YearSearch.this, 
        				R.layout.cursorrow, 
        				m_moviesCursor, 
        				new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID, MovieConnector.Constants.RATING_COL},
        				new int[] {R.id.rowMovieName, R.id.rowMovieID, R.id.rowMovieDetail});
        		
        		m_lstResults.setAdapter(monthAdapter);
        		
        		Toast.makeText(YearSearch.this, m_moviesCursor.getCount() + " Total", Toast.LENGTH_SHORT).show();
        	}
		});
        
        //Menu Item Click Listener
        m_lstResults.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				final Intent returningIntent = new Intent();
				TextView idField = (TextView)arg1.findViewById(R.id.rowMovieID);
				returningIntent.putExtra("SelectedID", Integer.parseInt(idField.getText().toString()));
				YearSearch.this.setResult(ActivityCodes.RESPONSE_OK, returningIntent);
				finish();
			}
		});
    }
	@Override
    public void onDestroy()
    {
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
    			YearSearch.this.setResult(ActivityCodes.RESPONSE_CANCEL);
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
			YearSearch.this.setResult(ActivityCodes.RESPONSE_CANCEL);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private Calendar GetRange(int year, boolean LastDay)
	{
		if(LastDay)
		{
			Calendar lastDay = Calendar.getInstance();
			lastDay.set(Calendar.HOUR_OF_DAY, 0);
			lastDay.set(Calendar.MINUTE, 0);
			lastDay.set(Calendar.SECOND, 0);
			lastDay.set(Calendar.MILLISECOND, 0);
			lastDay.set(Calendar.MONTH, 11);
			lastDay.set(Calendar.DAY_OF_MONTH, 1);
			lastDay.set(Calendar.DATE, 1);
			lastDay.set(Calendar.YEAR, year);
			return lastDay;
		}
		else
		{
			Calendar firstDay = Calendar.getInstance();
			firstDay.set(Calendar.HOUR_OF_DAY, 0);
			firstDay.set(Calendar.MINUTE, 0);
			firstDay.set(Calendar.SECOND, 0);
			firstDay.set(Calendar.MILLISECOND, 0);
			firstDay.set(Calendar.MONTH, 0);
			firstDay.set(Calendar.DAY_OF_MONTH, 1);
			firstDay.set(Calendar.DATE, 1);
			firstDay.set(Calendar.YEAR, year);
			return firstDay;
		}
	}
	//A Disclaimer Screen
    private void setUpDisclaimer()
    {
    	new AlertDialog.Builder(YearSearch.this).setTitle("About")
		.setMessage("Icons Provided By The \"Axialis Team\"\n\nhttp://www.axialis.com/free/icons/").setNeutralButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//This will close on its own
			}
		}).show();
    }
  //A Disclaimer Screen
    private void setUpErrorScreen(String msg)
    {
    	new AlertDialog.Builder(YearSearch.this).setTitle("Error")
		.setMessage(msg).setNeutralButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//This will close on its own
			}
		}).show();
    }
}
