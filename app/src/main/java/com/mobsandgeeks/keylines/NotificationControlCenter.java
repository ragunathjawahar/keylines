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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.util.Pair;
import android.support.v7.app.NotificationCompat;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.RemoteViews;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.functions.Action0;

import static com.mobsandgeeks.keylines.Shared.NAMESPACE_ACTION;
import static com.mobsandgeeks.keylines.Shared.PACKAGE_NAME;

/**
 * This {@link BroadcastReceiver} is responsible for handling actions that are broadcast
 * from the service's foreground {@link Notification}.
 *
 * @author Ragunath Jawahar
 */
class NotificationControlCenter extends BroadcastReceiver {

    // Actions
    private static final String ACTION_TOGGLE_GRID       = NAMESPACE_ACTION + ".TOGGLE_GRID";
    private static final String ACTION_FLIP_HORIZONTAL   = NAMESPACE_ACTION + ".FLIP_HORIZONTAL";
    private static final String ACTION_FLIP_VERTICAL     = NAMESPACE_ACTION + ".FLIP_VERTICAL";
    private static final String ACTION_TOGGLE_VISIBILITY = NAMESPACE_ACTION + ".TOGGLE_VISIBILITY";
    private static final String ACTION_EXIT              = NAMESPACE_ACTION + ".EXIT";

    /**
     * Maps drawable resources to {@link android.widget.ImageButton}s in {@link RemoteViews}.
     * See {@link #buildRemoteViews(Context)} for usage.
     */
    private static final SparseIntArray ID_DEFAULT_ICON_ARRAY = new SparseIntArray() {{
        put(R.id.gridImageButton,               R.drawable.ic_grid_on);
        put(R.id.flipHorizontalImageButton,     R.drawable.ic_flip_h_off);
        put(R.id.flipVerticalImageButton,       R.drawable.ic_flip_v_off);
        put(R.id.visibilityImageButton,         R.drawable.ic_visibility_on);
        put(R.id.exitImageButton,               R.drawable.ic_exit);
    }};

    /**
     * Used to create and map {@link PendingIntent}s to {@link android.widget.ImageButton}s present
     * inside {@link RemoteViews}. See {@link #buildRemoteViews(Context)} for usage.
     */
    private static final SparseArray<String> ID_ACTION_ARRAY = new SparseArray<String>() {{
        put(R.id.gridImageButton,               ACTION_TOGGLE_GRID);
        put(R.id.flipHorizontalImageButton,     ACTION_FLIP_HORIZONTAL);
        put(R.id.flipVerticalImageButton,       ACTION_FLIP_VERTICAL);
        put(R.id.visibilityImageButton,         ACTION_TOGGLE_VISIBILITY);
        put(R.id.exitImageButton,               ACTION_EXIT);
    }};

    /**
     * A {@link Map} containing functions associated with corresponding intent actions. See
     * {@link #onReceive(Context, Intent)} for usage.
     */
    private final Map<String, Action0> ACTION_MAP = Collections.unmodifiableMap(
            new HashMap<String, Action0>() {{
                put(ACTION_TOGGLE_GRID,         () -> toggleGrid());
                put(ACTION_FLIP_HORIZONTAL,     () -> toggleFlipH());
                put(ACTION_FLIP_VERTICAL,       () -> toggleFlipV());
                put(ACTION_TOGGLE_VISIBILITY,   () -> toggleVisibility());
                put(ACTION_EXIT,                () -> exit());
            }}
    );

    /**
     * // TODO 25/10/16 Make these values sane.
     * These default values should be reflected in {@link org.lucasr.dspec.SpecDrawable}.
     */
    private boolean showGrid = true;
    private boolean flipHorizontal = false;
    private boolean flipVertical = false;
    private boolean visible = true;

    private KeylinesService keylinesService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        Action0 action = ACTION_MAP.get(intentAction);

        if (action != null) {
            action.call();
        } else {
            String message = String.format("Unknown action '%s'", intentAction);
            throw new UnsupportedOperationException(message);
        }
    }

    /**
     * Registers the {@link NotificationControlCenter} (Broadcast Receiver) with a corresponding
     * {@link IntentFilter} to a {@link KeylinesService} instance.
     *
     * @param keylinesService  Instance to which the broadcast receiver should be registered to.
     */
    void register(KeylinesService keylinesService) {
        this.keylinesService = keylinesService;

        // Intent filter
        IntentFilter intentFilter = new IntentFilter();
        for (int i = 0, n = ID_ACTION_ARRAY.size(); i < n; i++) {
            intentFilter.addAction(ID_ACTION_ARRAY.valueAt(i));
        }
        keylinesService.registerReceiver(this, intentFilter);
    }

    /**
     * Unregister this {@link NotificationControlCenter} (Broadcast Receiver) from the registered
     * {@link KeylinesService} instance.
     */
    void unregister() {
        if (keylinesService == null) {
            String message = "You must call 'register(KeylinesService)' before "
                    + "calling 'unregister()'.";
            throw new IllegalStateException(message);
        }
        keylinesService.unregisterReceiver(this);
        keylinesService = null;
    }

    /**
     * Obtains a notification which will be used (persistent) while {@link KeylinesService}
     * is run in foreground.
     *
     * @param context  A context.
     * @return  A {@link Pair} containing the {@link Notification} and the {@link RemoteViews} used
     *          as a content view inside the notification.
     */
    Pair<Notification, RemoteViews> createNotification(Context context) {
        RemoteViews contentView = buildRemoteViews(context);
        Notification notification = buildNotification(context, contentView);
        return new Pair<>(notification, contentView);
    }

    // TODO 25/10/16 Refactor toggle methods
    private void toggleGrid() {
        @DrawableRes int iconRes = (showGrid = !showGrid)
                ? R.drawable.ic_grid_on : R.drawable.ic_grid_off;
        keylinesService.showGrid(showGrid);
        keylinesService.updateNotificationIcon(R.id.gridImageButton, iconRes);
    }

    private void toggleFlipH() {
        @DrawableRes int iconRes = (flipHorizontal = !flipHorizontal)
                ? R.drawable.ic_flip_h_on : R.drawable.ic_flip_h_off;
        keylinesService.flipHorizontal(flipHorizontal);
        keylinesService.updateNotificationIcon(R.id.flipHorizontalImageButton, iconRes);
    }

    private void toggleFlipV() {
        @DrawableRes int iconRes = (flipVertical = !flipVertical)
                ? R.drawable.ic_flip_v_on : R.drawable.ic_flip_v_off;
        keylinesService.flipVertical(flipVertical);
        keylinesService.updateNotificationIcon(R.id.flipVerticalImageButton, iconRes);
    }

    private void toggleVisibility() {
        @DrawableRes int iconRes = (visible = !visible)
                ? R.drawable.ic_visibility_on : R.drawable.ic_visibility_off;
        keylinesService.visible(visible);
        keylinesService.updateNotificationIcon(R.id.visibilityImageButton, iconRes);
    }

    private void exit() {
        keylinesService.exit();
    }

    private RemoteViews buildRemoteViews(Context context) {
        RemoteViews remoteViews = new RemoteViews(PACKAGE_NAME, R.layout.notification_control_center);

        // Icons
        for (int i = 0, n = ID_DEFAULT_ICON_ARRAY.size(); i < n; i++) {
            @IdRes int viewId = ID_DEFAULT_ICON_ARRAY.keyAt(i);
            @DrawableRes int drawableRes = ID_DEFAULT_ICON_ARRAY.get(viewId);
            RemoteViewsUtil.setImageViewResource(context, remoteViews, viewId, drawableRes);
        }

        // Actions
        for (int j = 0, m = ID_ACTION_ARRAY.size(); j < m; j++) {
            @IdRes int viewId = ID_ACTION_ARRAY.keyAt(j);
            String action = ID_ACTION_ARRAY.get(viewId);

            PendingIntent actionPendingIntent = getPendingIntent(context, action);
            remoteViews.setOnClickPendingIntent(viewId, actionPendingIntent);
        }

        return remoteViews;
    }

    private Notification buildNotification(Context context, RemoteViews contentView) {
        @DrawableRes int smallIcon = visible
                ? R.drawable.ic_visibility_on : R.drawable.ic_visibility_off;

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIcon)
                .setContent(contentView)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        return notification;
    }

    private PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
