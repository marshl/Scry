package com.example.scry;

import java.util.ArrayList;

public class OracleCard implements Comparable<Object> 
{
	public int id;
	public String name;
	
	public String cost = null;
	public int cmc = 0;
	public int colour = 0;
	public int numColours = 0;
	public int colourID = 0;
	
	public String type = null;
	public String subtype = null;
	public String power = null;
	public String toughness = null;
	public Float numtoughness = null;
	public Float numpower = null;
	public String loyalty;
	
	public String rules = null;
	
	public int total = 0;
	public ArrayList<CardSet> sets = new ArrayList<CardSet>();
	public String watermark;
	public String handmod;
	public String lifemod;
	public String linkType;
	public Integer linkID = null;

	@Override
	public int compareTo( Object another )
	{
		return this.name.compareTo( ((OracleCard)another).name );
	}
	
	
	
}
