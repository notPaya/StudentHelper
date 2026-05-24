package com.example.student_helper.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.student_helper.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String examName = intent.getStringExtra("exam_name");
        if (examName == null) examName = "Ispit";
        int examId = intent.getIntExtra("exam_id", 0);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "exam_reminders")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("⏰ Sutra je ispit!")
                .setContentText("Ispit iz " + examName + " je sutra. Sretno! 🎯")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(examId, notification.build());
        }
    }
}