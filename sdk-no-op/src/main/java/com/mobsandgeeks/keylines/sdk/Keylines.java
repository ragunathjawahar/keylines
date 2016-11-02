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

import android.app.Fragment;
import android.support.annotation.NonNull;

/**
 * @author Ragunath Jawahar
 */
@SuppressWarnings("unused")
public class Keylines {

    /**
     * No-op implementation.
     *
     * @return A dummy {@link Keylines} instance.
     */
    public static Keylines getInstance() {
        return new Keylines();
    }

    /**
     * No-op implementation.
     *
     * @param fragment A {@link Fragment}.
     */
    public void spec(@NonNull Fragment fragment) {
        // No-op
    }

    /**
     * No-op implementation.
     *
     * @param supportFragment A {@link android.support.v4.app.Fragment}.
     */
    public void spec(@NonNull android.support.v4.app.Fragment supportFragment) {
        // No-op
    }

    private Keylines() {
        // No-op, private constructor
    }

}
