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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mobsandgeeks.keylines.sdk.Shared.NAMESPACE_ACTION;
import static com.mobsandgeeks.keylines.sdk.Shared.NAMESPACE_EXTRA;

/**
 * @author Ragunath Jawahar
 */
public class Keylines {

    // Constants
    private static final String TAG = Keylines.class.getSimpleName();

    // Actions
    private static final String ACTION_SHOW = NAMESPACE_ACTION + ".SHOW";
    private static final String ACTION_STOP = NAMESPACE_ACTION + ".STOP";

    // Extras
    private static final String EXTRA_SPEC = NAMESPACE_EXTRA + ".SPEC";

    // Attributes
    private Resources resources;

    private static class SingletonHolder {
        static final Keylines INSTANCE = new Keylines();
    }

    /**
     * Returns an initialized  singleton instance of the {@link Keylines}.
     *
     * @return An initialized {@link Keylines} instance.
     */
    @SuppressWarnings("WeakerAccess")
    public static Keylines getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Specs a {@link Fragment}, if annotated with the {@link Design} annotation.
     *
     * @param fragment A fragment.
     */
    @SuppressWarnings("unused")
    public void spec(@NonNull Fragment fragment) {
        spec(fragment.getActivity(), fragment);
    }

    /**
     * Specs a {@link android.support.v4.app.Fragment}, if annotated with the {@link Design}
     * annotation.
     *
     * @param supportFragment A fragment.
     */
    @SuppressWarnings("unused")
    public void spec(@NonNull android.support.v4.app.Fragment supportFragment) {
        spec(supportFragment.getActivity(), supportFragment);
    }

    void init(Application application) {
        if (this.resources != null) {
            throw new IllegalStateException("Keylines has to be initialized only once.");
        }
        this.resources = application.getResources();
        spyOnActivities(application);
    }

    private Keylines() {
        // Default private constructor
    }

    private void spec(Context context, Object theAnnotatedOne) {
        if (this.resources == null) {
            String message = "Keylines must be initialized using the 'init(Application)' method.";
            throw new IllegalStateException(message);
        }

        if (context == null) {
            String message = "Unable to retrieve 'context' from "
                    + theAnnotatedOne.getClass().getSimpleName();
            throw new NullPointerException(message);
        }

        showSpec(context, theAnnotatedOne.getClass());
    }

    /**
     * This method serves two purposes, of which only the first is reliable.
     *
     * <ol>
     *     <li>
     *         Automatically get {@link Design} annotations from any started {@link Activity}
     *         and send specs to the Keylines app. This is the <bold>primary</bold> purpose.
     *     </li>
     *     <li>
     *         Send terminal notifications to the Keylines app if the app goes to
     *         background or quits.
     *     </li>
     * </ol>
     *
     * However, there are caveats while sending terminal notifications. This is a fairly naive
     * implementation which fails spectacularly in a few cases. Some of them are listed.
     *
     * <ul>
     *     <li>The app crashes or is killed.</li>
     *     <li>For some reason, the Activity lifecycle callbacks aren't invoked as expected.</li>
     * </ul>
     *
     * The second purpose is solved mostly in the ideal cases. (i.e) User navigates through the app
     * and dutifully exits by popping all the activities out of the back-stack using the back
     * button.
     *
     * @param application The {@link Application} instance of the host application.
     */
    private void spyOnActivities(final Application application) {
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {

            private AtomicInteger activityCounter = new AtomicInteger();

            @Override
            public void onActivityStarted(Activity activity) {
                // Show spec
                Class<? extends Activity> activityClass = activity.getClass();
                showSpec(activity, activityClass);

                // Increment counter
                activityCounter.incrementAndGet();
            }

            @Override
            public void onActivityStopped(Activity activity) {
                int liveActivities = activityCounter.decrementAndGet();

                if (liveActivities == 0) {
                    application.sendBroadcast(new Intent(ACTION_STOP));
                }
            }
        });
    }

    private void showSpec(final Context context, final Class<?> hostClass) {
        final Design design = hostClass.getAnnotation(Design.class);
        if (design == null) {
            String message = String.format("%s is not annotated with @%s",
                    hostClass.getName(), Design.class.getSimpleName());
            Log.i(TAG, message);
            return;
        }

        // IO on a different thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                String jsonSpec = getJsonSpec(hostClass, design.value()); // TODO 26/10/16 Validate spec
                if (jsonSpec != null) {
                    send(context, jsonSpec);
                } // TODO 28/10/16 Log a warning
            }
        }).start();
    }

    @Nullable
    private String getJsonSpec(Class<?> hostClass, @RawRes int specResourceId) {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(resources.openRawResource(specResourceId))
            );

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (Resources.NotFoundException e) {
            @SuppressLint("DefaultLocale") String message = String.format(
                    "Unable to find RAW resource with ID: %d for %s", specResourceId, hostClass.getName()
            );
            Log.e(TAG,  message);
            return null;

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try { if (reader != null) { reader.close(); } } catch (IOException ignored) {}
        }

        return stringBuilder.toString();
    }

    private void send(Context context, String spec) {
        Intent intent = new Intent(ACTION_SHOW);
        intent.putExtra(EXTRA_SPEC, spec);
        context.sendBroadcast(intent);
    }

}
