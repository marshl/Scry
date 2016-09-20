package com.example.scry;

import android.app.Activity;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SetListSpinnerAdapter implements SpinnerAdapter {
    private Activity context;

    public SetListSpinnerAdapter(Activity _context) {
        this.context = _context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getCount() {
        return ScryApplication.instance.setList.size() + 1;
    }

    @Override
    public Object getItem(int _position) {
        return _position == 0 ? null : ScryApplication.instance.setList.get(_position - 1);
    }

    @Override
    public long getItemId(int _position) {
        return _position;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        return this.getDropDownView(_position, _convertView, _parent);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public View getDropDownView(int _position, View _convertView,
                                ViewGroup _parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();

        if (_convertView == null) {
            _convertView = inflater.inflate(R.layout.search_setlist_item, _parent, false);
        }
        _convertView.setId(_position);

        TextView textView = (TextView) _convertView.findViewById(R.id.search_setlist_textview);

        ScrySet set = (ScrySet) this.getItem(_position);
        if (set == null) {
            textView.setText("No Set Selected");
        } else {
            textView.setText(set.name);
        }

        return _convertView;
    }

}
