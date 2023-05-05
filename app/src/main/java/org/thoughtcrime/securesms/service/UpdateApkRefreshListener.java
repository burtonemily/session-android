package org.thoughtcrime.securesms.service;


import android.content.Context;
import android.content.Intent;

import org.session.libsession.messaging.jobs.JobQueue;
import org.session.libsignal.utilities.Log;

import org.thoughtcrime.securesms.ApplicationContext;
import network.loki.messenger.BuildConfig;
import org.thoughtcrime.securesms.jobs.UpdateApkJob;
import org.session.libsession.utilities.TextSecurePreferences;

import java.util.concurrent.TimeUnit;

public class UpdateApkRefreshListener extends PersistentAlarmManagerListener {

  private static final String TAG = UpdateApkRefreshListener.class.getSimpleName();

  private static final long INTERVAL = TimeUnit.HOURS.toMillis(6);

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getUpdateApkRefreshTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    Log.i(TAG, "onAlarm...");

    if (scheduledTime != 0 && BuildConfig.PLAY_STORE_DISABLED) {
      Log.i(TAG, "Queueing APK update job...");
      ApplicationContext.getInstance(context)
                        .getJobManager()
                        .add(new UpdateApkJob());
    }

    long newTime = System.currentTimeMillis() + INTERVAL;
    TextSecurePreferences.setUpdateApkRefreshTime(context, newTime);

    return newTime;
  }

  public static void schedule(Context context) {
    new UpdateApkRefreshListener().onReceive(context, new Intent());
  }

}
