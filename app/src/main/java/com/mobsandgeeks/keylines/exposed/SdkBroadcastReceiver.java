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

package com.mobsandgeeks.keylines.exposed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.mobsandgeeks.keylines.KeylinesService;

import static com.mobsandgeeks.keylines.KeylinesService.ACTION_SHOW;
import static com.mobsandgeeks.keylines.KeylinesService.ACTION_STOP;
import static com.mobsandgeeks.keylines.KeylinesService.EXTRA_SPEC;

/**
 * @author Ragunath Jawahar
 */
public class SdkBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        String action = intent.getAction();

        if (ACTION_SHOW.equals(action)) {
            String spec = intent.getExtras().getString(EXTRA_SPEC);
            if (spec == null) {
                return;
            }
            Intent showIntent = new Intent(context, KeylinesService.class);
            showIntent.setAction(ACTION_SHOW);
            showIntent.putExtra(EXTRA_SPEC, spec);
            context.startService(showIntent);

        } else if (ACTION_STOP.equals(action)) {
            Intent stopIntent = new Intent(context, KeylinesService.class);
            stopIntent.setAction(ACTION_STOP);
            context.startService(stopIntent);

        } else {
            throw new AssertionError("Unknown action: " + action);
        }
    }

}
