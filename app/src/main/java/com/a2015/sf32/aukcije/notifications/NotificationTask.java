package com.a2015.sf32.aukcije.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class NotificationTask extends AsyncTask<Context, String, Context> {

    private Context mContext;

    public NotificationTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Context doInBackground(Context... params) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Context context) {
        super.onPostExecute(context);
        Intent intent = new Intent("USER_HIGHEST_BIDDER");
        mContext.sendBroadcast(intent);
        Log.d("Task", "Poslat broadcast");
    }
}
