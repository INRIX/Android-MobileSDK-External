/**
 * Copyright (c) 2013-2017 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class ParkingRecyclerView extends RecyclerView {
    private View emptyView;
    private View progressView;

    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyView();
        }
    };

    public ParkingRecyclerView(Context context) {
        super(context);
    }

    public ParkingRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParkingRecyclerView(Context context, AttributeSet attrs, int defStyle) {
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
        if (this.emptyView == null) {
            return;
        }

        boolean showEmptyView = this.getAdapter() == null || this.getAdapter().getItemCount() == 0;
        this.emptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
        this.setVisibility(showEmptyView ? GONE : VISIBLE);
    }
}
