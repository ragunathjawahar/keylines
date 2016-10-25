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

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

/**
 * @author Ragunath Jawahar
 */
public class KeylinesApplication extends Application {

    // Attributes
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        // Android's Strict Mode
        if (BuildConfig.DEBUG) {
            enableStrictMode();
        }

        // LeakCanary
        refWatcher = installLeakCanary();

        // Timber
        Timber.plant(BuildConfig.DEBUG ? new Timber.DebugTree() : new ProductionTree());
    }

    public static RefWatcher getRefWatcher(Context context) {
        if (context instanceof KeylinesApplication) {
            return ((KeylinesApplication) context).refWatcher;
        } else {
            return ((KeylinesApplication) context.getApplicationContext()).refWatcher;
        }
    }

    private void enableStrictMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }

    protected RefWatcher installLeakCanary() {
        Timber.d("Leak Canary DISABLED.");
        return RefWatcher.DISABLED;
    }

    static class ProductionTree extends Timber.DebugTree {

        @Override
        protected boolean isLoggable(int priority) {
            return priority == Log.ERROR || priority == Log.WARN;
        }
    }

}
