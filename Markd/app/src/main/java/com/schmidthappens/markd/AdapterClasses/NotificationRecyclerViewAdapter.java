package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.data_objects.CustomerNotificationMessage;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 12/13/17.
 */

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {
    private final static String TAG = "NotificationRecycler";
    private List<CustomerNotificationMessage> notifications;

    public NotificationRecyclerViewAdapter(List<CustomerNotificationMessage> notifications) {
        this.notifications = notifications;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        holder.bindData(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != notifications ? notifications.size() : 0);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.list_row_notification;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView dateSent;
        private TextView companyFrom;
        private TextView message;

        NotificationViewHolder(View v) {
            super(v);
            dateSent = (TextView)v.findViewById(R.id.notification_date);
            companyFrom = (TextView)v.findViewById(R.id.notification_contractor);
            message = (TextView)v.findViewById(R.id.notification_message);
        }

        void bindData(final CustomerNotificationMessage notification) {
            dateSent.setText(notification.getDateSent());
            companyFrom.setText(notification.getCompanyFrom());
            message.setText(notification.getMessage());
        }
    }
}
