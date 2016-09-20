package com.example.scry;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SetListItemSelectedListener implements OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> _parent, View _view, int _position,
                               long _id) {
        ScryApplication.instance.selectedSetIndex = _position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> _parent) {
        ScryApplication.instance.selectedSetIndex = 0;
    }

}
