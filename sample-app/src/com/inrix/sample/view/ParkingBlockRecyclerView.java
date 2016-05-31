package com.inrix.sample.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ParkingBlockRecyclerView extends RecyclerView {
    private View emptyView;
    private View progressView;

    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyView();
        }
    };

    public ParkingBlockRecyclerView(Context context) {
        super(context);
    }

    public ParkingBlockRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParkingBlockRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEmptyView(final View emptyView) {
        this.emptyView = emptyView;
    }

    public void setProgressView(final View progressView) {
        this.progressView = progressView;
    }

    public void showProgress() {
        this.setVisibility(GONE);
        this.emptyView.setVisibility(GONE);
        this.progressView.setVisibility(VISIBLE);
    }

    @Override
    public void setAdapter(final RecyclerView.Adapter adapter) {
        if (this.getAdapter() != null) {
            this.getAdapter().unregisterAdapterDataObserver(dataObserver);
        }

        if (adapter != null) {
            adapter.registerAdapterDataObserver(dataObserver);
        }

        this.progressView.setVisibility(GONE);

        super.setAdapter(adapter);
        this.updateEmptyView();
    }

    private void updateEmptyView() {
        if (this.emptyView == null || getAdapter() == null) {
            return;
        }

        boolean showEmptyView = getAdapter().getItemCount() == 0;
        this.emptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
        this.setVisibility(showEmptyView ? GONE : VISIBLE);
    }
}
