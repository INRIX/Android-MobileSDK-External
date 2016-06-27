
/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.cameras.picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.CameraManager;
import com.inrix.sdk.CameraManager.CameraImageOptions;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.Camera;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The Class CamerasListAdapter, list adapter which uses Picasso for image downloading.
 */
public class CamerasListAdapter extends BaseAdapter {

    /**
     * The cameras list.
     */
    final List<Camera> camerasList;

    /**
     * The inflater.
     */
    final LayoutInflater inflater;

    /**
     * The manager.
     */
    final CameraManager manager;
    final Context context;

    /**
     * Instantiates a new cameras list adapter.
     *
     * @param camerasCollection the cameras collection
     */
    public CamerasListAdapter(final Context context, final List<Camera> camerasCollection) {
        this.camerasList = camerasCollection;
        this.context = context;
        this.manager = InrixCore.getCameraManager();
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return this.camerasList == null ? 0 : this.camerasList.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Camera getItem(int position) {
        return this.camerasList == null ? null : this.camerasList.get(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        return this.camerasList == null ? 0 : this.camerasList.get(position)
                .getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.camera_picasso_item,
                    (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.address = (TextView) convertView
                    .findViewById(R.id.camera_address);
            viewHolder.thumbnail = (ImageView) convertView
                    .findViewById(R.id.thumbnail);
            viewHolder.copyright = (TextView) convertView
                    .findViewById(R.id.copyright);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Camera currentItem = getItem(position);
        viewHolder.address.setText(currentItem.getName());
        viewHolder.copyright.setText(currentItem.getLicense()
                .getCopyrightNotice());

        Picasso.with(this.context)
                .load(manager.getCameraUrl(new CameraImageOptions(currentItem)))
                .into(viewHolder.thumbnail);

        return convertView;
    }

    /**
     * The Class ViewHolder.
     */
    class ViewHolder {

        /**
         * The address.
         */
        TextView address;

        /**
         * The copyright.
         */
        TextView copyright;

        /**
         * The thumbnail.
         */
        ImageView thumbnail;
    }
}
