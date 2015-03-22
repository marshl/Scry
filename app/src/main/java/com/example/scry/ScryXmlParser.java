package com.example.scry;

import android.app.Activity;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ScryXmlParser
{
	public String ns = null;
	
	public void parseOracleXml( Activity _context ) throws Exception
	{
		InputStream fileStream = _context.getAssets().open( "xml/oracle.xml" );
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature( XmlPullParser.FEATURE_PROCESS_NAMESPACES, false );
		parser.setInput( fileStream, null );
		parser.nextTag();
		this.readOracle( parser );
		
		fileStream.close();
	}
	
	public void parseSetListXml( Activity _context ) throws Exception
	{
		InputStream fileStream = _context.getAssets().open( "xml/setlist.xml" );
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput( fileStream, null );
		parser.nextTag();
		this.readSets( parser );
	}

	private void readOracle( XmlPullParser _parser ) throws Exception
	{
		_parser.require( XmlPullParser.START_TAG, ns, "oracle" );

		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}

			String name = _parser.getName();
			
			if ( name.equals( "card" ) )
			{
				this.readCard( _parser );
			}
			else
			{
				this.skip( _parser );
			}
		}
	}

	private void readCard( XmlPullParser _parser ) throws Exception
	{
		OracleCard card = new OracleCard();
		
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}
			
			String name = _parser.getName();
			
			if ( name.equals( "id" ) )
			{
				card.id = Integer.parseInt( this.readText( _parser ) );
			}
			else if ( name.equals( "n" ) )
			{
				 card.name = this.readText( _parser );
			}
			else if ( name.equals( "cs" ) )
			{
				card.cost = this.readText( _parser );
			}
			else if ( name.equals( "cmc" ) )
			{
				card.cmc = Integer.parseInt( this.readText( _parser ) );
			}
			else if ( name.equals( "cl" ) )
			{
				card.colour = Integer.parseInt( this.readText( _parser ) );
			}
			else if ( name.equals( "nc" ) )
			{
				card.numColours = Integer.parseInt( this.readText( _parser ) );
			}
			else if ( name.equals( "t" ) )
			{
				card.type = this.readText( _parser );
			}
			else if ( name.equals( "st" ) )
			{
				card.subtype = this.readText( _parser );
			}
			else if ( name.equals( "pw" ) )
			{
				card.power = this.readText( _parser );
			}
			else if ( name.equals( "tf" ) )
			{
				card.toughness = this.readText( _parser );
			}
			else if ( name.equals( "npw" ) )
			{
				card.numpower = Integer.parseInt( this.readText( _parser ) );
			}
			else if ( name.equals( "ntf" ) )
			{
				card.numtoughness = Integer.parseInt( this.readText( _parser ) );
			}
			else if ( name.equals( "ly" ) )
			{
				card.loyalty = this.readText( _parser );
			}
			else if ( name.equals( "r" ) )
			{
				// Add newlines
				card.rules = this.readText( _parser ).replace( "~", "<br>" );;
			}
			else if ( name.equals( "sets" ) )
			{
				this.readCardSets( _parser, card );
			}
			else if ( name.equals( "wm" ) )
			{
				card.watermark = this.readText( _parser );
			}
			else if ( name.equals( "hm" ) )
			{
				card.handmod = this.readText( _parser );
			}
			else if ( name.equals( "lm") )
			{
				card.lifemod =  this.readText( _parser );
			}
			else if ( name.equals( "lt" ) )
			{
				card.linkType = this.readText( _parser );
			}
			else if ( name.equals( "lid") )
			{
				card.linkID = Integer.parseInt( this.readText( _parser ) );
			}
			else
			{
				this.skip( _parser );
			}
		}
		// Set up the numerical power/toughness from the string version if it does not exist
		// ( Numerical P/T is only included on cards with symbols in their string version )
		if ( card.toughness != null && card.numtoughness == null )
		{
			card.numtoughness = Integer.parseInt( card.toughness );
		}
		if ( card.power != null && card.numpower == null )
		{
			card.numpower = Integer.parseInt( card.power );
		}
		
		ScryApplication.instance.AddCard( card );
	}
	
	private void readCardSets( XmlPullParser _parser, OracleCard _card ) throws IOException, XmlPullParserException
	{
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getName().equals( "s" ) )
			{
				CardSet set = this.readCardSet( _parser, _card );
				if ( !_card.sets.contains( set ) )
				{
					_card.sets.add( set );
					_card.total += set.count;
				}
			}
			else
			{
				this.skip( _parser );
			}
		}
	}
	
	private CardSet readCardSet( XmlPullParser _parser, OracleCard _card ) throws IOException, XmlPullParserException
	{
		CardSet set = new CardSet();
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getName().equals( "r" ) )
			{
				set.rarity = this.readText( _parser ).charAt( 0 );
			}
			else if ( _parser.getName().equals( "cd" ) )
			{
				set.setcode = this.readText( _parser );
			}
			else if ( _parser.getName().equals( "c" ) )
			{
				set.count = Integer.parseInt( this.readText( _parser ) );
			}
			else
			{
				this.skip( _parser );
			}
		}
		return set;
	}
	
	private String readText( XmlPullParser _parser ) throws IOException, XmlPullParserException
	{
		String result = "";
		if ( _parser.next() == XmlPullParser.TEXT )
		{
			result = _parser.getText();
			_parser.nextTag();
		}
		return result;
	}

	private void skip( XmlPullParser _parser ) throws XmlPullParserException, IOException
	{
		if ( _parser.getEventType() != XmlPullParser.START_TAG )
		{
			throw new IllegalStateException();
		}
		
		int depth = 1;
		
		while ( depth != 0 )
		{
			switch ( _parser.next() )
			{
			case XmlPullParser.START_TAG:
				++depth;
				break;
			case XmlPullParser.END_TAG:
				--depth;
				break;
			}
		}
	}
	
	private void readSets( XmlPullParser _parser ) throws Exception
	{
		ScryApplication.instance.formatList = new ArrayList<ScryFormat>();
		ScryApplication.instance.setList = new ArrayList<ScrySet>();
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}

			String name = _parser.getName();
			
			if ( name.equals( "format" ) )
			{
				this.readFormat( _parser );
			}
			else
			{
				throw new Exception( "Unknown Xml element name \"" + name + "\"" );
			}
		}
	}
	
	private void readFormat( XmlPullParser _parser ) throws XmlPullParserException, IOException, Exception
	{
		ScryFormat format = new ScryFormat();
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}

			String name = _parser.getName();
			
			if ( name.equals( "name" ) )
			{
				format.name = this.readText( _parser );
			}
			else if ( name.equals( "set" ) )
			{
				ScrySet set = this.readFormatSet( _parser );
				format.sets.add( set );
			}
			else
			{
				throw new Exception( "Unknown Xml tag \"" + name + "\"" );
			}
		}
		
		for ( int i = format.sets.size() - 1; i >= 0; --i )
		//for ( ScrySet set : format.sets )
		{
			//ScryApplication.setList.add( set );
			ScryApplication.instance.setList.add( format.sets.get( i ) );
		}
		ScryApplication.instance.formatList.add( format );
	}
	
	private ScrySet readFormatSet( XmlPullParser _parser ) throws XmlPullParserException, IOException, Exception
	{
		ScrySet set = new ScrySet();
		while ( _parser.next() != XmlPullParser.END_TAG )
		{
			if ( _parser.getEventType() != XmlPullParser.START_TAG )
			{
				continue;
			}

			String name = _parser.getName();
			
			if ( name.equals( "name" ) )
			{
				set.name = this.readText( _parser );
			}
			else if ( name.equals( "code" ) )
			{
				set.setcode = this.readText( _parser );
			}
			else
			{
				throw new Exception( "Unknown Xml tag \"" + name + "\"" );
			}
		}
		
		return set;
	}
	
}
