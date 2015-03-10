package com.inrix.sample.activity;

import com.inrix.sample.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * A widget that describes an activity that demonstrates a feature.
 */
public final class SimpleView extends FrameLayout {

    /**
     * Constructs a feature view by inflating layout/feature.xml.
     */
    public SimpleView(Context context) {
        super(context);

        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.simple_view, this);
    }

    /**
     * Set the resource id of the title of the demo.
     *
     * @param titleId the resource id of the title of the demo
     */
    public synchronized void setTitleId(int titleId) {
        ((TextView) (findViewById(R.id.title))).setText(titleId);
    }
    

    /**
     * Set the resource id of the title of the demo.
     *
     * @param titleId the resource id of the title of the demo
     */
    public synchronized void setTitle(String title) {
        ((TextView) (findViewById(R.id.title))).setText(title);
    }
    
    /**
     * Set the description
     * @param description
     */
    public synchronized void setDescription( String description ){
    	((TextView) (findViewById(R.id.description))).setText(description);
    }

    /**
     * Set the resource id of the description of the demo.
     *
     * @param descriptionId the resource id of the description of the demo
     */
    public synchronized void setDescriptionId(int descriptionId) {
        ((TextView) (findViewById(R.id.description))).setText(descriptionId);
    }

}
