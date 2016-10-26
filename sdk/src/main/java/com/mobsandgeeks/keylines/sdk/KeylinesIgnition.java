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

package com.mobsandgeeks.keylines.sdk;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.util.Log;

import static com.mobsandgeeks.keylines.sdk.Shared.PACKAGE_NAME;

/**
 * An empty {@link android.content.ContentProvider} that initializes the SDK for a host application
 * and facilitates the Keylines app to listen for "heart-beats". If the host application is killed
 * then the Keylines app will stop displaying the spec.
 *
 * <p>Idea from the <a href="https://medium.com/@andretietz/auto-initialize-your-android-library-2349daf06920#.tmhvd7swr">medium article</a> by André Tietz.</p>.
 *
 * @author Ragunath Jawahar
 */
public final class KeylinesIgnition extends ContentProviderAdapter implements ServiceConnection {

    private static final String TAG = KeylinesIgnition.class.getName();
    private static final String DEFAULT_AUTHORITY = ".ignition";
    private static final String STETHOSCOPE_SERVICE = PACKAGE_NAME + ".exposed.StethoscopeService";

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onCreate() {
        Keylines.getInstance().init(((Application) getContext()));
        return false;
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        if (DEFAULT_AUTHORITY.equals(info.authority.trim())) {
            String message = "Keylines SDK: Missing or incorrect 'applicationId' variable in "
                    + "application's 'build.gradle' file.";
            throw new IllegalStateException(message);
        }

        bindToService(context);
        super.attachInfo(context, info);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "Connected to Keylines app.");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "Disconnected from Keylines app.");
    }

    private void bindToService(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(PACKAGE_NAME, STETHOSCOPE_SERVICE));

        try {
            context.bindService(intent, this, Context.BIND_AUTO_CREATE);
        } catch (SecurityException ignored) {
            Log.e(TAG, "Unable to bind to " + STETHOSCOPE_SERVICE, ignored);
        }
    }

}
