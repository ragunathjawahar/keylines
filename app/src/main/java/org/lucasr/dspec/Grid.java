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
class Grid implements SpecElement {

    // Constants
    static final int DEFAULT_BASELINE_GRID_CELL_SIZE = 8;
    private static Paint PAINT = new Paint(ANTI_ALIAS_FLAG);

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
    private final float cellSize;

    Grid() {
        this(DEFAULT_BASELINE_GRID_CELL_SIZE);
    }

    Grid(float cellSize) {
        this.cellSize = cellSize;
    }

    @Override
    public void draw(Canvas canvas, float density, int width, int height) {
        float cellSizePx = density * this.cellSize;

        float x = cellSizePx;
        while (x < width) {
            canvas.drawLine(x, 0, x, height, PAINT);
            x += cellSizePx;
        }

        float y = cellSizePx;
        while (y < height) {
            canvas.drawLine(0, y, width, y, PAINT);
            y += cellSizePx;
        }
    }

}
