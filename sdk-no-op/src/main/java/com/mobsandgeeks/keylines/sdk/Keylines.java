package com.mobsandgeeks.keylines.sdk;

import android.app.Fragment;
import android.support.annotation.NonNull;

/**
 * @author Ragunath Jawahar
 */
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
