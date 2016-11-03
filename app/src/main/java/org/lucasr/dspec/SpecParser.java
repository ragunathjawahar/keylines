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

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.functions.Func1;
import timber.log.Timber;

import static org.lucasr.dspec.Grid.DEFAULT_BASELINE_GRID_CELL_SIZE;
import static org.lucasr.dspec.Spec.Builder.DEFAULT_BASELINE_GRID_COLOR;
import static org.lucasr.dspec.Spec.Builder.DEFAULT_KEYLINES_COLOR;
import static org.lucasr.dspec.Spec.Builder.DEFAULT_SPACINGS_COLOR;

/**
 * @author Lucas Rocha {@literal <lucasr@lucasr.org>}
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class SpecParser {

    // JSON Keys
    private static final String KEY_BASELINE_GRID_CELL_SIZE = "baselineGridCellSize";
    private static final String KEY_BASELINE_GRID_COLOR = "baselineGridColor";

    private static final String KEY_KEYLINES_COLOR = "keylinesColor";
    private static final String KEY_KEYLINES = "keylines";

    private static final String KEY_SPACINGS_COLOR = "spacingsColor";
    private static final String KEY_SPACINGS = "spacings";

    private static final String KEY_OFFSET = "offset";
    private static final String KEY_SIZE = "size";
    private static final String KEY_FROM = "from";
    private static final String KEY_LABEL = "label";

    // Value functions
    private static final JSONFunction<Integer> INT_FUNCTION = JSONObject::getInt;

    // Error message
    private static final String JSON_VALUE_PARSE_ERROR =
            "Invalid value for '%s', returning default value %s";

    // Creators, a non-fancy name for unmarshallers
    private static final Func1<JSONObject, Keyline> KEYLINE_CREATOR = keyline -> {
        try {
            int offset = keyline.getInt(KEY_OFFSET);
            From from = From.valueOf(keyline.getString(KEY_FROM).toUpperCase());
            String label = keyline.getString(KEY_LABEL);
            return new Keyline(offset, from, label);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    };

    private static final Func1<JSONObject, Spacing> SPACING_CREATOR = spacing -> {
        try {
            int offset = spacing.getInt(KEY_OFFSET);
            int size = spacing.getInt(KEY_SIZE);
            From from = From.valueOf(spacing.getString(KEY_FROM).toUpperCase());
            return new Spacing(offset, size, from);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    };

    /**
     * Creates a {@link Spec} from a {@link JSONObject}.
     * @param json The JSON object to use.
     *
     * @return A {@link Spec} object.
     */
    public static Spec fromJson(JSONObject json) {
        Spec.Builder builder = Spec.builder();

        buildBaselineGrid(builder, json);
        buildKeylines(builder, json);
        buildSpacings(builder, json);

        return builder.build();
    }

    private static void buildBaselineGrid(Spec.Builder builder, JSONObject json) {
        if (json.has(KEY_BASELINE_GRID_COLOR)) {
            int baselineGridColor = getInt(
                    json, KEY_BASELINE_GRID_COLOR, DEFAULT_BASELINE_GRID_COLOR
            );
            builder.baselineGridColor(baselineGridColor);
        }

        if (json.has(KEY_BASELINE_GRID_CELL_SIZE)) {
            int cellSize = getInt(
                    json, KEY_BASELINE_GRID_CELL_SIZE, DEFAULT_BASELINE_GRID_CELL_SIZE
            );
            builder.baselineGrid(new Grid(cellSize));
        }
    }

    private static void buildKeylines(Spec.Builder builder, JSONObject json) {
        if (json.has(KEY_KEYLINES_COLOR)) {
            int keylinesColor = getInt(json, KEY_KEYLINES_COLOR, DEFAULT_KEYLINES_COLOR);
            builder.keylinesColor(keylinesColor);
        }

        List<Keyline> keylines = getObjects(json.optJSONArray(KEY_KEYLINES), KEYLINE_CREATOR);
        if (keylines != null) {
            builder.keylines(keylines);
        }
    }

    private static void buildSpacings(Spec.Builder builder, JSONObject json) {
        if (json.has(KEY_SPACINGS_COLOR)) {
            int keylinesColor = getInt(json, KEY_SPACINGS_COLOR, DEFAULT_SPACINGS_COLOR);
            builder.spacingsColor(keylinesColor);
        }

        List<Spacing> spacings = getObjects(json.optJSONArray(KEY_SPACINGS), SPACING_CREATOR);
        if (spacings != null) {
            builder.spacings(spacings);
        }
    }

    private static int getInt(JSONObject json, String key, int defaultValue) {
        return getValue(INT_FUNCTION, json, key, defaultValue);
    }

    private static <T> T getValue(JSONFunction<T> func, JSONObject json, String key,
            T defaultValue) {
        try {
            return func.call(json, key);
        } catch (Exception e) {
            Timber.w(JSON_VALUE_PARSE_ERROR, key, String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    @Nullable
    private static <T> List<T> getObjects(JSONArray array, Func1<JSONObject, T> creator) {
        if (array == null || array.length() == 0) {
            return null;
        }

        final int count = array.length();
        final List<T> objects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                final JSONObject spacing = array.getJSONObject(i);
                objects.add(creator.call(spacing));
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        return !objects.isEmpty() ? Collections.unmodifiableList(objects) : null;
    }

    private SpecParser() {
        throw new AssertionError("No instances.");
    }

    /**
     * Because, {@link rx.functions.Func2} doesn't throw exceptions.
     *
     * @param <R> The type of value returned.
     */
    private interface JSONFunction<R> {
        R call(JSONObject json, String key) throws JSONException;
    }

}
