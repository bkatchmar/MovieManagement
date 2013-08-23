package bjk.movies;

import android.database.Cursor;

public class BK_Movies
{
	//*****************
	//Private Variables
	private String m_MovieName, m_MovieStrID;
    private long m_ReleaseDateAsLong;
    private boolean m_WantDVD, m_HaveDVD;
    private double m_rating;
    //Private Variables
    //*****************
    
    //************
	//Constructor
    public BK_Movies()
    {
    	populateEmptyObject();
    }
    public BK_Movies(Cursor result)
    {
    	//First make sure the Cursor object ain't null for whatever reason
		if(result != null)
		{
			if(result.getCount() > 0)
				populateObjectFromCursor(result);
			else
				populateEmptyObject();
		}
		else
			populateEmptyObject();
    }
    //Constructor
    //************
    
    //***************
    //Private Methods
    private void populateEmptyObject()
    {
        m_MovieName = "";
        m_MovieStrID = "";
        m_ReleaseDateAsLong = 0;
        m_WantDVD = false;
        m_HaveDVD = false;
        m_rating = 0.0;
    }
    private void populateObjectFromCursor(Cursor filler)
    {
    	int nameCol = filler.getColumnIndex(MovieConnector.Constants.MOVIE_NAME_COL);
    	int dateCol = filler.getColumnIndex(MovieConnector.Constants.MOVIE_CINEMA_DATE_COL);
    	int wantDvdCol = filler.getColumnIndex(MovieConnector.Constants.WANT_DVD_COL);
    	int haveDvdCol = filler.getColumnIndex(MovieConnector.Constants.HAVE_DVD_COL);
    	int ratingCol = filler.getColumnIndex(MovieConnector.Constants.RATING_COL);
    	int strID = filler.getColumnIndex(MovieConnector.Constants.MOVIE_CINEMA_DATE_AS_STRING_COL);
        
    	filler.moveToFirst();
    	
    	m_MovieName = filler.getString(nameCol);
    	m_ReleaseDateAsLong = filler.getLong(dateCol);
    	m_WantDVD = (filler.getInt(wantDvdCol) == 1) ? true : false;
    	m_HaveDVD = (filler.getInt(haveDvdCol) == 1) ? true : false;
    	m_rating = filler.getDouble(ratingCol);
    	m_MovieStrID = filler.getString(strID);;
    }
    private void buildID()
    {
        char[] nameAsCharArr = m_MovieName.toCharArray();
        m_MovieStrID = "";
        
        for(int i = 0; i < nameAsCharArr.length; i++)
        {
            if(nameAsCharArr[i] != ' ' && nameAsCharArr[i] != ':' && nameAsCharArr[i] != '(' && nameAsCharArr[i] != ')' && nameAsCharArr[i] != '\'')
            	m_MovieStrID += nameAsCharArr[i];
        }
        
        m_MovieStrID = m_MovieStrID.toUpperCase();
    }
    //Private Methods
    //***************
    
    //*********
    //Accessors
    public String getMovieStrID() { return m_MovieStrID; }
    public String getMovieName() { return m_MovieName; }
    public long getRelaseDate() { return m_ReleaseDateAsLong; }
    public boolean getIfWantDVD() { return m_WantDVD; }
    public boolean getIfHaveDVD() { return m_HaveDVD; }
    public double getRating() { return m_rating; }
    //Accessors
    //*********
    
    //*********
    //Mutators
    public void setMovieName(String name)
    { 
    	m_MovieName = name;
    	buildID();
	}
    public void setRelaseDate(long DateAsLong) { m_ReleaseDateAsLong = DateAsLong; }
    public void setIfWantDVD(boolean WantDVD) { m_WantDVD = WantDVD; }
    public void setIfHaveDVD(boolean HaveDVD) { m_HaveDVD = HaveDVD; }
    public void setRating(double Rating) { m_rating = Rating; }
    //Mutators
    //*********
}