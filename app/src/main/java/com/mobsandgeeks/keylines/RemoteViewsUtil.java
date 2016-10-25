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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatDrawableManager;
import android.widget.RemoteViews;

/**
 * @author Ragunath Jawahar
 */
class RemoteViewsUtil {

    static void setImageViewResource(Context context, RemoteViews remoteViews,
            @IdRes int imageViewId, @DrawableRes int drawableRes) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            remoteViews.setImageViewResource(imageViewId, drawableRes);
        } else {
            Bitmap bitmap = getBitmap(context, drawableRes);
            remoteViews.setImageViewBitmap(imageViewId, bitmap);
        }
    }

    private static Bitmap getBitmap(Context context, @DrawableRes int drawableRes) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableRes);

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        return bitmap;
    }

    private RemoteViewsUtil() {
        throw new AssertionError("No instances.");
    }

}
