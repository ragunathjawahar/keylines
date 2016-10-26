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

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.Nullable;

import timber.log.Timber;

import static com.mobsandgeeks.keylines.KeylinesService.ACTION_STOP;

/**
 * Listens to the "heart-beat" from an SDK integrated app and sends a terminate signal if the app
 * quits or dies.
 *
 * @author Ragunath Jawahar
 */
public class StethoscopeService extends Service {

    private final Messenger messenger = new Messenger(new HeartbeatHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Timber.i("Exciting! I can hear it go dub... dub... dub...");
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sendBroadcast(new Intent(ACTION_STOP));

        Timber.i("Everyone I listen to, dies :'(");
        return super.onUnbind(intent);
    }

    static class HeartbeatHandler extends Handler {
        // Nothing special here... I just like the name.
    }

}
