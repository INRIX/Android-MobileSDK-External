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
