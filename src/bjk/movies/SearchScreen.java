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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SearchScreen extends Activity
{
	Button m_btnSearch;
	EditText m_txtMovieSearch;
	Cursor m_moviesCursor;
	ListView m_lstResults;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.searchscreen);
        
        //Set Cursor initially
        m_moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID}, "", null, "ORDER BY " + MovieConnector.Constants.MOVIE_NAME_COL + " ASC");
        
        //Set Up Objects
        m_btnSearch = (Button)findViewById(R.id.btnSearch);
        m_txtMovieSearch = (EditText)findViewById(R.id.txtMovieSearch);
        m_lstResults = (ListView)findViewById(R.id.lstResults);
        
        m_btnSearch.setOnClickListener(new View.OnClickListener()
        {
        	@Override
			public void onClick(View v)
        	{
        		HandleSearch();
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
				SearchScreen.this.setResult(ActivityCodes.RESPONSE_OK, returningIntent);
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
    			SearchScreen.this.setResult(ActivityCodes.RESPONSE_CANCEL);
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
			SearchScreen.this.setResult(ActivityCodes.RESPONSE_CANCEL);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//Private Methods
	private void HandleSearch()
	{
		String searchTerm = m_txtMovieSearch.getText().toString();
		
		if(searchTerm.trim().length() > 0)
		{
			BK_Movies searchTermMovie = new BK_Movies();
			searchTermMovie.setMovieName(searchTerm);
			m_moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID}, "WHERE " + MovieConnector.Constants.MOVIE_NAME_COL + " LIKE '%" + searchTermMovie.getMovieStrID() + "%'", null, "ORDER BY " + MovieConnector.Constants.MOVIE_NAME_COL + " ASC");
			
			ListAdapter searchAdapter = new SimpleCursorAdapter(this, 
					R.layout.cursorrow, 
					m_moviesCursor, 
					new String[] {MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants._ID},
					new int[] {R.id.rowMovieName, R.id.rowMovieID});
			
			m_lstResults.setAdapter(searchAdapter);
		}
		else
		{
			new AlertDialog.Builder(SearchScreen.this).setTitle("Alert")
			.setMessage("Serch Term Must Have At Least One Character").setNeutralButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//This will close on its own
				}
			}).show();
		}
	}
	//A Disclaimer Screen
    private void setUpDisclaimer()
    {
    	new AlertDialog.Builder(SearchScreen.this).setTitle("About")
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