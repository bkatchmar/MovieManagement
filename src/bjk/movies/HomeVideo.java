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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HomeVideo extends Activity
{
	Cursor m_moviesCursor;
	ListView m_lstResults;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.dvdscreen);
        
        //Set Cursor initially
        m_moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, 
        		new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID}, 
        		"WHERE " + MovieConnector.Constants.WANT_DVD_COL + " = 1 AND " + MovieConnector.Constants.HAVE_DVD_COL + " = 0", 
        		null, 
        		"ORDER BY " + MovieConnector.Constants.MOVIE_CINEMA_DATE_COL + " ASC");
        
        m_lstResults = (ListView)findViewById(R.id.lstResults);
        
        ListAdapter monthAdapter = new SimpleCursorAdapter(this, 
				R.layout.cursorrow, 
				m_moviesCursor, 
				new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID},
				new int[] {R.id.rowMovieName, R.id.rowMovieID});
		
        m_lstResults.setAdapter(monthAdapter);
        
        //Menu Item Click Listener
        m_lstResults.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				final Intent returningIntent = new Intent();
				TextView idField = (TextView)arg1.findViewById(R.id.rowMovieID);
				returningIntent.putExtra("SelectedID", Integer.parseInt(idField.getText().toString()));
				HomeVideo.this.setResult(ActivityCodes.RESPONSE_OK, returningIntent);
				finish();
			}
		});
    }
	@Override
    public void onDestroy()
    {
		m_moviesCursor.close();
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
    			HomeVideo.this.setResult(ActivityCodes.RESPONSE_CANCEL);
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
			HomeVideo.this.setResult(ActivityCodes.RESPONSE_CANCEL);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//A Disclaimer Screen
    private void setUpDisclaimer()
    {
    	new AlertDialog.Builder(HomeVideo.this).setTitle("About")
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