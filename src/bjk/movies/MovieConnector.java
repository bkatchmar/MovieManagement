package bjk.movies;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public class MovieConnector extends ContentProvider
{
	private static final String DATABASE_NAME = "bkmovies.db";
	private static final int CONSTANTS = 1;
	private static final int CONSTANT_ID = 2;
	private static final UriMatcher MATCHER;
	private static HashMap<String, String> MOVIE_LIST_PROJECTION;
	
	private SQLiteDatabase movieDb;
	
	//**********************
	//Columns Constant Class
	public static final class Constants implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://bjk.movies.MovieConnector/bkmovies");
		public static final String DEFAULT_SORT_ORDER = "cinemadate";
		public static final String MOVIE_NAME_COL = "moviename";
		public static final String MOVIE_STRING_ID = "moviestrid";
		public static final String MOVIE_CINEMA_DATE_COL = "cinemadate";
		public static final String WANT_DVD_COL = "wantdvd";
		public static final String HAVE_DVD_COL = "havedvd";
		public static final String RATING_COL = "rating";
		public static final String MOVIE_CINEMA_DATE_AS_STRING_COL = "cinemadatestr";
	}
	//Columns Constant Class
	//**********************
	
	//******
	//Static
	static
	{
		MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
		MATCHER.addURI("bjk.movies.MovieConnector", "bkmovies", CONSTANTS);
		MATCHER.addURI("bjk.movies.MovieConnector", "bkmovies/#", CONSTANT_ID);

		MOVIE_LIST_PROJECTION=new HashMap<String, String>();
		MOVIE_LIST_PROJECTION.put(MovieConnector.Constants._ID, MovieConnector.Constants._ID);
		MOVIE_LIST_PROJECTION.put(MovieConnector.Constants.MOVIE_STRING_ID, MovieConnector.Constants.MOVIE_STRING_ID);
		MOVIE_LIST_PROJECTION.put(MovieConnector.Constants.MOVIE_NAME_COL, MovieConnector.Constants.MOVIE_NAME_COL);
		MOVIE_LIST_PROJECTION.put(MovieConnector.Constants.MOVIE_CINEMA_DATE_COL, MovieConnector.Constants.MOVIE_CINEMA_DATE_COL);
		MOVIE_LIST_PROJECTION.put(MovieConnector.Constants.WANT_DVD_COL, MovieConnector.Constants.WANT_DVD_COL);
		MOVIE_LIST_PROJECTION.put(MovieConnector.Constants.HAVE_DVD_COL, MovieConnector.Constants.HAVE_DVD_COL);
		MOVIE_LIST_PROJECTION.put(MovieConnector.Constants.RATING_COL, MovieConnector.Constants.RATING_COL);
		MOVIE_LIST_PROJECTION.put(MovieConnector.Constants.MOVIE_CINEMA_DATE_AS_STRING_COL, MovieConnector.Constants.MOVIE_CINEMA_DATE_AS_STRING_COL);
	}
	//Static
	//******
	
	//**********************
	//SQLiteOpenHelper Class
	private class MovieHelperDB extends SQLiteOpenHelper
	{
		public MovieHelperDB(Context context)
		{
			super(context, DATABASE_NAME, null, 1);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			Cursor c=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='bkmovies'", null);
			
			try
			{
				if (c.getCount()==0)
					db.execSQL("CREATE TABLE bkmovies (_id INTEGER PRIMARY KEY AUTOINCREMENT, moviename TEXT, cinemadate INTEGER, wantdvd INTEGER, havedvd INTEGER, rating REAL, cinemadatestr TEXT, moviestrid TEXT);");
			}
			finally
			{
				c.close();
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			//android.util.Log.w("Movies", "Upgrading database, which will destroy all old data");
			//db.execSQL("DROP TABLE IF EXISTS bkmovies");
			onCreate(db);
		}
	}
	//SQLiteOpenHelper Class
	//**********************
	
	//****************
	//Provider Methods
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getType(Uri uri)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) 
	{
		long rowID = 0;
		
		if (MATCHER.match(uri)!=CONSTANTS)
			throw new IllegalArgumentException("Unknown URL");
		
		if(initialValues.size() != 7)
			throw new IllegalArgumentException("Missing Arguments");
		
		//First check to see if row is already there
		Cursor c = movieDb.rawQuery("SELECT _id FROM bkmovies WHERE moviestrid = '" + initialValues.getAsString(Constants.MOVIE_STRING_ID) + "'", null);
		
		//If above query returns nothing
		if(c.getCount() == 0)
		{
			//We Insert A New Record
			rowID = movieDb.insert("bkmovies", "moviename", initialValues);
			
			if (rowID > 0)
			{
				Uri nUri=ContentUris.withAppendedId(Constants.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(nUri, null);
				return nUri;
			}
			else
				throw new SQLException("Failed to insert row");
		}
		else
		{
			//Otherwise, run an update
			c.moveToFirst();
			int idCol = c.getColumnIndex(Constants._ID);
			
			//We Update An Existing Record
			rowID = movieDb.update("bkmovies", initialValues, "_id = '" + c.getInt(idCol) + "'", null);
			
			Uri nUri=ContentUris.withAppendedId(Constants.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(nUri, null);
			return nUri;
		}
	}
	@Override
	public boolean onCreate()
	{
		movieDb=(new MovieHelperDB(getContext())).getWritableDatabase();
		return (movieDb == null) ? false : true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, 
			String selection, String[] selectionArgs, String sortOrder)
	{
		Cursor c = movieDb.rawQuery("SELECT _id, moviename, cinemadate, wantdvd, havedvd, rating, cinemadatestr, moviestrid FROM bkmovies " + selection + " " + sortOrder, null);
		return c;
		 
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	//Provider Methods
	//****************
}