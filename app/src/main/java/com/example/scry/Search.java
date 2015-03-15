package com.example.scry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.scry.ScryApplication.COLOUR;
import com.example.scry.ScryApplication.OWNERSHIP;
import com.example.scry.ScryApplication.TYPE;
import com.example.scry.ScryApplication.ToggleField;

import java.util.ArrayList;
import java.util.Map.Entry;

public class Search extends Activity
{
	public static final float IMAGEBUTTON_FADE = 0.25f;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_search );

		if ( ScryApplication.instance == null )
		{
			ScryApplication.instance = new ScryApplication();
		
			ScryXmlParser parser = new ScryXmlParser();
			try
			{
				parser.parseSetListXml( this );
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			Thread thread = new Thread( new LoadThread( this ) );
			thread.start();
		}
		
		Spinner setSpinner = (Spinner)this.findViewById( R.id.search_set_spinner );
		setSpinner.setAdapter( new SetListSpinnerAdapter( this ) );
		setSpinner.setOnItemSelectedListener( new SetListItemSelectedListener() );
		
		// Start searching when the user presses Go in the text field
		EditText editText = (EditText) findViewById( R.id.editText1 );
		editText.setOnEditorActionListener( new OnEditorActionListener()
		{
		    @Override
		    public boolean onEditorAction( TextView v, int actionId, KeyEvent event)
		    {
		        boolean handled = false;
		        if ( actionId == EditorInfo.IME_ACTION_SEND )
		        {
		        	onSearchButtonDown( null );
		        	handled = true;
		        }
		        return handled;
		    }
		} );
		
		OnLongClickListener longClickListener = new OnLongClickListener()
		{
			@Override
			public boolean onLongClick( View _v )
			{
				toggleButtonLongClick( _v );
				return true;
			}
		};

        for ( int i = 0; i < ScryApplication.instance.toggleButtonMap.size(); ++i )
		{
            int key = ScryApplication.instance.toggleButtonMap.keyAt(i);
			ImageButton imageButton = (ImageButton)this.findViewById( key );
			imageButton.setOnLongClickListener( longClickListener );
		}
		
		this.applySearchSettings();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.search, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if ( id == R.id.action_settings )
		{
			return true;
		}
		return super.onOptionsItemSelected( item );
	}
	
	public void onSearchButtonDown( View _view )
	{
		if ( !ScryApplication.instance.isInitialised )
		{
			return;
		}
		
		EditText editText = (EditText) this.findViewById( R.id.editText1 );
		ScryApplication.instance.searchString = editText.getText().toString();

		final CheckBox nameCheckbox = (CheckBox)this.findViewById( R.id.search_name_checkbox );
		ScryApplication.instance.isSearchingName = nameCheckbox.isChecked();
		
		final CheckBox typeCheckbox = (CheckBox)this.findViewById( R.id.search_types_checkbox );
		ScryApplication.instance.isSearchingType = typeCheckbox.isChecked();
		
		final CheckBox rulesCheckbox = (CheckBox)this.findViewById( R.id.search_rules_checkbox );
		ScryApplication.instance.isSearchingRules = rulesCheckbox.isChecked();
		
		final CheckBox excludeUnselectedCheckbox = (CheckBox)this.findViewById( R.id.search_exclude_colour_checkbox );
		ScryApplication.instance.excludeUnselectedColours = excludeUnselectedCheckbox.isChecked();
		
		final CheckBox matchExactlyCheckbox = (CheckBox)this.findViewById( R.id.search_match_colours_checkbox );
		ScryApplication.instance.matchColoursExactly = matchExactlyCheckbox.isChecked();
		
		final CheckBox multiOnlyCheckbox = (CheckBox)this.findViewById( R.id.search_multi_only_checkbox );
		ScryApplication.instance.multicolouredOnly = multiOnlyCheckbox.isChecked();
		
		//ScryApplication.instance.myCardsOnly = ((CheckBox)this.findViewById( R.id.search_mine_only_checkbox) ).isChecked();
		
		Intent intent = new Intent( this, Results.class );
		startActivity( intent );
	}
	
	public void onResetButtonDown( View _view )
	{
		ScryApplication.instance.Reset();
		this.applySearchSettings();
	}

	public void onSearchToggleButtonDown( View _view )
	{
		ToggleField field = ScryApplication.instance.toggleButtonMap.get( _view.getId() );
		field.bool = !field.bool;
		_view.setAlpha( field.bool ? 1.0f : IMAGEBUTTON_FADE );
	}
	
	private void applySearchSettings()
	{
		final EditText editText = (EditText) this.findViewById( R.id.editText1 );
		editText.setText( ScryApplication.instance.searchString );

		final CheckBox nameCheckbox = (CheckBox)this.findViewById( R.id.search_name_checkbox );
		nameCheckbox.setChecked( ScryApplication.instance.isSearchingName );
		
		final CheckBox typeCheckbox = (CheckBox)this.findViewById( R.id.search_types_checkbox );
		typeCheckbox.setChecked( ScryApplication.instance.isSearchingType );
		
		final CheckBox rulesCheckbox = (CheckBox)this.findViewById( R.id.search_rules_checkbox );
		rulesCheckbox.setChecked( ScryApplication.instance.isSearchingRules );
		
		final CheckBox excludeUnselectedCheckbox = (CheckBox)this.findViewById( R.id.search_exclude_colour_checkbox );
		excludeUnselectedCheckbox.setChecked( ScryApplication.instance.excludeUnselectedColours );
		
		final CheckBox matchExactlyCheckbox = (CheckBox)this.findViewById( R.id.search_match_colours_checkbox );
		matchExactlyCheckbox.setChecked( ScryApplication.instance.matchColoursExactly );
		
		final CheckBox multiOnlyCheckbox = (CheckBox)this.findViewById( R.id.search_multi_only_checkbox );
		multiOnlyCheckbox.setChecked( ScryApplication.instance.multicolouredOnly );
	
		OWNERSHIP ownership = ScryApplication.instance.ownershipSearch;
		final RadioButton allRadio = (RadioButton)this.findViewById( R.id.search_ownership_radio_all );
		final RadioButton ownedRadio = (RadioButton)this.findViewById( R.id.search_ownership_radio_owned );
		final RadioButton notOwnedRadio = (RadioButton)this.findViewById( R.id.search_ownership_radio_not_owned );
		allRadio.setChecked( ownership == OWNERSHIP.ALL );
		ownedRadio.setChecked( ownership == OWNERSHIP.OWNED );
		notOwnedRadio.setChecked( ownership == OWNERSHIP.NOT_OWNED );

        for ( int i = 0; i < ScryApplication.instance.toggleButtonMap.size(); ++i )
        {
            int key = ScryApplication.instance.toggleButtonMap.keyAt(i);
            ToggleField value = ScryApplication.instance.toggleButtonMap.valueAt(i);
			ImageButton imageButton = (ImageButton)this.findViewById( key );
			imageButton.setAlpha( value.bool ? 1.0f : 0.5f );
		}
		
		final Spinner setSpinner = (Spinner)this.findViewById( R.id.search_set_spinner );
		setSpinner.setSelection( ScryApplication.instance.selectedSetIndex );
		setSpinner.invalidate();
	}
	
	public boolean toggleButtonLongClick( View _view )
	{
		int id = _view.getId();
		
		ArrayList<ToggleField> otherFields = new ArrayList<ToggleField>();
		//Toggle the types if it is a type
		if ( id == R.id.type_creature_imagebutton
		  || id == R.id.type_artifact_imagebutton
		  || id == R.id.type_enchantment_imagebutton
		  || id == R.id.type_instant_imagebutton
		  || id == R.id.type_sorcery_imagebutton
		  || id == R.id.type_land_imagebutton
		  || id == R.id.type_planeswalker_imagebutton )
		{
			for ( Entry<TYPE, ToggleField> entry : ScryApplication.instance.typeSearchFlags.entrySet() )
			{
				if ( entry.getValue().resourceID != id )
				{
					otherFields.add( entry.getValue() );
				}
			}
		}
		// Otherwise do the colours
		else if ( id == R.id.ib_colour_w 
			   || id == R.id.ib_colour_u
			   || id == R.id.ib_colour_b
			   || id == R.id.ib_colour_r
			   || id == R.id.ib_colour_g
			   || id == R.id.ib_colour_c )
		{
			for ( Entry<COLOUR, ToggleField> entry : ScryApplication.instance.colourSearchFlags.entrySet() )
			{
				if ( entry.getValue().resourceID != id )
				{
					otherFields.add( entry.getValue() );
				}
			}
		}
		else
		{
			return false;
		}
		
		boolean allEnabled = true;
		
		for ( ToggleField field : otherFields )
		{
			if ( !field.bool )
			{
				allEnabled = false;
				break;
			}
		}
	
		for ( ToggleField field : otherFields )
		{
			field.bool = !allEnabled;
			ImageButton imageButton = (ImageButton)this.findViewById( field.resourceID );
			imageButton.setAlpha( field.bool ? 1.0f : IMAGEBUTTON_FADE );
		}
		
		return true;
	}
	
	public void onOwnershipRadioClick( View _view )
	{
	    boolean checked = ((RadioButton)_view).isChecked();
	    
	    switch ( _view.getId() )
	    {
	        case R.id.search_ownership_radio_all:
	        	if ( checked )
	        	{
	        		ScryApplication.instance.ownershipSearch = OWNERSHIP.ALL;
	        	}
	            break;
	        case R.id.search_ownership_radio_owned:
	        	if ( checked )
	        	{
	        		ScryApplication.instance.ownershipSearch = OWNERSHIP.OWNED;
	        	}
	        	break;
	        case R.id.search_ownership_radio_not_owned:
	        	if ( checked )
	        	{
	        		ScryApplication.instance.ownershipSearch = OWNERSHIP.NOT_OWNED;
	        	}
	            break;
	    }
	}
}
