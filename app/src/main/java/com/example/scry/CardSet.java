package com.example.scry;

public class CardSet
{
	public String setcode;
	public char rarity;
	public int count;
	
	@Override
	public boolean equals( Object _other )
	{
        CardSet set = (CardSet)_other;
        if ( set == null )
        {
            return false;
        }
		return this.setcode.equals( ((CardSet)_other).setcode );
	}
}
