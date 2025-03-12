package com.onfonmobile.projectx.Firestore.Helpers.Worker;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.onfonmobile.projectx.ui.login.Notifications;

import java.util.concurrent.TimeUnit;

public class NotificationScheduler  {

    public static void scheduleNotification(Context context) {
        // Create a periodic work request that runs every 24 hours
        PeriodicWorkRequest notificationWork =
                new PeriodicWorkRequest.Builder(
                        NotificationWorker.class,
                        1,
                        TimeUnit.HOURS)
                        .build();

        // Enqueue the work request
        WorkManager.getInstance(context).enqueue(notificationWork);
    }

    // Worker class that will be executed periodically
    public static class NotificationWorker extends Worker {

        public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            // Create and send notification
            String title = "Contribution Reminder";
            String message = "Please make your contribution as soon as possible.";

            Intent intent = new Intent(getApplicationContext(), Notifications.class);
            intent.putExtra("NOTIFICATION_TITLE", title);
            intent.putExtra("NOTIFICATION_MESSAGE", message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            getApplicationContext().startActivity(intent);

            return Result.success();
        }
    }
}