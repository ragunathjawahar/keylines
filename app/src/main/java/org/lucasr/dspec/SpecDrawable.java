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

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Lucas Rocha {@literal <lucasr@lucasr.org>}
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class SpecDrawable extends Drawable {

    // Attributes
    private int width, height;
    private float density;
    private Spec spec;

    /**
     * Defaults as in {@link com.mobsandgeeks.keylines.NotificationBroadcastReceiver}.
     */
    private boolean showGrid = true;
    private boolean flipHorizontal = false;
    private boolean flipVertical = false;

    public SpecDrawable(Resources resources) {
        this.density = resources.getDisplayMetrics().density;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (spec == null) {
            return;
        }

        // Start painting!
        drawSpacings(spec.spacings(), canvas, width, height);
        drawBaselineGrid(spec.baselineGrid(), canvas, width, height);
        drawKeylines(spec.keylines(), canvas, width, height);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        width = bounds.width();
        height = bounds.height();
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public void setAlpha(int alpha) {
        Grid.setAlpha(alpha);
        Keyline.setAlpha(alpha);
        Spacing.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        Grid.setColorFilter(colorFilter);
        Keyline.setColorFilter(colorFilter);
        Spacing.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setSpec(@Nullable Spec spec) {
        this.spec = spec;
        setColors(spec);
        invalidateSelf();
    }

    public void showGrid(boolean show) {
        this.showGrid = show;
        invalidateSelf();
    }

    public void flipHorizontal(boolean flip) {
        this.flipHorizontal = flip;
        invalidateSelf();
    }

    public void flipVertical(boolean flip) {
        this.flipVertical = flip;
        invalidateSelf();
    }

    private void setColors(@Nullable Spec spec) {
        if (spec == null) {
            return;
        }
        Grid.setColor(spec.baselineGridColor());
        Keyline.setColor(spec.keylineColor());
        Spacing.setColor(spec.spacingColor());
    }

    private void drawSpacings(List<Spacing> spacings, Canvas canvas, int width, int height) {
        if (spacings == null || spacings.isEmpty()) {
            return;
        }

        for (int i = 0, n = spacings.size(); i < n; i++) {
            spacings.get(i).draw(canvas, density, width, height);
        }
    }

    private void drawBaselineGrid(Grid baselineGrid, Canvas canvas, int width, int height) {
        if (!showGrid || baselineGrid == null) {
            return;
        }

        canvas.save();

        if (flipHorizontal) { // Flip H
            canvas.scale(-1, 1, width / 2, 0);
        }

        if (flipVertical) { // Flip V
            canvas.scale(1, -1, 0, height / 2);
        }

        baselineGrid.draw(canvas, density, width, height);

        canvas.restore();
    }

    private void drawKeylines(List<Keyline> keylines, Canvas canvas, int width, int height) {
        if (keylines == null || keylines.isEmpty()) {
            return;
        }

        for (int i = 0, n = keylines.size(); i < n; i++) {
            keylines.get(i).draw(canvas, density, width, height);
        }
    }

}
