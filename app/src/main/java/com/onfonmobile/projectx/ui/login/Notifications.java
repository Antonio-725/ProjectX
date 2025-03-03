package com.onfonmobile.projectx.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onfonmobile.projectx.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Notifications extends AppCompatActivity {

    private TextView tvNotificationTitle;
    private TextView tvNotificationMessage;
    private TextView tvNotificationTime;
    private RecyclerView recyclerViewNotifications;
    private TextView tvNoNotifications;
    private List<NotificationItem> notificationList = new ArrayList<>();
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        // Initialize views
        tvNotificationTitle = findViewById(R.id.tvNotificationTitle);
        tvNotificationMessage = findViewById(R.id.tvNotificationMessage);
        tvNotificationTime = findViewById(R.id.tvNotificationTime);
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup RecyclerView
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList);
        recyclerViewNotifications.setAdapter(adapter);

        // Get notification data from intent
        if (getIntent().hasExtra("NOTIFICATION_TITLE") && getIntent().hasExtra("NOTIFICATION_MESSAGE")) {
            String title = getIntent().getStringExtra("NOTIFICATION_TITLE");
            String message = getIntent().getStringExtra("NOTIFICATION_MESSAGE");
            String currentTime = getCurrentTime();

            // Display in the card view
            tvNotificationTitle.setText(title);
            tvNotificationMessage.setText(message);
            tvNotificationTime.setText("Just now");

            // Add to notification list
            notificationList.add(0, new NotificationItem(title, message, currentTime));
            adapter.notifyDataSetChanged();

            // Hide "No notifications" text if visible
            tvNoNotifications.setVisibility(View.GONE);
        } else {
            // No new notification, show empty state
            tvNoNotifications.setVisibility(View.VISIBLE);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Notification item class
    private static class NotificationItem {
        String title;
        String message;
        String time;

        NotificationItem(String title, String message, String time) {
            this.title = title;
            this.message = message;
            this.time = time;
        }
    }

    // Adapter for the RecyclerView
    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

        private List<NotificationItem> notifications;

        NotificationAdapter(List<NotificationItem> notifications) {
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = getLayoutInflater().inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NotificationItem item = notifications.get(position);
            holder.tvTitle.setText(item.title);
            holder.tvMessage.setText(item.message);
            holder.tvTime.setText(item.time);

            holder.itemView.setOnClickListener(v ->
                    Toast.makeText(Notifications.this, item.title, Toast.LENGTH_SHORT).show()
            );
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvMessage, tvTime;

            ViewHolder(@NonNull android.view.View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvItemTitle);
                tvMessage = itemView.findViewById(R.id.tvItemMessage);
                tvTime = itemView.findViewById(R.id.tvItemTime);
            }
        }
    }
}