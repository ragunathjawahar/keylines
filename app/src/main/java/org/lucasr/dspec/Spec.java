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

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Lucas Rocha {@literal <lucasr@lucasr.org>}
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
class Spec {

    // Baseline Grid
    private final Grid baselineGrid;
    @ColorInt private final int baselineGridColor;

    // Keylines
    @Nullable private final List<Keyline> keylines;
    @ColorInt private final int keylineColor;

    // Spacings
    @Nullable private final List<Spacing> spacings;
    @ColorInt private final int spacingColor;

    static Builder builder() {
        return new Builder();
    }

    Grid baselineGrid() {
        return baselineGrid;
    }

    int baselineGridColor() {
        return baselineGridColor;
    }

    @Nullable List<Keyline> keylines() {
        return keylines;
    }

    int keylineColor() {
        return keylineColor;
    }

    @Nullable List<Spacing> spacings() {
        return spacings;
    }

    int spacingColor() {
        return spacingColor;
    }

    private Spec(Grid baselineGrid, int baselineGridColor,
                 @Nullable List<Keyline> keylines, int keylineColor,
                 @Nullable List<Spacing> spacings, int spacingColor) {
        this.baselineGrid = baselineGrid;
        this.baselineGridColor = baselineGridColor;
        this.keylines = keylines;
        this.keylineColor = keylineColor;
        this.spacings = spacings;
        this.spacingColor = spacingColor;
    }

    static class Builder {

        // Color (defaults)
        static final int DEFAULT_BASELINE_GRID_COLOR = 0x44C2185B;
        static final int DEFAULT_KEYLINES_COLOR = 0xCCC2185B;
        static final int DEFAULT_SPACINGS_COLOR = 0xAA89FDFD;

        // Attributes
        private Grid baselineGrid;
        private int baselineGridColor = DEFAULT_BASELINE_GRID_COLOR;

        private List<Keyline> keylines;
        private int keylineColor = DEFAULT_KEYLINES_COLOR;

        private List<Spacing> spacings;
        private int spacingColor = DEFAULT_SPACINGS_COLOR;

        private Builder() {
            // No-op
        }

        Builder baselineGrid(Grid baselineGrid) {
            this.baselineGrid = baselineGrid;
            return this;
        }

        Builder baselineGridColor(int baselineGridColor) {
            this.baselineGridColor = baselineGridColor;
            return this;
        }

        Builder keylines(List<Keyline> keylines) {
            this.keylines = keylines;
            return this;
        }

        Builder keylinesColor(int keylineColor) {
            this.keylineColor = keylineColor;
            return this;
        }

        Builder spacings(List<Spacing> spacings) {
            this.spacings = spacings;
            return this;
        }

        Builder spacingsColor(int spacingColor) {
            this.spacingColor = spacingColor;
            return this;
        }

        Spec build() {
            if (baselineGrid == null) {
                baselineGrid = new Grid(); // Supply a default 8dp baseline grid, if unset.
            }

            return new Spec(baselineGrid, baselineGridColor, keylines, keylineColor,
                    spacings, spacingColor);
        }
    }

}
