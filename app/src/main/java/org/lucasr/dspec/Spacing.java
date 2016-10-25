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
class Spacing implements SpecElement {

    // Constants
    private static final Paint PAINT = new Paint(ANTI_ALIAS_FLAG);

    static void setColor(@ColorInt int color) {
        PAINT.setColor(color);
    }

    static void setColorFilter(@Nullable ColorFilter colorFilter) {
        PAINT.setColorFilter(colorFilter);
    }

    static void setAlpha(int alpha) {
        PAINT.setAlpha(alpha);
    }

    // Attributes
    private final float offset;
    private final float size;
    private final From from;

    Spacing(float offset, float size, From from) {
        this.offset = offset;
        this.size = size;
        this.from = from;
    }

    @Override
    public void draw(Canvas canvas, float density, int width, int height) {
        final float position1Px;
        final float position2Px;
        switch (from) {
            case LEFT:
            case TOP:
                position1Px = offset * density;
                position2Px = position1Px + (size * density);
                break;

            case RIGHT:
                position1Px = width - ((offset + size) * density);
                position2Px = width - (offset * density);
                break;

            case BOTTOM:
                position1Px = height - ((offset + size) * density);
                position2Px = height - (offset * density);
                break;

            case VERTICAL_CENTER:
                position1Px = (height / 2) + (offset * density);
                position2Px = position1Px + (size * density);
                break;

            case HORIZONTAL_CENTER:
                position1Px = (width / 2) + (offset * density);
                position2Px = position1Px + (size * density);
                break;

            default:
                throw new IllegalStateException("Invalid spacing offset.");
        }

        switch (from) {
            case LEFT:
            case RIGHT:
            case HORIZONTAL_CENTER:
                canvas.drawRect(position1Px, 0, position2Px, height, PAINT);
                break;

            case TOP:
            case BOTTOM:
            case VERTICAL_CENTER:
                canvas.drawRect(0, position1Px, width, position2Px, PAINT);
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Spacing)) {
            return false;
        }

        if (o == this) {
            return true;
        }

        final Spacing other = (Spacing) o;
        return this.offset == other.offset && this.size == other.size && this.from == other.from;
    }

}
