/*
 * Copyright (C) 2016 Ragunath Jawahar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobsandgeeks.keylines;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.dspec.SpecDrawable;
import org.lucasr.dspec.SpecParser;

import timber.log.Timber;

import static com.mobsandgeeks.keylines.Shared.NAMESPACE_ACTION;
import static com.mobsandgeeks.keylines.Shared.NAMESPACE_EXTRA;

/**
 * @author Ragunath Jawahar
 */
public class KeylinesService extends Service {

    // Constants
    private static final int NOTIFICATION_ID = 0X524A; // Being 'Narcissistic'

    // Actions
    static final String ACTION_SHOW = NAMESPACE_ACTION + ".SHOW";
    static final String ACTION_STOP = NAMESPACE_ACTION + ".STOP";

    // Extras
    static final String EXTRA_SPEC = NAMESPACE_EXTRA + ".SPEC";

    // Attributes
    private SpecDrawable specDrawable;
    private View hostView;
    private WindowManager windowManager;

    private NotificationBroadcastReceiver notificationBroadcastReceiver;
    private Pair<Notification, RemoteViews> notificationRemoteViewsPair;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // Prep the View and add it to the Window
        hostView = new View(this);
        specDrawable = new SpecDrawable(getResources());
        setBackground(hostView, specDrawable);

        // Add View to window
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(hostView, getLayoutParams(true));

        // Broadcast Receiver for handling actions from the foreground service Notification
        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        notificationBroadcastReceiver.register(this);

        // Start Foreground
        notificationRemoteViewsPair = notificationBroadcastReceiver.createNotification(this);
        startForeground(NOTIFICATION_ID, notificationRemoteViewsPair.first);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_SHOW.equals(intent.getAction())) {
            String payload = intent.getExtras().getString(EXTRA_SPEC);
            show(payload);
        } else if (ACTION_STOP.equals(intent.getAction())) {
            stop();
        } else {
            throw new UnsupportedOperationException("Unknown action: " + intent.getAction());
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Destroy the view
        if (hostView != null) {
            windowManager.removeView(hostView);
        }

        // Unregister broadcast receiver
        notificationBroadcastReceiver.unregister();

        // Check for leaks
        KeylinesApplication.getRefWatcher(this).watch(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // No-op
    }

    void updateNotificationIcon(@IdRes int remoteViewId, @DrawableRes int drawableRes) {
        RemoteViews remoteViews = notificationRemoteViewsPair.second;
        RemoteViewsUtil.setImageViewResource(this, remoteViews, remoteViewId, drawableRes);
        updateNotification(notificationRemoteViewsPair.first);
    }

    void showGrid(boolean show) {
        // TODO 25/10/16 Show / hide
    }

    void flipHorizontal(boolean flip) {
        // TODO 25/10/16 Flip
    }

    void flipVertical(boolean flip) {
        // TODO 25/10/16 Flip
    }

    void visible(boolean visible) {
        float from = visible ? 0.0f : 1.0f;
        float to   = visible ? 1.0f : 0.0f;
        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(hostView, "alpha", from, to);
        fadeAnimator.start();
    }

    void exit() {
        stopForeground(true);
        stopSelf();
    }

    private void setBackground(View view, SpecDrawable designSpec) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(designSpec);
        } else {
            // noinspection deprecation
            view.setBackgroundDrawable(designSpec);
        }
    }

    private WindowManager.LayoutParams getLayoutParams(boolean fullscreen) {
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        if (fullscreen) {
            flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
        int type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        return new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                type, flags, PixelFormat.TRANSLUCENT);
    }

    private void show(String payload) {
        JSONObject jsonSpec = null;
        try {
            jsonSpec = new JSONObject(payload);
        } catch (JSONException e) {
            Timber.e("Invalid JSON format: %s", payload);
        }

        // Update the Spec
        if (jsonSpec != null) {
            specDrawable.setSpec(SpecParser.fromJson(jsonSpec));
            specDrawable.invalidateSelf();
        }

        if (!hostView.isShown()) {
            hostView.setVisibility(View.VISIBLE);
        }
    }

    private void stop() {
        hostView.setVisibility(View.INVISIBLE);
        specDrawable.setSpec(null);
    }

    private void updateNotification(Notification notification) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
