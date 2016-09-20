package com.example.scry;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultsExpandableAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private ScryImageGetter scryImageGetter;
    private Pattern symbolPattern;

    public ResultsExpandableAdapter(Activity _context) {
        this.context = _context;
        this.scryImageGetter = new ScryImageGetter(_context);

        this.symbolPattern = Pattern.compile(Pattern.quote("{") + "(.+?)" + Pattern.quote("}"));
    }

    @Override
    public Object getChild(int _groupPosition, int _childPosition) {
        return ScryApplication.instance.searchResults.get(_groupPosition);
    }

    @Override
    public long getChildId(int _groupPosition, int _childPosition) {
        return _childPosition;
    }

    @Override
    public View getChildView(final int _groupPosition, final int _childPosition,
                             boolean _isLastChild, View _convertView, ViewGroup _parent) {
        OracleCard oracleCard = (OracleCard) getChild(_groupPosition, _childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (_convertView == null) {
            _convertView = inflater.inflate(R.layout.results_child, _parent, false);
        }
        _convertView.setId(_childPosition);

        TextView textView = null;
        StringBuilder strBldr = null;

        { // Rules
            textView = (TextView) _convertView.findViewById(R.id.results_child_rules);

            if (oracleCard.rules != null) {
                textView.setText(Html.fromHtml(replaceMTGSymbols(oracleCard.rules), this.scryImageGetter, null));
            }
            textView.setVisibility(oracleCard.rules != null ? View.VISIBLE : View.GONE);
        }

        { // Types
            textView = (TextView) _convertView.findViewById(R.id.results_child_types);
            strBldr = new StringBuilder(oracleCard.type);
            if (oracleCard.subtype != null) {
                strBldr.append(" - ").append(oracleCard.subtype);
            }
            textView.setText(strBldr.toString());
        }

        { // Power/Toughness/Loyalty/Handmod/Lifemod
            textView = (TextView) _convertView.findViewById(R.id.results_child_powtough);
            strBldr = null;
            if (oracleCard.loyalty != null) {
                strBldr = new StringBuilder(oracleCard.loyalty);
            } else if (oracleCard.power != null && oracleCard.toughness != null) {
                strBldr = new StringBuilder(oracleCard.power);
                strBldr.append('/').append(oracleCard.toughness);
            } else if (oracleCard.handmod != null && oracleCard.lifemod != null) {
                strBldr = new StringBuilder(oracleCard.handmod);
                strBldr.append("/").append(oracleCard.lifemod);
            }
            this.displayText(textView, strBldr != null ? strBldr.toString() : null);
        }

        { // Sets
            textView = (TextView) _convertView.findViewById(R.id.results_child_total);
            this.displayText(textView, oracleCard.total == 0 ? null : "Total: " + oracleCard.total);

            textView = (TextView) _convertView.findViewById(R.id.results_child_sets);
            strBldr = new StringBuilder();
            for (int i = 0; i < oracleCard.sets.size(); ++i) {
                CardSet set = oracleCard.sets.get(i);
                if (set.count > 0) {
                    strBldr.append(set.count).append("x");
                }
                strBldr.append("<img src=\"set_").append(set.setcode.toLowerCase())
                        .append('_').append(Character.toLowerCase(set.rarity)).append("\" />&nbsp;&nbsp;&nbsp;");
            }
            textView.setText(Html.fromHtml(strBldr.toString(), this.scryImageGetter, null));
        }

        { // Card link
            textView = (TextView) _convertView.findViewById(R.id.results_child_link);
            strBldr = null;
            if (oracleCard.linkID != null) {
                OracleCard linkedCard = ScryApplication.instance.cardMap.get(oracleCard.linkID);
                if (linkedCard != null) {
                    strBldr = new StringBuilder();
                    strBldr.append("The other half of ").append(oracleCard.name).append(" is ").append(linkedCard.name);
                }
            }
            this.displayText(textView, strBldr != null ? strBldr.toString() : null);
        }

        { // Watermark
            textView = (TextView) _convertView.findViewById(R.id.results_child_watermark);
            strBldr = null;
            if (oracleCard.watermark != null) {
                strBldr = new StringBuilder("Watermark: ").append(oracleCard.watermark);
            }
            this.displayText(textView, strBldr != null ? strBldr.toString() : null);
        }

        return _convertView;
    }

    public int getChildrenCount(int _groupPosition) {
        return 1;
    }

    public Object getGroup(int _groupPosition) {
        return ScryApplication.instance.searchResults.get(_groupPosition);
    }

    public int getGroupCount() {
        return ScryApplication.instance.searchResults.size();
    }

    public long getGroupId(int _groupPosition) {
        return _groupPosition;
    }

    public View getGroupView(int _groupPosition, boolean _isExpanded,
                             View _convertView, ViewGroup _parent) {
        OracleCard oracleCard = (OracleCard) this.getGroup(_groupPosition);
        if (_convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _convertView = infalInflater.inflate(R.layout.results_group,
                    _parent, false);
        }

        TextView textView = (TextView) _convertView.findViewById(R.id.results_group_cardname);
        textView.setText(oracleCard.name);

        textView = (TextView) _convertView.findViewById(R.id.results_group_cost);
        StringBuilder strBldr = new StringBuilder();
        if (oracleCard.cost != null) {
            strBldr.append(this.replaceMTGSymbols(oracleCard.cost));
        }
        //strBldr.append( " (" ).append( oracleCard.cmc ).append( ')' );
        textView.setText(Html.fromHtml(strBldr.toString(), this.scryImageGetter, null));
        //textView.setText( strBldr.toString() );
        return _convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void displayText(TextView _view, String _text) {
        _view.setVisibility(_text != null ? View.VISIBLE : View.GONE);
        if (_text != null) {
            _view.setText(_text);
        }
    }

    private String replaceMTGSymbols(String _str) {
        Matcher matcher = this.symbolPattern.matcher(_str);
        _str = matcher.replaceAll("<img src=\"sym_$1\" />");
        return _str;
    }
}
