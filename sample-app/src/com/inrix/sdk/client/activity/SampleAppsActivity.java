package com.inrix.sdk.client.activity;

import com.inrix.sdk.client.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * The main activity of the Sample App Gallery.
 */
public final class SampleAppsActivity extends ListActivity {

    /**
     * A custom array adapter that shows a {@link SimpleView} containing details about the Sample Apps.
     */
    private static class CustomArrayAdapter extends ArrayAdapter<SampleAppDetails> {

        /**
         * @param demos An array containing the details of the demos to be displayed.
         */
        public CustomArrayAdapter(Context context, SampleAppDetails[] demos) {
            super(context, R.layout.simple_view, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SimpleView featureView;
            if (convertView instanceof SimpleView) {
                featureView = (SimpleView) convertView;
            } else {
                featureView = new SimpleView(getContext());
            }

            SampleAppDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            return featureView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_apps);

        ListAdapter adapter = new CustomArrayAdapter(this, SampleAppDetailsList.SAMPLES);

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        SampleAppDetails demo = (SampleAppDetails) getListAdapter().getItem(position);
        startActivity(new Intent(this, demo.activityClass));
    }
}
