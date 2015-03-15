package com.example.scry;

import android.os.AsyncTask;

public class SearchThread extends AsyncTask<Object, Object, Object>
{
	@Override
	protected Object doInBackground( Object... params )
	{
		ScryApplication.instance.Search();
		
		return params[0];
	}
	
	protected void onPostExecute( Object result )
	{
		((Results)result).onSearchComplete();
    }

}
