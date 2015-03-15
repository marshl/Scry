package com.example.scry;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.text.Html;

public class ScryImageGetter implements Html.ImageGetter
{
	public Context context;
	
	public ScryImageGetter( Context _context ) 
	{
		this.context = _context;
	}
	
	@Override
	public Drawable getDrawable( String _source )
	{
		final String symbol = _source.toLowerCase().replace( "/", "" );
		
		int image = this.context.getResources().getIdentifier( symbol, "drawable", "com.example.scry" );
		
		try
		{
			Drawable d = this.context.getResources().getDrawable( image );
			d.setBounds( 0, 0, d.getIntrinsicWidth(),
								d.getIntrinsicHeight() );
			return d;
		}
		catch ( NotFoundException _e )
		{
			return null;	
		}
	}

}
