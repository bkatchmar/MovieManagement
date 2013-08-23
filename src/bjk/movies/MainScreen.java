package bjk.movies;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;

public class MainScreen extends Activity
{
	Calendar cDateAndTime = Calendar.getInstance();
	Cursor moviesCursor;
	
	TextView dateLabel;
	EditText txtMovieName;
	CheckBox chkWantDVD, chkHaveDVD;
	Button btnAdd;
	Spinner ddlRatings;
	
	private String[] INSTANCES = new String[] { MovieConnector.Constants._ID };
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //On Create, we set the calendar to the current one
        cDateAndTime = Calendar.getInstance();
        
        //Set Cursor initially
        moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, INSTANCES, "", null, "ORDER BY " + MovieConnector.Constants.MOVIE_NAME_COL + " ASC");
        
        //Set Widget Objects
        dateLabel = (TextView)findViewById(R.id.lblMovieDateAndTime);
        txtMovieName = (EditText)findViewById(R.id.txtMovieName);
        chkWantDVD = (CheckBox)findViewById(R.id.chkWantDVD);
        chkHaveDVD = (CheckBox)findViewById(R.id.chkHaveDVD);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        ddlRatings = (Spinner)findViewById(R.id.ddlRatings);
        
        //This one we don't need to store as something at class level
        Button btnSetDate = (Button)findViewById(R.id.btnSetDate);
        
        //Sets listeners
        btnSetDate.setOnClickListener(new View.OnClickListener()
        {
        	@Override
			public void onClick(View v)
        	{
        		new DatePickerDialog(MainScreen.this, 
        				dateSetListener, 
        				cDateAndTime.get(Calendar.YEAR), 
        				cDateAndTime.get(Calendar.MONTH), 
        				cDateAndTime.get(Calendar.DAY_OF_MONTH)).show();
        	}
		});
        btnAdd.setOnClickListener(new View.OnClickListener()
        {
        	@Override
			public void onClick(View v)
        	{
        		BK_Movies movieEntered = buildMovieBasedOnScreen();
        		HandleAdd(movieEntered);
        		
        		Toast.makeText(MainScreen.this, movieEntered.getMovieName() + " has been entered", Toast.LENGTH_SHORT).show();
        	}
		});
        
        //Build Screen As Empty Object
        updateScreen();
        updateDateLabel(savedInstanceState);
    }
    @Override
    protected void onSaveInstanceState(Bundle outstate)
    {
    	super.onSaveInstanceState(outstate);
    	outstate.putLong("SetDate", cDateAndTime.getTimeInMillis());
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
    	new MenuInflater(getApplication()).inflate(R.menu.moviemenu, menu);
    	return(super.onCreateOptionsMenu(menu));
    }
    @Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{
    		case R.id.close:
    			finish();
    			return true;
    		
    		case R.id.search:
    			setUpSearch();
    			return true;
    			
    		case R.id.yearsearch:
    			setUpYearSearch();
    			return true;
    			
    		case R.id.dvd:
    			setUpDVD();
    			return true;
    			
    		case R.id.about:
    			setUpDisclaimer();
    			return true;
    			
    		case R.id.monthsearch:
    			setUpMonthlyCalendar();
    			return true;
    	}
    	return(super.onOptionsItemSelected(item));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if(resultCode == ActivityCodes.RESPONSE_CANCEL)
    	{
    		updateScreen();
    		updateDateLabel();
    	}
    	else
    	{
    		int ReturnedID = data.getExtras().getInt("SelectedID");
			moviesCursor = managedQuery(MovieConnector.Constants.CONTENT_URI, INSTANCES, "WHERE _id = " + ReturnedID, null, "ORDER BY " + MovieConnector.Constants.MOVIE_NAME_COL + " ASC");
			BK_Movies selectedMovie = new BK_Movies(moviesCursor);
			updateScreen(selectedMovie);
    	}
    }
    
    //Define Date Picket Dialog
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
    {
    	@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    	{
    		cDateAndTime.set(Calendar.HOUR_OF_DAY, 0);
    		cDateAndTime.set(Calendar.MINUTE, 0);
			cDateAndTime.set(Calendar.SECOND, 0);
			cDateAndTime.set(Calendar.MILLISECOND, 0);
    		cDateAndTime.set(Calendar.YEAR, year);
    		cDateAndTime.set(Calendar.MONTH, monthOfYear);
    		cDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    		updateDateLabel(null);
		}
    };
    
    //Activities' Private Methods
    //Build Screen As Empty Object
    private void updateScreen()
    {
    	txtMovieName.setText("");
        chkWantDVD.setChecked(false);
        chkHaveDVD.setChecked(false);
        
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, 
        		GetAllPossibleScores());
        
        
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddlRatings.setAdapter(aa);
    }
    //Updates Screen Based On Object
    private void updateScreen(BK_Movies fillInWithThis)
    {
    	txtMovieName.setText(fillInWithThis.getMovieName());
        chkWantDVD.setChecked(fillInWithThis.getIfWantDVD());
        chkHaveDVD.setChecked(fillInWithThis.getIfHaveDVD());
        
        cDateAndTime = Calendar.getInstance();
        cDateAndTime.setTime(new Date(fillInWithThis.getRelaseDate()));
    	DateFormat dfLabelTimeFormat = DateFormat.getDateInstance();
		dateLabel.setText(dfLabelTimeFormat.format(cDateAndTime.getTime()));
        
        ArrayList<String> possibleScores = GetAllPossibleScores();
        
        for(int i = 0; i < possibleScores.size(); i++)
        	if(Double.parseDouble(possibleScores.get(i)) == fillInWithThis.getRating())
        		ddlRatings.setSelection(i);
    }
    //Updates the data label based on what the user has entered
    private void updateDateLabel()
    {
    	cDateAndTime = Calendar.getInstance();
    	DateFormat dfLabelTimeFormat = DateFormat.getDateInstance();
		dateLabel.setText(dfLabelTimeFormat.format(cDateAndTime.getTime()));
    }
    private void updateDateLabel(Bundle state)
    {
    	if(state == null)
    	{
    		DateFormat dfLabelTimeFormat = DateFormat.getDateInstance();
    		dateLabel.setText(dfLabelTimeFormat.format(cDateAndTime.getTime()));
    	}
    	else
    	{
    		if(state.containsKey("SetDate"))
    		{
    			long savedDate = state.getLong("SetDate");
    			java.util.Date loadedDate = new java.util.Date(savedDate);
    			cDateAndTime.setTime(loadedDate);
    			
    			DateFormat dfLabelTimeFormat = DateFormat.getDateInstance();
        		dateLabel.setText(dfLabelTimeFormat.format(cDateAndTime.getTime()));
    		}
    		else
    		{
    			DateFormat dfLabelTimeFormat = DateFormat.getDateInstance();
        		dateLabel.setText(dfLabelTimeFormat.format(cDateAndTime.getTime()));
    		}
    	}
    }
    //Returns a list of possible scores for the score drop down to use
    private ArrayList<String> GetAllPossibleScores()
    {
        ArrayList<String> possibleScores = new ArrayList<String>();
    	
    	try
    	{
    		XmlPullParser xpp = getResources().getXml(R.xml.scores);
    		
    		while(xpp.getEventType() != XmlPullParser.END_DOCUMENT)
    		{
    			if(xpp.getEventType() == XmlPullParser.START_TAG)
    				if(xpp.getName().equals("score"))
    					possibleScores.add(xpp.getAttributeValue(0));
    			
    			xpp.next();
    		}
    	}
    	catch(Throwable t)
    	{
    		//For some reason, it reaches here because an exception was thrown
    		//So we'll build a 0.0-1.0 array
    		String[] scores = { "0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0" };
    		
    		for(int i = 0; i < scores.length; i++)
    			possibleScores.add(scores[i]);
    	}
    	
    	return possibleScores;
    }
    //Builds a new instance of BK_Movies based on what the user has entered
    private BK_Movies buildMovieBasedOnScreen()
    {
    	BK_Movies movieEntered = new BK_Movies();
    	
    	movieEntered.setMovieName(txtMovieName.getText().toString());
    	movieEntered.setRelaseDate(cDateAndTime.getTimeInMillis());
    	movieEntered.setIfWantDVD(chkWantDVD.isChecked());
    	movieEntered.setIfHaveDVD(chkHaveDVD.isChecked());
    	movieEntered.setRating(Double.parseDouble(ddlRatings.getSelectedItem().toString()));
    	
    	return movieEntered;
    }
    //Enter New Movie Into DB
    private void HandleAdd(BK_Movies movieToAdd)
    {
    	if(movieToAdd.getMovieStrID().length() > 0)
    	{
    		ContentValues values=new ContentValues(7);
        	
        	int WantDVD, HaveDVD;
        	
        	WantDVD = ((movieToAdd.getIfWantDVD()) ? 1 : 0);
        	HaveDVD = ((movieToAdd.getIfHaveDVD()) ? 1 : 0);
        	
        	values.put(MovieConnector.Constants.MOVIE_STRING_ID, movieToAdd.getMovieStrID());
    		values.put(MovieConnector.Constants.MOVIE_NAME_COL, movieToAdd.getMovieName());
    		values.put(MovieConnector.Constants.MOVIE_CINEMA_DATE_COL, cDateAndTime.getTimeInMillis());
    		values.put(MovieConnector.Constants.WANT_DVD_COL, WantDVD);
    		values.put(MovieConnector.Constants.HAVE_DVD_COL, HaveDVD);
    		values.put(MovieConnector.Constants.RATING_COL, movieToAdd.getRating());
    		values.put(MovieConnector.Constants.MOVIE_CINEMA_DATE_AS_STRING_COL, dateLabel.getText().toString());
    		
    		getContentResolver().insert(MovieConnector.Constants.CONTENT_URI, values);
    		moviesCursor.requery();
    	}
    }
    //Sets up monthly calendar view
    private void setUpMonthlyCalendar()
    {
    	final Intent goToMonthlyView = new Intent();
		goToMonthlyView.setClass(this, MovieMonthSearch.class);
		startActivityForResult(goToMonthlyView, ActivityCodes.REQUEST_STANDARD);
    }
    //Sets up search view
    private void setUpSearch()
    {
    	final Intent goToSearchView = new Intent();
    	goToSearchView.setClass(this, SearchScreen.class);
		startActivityForResult(goToSearchView, ActivityCodes.REQUEST_STANDARD);
    }
    //Year Search
    private void setUpYearSearch()
    {
    	//YearSearch
    	final Intent goToSearchYearView = new Intent();
    	goToSearchYearView.setClass(this, YearSearch.class);
		startActivityForResult(goToSearchYearView, ActivityCodes.REQUEST_STANDARD);
    }
    //DVDs To Get Screen
    private void setUpDVD()
    {
    	final Intent goToVideoView = new Intent();
    	goToVideoView.setClass(this, HomeVideo.class);
		startActivityForResult(goToVideoView, ActivityCodes.REQUEST_STANDARD);
    }
    //A Disclaimer Screen
    private void setUpDisclaimer()
    {
    	new AlertDialog.Builder(MainScreen.this).setTitle("About")
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