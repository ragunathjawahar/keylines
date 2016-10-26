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
import android.content.Intent;
import android.content.res.Resources;
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
class Keylines {

    // Constants
    private static final String TAG = Keylines.class.getSimpleName();

    // Actions
    private static final String ACTION_SHOW = NAMESPACE_ACTION + ".SHOW";
    private static final String ACTION_STOP = NAMESPACE_ACTION + ".STOP";

    // Extras
    private static final String EXTRA_SPEC = NAMESPACE_EXTRA + ".SPEC";

    // Attributes
    private Application application;
    private Resources resources;

    Keylines(final Application application) {
        this.application = application;
        this.resources = application.getResources();
        spyOnActivities(application);
    }

    /**
     * This method serves two purposes, of which only the first is reliable.
     *
     * <ol>
     *     <li>
     *         Automatically get {@link DesignSpec} annotations from any started {@link Activity}
     *         and send specs to the Keylines app. This is the <bold>primary</bold> purpose.
     *     </li>
     *     <li>
     *         Send terminal notifications to the Keylines app if the user exits the app.
     *     </li>
     * </ol>
     *
     * However, there are caveats while sending terminal notifications. This is a fairly naive
     * implementation which fails spectacularly in several cases. Some of them are listed.
     * <ul>
     *     <li>An intent clears the backstack when launching an Activity.</li>
     *     <li>The app crashes or is killed.</li>
     *     <li>An activity goes to the background and there .</li>
     * </ul>
     *
     * The second purpose is solved only in the ideal world. (i.e) User navigates throught the app
     * and dutifully exits it by popping all the activities out of the backstack using the back
     * button.
     *
     * @param application The {@link Application} instance of the host application.
     */
    private void spyOnActivities(final Application application) {
        this.application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {

            private AtomicInteger activityCounter = new AtomicInteger();

            @Override
            public void onActivityStarted(Activity activity) {
                // Show spec
                Class<? extends Activity> activityClass = activity.getClass();
                showSpec(activityClass);

                // Increment counter
                activityCounter.incrementAndGet();
            }

            @Override
            public void onActivityStopped(Activity activity) {
                int activitiesInStack = activityCounter.decrementAndGet();

                if (activitiesInStack == 0) {
                    application.sendBroadcast(new Intent(ACTION_STOP));
                }
            }
        });
    }

    private void showSpec(Class<?> hostClass) {
        DesignSpec designSpec = hostClass.getAnnotation(DesignSpec.class);
        if (designSpec != null) {
            String jsonSpec = getJsonSpec(hostClass.getName(), designSpec.value()); // TODO 26/10/16 Validate JSON
            if (jsonSpec != null) {
                send(jsonSpec);
            }
        } else {
            String message = String.format("%s is not annotated with @%s",
                    hostClass.getName(), DesignSpec.class.getSimpleName());
            Log.i(TAG, message);
        }
    }

    @Nullable
    private String getJsonSpec(String activity, @RawRes int specResourceId) {
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
                    "Unable to find RAW resource with ID: %d for %s", specResourceId, activity
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

    private void send(String spec) {
        Intent intent = new Intent(ACTION_SHOW);
        intent.putExtra(EXTRA_SPEC, spec);
        application.sendBroadcast(intent);
    }

}
