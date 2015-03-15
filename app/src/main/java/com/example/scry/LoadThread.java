package com.example.scry;

public class LoadThread implements Runnable
{
	private Search context;
	
	public LoadThread( Search _context )
	{
		this.context = _context;
	}
	
	@Override
	public void run()
	{
		try
		{
			ScryXmlParser parser = new ScryXmlParser();
			parser.parseOracleXml( this.context );
			ScryApplication.instance.isInitialised = true;
		}
		catch ( Exception _e )
		{
			_e.printStackTrace();
		}
	}

}
