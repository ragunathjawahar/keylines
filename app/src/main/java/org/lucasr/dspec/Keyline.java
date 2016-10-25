/*
 * Copyright (C) 2014 Lucas Rocha
 * Modifications (C) 2016 Ragunath Jawahar
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

package org.lucasr.dspec;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * @author Lucas Rocha {@literal <lucasr@lucasr.org>}
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
class Keyline implements SpecElement {

    // Constants
    private static Paint PAINT = new Paint(ANTI_ALIAS_FLAG);
    private static final float KEYLINE_STROKE_WIDTH_DIP = 1.1f;

    static {
        PAINT.setStrokeWidth(KEYLINE_STROKE_WIDTH_DIP);
    }

    static void setColor(@ColorInt int color) {
        PAINT.setColor(color);
    }

    static void setColorFilter(@Nullable ColorFilter colorFilter) {
        PAINT.setColorFilter(colorFilter);
    }

    static void setAlpha(int alpha) {
        PAINT.setAlpha(alpha);
    }

    Keyline(float position, From from) {
        this.position = position;
        this.from = from;
    }

    private final float position;
    private final From from;

    @Override
    public void draw(Canvas canvas, float density, int width, int height) {

        final float positionPx;
        switch (from) {
            case LEFT:
            case TOP:
                positionPx = position * density;
                break;

            case RIGHT:
                positionPx = width - (position * density);
                break;

            case BOTTOM:
                positionPx = height - (position * density);
                break;

            case VERTICAL_CENTER:
                positionPx = (height / 2) + (position * density);
                break;

            case HORIZONTAL_CENTER:
                positionPx = (width / 2) + (position * density);
                break;

            default:
                throw new IllegalStateException("Invalid keyline offset.");
        }

        switch (from) {
            case LEFT:
            case RIGHT:
            case HORIZONTAL_CENTER:
                canvas.drawLine(positionPx, 0, positionPx, height, PAINT);
                break;

            case TOP:
            case BOTTOM:
            case VERTICAL_CENTER:
                canvas.drawLine(0, positionPx, width, positionPx, PAINT);
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Keyline)) {
            return false;
        }

        if (o == this) {
            return true;
        }

        final Keyline other = (Keyline) o;
        return this.position == other.position && this.from == other.from;
    }

}
