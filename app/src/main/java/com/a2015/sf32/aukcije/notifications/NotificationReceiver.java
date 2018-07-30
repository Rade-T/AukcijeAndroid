package com.a2015.sf32.aukcije.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @TargetApi(16)
    @Override
    public void onReceive(Context context, Intent intent) {
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Izvestaj aukcije")
                .setContentText("Vasa ponuda je najveca")
                .setSmallIcon(android.R.mipmap.sym_def_app_icon);

        NotificationManager manager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
        Log.d("Receiver", "Poslata notifikacija");
    }
}
