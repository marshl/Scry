package com.example.scry;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class Results extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        new SearchThread().execute((Object) this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        getMenuInflater().inflate(R.menu.results, _menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSearchComplete() {
        TextView infoView = (TextView) this.findViewById(R.id.results_search_info);
        StringBuilder strBldr = new StringBuilder();
        int size = ScryApplication.instance.searchResults.size();
        strBldr.append(size).append(
                size == 1 ? " card found" : " cards found");
        infoView.setText(strBldr.toString());

        View view = this.findViewById(R.id.results_display_layout);
        view.setVisibility(View.VISIBLE);

        view = this.findViewById(R.id.results_progress_bar);
        view.setVisibility(View.GONE);

        final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.results_expandable_list);

        final ResultsExpandableAdapter resultsExpandableAdapter = new ResultsExpandableAdapter(this);
        expListView.setAdapter(resultsExpandableAdapter);
    }
}
