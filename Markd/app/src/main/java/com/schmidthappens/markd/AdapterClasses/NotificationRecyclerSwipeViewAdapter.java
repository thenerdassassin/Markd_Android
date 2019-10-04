package com.schmidthappens.markd.AdapterClasses;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_menu_activities.NotificationsActivity;
import com.schmidthappens.markd.data_objects.CustomerNotificationMessage;
import com.schmidthappens.markd.utilities.NotificationHandler;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 12/13/17.
 */

public class NotificationRecyclerSwipeViewAdapter extends RecyclerSwipeAdapter<NotificationRecyclerSwipeViewAdapter.NotificationViewHolder> {
    private final static String TAG = "NotificationRecycler";
    private NotificationsActivity context;
    private String customerId;
    private List<CustomerNotificationMessage> notifications;

    public NotificationRecyclerSwipeViewAdapter(
            final NotificationsActivity context,
            final String customerId,
            List<CustomerNotificationMessage> notifications) {
        this.context = context;
        this.customerId = customerId;
        this.notifications = notifications;
        this.setMode(Attributes.Mode.Single);
    }

    @Override @NonNull
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        return new NotificationViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final NotificationViewHolder holder, final int position) {
        holder.bindData(notifications.get(position));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Deleted notification.");
                final int index = holder.getAdapterPosition();
                NotificationHandler.removeNotification(customerId, index);
                mItemManger.removeShownLayouts(holder.swipeLayout);
                notifications.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, notifications.size());
                mItemManger.closeAllItems();
                Toast.makeText(context, "Notification Deleted.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (notifications != null ? notifications.size():0);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.list_row_notification;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView deleteButton;

        private TextView dateSent;
        private TextView companyFrom;
        private TextView message;

        NotificationViewHolder(View v) {
            super(v);
            swipeLayout = v.findViewById(R.id.swipe_layout);
            deleteButton = v.findViewById(R.id.delete_text_view);
            dateSent = v.findViewById(R.id.notification_date);
            companyFrom = v.findViewById(R.id.notification_contractor);
            message = v.findViewById(R.id.notification_message);
        }

        void bindData(final CustomerNotificationMessage notification) {
            dateSent.setText(notification.getDateSent());
            companyFrom.setText(notification.getCompanyFrom());
            message.setText(notification.getMessage());

            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(
                    SwipeLayout.DragEdge.Right,
                    swipeLayout.findViewById(R.id.item_list_bottom_wrapper));
        }

    }
}
