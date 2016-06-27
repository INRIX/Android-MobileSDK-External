/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.inrix.sdk.model.ParkingBlock;

import java.util.List;

public class ParkingBlockRecyclerAdapter extends RecyclerView.Adapter<ParkingBlockRecyclerAdapter.ViewHolder> {
    private List<ParkingBlock> items;

    public ParkingBlockRecyclerAdapter(final List<ParkingBlock> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(new ParkingBlockView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ParkingBlock item = items.get(position);
        holder.itemView.bind(item);
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return this.items == null ? 0 : this.items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ParkingBlockView itemView;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.itemView = (ParkingBlockView) itemView;
        }
    }
}
