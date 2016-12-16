package com.inrix.sample.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.DangerousSlowdownsManager;
import com.inrix.sdk.DangerousSlowdownsManager.DangerousSlowdownsBoxOptions;
import com.inrix.sdk.DangerousSlowdownsManager.IDangerousSlowdownsResponseListener;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.DangerousSlowdown;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.utils.UserPreferences.Unit;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DangerousSlowdownsActivity extends InrixSdkActivity {
    private static final String STATE_DATA = "_data";

    @BindView(R.id.dangerous_slowdowns_list)
    protected RecyclerView list;

    private DangerousSlowdownsManager dangerousSlowdownsManager;
    private ICancellable request;

    private ArrayList<DangerousSlowdown> data;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_dangerous_slowdowns;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        this.list.setLayoutManager(new LinearLayoutManager(this));

        this.dangerousSlowdownsManager = InrixCore.getDangerousSlowdownsManager();

        if (savedInstanceState != null) {
            this.data = savedInstanceState.getParcelableArrayList(STATE_DATA);
            if (this.data == null || this.data.isEmpty()) {
                this.reload();
            } else {
                this.showList();
            }
        } else {
            this.reload();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (this.data != null) {
            outState.putParcelableArrayList(STATE_DATA, this.data);
        }
    }

    @Override
    protected void onStop() {
        if (this.request != null) {
            this.request.cancel();
            this.request = null;
        }

        super.onStop();
    }

    private void reload() {
        final GeoPoint corner1 = new GeoPoint(48.381861,-124.694426);
        final GeoPoint corner2 = new GeoPoint(25.395384,-80.392867);
        final DangerousSlowdownsBoxOptions options = new DangerousSlowdownsBoxOptions(corner1, corner2, Unit.MILES);

        final IDangerousSlowdownsResponseListener listener = new IDangerousSlowdownsResponseListener() {
            @Override
            public void onResult(List<DangerousSlowdown> dangerousSlowdowns) {
                request = null;
                data = new ArrayList<>(dangerousSlowdowns);
                showList();
            }

            @Override
            public void onError(Error error) {
                request = null;
                data = null;
                showList();
            }
        };

        this.request = this.dangerousSlowdownsManager.getDangerousSlowdownsInBox(options, listener);
    }

    private void showList() {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }

        final Adapter adapter = new Adapter(this);
        this.list.setAdapter(adapter);
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final LayoutInflater inflater;

        Adapter(final Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = this.inflater.inflate(R.layout.view_dangerous_slowdown_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.dangerous_slowdown_list_item_icon)
        protected ImageView icon;

        @BindView(R.id.dangerous_slowdown_list_item_text)
        protected TextView text;

        private Resources resources;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            this.resources = this.icon.getContext().getResources();
        }

        public void bind(final DangerousSlowdown data) {
            final String displayText = resources.getString(R.string.dangerous_slowdowns_list_item_text,
                    data.getSpeedBefore(),
                    data.getSpeedAt(),
                    data.getRoadName());

            final Drawable drawable = this.severityToIcon(data.getSeverity());

            this.icon.setImageDrawable(drawable);
            this.text.setText(displayText);
        }

        @SuppressWarnings("deprecation")
        private Drawable severityToIcon(final DangerousSlowdown.Severity severity) {

            Drawable drawable = this.resources.getDrawable(R.drawable.ic_dangerous_slowdown);
            int color = this.resources.getColor(R.color.dangerous_slowdowns_severity_unknown);

            switch (severity) {
                case SEVERITY_5:
                    color = this.resources.getColor(R.color.dangerous_slowdowns_severity_5);
                    break;
                case SEVERITY_4:
                    color = this.resources.getColor(R.color.dangerous_slowdowns_severity_4);
                    break;
                case SEVERITY_3:
                    color = this.resources.getColor(R.color.dangerous_slowdowns_severity_3);
                    break;
                case SEVERITY_2:
                    color = this.resources.getColor(R.color.dangerous_slowdowns_severity_2);
                    break;
                case SEVERITY_1:
                    color = this.resources.getColor(R.color.dangerous_slowdowns_severity_1);
                    break;
                case UNKNOWN:
                    color = this.resources.getColor(R.color.dangerous_slowdowns_severity_unknown);
                    break;
            }

            final Drawable result = drawable.mutate();
            result.setColorFilter(color, Mode.SRC_ATOP);
            return result;
        }
    }
}
